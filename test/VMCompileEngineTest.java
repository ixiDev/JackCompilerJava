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
        File squareDir=new File("/home/ixi/IdeaProjects/nand2tetris/JackCompilerII/test/Square");

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
        File squareDir=new File("/home/ixi/IdeaProjects/nand2tetris/JackCompilerII/test/Seven");

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
        File squareDir=new File("/home/ixi/IdeaProjects/nand2tetris/JackCompilerII/test/Pong");

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
        File squareDir=new File("/home/ixi/IdeaProjects/nand2tetris/JackCompilerII/test/Average");

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
        File squareDir=new File("/home/ixi/IdeaProjects/nand2tetris/JackCompilerII/test/ComplexArrays");

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
        File squareDir=new File("/home/ixi/IdeaProjects/nand2tetris/JackCompilerII/test/ConvertToBin");

        File[] files = squareDir.listFiles((dir, name) -> name.endsWith(".jack"));
        if (files!=null) {
            for (File file : files) {
                VMCompileEngine engine=new VMCompileEngine(file);
                engine.compileClass();
            }
        }
    }
}