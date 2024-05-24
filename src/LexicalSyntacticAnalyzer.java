import java.io.*;
import java.util.*;

public class LexicalSyntacticAnalyzer {
    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Usage: java LexicalSyntacticAnalyzer <input-file>");
//            return;
//        }
//        String inputFile = args[0];
        String inputFile = "C:\\Users\\Егор\\IdeaProjects\\test\\src\\Test.java";

        try {
            List<Token> tokens = LexicalAnalyzer.tokenize(inputFile);
            Parser parser = new Parser(tokens);
            Program program = parser.parse();
            System.out.println("Parsing completed successfully. AST:");
            program.accept(new ASTPrinter());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Parsing error: " + e.getMessage());
        }
    }
}
