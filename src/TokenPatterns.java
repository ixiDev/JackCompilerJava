import java.util.regex.Pattern;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 02/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class TokenPatterns {

    public static final Pattern PATTERN_KEYWORD = Pattern.compile("("
            + "(class)|(constructor)|(function)|(method)|(field)|(static)|"
            + "(var)|(int)|(char)|(boolean)|(void)|(true)|(false)|(null)|(this)|"
            + "(let)|(do)|(if)|(else)|(while)|(return))"
    );

    public static final Pattern PATTERN_SYMBOL = Pattern.compile(
            "([\\[\\](){}.,;+=\\-*/&|<>~])"
    );
    public static final Pattern PATTERN_INTEGER_CONSTANT = Pattern.compile(
            "([0-9]+)"
    );
    public static final Pattern PATTERN_STRING_CONSTANT = Pattern.compile(
            "(\"(.*?)\")"
    );
    public static final Pattern PATTERN_MULTILINE_COMMENT = Pattern.compile(
            "((/\\*{2})(.*?)(\\*/)$)"
    );
    public static final Pattern PATTERN_IDENTIFIER = Pattern.compile(
            "((^([a-zA-Z]+)(\\w+)?)+)"
    );
    public static final Pattern PATTERN_SPLIT = Pattern.compile(
            "(\\w+)|([()\\[\\]{}.,;+=\\-*/&|<>~])|(\"(.*?)\")"
    );


    public static final Pattern TYPE_PATTERN = Pattern.compile(
            "(int)|(char)|(boolen)|(void)|" + PATTERN_IDENTIFIER.pattern()
    );

    private TokenPatterns() {
    }

    public static boolean isType(String tokenValue) {
        return TYPE_PATTERN.matcher(tokenValue).matches();
    }
}
