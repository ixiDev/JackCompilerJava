import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 06/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class VMWriter {

    private final BufferedWriter writer;
    private final File jackFile;
    private final File vmFile;

    public VMWriter(File jackFile) throws IOException {
        this.jackFile = jackFile;
        vmFile = new File(jackFile.getParent(), getOutputFileName(jackFile));
        if (vmFile.exists()) vmFile.delete();
        writer = new BufferedWriter(new FileWriter(vmFile));
    }

    public void writePush(SegmentsEnum segment, int index) throws Exception {
        if (segment == null)
            throw new IllegalJackException("segment cannot be null");
        writer.write("push " + segment.name().toLowerCase() + " " + index);
        writer.newLine();
    }

    public void writePop(SegmentsEnum segment, int index) throws Exception {

        if (segment == null)
            throw new IllegalJackException("segment cannot be null");
        writer.write("pop " + segment.name().toLowerCase() + " " + index);
        writer.newLine();
    }

    public void writeArithmetic(CommandEnum command) throws IOException {
        writer.write(command.name().toLowerCase());
        writer.newLine();
    }

    public void writeLabel(String label) throws IOException {
        writer.write("label " + label);
        writer.newLine();
    }

    public void writeGoto(String label) throws IOException {
        writer.write("goto " + label);
        writer.newLine();
    }

    public void writeIf(String label) throws IOException {
        writer.write("if-goto " + label);
        writer.newLine();
    }

    public void writeCall(String name, int nArgs) throws IOException {
        writer.write("call " + name + " " + nArgs);
        writer.newLine();
    }

    public void writeFunction(String name, int nVars) throws IOException {
        writer.write("function " + name + " " + nVars);
        writer.newLine();
    }

    public void writeReturn() throws IOException {
        // TODO: 11/03/2022
        writer.write("return");
        writer.newLine();
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }


    private String getOutputFileName(File jackFile) {
        String name = jackFile.getName();
        return name.substring(0, name.lastIndexOf(".")) + ".vm";
    }

    public void writeString(String str) throws Exception {
        str=str.substring(1, str.length() - 1);
        writePush(SegmentsEnum.CONSTANT, str.length());
        writeCall("String.new", 1);
        for (char c : str.toCharArray()) {
            writePush(SegmentsEnum.CONSTANT, c);
            writeCall("String.appendChar", 2);
        }
    }
}
