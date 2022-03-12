/**
 * * Author : Abdelmajid ID ALI
 * * On : 02/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class IllegalJackException extends Exception {
    public IllegalJackException() {
    }

    public IllegalJackException(String message) {
        super(message);
    }

    public IllegalJackException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalJackException(Throwable cause) {
        super(cause);
    }

    public IllegalJackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
