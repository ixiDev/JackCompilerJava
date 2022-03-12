import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 08/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class JackAnalyzer {

    private final BufferedWriter writer;


    public JackAnalyzer(File jackFile) throws IOException {
        File out = new File(jackFile.getParent(), getXMLFileName(jackFile));
        writer = new BufferedWriter(
                new FileWriter(out)
        );
    }

    public void writeTag(String tage) throws IOException {
        writer.write(tage);
        writer.newLine();
    }

    public void writeTokenTag(JackToken token) throws IOException {
        writer.write(token.getTag());
        writer.newLine();
    }


    private String getXMLFileName(File jackFile) {
        String name = jackFile.getName();
        return name.substring(0, name.lastIndexOf(".")) + ".xml";
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
