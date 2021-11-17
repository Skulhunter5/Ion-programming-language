package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Decrement extends AST_Expression {

    private String identifier;
    private boolean after;

    public AST_Decrement(String identifier, boolean after) {
        super(ASTType.DECREMENT);

        this.identifier = identifier;
        this.after = after;
    }

    // Getters and Setters
    public String getIdentifier() {return identifier;}
    public boolean isAfter() {return after;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " identifier='" + identifier + "'>";
    }

}
