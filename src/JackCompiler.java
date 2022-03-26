import java.io.File;

public class JackCompiler {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage JackCompiler filename.jack");
            System.exit(1);
            return;
        }
//
        File file = new File(args[0]);
        try {
            if (file.isDirectory()) {
                File[] jacks = file.listFiles((dir, name) -> name.endsWith("jack"));
                assert jacks != null;
                for (File jack : jacks) {
                    VMCompileEngine engine = new VMCompileEngine(jack);
                    engine.compileClass();
                }
            } else {
                VMCompileEngine engine = new VMCompileEngine(file);
                engine.compileClass();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
