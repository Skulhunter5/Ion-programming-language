package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Increment extends AST_Expression {

    private String identifier;
    private boolean after;

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

}
