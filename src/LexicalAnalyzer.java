import java.io.*;
import java.util.*;

enum TokenType {
    KEYWORD, IDENTIFIER, NUMBER, OPERATOR, SEPARATOR, STRING_LITERAL, UNKNOWN
}

class Token {
    TokenType type;
    String value;
    int lineNumber;

    Token(TokenType type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return String.format("Token[type=%s, value='%s', line=%d]", type, value, lineNumber);
    }
}

public class LexicalAnalyzer {
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "package", "private", "protected", "public", "return", "short", "static",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
            "void", "volatile", "while", "true", "false", "null", "if", "."
    ));

    private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList(
            "+", "-", "*", "/", "%", "=", "&", "|", "!", "<", ">", "^", "~", "?", ":", "==", "!=", "<=", ">=", "++", "--",
            "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<", ">>", ">>>", "<<=", ">>=", ">>>="
    ));

    private static final Set<String> SEPARATORS = new HashSet<>(Arrays.asList(
            "(", ")", "{", "}", "[", "]", ";", ",", ".", "@", "::"
    ));

    public static List<Token> tokenize(String inputFile) throws IOException {
        List<Token> tokens = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line;
        int lineNumber = 1;
        while ((line = reader.readLine()) != null) {
            tokenizeLine(line, lineNumber, tokens);
            lineNumber++;
        }
        reader.close();
        return tokens;
    }

    private static void tokenizeLine(String line, int lineNumber, List<Token> tokens) {
        StringTokenizer tokenizer = new StringTokenizer(line, " \t\n\r\f(){}[]=.;,+-*/%&|!<>?^:~\"'", true);
        boolean inString = false;
        StringBuilder stringLiteral = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            String tokenValue = tokenizer.nextToken();
            if (tokenValue.equals("\"")) {
                if (inString) {
                    stringLiteral.append("\"");
                    tokens.add(new Token(TokenType.STRING_LITERAL, stringLiteral.toString(), lineNumber));
                    stringLiteral.setLength(0);
                    inString = false;
                } else {
                    stringLiteral.append("\"");
                    inString = true;
                }
                continue;
            }
            if (inString) {
                stringLiteral.append(tokenValue);
                continue;
            }
            if (tokenValue.trim().isEmpty()) continue;
            TokenType tokenType = determineTokenType(tokenValue);
            tokens.add(new Token(tokenType, tokenValue, lineNumber));
        }
    }

    private static TokenType determineTokenType(String token) {
        if (KEYWORDS.contains(token)) {
            return TokenType.KEYWORD;
        } else if (Character.isDigit(token.charAt(0))) {
            return TokenType.NUMBER;
        } else if (isIdentifier(token)) {
            return TokenType.IDENTIFIER;
        } else if (OPERATORS.contains(token)) {
            return TokenType.OPERATOR;
        } else if (SEPARATORS.contains(token)) {
            return TokenType.SEPARATOR;
        } else {
            return TokenType.UNKNOWN;
        }
    }

    private static boolean isIdentifier(String token) {
        if (!Character.isJavaIdentifierStart(token.charAt(0))) {
            return false;
        }
        for (int i = 1; i < token.length(); i++) {
            if (!Character.isJavaIdentifierPart(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
