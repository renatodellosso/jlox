import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign : Token name, Expr value",
                "Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal : Object value",
                "Logical : Expr left, Token operator, Expr right",
                "Unary : Token operator, Expr right",
                "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block : List<Stmt> statements",
                "Expression : Expr expression",
                "If : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print : Expr expression",
                "Var : Token name, Expr initializer",
                "While : Expr condition, Stmt body",
                "Break :",
                "Continue :"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String[] split = type.split(":");

            String className = split[0].trim();
            String fields = split.length > 1 ? split[1].trim() : "";
            defineType(writer, baseName, className, fields);
        }

        // Base accept() method
        writer.println("\tabstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");

        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("\tstatic class " + className + " extends " + baseName + " {");

        String[] fields = fieldList.split(", ");

        // Fields
        for (String field : fields) {
            if (!field.isEmpty())
                writer.println("\t\tfinal " + field + ";");
        }

        // Constructor
        writer.println("\t\t" + className + "(" + fieldList + ") {");
        for (String field : fields) {
            if (field.isEmpty())
                continue;
            String name = field.split(" ")[1];
            writer.println("\t\t\tthis." + name + " = " + name + ";");
        }
        writer.println("\t\t}");

        // Visitor pattern
        writer.println();
        writer.println("\t\t@Override");
        writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
        writer.println("\t\t}");

        writer.println("\t}");
        writer.println();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("\t\tR visit" + typeName + baseName + "(" + typeName + " "
                + baseName.toLowerCase() + ");");
        }

        writer.println("\t}");
        writer.println();
    }
}
