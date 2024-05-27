//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.EOFException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Parser {
    private List<Token> tokens;
    private int currentTokenIndex = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token getCurrentToken() {
        return this.currentTokenIndex < this.tokens.size() ? (Token)this.tokens.get(this.currentTokenIndex) : null;
    }

    private void advanceToken() {
        ++this.currentTokenIndex;
    }
    private void backToken(){
        --this.currentTokenIndex;
    }

    public Program parse() {
        Program program = new Program();

        while(this.getCurrentToken() != null) {
            program.addStatement(this.parseStatement());
        }

        return program;
    }

    private ASTNode parseStatement() {
        Token token = this.getCurrentToken();
        if (token.type == TokenType.KEYWORD) {
            switch (token.value) {
                case "public":
                case "protected":
                case "private":
                    this.advanceToken();
                    return this.parsePublicDeclaration();
                case "int":
                case "boolean":
                case "char":
                case "double":
                case "float":
                case "long":
                case "short":
                case "byte":
                case "void":
                    return this.parseMethodOrVariableDeclaration();
                case "for":
                    return this.parseForLoopDeclaration();
                case "if":
                    return this.parseIfStatement();
                case "else":
                    throw new RuntimeException("Using else without if statement");
                case "while":
                    return this.parseWhileLoopDeclaration();
                case "class":
                    return this.parseClassDeclaration();
                case ".":
                    return parseDotDeclaration();
                default:
                    throw new RuntimeException("Unexpected keyword: " + token.value);
            }
        } else if (token.type == TokenType.IDENTIFIER) {
            return this.parseExpression();
        } else {
            this.advanceToken();
            return null;
        }
    }

    private ASTNode parseDotDeclaration(){
        Token token = getCurrentToken();
        String library = token.value;
        advanceToken();
        ArrayList<String> arr = new ArrayList<>();
        arr.add(library);
        advanceToken();
        token = getCurrentToken();
        while (!Objects.equals(token.value, ";")&&!token.value.equals("=")&&!token.value.equals("(")){

            if (!token.value.equals(".")&&token.type!=TokenType.IDENTIFIER){
                Token err = token;
                backToken();
                token = getCurrentToken();
                if (token.value.equals("."))
                    throw new RuntimeException("Expected field or method but found "+err);
                else
                    throw new RuntimeException("Expected . but found "+err);

            }
            if (token.type == TokenType.IDENTIFIER)
                arr.add(token.value);
            advanceToken();
            token = getCurrentToken();


        }
        if (token.value.equals("(")){
            ArrayList<String> body = new ArrayList<>();
            advanceToken();
            token = getCurrentToken();
            while(!token.value.equals(")")){

                if (token.type!=TokenType.IDENTIFIER&&!token.value.equals(",")){
                    throw new RuntimeException("Expected ) but found "+token);
                }
                if (token.type==TokenType.IDENTIFIER){
                    body.add(token.value);
                }
                advanceToken();
                token = getCurrentToken();
            }
            advanceToken();
            token = getCurrentToken();
            if (token.value.equals(";"))
                return new MethodOrFieldCall(true,arr,body);
            else throw new RuntimeException("Expected ; but found "+token);
        }
        return new MethodOrFieldCall(false,arr,null);
    }
    private ASTNode parsePublicDeclaration() {
        Token nextToken = this.getCurrentToken();
        if (nextToken != null && nextToken.value.equals("class"))
            return this.parseClassDeclaration();

        else
            if (nextToken != null && nextToken.value.equals("static")) {
                try {
                    return this.parseStaticMethodDeclaration();
                }
                catch (EOFException e){
                    String[] temp = e.getMessage().split("//");
                    return this.parseVariableDeclaration(temp[1],temp[0]);
                }
            }
            else
                if(nextToken != null && nextToken.type == TokenType.KEYWORD) {
                    return this.parseMethodOrVariableDeclaration();
                }
                else {
                    throw new RuntimeException("Expected 'class' or 'static' but found " + String.valueOf(nextToken));
                }
    }

    private MethodDeclaration parseStaticMethodDeclaration() throws EOFException{
        this.advanceToken();
        try {
            return this.parseMethodDeclaration("static");
        }
        catch (EOFException e){
            throw e;
        }
    }

    private MethodDeclaration parseMethodDeclaration(String modifier) throws EOFException{
        String returnType = this.getCurrentToken().value;
        this.advanceToken();
        String methodName = this.getCurrentToken().value;
        this.advanceToken();
        Token token = this.getCurrentToken();
        if (token.type == TokenType.SEPARATOR && token.value.equals("(")) {
            this.advanceToken();
            MethodDeclaration method = new MethodDeclaration(modifier, returnType, methodName);

            while(this.getCurrentToken() != null && !this.getCurrentToken().value.equals(")")) {
                String paramType = this.getCurrentToken().value;
                this.advanceToken();

                try {
                    if (this.getCurrentToken().value.equals("[")) {
                        this.advanceToken();
                        if (this.getCurrentToken().value.equals("]")) {
                            paramType = paramType + "[]";
                        } else if (Integer.valueOf(this.getCurrentToken().value) instanceof Integer) {
                            paramType = paramType + this.getCurrentToken().value + "]";
                            this.advanceToken();
                        }
                        this.advanceToken();
                    }
                } catch (Exception var8) {
                    throw new RuntimeException("Expected ] or size, but found " + this.getCurrentToken().value);
                }

                String paramName = this.getCurrentToken().value;
                this.advanceToken();
                method.addParameter(new VariableDeclaration(paramType, paramName));
                if (this.getCurrentToken().value.equals(",")) {
                    this.advanceToken();
                }
            }

            this.advanceToken();
            token = this.getCurrentToken();
            if (token.type == TokenType.SEPARATOR && token.value.equals("{")) {
//                System.out.println("Opening brace found for method body");
                this.advanceToken();
                int openingBraces = 1;

                while(this.getCurrentToken() != null) {
                    token = this.getCurrentToken();
                    if (token.type == TokenType.SEPARATOR) {
                        if (token.value.equals("{")) {
//                            System.out.println("Opening brace found");
                            ++openingBraces;
                        } else if (token.value.equals("}")) {
//                            System.out.println("Closing brace found");
                            --openingBraces;
                            if (openingBraces == 0) {
                                this.advanceToken();
                                break;
                            }
                        }
                    }


                    if (token.value.equals("return") && !method.returnType.equals("void")) {
                        this.advanceToken();
                        token = this.getCurrentToken();
                        method.setReturnVariable(token.value);
                        this.advanceToken();
                    }
                    else {
                        if (token.value.equals("return")) {
//                        method.addBodyStatement();
                        }
                        else {
                            token = getCurrentToken();
                            advanceToken();
                            Token nextToken = getCurrentToken();
                            this.backToken();
                            if (token.value.equals(".")||nextToken.value.equals("."))
                                method.addBodyStatement(parseDotDeclaration());
                            else{
                                ASTNode statement = this.parseStatement();
                                if (statement != null) {
                                    method.addBodyStatement(statement);
                                }
                            }
                        }
                    }
                }

                return method;
            } else {
                if (token.value.equals(";")){
                    throw new EOFException(methodName+"//"+returnType);
                }
                throw new RuntimeException("Expected '{' but found " + String.valueOf(token));
            }
        } else {
            if (token.value.equals(";")){
                throw new EOFException(methodName+"//"+returnType);
            }
            throw new RuntimeException("Expected '(' but found " + String.valueOf(token));
        }
    }

    private LoopDeclaration parseForLoopDeclaration(){
        this.advanceToken();
        Token token = this.getCurrentToken();
        if(token.type==TokenType.SEPARATOR && token.value.equals("(")){
            this.advanceToken();
            token = this.getCurrentToken();
            if (token.type==TokenType.KEYWORD){
                String type = token.value;
                this.advanceToken();
                token = this.getCurrentToken();
                this.advanceToken();
                VariableDeclaration iterator = parseVariableDeclaration(type,token.value);
                if (!this.getCurrentToken().value.equals(";"))
                    throw new RuntimeException("Expected ; but found "+this.getCurrentToken().toString());
                this.advanceToken();
                Expression condition = parseExpression();
                if (!this.getCurrentToken().value.equals(";"))
                    throw new RuntimeException("Expected ; but found "+this.getCurrentToken().toString());
                this.advanceToken();
                Expression expr = parseExpression();
                this.advanceToken();
                ArrayList<ASTNode> body = new ArrayList<>();
                if (this.getCurrentToken().value.equals("{")) {
                    body.add(this.parseBlockStatement());
                    if(!(this.getCurrentToken().type==TokenType.SEPARATOR&this.getCurrentToken().value.equals("}"))){
                        throw new RuntimeException("Expected } but found "+this.getCurrentToken().toString());
                    }
                }
                else{
                    body.add(this.parseExpression());
                }
                this.advanceToken();
                return new LoopDeclaration(iterator,condition,expr,body);

            }
            else{
                return null;
            }

        }
        else {
            throw new RuntimeException("Expected '(' but found " + String.valueOf(token));
        }
    }

    private ASTNode parseWhileLoopDeclaration(){
        this.advanceToken();
        Token token = this.getCurrentToken();
        if(token.type==TokenType.SEPARATOR && token.value.equals("(")) {
            this.advanceToken();
            Expression expr = this.parseExpression();
            token = this.getCurrentToken();
            if (token.value.equals(")")) {
                this.advanceToken();
                if (this.getCurrentToken().value.equals("{")) {
                    ArrayList<ASTNode> body = new ArrayList<>();
                    body.add(this.parseBlockStatement());
                    return new WhileLoop(expr, body);
                }
                else{
                    ArrayList<ASTNode> body = new ArrayList<>();
                    body.add(this.parseExpression());
                    return new WhileLoop(expr,body);
                }
            }
            else
                throw new RuntimeException("Expected ), but found "+token.toString());
        }
        else{
            throw new RuntimeException("Expected ( but found "+token.toString());
        }
    }
    private ASTNode parseMethodOrVariableDeclaration() {
        StringBuilder typeBuilder = new StringBuilder();

        while(this.getCurrentToken().type == TokenType.KEYWORD) {
            typeBuilder.append(this.getCurrentToken().value).append(" ");
            this.advanceToken();
        }

        String type = typeBuilder.toString().trim();
        Token token = this.getCurrentToken();
        if (token.type != TokenType.IDENTIFIER) {
            throw new RuntimeException("ќжидалс€ идентификатор, но найдено " + String.valueOf(token));
        } else {
            String name = token.value;
            this.advanceToken();
            if (this.getCurrentToken() != null && this.getCurrentToken().type == TokenType.SEPARATOR && this.getCurrentToken().value.equals("[")) {
                this.advanceToken();
                if (this.getCurrentToken() != null && this.getCurrentToken().type == TokenType.SEPARATOR && this.getCurrentToken().value.equals("]")) {
                    this.advanceToken();
                    token = this.getCurrentToken();
                    if (token != null && (token.type == TokenType.IDENTIFIER || token.value.equals("("))) {
                        if (token.value.equals("(")) {
                            this.advanceToken();
                            if (this.getCurrentToken() != null && this.getCurrentToken().value.equals(")")) {
                                this.advanceToken();
                                return new MethodDeclaration(type, name);
                            }
                        }

                        return new MethodDeclaration(type, name, token.value);
                    } else {
                        throw new RuntimeException("ќжидалось им€ переменной или начало списка аргументов метода после ']'");
                    }
                } else {
                    throw new RuntimeException("ќжидалось ']', но найдено " + String.valueOf(this.getCurrentToken()));
                }
            } else {
                return this.parseVariableDeclaration(type, name);
            }
        }
    }

    private VariableDeclaration parseVariableDeclaration(String type, String name) {
        Token token = this.getCurrentToken();
        if (token.type == TokenType.SEPARATOR && token.value.equals(";")) {
            this.advanceToken();
            return new VariableDeclaration(type, name);
        } else if (token.type == TokenType.OPERATOR && token.value.equals("=")) {
            this.advanceToken();
            String value = "";

            while(!(token = this.getCurrentToken()).value.equals(";")) {
                value = value + token.value;
                this.advanceToken();
            }

            return new VariableDeclaration(type, name, value);
        } else {
            throw new RuntimeException("Expected ';' but found " + String.valueOf(token));
        }
    }

    private IfStatement parseIfStatement() {
         this.advanceToken();

        if (this.getCurrentToken().type == TokenType.SEPARATOR && this.getCurrentToken().value.equals("(")) {
            this.advanceToken();
            ASTNode condition = this.parseExpression();
            ArrayList<ASTNode> thenBranch = new ArrayList<>();
            if (this.getCurrentToken().type == TokenType.SEPARATOR && this.getCurrentToken().value.equals(")")) {
                this.advanceToken();
                if (this.getCurrentToken().value.equals("{")) {
                    while (!this.getCurrentToken().value.equals("else")) {
                        this.advanceToken();
                        if (this.getCurrentToken().value.equals("}")) {
                            this.advanceToken();
                            break;
                        }
                        thenBranch.add(this.parseStatement());
                    }
                    //thenBranch.add(this.parseBlockStatement());
                }
                ArrayList<ASTNode> elseBranch = null;
//                this.advanceToken();
                if (this.getCurrentToken() != null && this.getCurrentToken().type == TokenType.KEYWORD && this.getCurrentToken().value.equals("else")) {
                    this.advanceToken();
                    if (this.getCurrentToken().type == TokenType.SEPARATOR && this.getCurrentToken().value.equals("{")) {
                        elseBranch = new ArrayList<>();
                        elseBranch.add(this.parseBlockStatement());
                    } else {
                        elseBranch = new ArrayList<>();
                        elseBranch.add(this.parseStatement());
                    }
                }

                return new IfStatement(condition, thenBranch, elseBranch);
            } else {
                throw new RuntimeException("Expected ')' but found " + String.valueOf(this.getCurrentToken()));
            }
        } else {
            throw new RuntimeException("Expected '(' but found " + String.valueOf(this.getCurrentToken()));
        }
    }

    private ASTNode parseBlockStatement() {
        int openingBraces = 0;
        Program program = new Program();

        while(this.getCurrentToken() != null) {
            Token token = this.getCurrentToken();
            if (token.type == TokenType.SEPARATOR) {
                if (token.value.equals("{")) {
                    ++openingBraces;
                } else if (token.value.equals("}")) {
                    --openingBraces;
                    if (openingBraces == 0) {
                        break;
                    }
                }
            }

            ASTNode statement = this.parseStatement();
            if (statement!=null)
                program.addStatement(statement);
        }

        return program;
    }

    private ClassDeclaration parseClassDeclaration() {
        this.advanceToken();
        Token token = this.getCurrentToken();
        if (token.type != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected class name but found " + String.valueOf(token));
        } else {
            String className = token.value;
            this.advanceToken();
            ClassDeclaration classDecl = new ClassDeclaration(className);
            int openingBraces = 0;
            token = this.getCurrentToken();
            if (token.type == TokenType.SEPARATOR && token.value.equals("{")) {
//                System.out.println("Opening brace found");
                ++openingBraces;
                this.advanceToken();

                while(this.getCurrentToken() != null) {
                    token = this.getCurrentToken();
                    if (token.type == TokenType.SEPARATOR) {
                        if (token.value.equals("{")) {
//                            System.out.println("Opening brace found");
                            ++openingBraces;
                        } else if (token.value.equals("}")) {
//                            System.out.println("Closing brace found");
                            --openingBraces;
                            if (openingBraces == 0) {
                                this.advanceToken();
                                break;
                            }
                        }
                    }

                    ASTNode member = this.parseStatement();
                    if (member != null) {
//                        System.out.println("Adding member: " + String.valueOf(member));
                        classDecl.addMember(member);
                    }
                }

                return classDecl;
            } else {
                throw new RuntimeException("Expected '{' but found " + String.valueOf(token));
            }
        }
    }

    private Expression parseExpression() {
        StringBuilder expr = new StringBuilder();

        while(this.getCurrentToken() != null && this.getCurrentToken().type != TokenType.SEPARATOR) {

            expr.append(this.getCurrentToken().value).append(" ");
            this.advanceToken();

        }

        return new Expression(expr.toString().trim());
    }
    class ArrayDeclaration extends ASTNode {
        private String type;
        private String name;
        private String arrayName;
        private List<Expression> values;

        ArrayDeclaration(Parser this$0) {
        }

        List<Expression> getValues() {
            return this.values;
        }

        String getName() {
            return this.name;
        }

        String getType() {
            return this.type;
        }

        void accept(ASTVisitor visitor) {
            visitor.visit(this);
        }
    }

}
