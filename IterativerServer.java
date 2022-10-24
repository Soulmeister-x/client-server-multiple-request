import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class IterativerServer extends Thread {

    protected int id;
	protected Socket client;

    /* 
    iterativer Server
    - Clientkomponente soll sich mit der Serverkomponente verbinden (TCP basierte Kommunikation)
    - synchrone Kommunikation ... Clientkomponente soll in der Lage sein, Daten an die Serverkomponente zu senden (Request) und auf die zugehörige Response zu warten
    - Serverkomponente soll in der Lage sein, von einer Clientkomponente Request-Nachrichten empfangen, diese verarbeiten und entsprechende Response-Nachrichten an die Clientkomponente zu senden.
    - Client- und Serverkomponente kommunizieren in einem Multiple-Request-Szenario miteinander.

    */

    public IterativerServer(Socket socket, int id) {
        this.client = socket;
        this.id = id;

        System.out.println("\n######## Client #" + id + " ########");
        System.out.println("connected from: " + client.toString());

        run();
    }


    public static void main(String[] args) {
        int port = 5555;
        ServerSocket listenSocket = null;
        int id = 0;
        Socket socket = null;
       
        try {
            listenSocket = new ServerSocket(port);

            while ((socket = listenSocket.accept()) != null) {
                
                id++;
                System.out.println("Client #"+id+" from "+socket.getLocalAddress());

                new IterativerServer(socket, id);
                
                System.out.println("Client #"+id+" disconnected");
            }


            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                listenSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static String evalMsg(int lines) {
        return lines > 0 ? "" : "\0";
    }

    
    public void run () {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            
            String response = "";
            do {
                
                // receive lines
                int lines = in.read();
                // receive request
                String msgReceived="";
                for (int i=0; i<lines; i++) {
                    msgReceived += "\n> "+in.readLine();
                }
                System.out.println(msgReceived);

                // send response
                response = evalMsg(lines);
                out.println(response);
                out.flush();


            } while ( !response.equals("\0") );


            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
