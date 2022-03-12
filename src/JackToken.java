/**
 * * Author : Abdelmajid ID ALI
 * * On : 02/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class JackToken {
    private String value;
    private JackInstructionType type;
    private int lineNumber;

    public JackToken(String value, JackInstructionType type, int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
    }

//    public JackToken(String value, JackInstructionType type) {
//        this.value = value;
//        this.type = type;
//    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JackInstructionType getType() {
        return type;
    }

    public void setType(JackInstructionType type) {
        this.type = type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "'" + value + "' at line " + lineNumber;
    }

    public String getTag() {
        switch (type) {
            case KEYWORD:
                return "<keyword> " + value + " </keyword>";
            case SYMBOL:
                String tmp = value;
                if (tmp.equals("<"))
                    tmp = "&lt;";
                else if (tmp.equals(">"))
                    tmp = "&gt;";
                else if (tmp.equals("&"))
                    tmp = "&amp;";
                return "<symbol> " + tmp + " </symbol>";
            case IDENTIFIER:
                return "<identifier> " + value + " </identifier>";
            case INTEGER_CONSTANT:
                return "<integerConstant> " + value + " </integerConstant>";
            case STRING_CONSTANT:
                return "<stringConstant> " + value.substring(1, value.length() - 1) + " </stringConstant>";
            default:
                return "<unknown> " + value + " </unknown>";
        }
    }
}
