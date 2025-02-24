import java.util.List;

public interface LoxCallable {
    /**
     * How many arguments this callable takes.
     */
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
