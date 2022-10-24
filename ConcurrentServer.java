import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConcurrentServer implements Runnable {

    /* 
    Concurrent Server
    - Serverkomponente soll zu beliebigen Zeitpunkten neue Verbindungsanforderungen von Clientkomponenten akzeptieren können
    - Serverkomponente soll in der Lage sein, gleichzeitig mit mehreren Clientkomponenten verbunden zu sein
    - Clientkomponente soll sich mit der Serverkomponente verbinden (TCP basierte Kommunikation)
    - synchrone Kommunikation ... Clientkomponente soll in der Lage sein, Daten an die Serverkomponente zu senden (Request) und auf die zugehörige Response zu warten
    - Serverkomponente soll in der Lage sein, von einer Clientkomponente Request-Nachrichten empfangen, diese verarbeiten und entsprechende Response-Nachrichten an die Clientkomponente zu senden.
    - Client- und Serverkomponente kommunizieren in einem Multiple-Request-Szenario miteinander.

    */

    private final Socket clientSocket;
    private final int id;

    public ConcurrentServer(Socket client, int id) {
        this.clientSocket = client;
        this.id = id;
    }

    public void run()
    {
        
        try {
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String response = "";
            do {
                
                // receive lines
                int lines = in.read();
                // receive request
                String msgReceived="";
                for (int i=0; i<lines; i++) {
                    msgReceived += "\n"+id+"> "+in.readLine();
                }
                System.out.println(msgReceived);

                // send response
                response = evalMsg(lines);
                out.println(response);
                out.flush();


            } while ( !response.equals("\0") );
            
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

        

    

    public static void main(String[] args) {
        int port = 5555;
        ServerSocket listenSocket = null;
        int id=0;

        try {
            listenSocket = new ServerSocket(port);
            while (true) {
                Socket client = listenSocket.accept();
                id++;

                System.out.println("Client #"+id+" connected from "+client.getLocalAddress());
                
                // start new thread
                ConcurrentServer clientSock = new ConcurrentServer(client, id);
                new Thread(clientSock).start();
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (listenSocket != null) {
                try { 
                    listenSocket.close(); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    static String evalMsg(int lines) {
        return lines > 0 ? "" : "\0";
    }
}
