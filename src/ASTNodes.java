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

class MethodOrFieldCall extends ASTNode{
    boolean isMethod;
    ArrayList<String> call;
    ArrayList<String> args;

    @Override
    void accept(ASTVisitor visitor){
        visitor.visit(this);
    }

    public MethodOrFieldCall(boolean isMethod, ArrayList<String> call, ArrayList<String> args) {
        this.isMethod = isMethod;
        this.call = call;
        if (isMethod)
            this.args = args;
        else
            this.args = null;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return "Type: "+type+", name: "+name+", value: "+value;
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
    ASTNode returnVariable = null;

    MethodDeclaration(String modifier, String returnType, String name) {
        this.modifier = modifier;
        this.returnType = returnType;
        this.name = name;
        this.parameters = new ArrayList<>();
        this.body = new ArrayList<>();
    }

    public void setParameters(List<VariableDeclaration> parameters) {
        this.parameters = parameters;
    }

    public void setBody(List<ASTNode> body) {
        this.body = body;
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

    public void setReturnVariable(String name) {
        for (ASTNode i: body){
            if (((VariableDeclaration)i).getName().equals(name)){
                this.returnVariable = i;
            }
        }
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

class WhileLoop extends ASTNode {
    Expression expr;
    ArrayList<ASTNode> body = null;

    @Override
    void accept(ASTVisitor visitor){
        visitor.visit(this);
    }

    public WhileLoop(Expression expr, ArrayList<ASTNode> body) {
        this.expr = expr;
        this.body = body;
    }
}

class IfStatement extends ASTNode {
    ASTNode condition;
    ArrayList<ASTNode> thenBranch = new ArrayList<>();
    ArrayList<ASTNode> elseBranch = new ArrayList<>();

    IfStatement(ASTNode condition, ArrayList<ASTNode> thenBranch, ArrayList<ASTNode> elseBranch) {
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
    int startValue;
    VariableDeclaration iterator;
    Expression condition;
    Expression expr;
    ArrayList<ASTNode> body;
    @Override
    void accept(ASTVisitor visitor){
        visitor.visit(this);
    }

    public LoopDeclaration(VariableDeclaration iterator, Expression condition, Expression expr, ArrayList<ASTNode> body) {
        this.iterator = iterator;
        this.condition = condition;
        this.expr = expr;
        this.body = body;
    }

    public VariableDeclaration getIterator() {
        return iterator;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public ArrayList<ASTNode> getBody() {
        return body;
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
    void visit(WhileLoop whileLoop);
    void visit(MethodOrFieldCall methodOrFieldCall);
}