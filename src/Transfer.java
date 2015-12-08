import java.io.*;
import java.util.Date;

/**
 * Created by liviu on 12/8/2015.
 */
public class Transfer {
    public String Message;
    public Date SendDate;
    public Date ReceiveDate;
    public String ContentType;
    public String SizeInBytes;
    public String FileContent;
    public String FileName;

    public void saveFile() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(FileName), "utf-8"))) {
            writer.write(FileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
