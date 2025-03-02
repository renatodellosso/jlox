import java.util.List;

public abstract class LoxNativeFunction implements LoxCallable {

    String name;
    int arity;

    LoxNativeFunction(String name, int arity) {
        this.name = name;
        this.arity = arity;
    }

    @Override
    public int arity() {
        return arity;
    }

    @Override
    public String toString() {
        return "<native fn clock>";
    }
}
