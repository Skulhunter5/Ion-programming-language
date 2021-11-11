package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Print extends AST_Expression {

    private String identifier;

    public AST_Print(String identifier) {
        super(ASTType.PRINT);
        this.identifier = identifier;
    }

    // Getters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " identifier='" + identifier + "'>";
    }

}
