import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 08/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
class VMCompileEngineTest {


    @Test
    void testCompileSquare() throws Exception {
        File squareDir=new File("./Square");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }
    @Test
    void testCompileSeven() throws Exception {
        File squareDir=new File("./Seven");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }

    @Test
    void testCompilePong() throws Exception {
        File squareDir=new File("./Pong");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }


    @Test
    void testCompileAverage() throws Exception {
        File squareDir=new File("./Average");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }

    @Test
    void testCompileComplexArrays() throws Exception {
        File squareDir=new File("./ComplexArrays");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }

    @Test
    void testCompileConvertToBin() throws Exception {
        File squareDir=new File("./ConvertToBin");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }
}