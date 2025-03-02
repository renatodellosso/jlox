import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    private class Local {
        boolean initialized = false;
        int index;
        Local(boolean initialized, int index) {
            this.initialized = initialized;
            this.index = index;
        }
    }

    private final Interpreter interpreter;
    /**
     * Value is whether we've finished initializing the variable yet.
     */
    private final Stack<Map<String, Local>> scopes = new Stack<>();

    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    void runResolver(List<Stmt> statements) {
        beginScope();

        for (Object obj : interpreter.globals.values) {
            if (obj instanceof LoxNativeFunction fn) {
                scopes.peek().put(fn.name, new Local(true, scopes.peek().size()));
            } else Lox.error(-1, "Could not load default global: " + obj);
        }

        resolve(statements);

        endScope();
    }

    private void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt statement) {
        statement.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Local local = scopes.get(i).get(name.lexeme);
            if (local != null) {
                if (expr instanceof Expr.Assign assign) {
                    assign.depth = scopes.size() - 1 - i;
                    assign.index = local.index;
                } else if (expr instanceof Expr.Variable var) {
                    var.depth = scopes.size() - 1 - i;
                    var.index = local.index;
                }
            }
        }
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = type;
        currentFunction = type;

        beginScope();

        for (Token param : function.params) {
            declare(param);
            define(param);
        }

        resolve(function.body);

        endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Local>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty())
            return;

        Map<String, Local> scope = scopes.peek();

        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with the name '" + name.lexeme + "' in this scope.");
        }

        scope.put(name.lexeme, new Local(false, scope.size()));
    }

    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().get(name.lexeme).initialized = true;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr arg : expr.arguments)
            resolve(arg);

        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        // No work to do here!
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && !scopes.peek().get(expr.name.lexeme).initialized) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        resolve(stmt.statements);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name); // Define before body, allowing recursion

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null)
            resolve(stmt.elseBranch);

        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        if (stmt.expression != null)
            resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        }

        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }
}
