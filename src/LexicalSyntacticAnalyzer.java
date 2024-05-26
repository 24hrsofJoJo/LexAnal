import java.io.*;
import java.util.*;

public class LexicalSyntacticAnalyzer {
    public static void main(String[] args) {
        String inputFile = PathToFile;

        try {
            List<Token> tokens = LexicalAnalyzer.tokenize(inputFile);
            Parser parser = new Parser(tokens);
            Program program = parser.parse();
            System.out.println("Parsing completed successfully. AST:");

            // Print AST
            program.accept(new ASTPrinter());

            // Perform semantic analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            program.accept(semanticAnalyzer);
            System.out.println("Semantic analysis completed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println("Parsing or semantic error: " + e.getMessage());
        }
    }
}
