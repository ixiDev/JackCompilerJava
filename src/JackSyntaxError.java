/**
 * * Author : Abdelmajid ID ALI
 * * On : 03/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class JackSyntaxError extends Exception{
    public JackSyntaxError() {
    }

    public JackSyntaxError(String message) {
        super(message);
    }

    public JackSyntaxError(String message, Throwable cause) {
        super(message, cause);
    }

    public JackSyntaxError(Throwable cause) {
        super(cause);
    }

    public JackSyntaxError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
