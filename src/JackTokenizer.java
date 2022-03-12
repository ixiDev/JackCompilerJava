import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 02/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class JackTokenizer {
    private final File input;
    private int linesCount = 0;

    public JackTokenizer(File input) {
        this.input = input;
    }

    private boolean multiComments = false;

    public Iterator<JackToken> getTokens() throws FileNotFoundException {
        ArrayList<JackToken> tokens = new ArrayList<>();
        FileReader reader = new FileReader(input);
        BufferedReader buffer = new BufferedReader(reader);

        linesCount = 1;


        buffer.lines().forEach(line -> {
            String trim = line.trim();
            if (multiComments && trim.endsWith("*/")) {
                multiComments = false;
            } else {
                if (canSkip(trim)) {
                    multiComments = false;
                } else if (trim.startsWith("/*") || trim.startsWith("*")) {
                    multiComments = true;
                } else {
                    tokens.addAll(readLineTokens(line));
                    multiComments = false;
                }
            }
            // else skip
            linesCount++;
        });
        return tokens.iterator();
    }

    private List<JackToken> readLineTokens(String line) {
        if (line.contains("//"))
            line = line.substring(0, line.lastIndexOf("//"));
        ArrayList<JackToken> tokens = new ArrayList<>();
        Matcher matcher = TokenPatterns.PATTERN_SPLIT.matcher(line);
        while (matcher.find()) {
            String tmp = matcher.group().trim();
            if (!tmp.isBlank()) {
                if (isMatches(TokenPatterns.PATTERN_KEYWORD, tmp)) {

                    tokens.add(new JackToken(tmp, JackInstructionType.KEYWORD, linesCount));

                } else if (isMatches(TokenPatterns.PATTERN_IDENTIFIER, tmp)) {

                    tokens.add(new JackToken(tmp, JackInstructionType.IDENTIFIER, linesCount));

                } else if (isMatches(TokenPatterns.PATTERN_SYMBOL, tmp)) {

                    tokens.add(new JackToken(tmp, JackInstructionType.SYMBOL, linesCount));

                } else if (isMatches(TokenPatterns.PATTERN_STRING_CONSTANT, tmp)) {

//                    tmp = tmp.substring(1, tmp.length() - 1);
                    tokens.add(new JackToken(tmp, JackInstructionType.STRING_CONSTANT, linesCount));

                } else if (isMatches(TokenPatterns.PATTERN_INTEGER_CONSTANT, tmp)) {

                    tokens.add(new JackToken(tmp, JackInstructionType.INTEGER_CONSTANT, linesCount));

                } else {
                    throw new RuntimeException("Unexpected token " + tmp);
                }
            }
        }
        return tokens;
    }

    private boolean isMatches(Pattern pattern, String tmp) {
        return pattern.matcher(tmp).matches();
    }

    private boolean canSkip(String line) {
        return line.isEmpty()
                || line.isBlank()
                || line.startsWith("//")
                || isMatches(TokenPatterns.PATTERN_MULTILINE_COMMENT, line);
    }
}
