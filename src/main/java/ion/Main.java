package ion;

import ion.assembly_frontend.AssemblyFrontend;
import ion.lexer.Lexer;
import ion.parser.Parser;
import ion.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static File linuxDir = new File("\\\\wsl$\\Ubuntu-20.04\\shared");
    public static File inputFile = new File(linuxDir.getAbsolutePath() + "\\test.ion");
    public static File outputFile = new File(inputFile + ".asm");
    public static File astOutputFile = new File("src/main/resources/AST-out");

    public static void main(String[] args) {
        String code = "";
        try {
            code = Utils.readFileToString(inputFile);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        //System.out.println(parser.parse());
        String astString = parser.parse().toString();
        AssemblyFrontend asmFront = new AssemblyFrontend(parser);
        try {
            Utils.writeFileFromString(outputFile, asmFront.generate("nasm linux x86_64"));
            Utils.writeFileFromString(astOutputFile, astString);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
