import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectClass extends LoxClass {
    ObjectClass(String name) {
        super(name, new ArrayList<>(), new HashMap<>());
    }
}
