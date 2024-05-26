import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SemanticAnalyzer implements ASTVisitor {
    private Map<String, String> variableTypes = new HashMap<>();
    private Map<String, MethodDeclaration> methods = new HashMap<>();

    @Override
    public void visit(Program program) {
        for (ASTNode statement : program.statements) {
            if (statement!=null)
                statement.accept(this);
        }
    }

    @Override
    public void visit(VariableDeclaration variableDeclaration) {
        if (variableTypes.containsKey(variableDeclaration.name)) {
            throw new RuntimeException("Variable " + variableDeclaration.name + " is already declared.");
        }
        variableTypes.put(variableDeclaration.name, variableDeclaration.type);
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if (methods.containsKey(methodDeclaration.name)) {
            throw new RuntimeException("Method " + methodDeclaration.name + " is already declared.");
        }
        methods.put(methodDeclaration.name, methodDeclaration);

        // Create a new scope for method parameters and body
        Map<String, String> oldVariableTypes = new HashMap<>(variableTypes);
        for (VariableDeclaration param : methodDeclaration.parameters) {
            param.accept(this);
        }
        for (ASTNode statement : methodDeclaration.body) {
            statement.accept(this);
        }

        // Restore the old scope
        variableTypes = oldVariableTypes;
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        // For simplicity, we assume class declarations only contain methods
        for (ASTNode member : classDeclaration.members) {
            member.accept(this);
        }
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.condition.accept(this);
        for (ASTNode statement : ifStatement.thenBranch) {
            statement.accept(this);
        }
        for (ASTNode statement : ifStatement.elseBranch) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(Expression expression) {
        // Perform type checking for the expression if needed
    }

    @Override
    public void visit(LoopDeclaration loopDeclaration) {
        loopDeclaration.iterator.accept(this);
        loopDeclaration.condition.accept(this);
        loopDeclaration.expr.accept(this);
        for (ASTNode statement : loopDeclaration.body) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(WhileLoop whileLoop) {
        whileLoop.expr.accept(this);
        for (ASTNode statement : whileLoop.body) {
            statement.accept(this);
        }
    }

    // Add methods for visiting array declarations, etc.
    @Override
    public void visit(Parser.ArrayDeclaration arrayDeclaration) {
        // Perform checks for array declarations
    }
}
