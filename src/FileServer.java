import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    int port;
    String wwwhome;
    Socket con;

    FileServer(int port, String wwwhome) {
        this.port = port;
        this.wwwhome = wwwhome;
    }

    public static void main(String[] args) {

        int port = 5000;
        String wwwhome = "C:/www";
        FileServer fs = new FileServer(port, wwwhome);
        fs.run();
    }

    void run() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not start server: " + e);
            System.exit(-1);
        }
        System.out.println("FileServer accepting connections on port " +
                port);

        while (true) {
            try {
                con = ss.accept();
                Runnable connectionHandler = new ConnectionHandler(con, wwwhome);
                new Thread(connectionHandler).start();

            } catch (IOException e) {
                System.err.println(e);
                break;
            }

        }
        try {
            if (con != null)
                con.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}