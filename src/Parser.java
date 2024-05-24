import java.util.*;

class Parser {
    private List<Token> tokens;
    private int currentTokenIndex = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null;
    }

    private void advanceToken() {
        currentTokenIndex++;
    }

    public Program parse() {
        Program program = new Program();
        while (getCurrentToken() != null) {
            program.addStatement(parseStatement());
        }
        return program;
    }

    private ASTNode parseStatement() {

        //TODO: static fields parsing
        //TODO: for loops parsing
        //TODO: volatile, final, ... parsing

        Token token = getCurrentToken();
        if (token.type == TokenType.KEYWORD) {
            switch (token.value) {
                case "public":
                    advanceToken();
                    return parsePublicDeclaration();
                case "int":
                case "boolean":
                case "char":
                case "double":
                case "float":
                case "long":
                case "short":
                case "byte":
                case "void":
                    return parseMethodOrVariableDeclaration();
                case "if":
                    return parseIfStatement();
                case "class":
                    return parseClassDeclaration();
                default:
                    throw new RuntimeException("Unexpected keyword: " + token.value);
            }
        } else if (token.type == TokenType.IDENTIFIER) {
            return parseExpression();
        } else {
            advanceToken();
            return null; // Return null if no matching statement found
        }
    }

    private ASTNode parsePublicDeclaration() {
        Token nextToken = getCurrentToken();
        if (nextToken != null && nextToken.value.equals("class")) {
            return parseClassDeclaration();
        } else if (nextToken != null && nextToken.value.equals("static")) {
            return parseStaticMethodDeclaration();
        } else {
            throw new RuntimeException("Expected 'class' or 'static' but found " + nextToken);
        }
    }

    private MethodDeclaration parseStaticMethodDeclaration() {
        advanceToken();
        return parseMethodDeclaration("static");
    }

    private MethodDeclaration parseMethodDeclaration(String modifier) {
        String returnType = getCurrentToken().value;
        advanceToken();
        String methodName = getCurrentToken().value;
        advanceToken();
        Token token = getCurrentToken();
        if (token.type == TokenType.SEPARATOR && token.value.equals("(")) {
            advanceToken();
            MethodDeclaration method = new MethodDeclaration(modifier, returnType, methodName);
            while (getCurrentToken() != null && !getCurrentToken().value.equals(")")) {
                String paramType = getCurrentToken().value;
                advanceToken();
                try {
                    if (getCurrentToken().value.equals("[")) {
                        advanceToken();
                        if (getCurrentToken().value.equals("]"))
                            paramType += "[]";
                        else if (Integer.valueOf(getCurrentToken().value) instanceof Integer) {
                            paramType+=getCurrentToken().value+"]";
                            advanceToken();
                        }
                    }
                }
                catch (Exception e){
                    throw new RuntimeException("Expected ] or size, but found "+getCurrentToken().value);
                }
                advanceToken();
                String paramName = getCurrentToken().value;
                advanceToken();
                method.addParameter(new VariableDeclaration(paramType, paramName));
                if (getCurrentToken().value.equals(",")) {
                    advanceToken();
                }
            }
            advanceToken();
            token = getCurrentToken();
            if (token.type == TokenType.SEPARATOR && token.value.equals("{")) {
                int vlSep = 0;
                advanceToken();
                while (getCurrentToken() != null && !getCurrentToken().value.equals("}") && vlSep==0) {
                    method.addBodyStatement(parseStatement());
                }
                advanceToken();
                return method;
            } else {
                throw new RuntimeException("Expected '{' but found " + token);
            }
        } else {
            throw new RuntimeException("Expected '(' but found " + token);
        }
    }

    private ASTNode parseMethodOrVariableDeclaration() {
        StringBuilder typeBuilder = new StringBuilder();
        while (getCurrentToken().type == TokenType.KEYWORD) {
            typeBuilder.append(getCurrentToken().value).append(" ");
            advanceToken();
        }
        String type = typeBuilder.toString().trim();

        Token token = getCurrentToken();
        if (token.type == TokenType.IDENTIFIER) {
            String name = token.value;
            advanceToken();

            if (getCurrentToken() != null && getCurrentToken().type == TokenType.SEPARATOR && getCurrentToken().value.equals("[")) {

                advanceToken();
                if (getCurrentToken() != null && getCurrentToken().type == TokenType.SEPARATOR && getCurrentToken().value.equals("]")) {
                    advanceToken();
                    token = getCurrentToken();
                    if (token != null && (token.type == TokenType.IDENTIFIER || token.value.equals("("))) {
                        if (token.value.equals("(")) {
                            advanceToken();
                            if (getCurrentToken() != null && getCurrentToken().value.equals(")")) {
                                advanceToken();
                                return new MethodDeclaration(type, name);
                            }
                        }
                        return new MethodDeclaration(type, name, token.value);
                    } else {
                        throw new RuntimeException("ќжидалось им€ переменной или начало списка аргументов метода после ']'");
                    }
                } else {
                    throw new RuntimeException("ќжидалось ']', но найдено " + getCurrentToken());
                }
            } else {
                return parseVariableDeclaration(type, name);
            }
        } else {
            throw new RuntimeException("ќжидалс€ идентификатор, но найдено " + token);
        }
    }

    private VariableDeclaration parseVariableDeclaration(String type, String name) {
        Token token = getCurrentToken();
        if (token.type == TokenType.SEPARATOR && token.value.equals(";")) {
            advanceToken();
            return new VariableDeclaration(type, name);
        }
        else {
            if (token.type == TokenType.OPERATOR && token.value.equals("=")) {
                advanceToken();
                String value = "";
                while (!(token = getCurrentToken()).value.equals(";")){
                    value+=token.value;
                    advanceToken();
                }
                return new VariableDeclaration(type, name, value);
            } else {
                throw new RuntimeException("Expected ';' but found " + token);
            }
        }
    }

    private IfStatement parseIfStatement() {
        advanceToken();
        if (getCurrentToken().type == TokenType.SEPARATOR && getCurrentToken().value.equals("(")) {
            advanceToken();
            ASTNode condition = parseExpression();
            if (getCurrentToken().type == TokenType.SEPARATOR && getCurrentToken().value.equals(")")) {
                advanceToken();
                ASTNode thenBranch = parseStatement();
                ASTNode elseBranch = null;
                //TODO: Fix elseBranch
                if (getCurrentToken() != null && getCurrentToken().type == TokenType.KEYWORD && getCurrentToken().value.equals("else")) {
                    advanceToken();
                    elseBranch = parseStatement();
                }
                return new IfStatement(condition, thenBranch, elseBranch);
            } else {
                throw new RuntimeException("Expected ')' but found " + getCurrentToken());
            }
        } else {
            throw new RuntimeException("Expected '(' but found " + getCurrentToken());
        }
    }

    private ClassDeclaration parseClassDeclaration() {
        advanceToken();
        Token token = getCurrentToken();
        if (token.type == TokenType.IDENTIFIER) {
            String className = token.value;
            advanceToken();

            ClassDeclaration classDecl = new ClassDeclaration(className);

            token = getCurrentToken();
            if (token.type == TokenType.SEPARATOR && token.value.equals("{")) {
                advanceToken();
                while (getCurrentToken() != null && !getCurrentToken().value.equals("}")) {
                    ASTNode member = parseStatement();
                    if (member != null) {
                        classDecl.addMember(member);
                    }
                }
                advanceToken();
                return classDecl;
            } else {
                throw new RuntimeException("Expected '{' but found " + token);
            }
        } else {
            throw new RuntimeException("Expected class name but found " + token);
        }
    }

    private Expression parseExpression() {
        StringBuilder expr = new StringBuilder();
        while (getCurrentToken() != null && getCurrentToken().type != TokenType.SEPARATOR) {
            expr.append(getCurrentToken().value).append(" ");
            advanceToken();
        }
        return new Expression(expr.toString().trim());
    }

    class ArrayDeclaration extends ASTNode {
        private String type;
        private String name;
        private String arrayName;
        private List<Expression> values;

        List<Expression> getValues() {
            return values;
        }

        String getName() {
            return name;
        }

        String getType() {
            return type;
        }

        @Override
        void accept(ASTVisitor visitor) {
            visitor.visit(this);
        }
    }

}

