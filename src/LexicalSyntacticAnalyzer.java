import java.io.*;
import java.util.*;

public class LexicalSyntacticAnalyzer {
    public static void main(String[] args) {
        String inputFile = "C:\\Users\\Егор\\IdeaProjects\\test\\src\\Test.java";

        try {
            List<Token> tokens = LexicalAnalyzer.tokenize(inputFile);
            Parser parser = new Parser(tokens);
            Program program = parser.parse();
            System.out.println("Parsing completed successfully.");

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            program.accept(semanticAnalyzer);
            System.out.println("Semantic analysis completed successfully. AST:");



            // Print AST
            program.accept(new ASTPrinter());

            // Perform semantic analysis


        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Parsing or semantic error: " + e.getMessage());
        }
    }
}
