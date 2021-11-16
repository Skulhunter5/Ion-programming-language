package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Increment extends AST_Expression {

    private String identifier;

    public AST_Increment(String identifier) {
        super(ASTType.INCREMENT);

        this.identifier = identifier;
    }

    // Getters and Setters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " identifier='" + identifier + "'>";
    }

}
