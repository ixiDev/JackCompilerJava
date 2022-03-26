import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 24/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class TokensWriter {

    private final BufferedWriter writer;


    public TokensWriter(File jackFile) throws IOException {
        File out = new File(jackFile.getParent(), getXMLFileName(jackFile));
        writer = new BufferedWriter(
                new FileWriter(out)
        );
        writeTag("<tokens>");
    }

    public void writeTag(String tage) throws IOException {
        writer.write(tage);
        writer.newLine();
    }

    public void writeTokenTag(JackToken token) throws Exception {
        writer.write(token.getTag());
        writer.newLine();
    }


    private String getXMLFileName(File jackFile) {
        String name = jackFile.getName();
        return name.substring(0, name.lastIndexOf(".")) + "T.xml";
    }

    public void close() throws IOException {
        writeTag("</tokens>");
        writer.flush();
        writer.close();
    }

    public void writeTokenTokens(List<JackToken> jackTokens) {
        for (JackToken token : jackTokens) {
            try {
                writeTokenTag(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
