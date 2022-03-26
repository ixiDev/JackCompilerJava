import java.io.File;

/**
 * * Author : Abdelmajid ID ALI
 * * On : 24/03/2022
 * * Email :  abdelmajid.idali@gmail.com
 **/
public class JackAnalyzer {


    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage JackAnalyzer filename.jack");
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
