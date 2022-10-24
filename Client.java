import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        int port = 5555;
        String addr = "localhost";
        
        try {
            // connect() to server
            Socket socket = new Socket(addr, port);
            System.out.println("Connection to "+addr+" established");

            // after established connection: open streams for R/W
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(
            new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Multiple-Request Szenario
            String msgReceived;
            // (msgReceived = in.readLine()) != null
            do {
                String msgSend = "", tmp;
                int lines = 0;

                // read input until empty line
                System.out.print("> ");
                while (!(tmp = stdIn.readLine()).equals("")) {
                    lines++;
                    msgSend += tmp+"\n";
                    System.out.print("> ");
                }

                // send line count
                out.write(lines);
                out.flush();
                // send message
                out.write(msgSend);
                out.flush();
                
                // receive response
                msgReceived = in.readLine();
                System.out.println(msgReceived);
            } while (evalMsg(msgReceived));
            

            in.close();
            out.close();
            socket.close();
            stdIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }

    static boolean evalMsg(String msg) {
        return msg.equals("\0") ? false : true;
    }
}
