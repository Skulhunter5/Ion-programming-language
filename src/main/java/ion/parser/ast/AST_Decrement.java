package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Decrement extends AST_Expression {

    private final String identifier;
    private final boolean after;

    public AST_Decrement(String identifier, boolean after) {
        super(ExpressionType.DECREMENT);

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

}
