package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Boolean extends AST_Expression {

    private boolean value;

    public AST_Boolean(boolean value) {
        super(ASTType.BOOLEAN);

        this.value = value;
    }

    // Getters and Setters
    public boolean getValue() {return value;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " value='" + value + "'>";
    }

}
