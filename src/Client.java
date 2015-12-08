import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            Transfer transfer = new Transfer();
            transfer.FileName = "1.txt";
            out.print("GET /" + transfer.FileName + " HTTP/1.0\r\n\r\n");
            out.flush();
            // read one line of input
            String readline = in.readLine();

            int i = 0;
            transfer.ReceiveDate = new Date();
            while (readline != null) {
                System.out.println("Response from " + ip + ": " + readline);
                if (i == 0) {
                    transfer.Message = readline;
                } else if (i == 1) {
                    transfer.ContentType = readline;
                } else if (i == 2) {
                    transfer.SizeInBytes = readline;
                } else if (i == 3) {
                    DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    Date date = format.parse(readline.substring(6));
                    transfer.SendDate = date;
                } else if (i == 4) {
                    transfer.FileContent = readline;
                }
                i++;
                readline = in.readLine();
            }
            transfer.saveFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
    }
}
