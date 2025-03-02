import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {
    final Environment enclosing;
    final List<Object> values = new ArrayList<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(Object value) {
        values.add(value);
    }

    void assign(Token name, int index, Object value) {
        values.set(index, value);
    }

    void assignAt(int distance, int index, Object value) {
        ancestor(distance).values.set(index, value);
    }

    Object get(int index) {
        return values.get(index);
    }

    Object getAt(int distance, int index) {
        return ancestor(distance).values.get(index);
    }

    Environment ancestor(int distance) {
        Environment environment = this;

        for (int i = 0; i < distance; i++)
            environment = environment.enclosing;

        return environment;
    }
}
