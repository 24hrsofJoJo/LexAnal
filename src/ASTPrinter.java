import java.util.List;

public class ASTPrinter implements ASTVisitor {
    @Override
    public void visit(Program program) {
        for (ASTNode statement : program.statements) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        if (variableDeclaration.value == null)
            System.out.println("VariableDeclaration: type=" + variableDeclaration.type + ", name=" + variableDeclaration.name);
        else
            System.out.println("VariableDeclaration: type=" + variableDeclaration.type + ", name=" + variableDeclaration.name + ", value="+variableDeclaration.value);

    }

    @Override
    public void visit(LoopDeclaration loopDeclaration){

    }


    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        System.out.println("MethodDeclaration: returnType=" + methodDeclaration.returnType + ", name=" + methodDeclaration.name);
        System.out.println("NOW METHODS");
        for (VariableDeclaration param : methodDeclaration.parameters) {
            param.accept(this);
        }
        System.out.println("METHODS ENDED");
        System.out.println("BODY");
        for (ASTNode statement : methodDeclaration.body) {
            if (statement!=null)
                statement.accept(this);
            else
                break;
        }
        System.out.println("BODY ENDED");
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        System.out.println("ClassDeclaration: name=" + classDeclaration.name);
        for (ASTNode member : classDeclaration.members) {
            member.accept(this);
        }
    }

    @Override
    public void visit(IfStatement ifStatement) {
        System.out.println("IfStatement: ");
        System.out.print("Condition: ");
        ifStatement.condition.accept(this);
        System.out.print("Then: ");
        ifStatement.thenBranch.accept(this);
        if (ifStatement.elseBranch != null) {
            System.out.print("Else: ");
            ifStatement.elseBranch.accept(this);
        }
    }

    @Override
    public void visit(Expression expression) {
        System.out.println("Expression: " + expression.expression);
    }

    @Override
    public void visit(Parser.ArrayDeclaration arrayDeclaration) {
        System.out.println("Array declaration:");
        System.out.println("Type: " + arrayDeclaration.getType());
        System.out.println("Name: " + arrayDeclaration.getName());
        List<Expression> values = arrayDeclaration.getValues();
        if (!values.isEmpty()) {
            System.out.println("Values:");
            for (int i = 0; i < values.size(); i++) {
                System.out.println("Value " + (i + 1) + ": " + values.get(i).expression);
            }
        } else {
            System.out.println("No values specified for the array.");
        }
    }
}
