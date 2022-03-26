import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 02/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class JackParser {
    private final File input;
    private JackToken currentToken;
    private JackToken prevToken;
    private Iterator<JackToken> tokenIterator;

    public JackParser(File input) {
        this.input = input;
    }

    public boolean hasMoreTokens() {
        return tokenIterator != null && tokenIterator.hasNext();
    }

    public void advance() {
        if (hasMoreTokens()) {
            prevToken = currentToken;
            currentToken = tokenIterator.next();
        }
    }

    public JackInstructionType tokenType() {
        if (currentToken == null)
            return JackInstructionType.UNKNOWN;
        return currentToken.getType();
    }

    public String tokenValue() {
        if (currentToken == null)
            return "";
        return currentToken.getValue();
    }

    public JackToken getCurrentToken() {
        return currentToken;
    }

    public void parse() throws Exception {
        JackTokenizer tokenizer = new JackTokenizer(input);
        tokenIterator = tokenizer.getTokens();
    }

    public JackToken getPrevToken() {
        return prevToken;
    }

    public int getLine() {
        return currentToken.getLineNumber();
    }
}
