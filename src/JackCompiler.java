import java.io.File;

public class JackCompiler {

    public static void main(String[] args) {

        System.out.println("Jack Compiler ");
        try {
            CompileEngine engine = new CompileEngine(
                    new File("/home/ixi/IdeaProjects/nand2tetris/JackCompiler/src/test/Square/Main.jack")
            );
            engine.compileClass();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
