import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
    final String name;
    final List<LoxClass> superclasses;
    final Map<String, LoxFunction> methods;

    LoxClass(String name, List<LoxClass> superclasses, Map<String, LoxFunction> methods) {
        this.name = name;
        this.superclasses = superclasses;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null)
            return 0;
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);

        LoxFunction initializer = findMethod("init");
        if (initializer != null)
            initializer.bind(instance).call(interpreter, arguments);

        return instance;
    }

    LoxFunction findMethod(String name) {
        if (methods.containsKey(name))
            return methods.get(name);

        for (LoxClass superclass : superclasses) {
            LoxFunction method = superclass.findMethod(name);
            if (method != null)
                return method;
        }
        return null;
    }
}
