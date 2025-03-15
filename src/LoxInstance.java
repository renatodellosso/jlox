import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
    private LoxClass loxClass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass loxClass) {
        this.loxClass = loxClass;
    }

    @Override
    public String toString() {
        return "<" + loxClass.name + " instance>";
    }

    Object get(Token name, Interpreter interpreter) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = loxClass.findMethod(name.lexeme);
        if (method != null) {
            if (method.variant == LoxFunction.Variant.GETTER)
                return method.bind(this).call(interpreter, new ArrayList<>());
            return method.bind(this);
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    Object getIfPresent(Token name, Interpreter interpreter) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = loxClass.findMethod(name.lexeme);
        if (method != null) {
            return method.bind(this);
        }

        return null;
    }

    Object set(Token name, Object value) {
        fields.put(name.lexeme, value);
        return value;
    }
}
