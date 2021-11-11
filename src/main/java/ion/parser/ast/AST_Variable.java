package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Variable extends AST_Expression {

    private String identifier;

    public AST_Variable(String identifier) {
        super(ASTType.VARIABLE);
        this.identifier = identifier;
    }

    // Getters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + "' identifier='" + identifier + "'>";
    }

}
