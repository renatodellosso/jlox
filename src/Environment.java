import java.util.*;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();
    private final List<String> uninitializedVars = new ArrayList<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value, boolean initialized) {
        if (initialized)
            values.put(name, value);
        else uninitializedVars.add(name);
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (uninitializedVars.remove(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (uninitializedVars.contains(name.lexeme))
            throw new RuntimeError(name, "Uninitialized variable '" + name.lexeme + "'.");

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
