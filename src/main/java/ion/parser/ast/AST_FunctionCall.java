package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_FunctionCall extends AST_Expression {

    private final String identifier;

    public AST_FunctionCall(String identifier) {
        super(ExpressionType.FUNCTION_CALL);

        this.identifier = identifier;
    }

    // Getter and Setters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " identifer='" + identifier + "'>";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\tidentifier='" + identifier + "'\n>";
    }
}
