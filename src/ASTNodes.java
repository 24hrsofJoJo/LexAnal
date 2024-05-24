import java.util.*;

abstract class ASTNode {
    abstract void accept(ASTVisitor visitor);
}

class Program extends ASTNode {
    List<ASTNode> statements;

    Program() {
        this.statements = new ArrayList<>();
    }

    void addStatement(ASTNode statement) {
        statements.add(statement);
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

class VariableDeclaration extends ASTNode {
    String type;
    String name;
    String value;

    VariableDeclaration(String type, String name) {
        this.type = type;
        this.name = name;
        value = null;
    }
    VariableDeclaration(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

//class MethodDeclaration extends ASTNode {
//    String returnType;
//    String name;
//    List<VariableDeclaration> parameters;
//    List<ASTNode> body;
//
//    MethodDeclaration(String returnType, String name) {
//        this.returnType = returnType;
//        this.name = name;
//        this.parameters = new ArrayList<>();
//        this.body = new ArrayList<>();
//    }
//
//    void addParameter(VariableDeclaration param) {
//        parameters.add(param);
//    }
//
//    void addBodyStatement(ASTNode statement) {
//        body.add(statement);
//    }
//
//    @Override
//    void accept(ASTVisitor visitor) {
//        visitor.visit(this);
//    }
//}

class MethodDeclaration extends ASTNode {
    String modifier;
    String returnType;
    String name;
    List<VariableDeclaration> parameters;
    List<ASTNode> body;

    MethodDeclaration(String modifier, String returnType, String name) {
        this.modifier = modifier;
        this.returnType = returnType;
        this.name = name;
        this.parameters = new ArrayList<>();
        this.body = new ArrayList<>();
    }

    MethodDeclaration(String returnType, String methodName) {
        this.returnType = returnType;
        this.name = methodName;
    }

    void addParameter(VariableDeclaration param) {
        parameters.add(param);
    }

    void addBodyStatement(ASTNode statement) {
        body.add(statement);
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}


class ClassDeclaration extends ASTNode {
    String name;
    List<ASTNode> members;

    ClassDeclaration(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    void addMember(ASTNode member) {
        members.add(member);
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

class IfStatement extends ASTNode {
    ASTNode condition;
    ASTNode thenBranch;
    ASTNode elseBranch;

    IfStatement(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

class LoopDeclaration extends ASTNode {
    ASTNode startValue;
    ASTNode condition;
    ASTNode expr;
    ASTNode body;
    @Override
    void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}

class Expression extends ASTNode {
    String expression;

    Expression(String expression) {
        this.expression = expression;
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

interface ASTVisitor {
    void visit(Program program);
    void visit(VariableDeclaration variableDeclaration);
    void visit(MethodDeclaration methodDeclaration);
    void visit(ClassDeclaration classDeclaration);
    void visit(IfStatement ifStatement);
    void visit(Expression expression);
    void visit(Parser.ArrayDeclaration arrayDeclaration);
    void visit(LoopDeclaration loopDeclaration);
}