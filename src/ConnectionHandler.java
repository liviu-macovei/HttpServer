import java.io.*;
import java.net.Socket;
import java.net.URLConnection;
import java.util.Date;

/**
 * Created by liviu on 10/25/2015.
 */
public class ConnectionHandler implements Runnable {

    private Socket socket;
    BufferedReader in;
    PrintStream pout;
    private String wwwhome;
    OutputStream out;

    ConnectionHandler(Socket socket, String wwwhome) {
        this.socket = socket;
        this.wwwhome = wwwhome;

        try {
            out = new BufferedOutputStream(socket.getOutputStream());
            pout = new PrintStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader
                    (new InputStreamReader(socket.getInputStream()));
            String request = in.readLine();
            socket.shutdownInput(); // ignore the rest
            log(socket, request);

            processRequest(request);
            pout.flush();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void processRequest(String request) throws IOException {
        if (!request.startsWith("GET") || request.length() < 14 ||
                !(request.endsWith("HTTP/1.0") ||
                        request.endsWith("HTTP/1.1")) ||
                request.charAt(4) != '/') {
            errorReport(pout, socket, "400", "Bad Request",
                    "Your browser sent a request that " +
                            "this server could not understand.");
        } else {
            String req = request.substring(4, request.length() - 9).trim();
            if (req.indexOf("/.") != -1 || req.endsWith("~")) {
                errorReport(pout, socket, "403", "Forbidden",
                        "You don't have permission to access " +
                                "the requested URL.");
            } else {
                String path = wwwhome + "/" + req;
                File f = new File(path);
                if (f.isDirectory() && !path.endsWith("/")) {
                    pout.print("HTTP/1.0 301 Moved Permanently\r\n" +
                            "Location: http://" +
                            socket.getLocalAddress().getHostAddress() + ":" +
                            socket.getLocalPort() + req + "/\r\n\r\n");
                    log(socket, "301 Moved Permanently");
                } else {
                    if (f.isDirectory()) {
                        errorReport(pout, socket, "404", "Not Found",
                                "The requested URL a folder not a file.");
                    }
                    try {
                        InputStream file = new FileInputStream(f);
                        String contenttype =
                                URLConnection.guessContentTypeFromName(path);
                        pout.print("HTTP/1.0 200 OK\r\n");
                        pout.print("Content-Type: " + contenttype + "\r\n");
                        pout.print("Size: " + f.length() + "\r\n");
                        pout.print("Date: " + new Date() + "\r\n");
                        sendFile(file, out); // send raw file
                        log(socket, "200 OK");

                    } catch (FileNotFoundException e) {
                        errorReport(pout, socket, "404", "Not Found",
                                "The requested URL was not found " +
                                        "on this server.");
                    }
                }
            }
        }
    }

    void log(Socket con, String msg) {
        System.out.println(new Date() + " [" +
                con.getInetAddress().getHostAddress() +
                ":" + con.getPort() + "] " + msg);
    }

    void errorReport(PrintStream pout, Socket con,
                     String code, String title, String msg) {
        pout.print(
                "HTTP/1.0 " + code + " " + title + "\r\n" +
                        "\r\n" + msg + "\r\n" +
                        "<ADDRESS>IXWT FileServer 1.0 at " + con.getLocalAddress().getHostName() +
                        " Port " + con.getLocalPort() + "</ADDRESS>\r\n" +
                        "\r\n");
        log(con, code + " " + title);
    }

    void sendFile(InputStream file, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1000];
        while (file.available() > 0)
            out.write(buffer, 0, file.read(buffer));
    }
}
