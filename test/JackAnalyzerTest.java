import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 24/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
class JackAnalyzerTest {

    @Test
    void main() {
        JackAnalyzer.main(new String[]{"./Square"});
    }
    @Test
    void testConvertToBin() {
        JackAnalyzer.main(new String[]{"./ConvertToBin"});
    }
}