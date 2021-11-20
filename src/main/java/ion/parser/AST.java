package ion.parser;

public class AST {

    protected ASTType type;

    public AST(ASTType type) {
        this.type = type;
    }

    public ASTType getType() {
        return type;
    }

    // Print
    @Override
    public String toString() {
        return "<AST-" + type;
    }

    // Utility

    protected static String indent(AST ast) {
        String str = ast.toString();
        String[] lines = str.split("\n");
        String res = "";
        for(String line : lines) res += "\t" + line + "\n";
        return res.substring(0, res.length() - 1);
    }

    protected static String indent(AST ast, int n) {
        String str = ast.toString();
        String indent = "";
        for(int i = 0; i < n; i++) indent += "\t";
        String[] lines = str.split("\n");
        String res = "";
        for(String line : lines) res += indent + line + "\n";
        return res.substring(0, res.length() - 1);
    }

}
