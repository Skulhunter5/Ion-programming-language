package ion.parser.ast.code;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Increment extends AST_Expression {

    private final String identifier;
    private final boolean after;

    public AST_Increment(String identifier, boolean after) {
        super(ExpressionType.INCREMENT);

        this.identifier = identifier;
        this.after = after;
    }

    // Getters and Setters
    public String getIdentifier() {return identifier;}
    public boolean isBefore() {return !after;}
    public boolean isAfter() {return after;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " identifier='" + identifier + "'>";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\tidentifier='" + identifier + "'\n>";
    }

}
