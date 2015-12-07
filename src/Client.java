import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by liviu on 10/25/2015.
 */
public class Client {
    String ip = "127.0.0.1";

    Client() {
        //Open socket on ip address
        Socket socket = null;
        PrintWriter out = null;
        try {
            socket = new Socket(ip, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // send request
            out.print("GET /1.txt HTTP/1.0\r\n\r\n");
            out.flush();
            // read one line of input
            String ceva = in.readLine();
            while (ceva != null) {
                System.out.println("Response from " + ip + ": " + ceva);
                ceva = in.readLine();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
