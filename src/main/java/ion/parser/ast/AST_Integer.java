package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Integer extends AST_Expression {

    private int value;

    public AST_Integer(int value) {
        super(ASTType.INTEGER);
        this.value = value;
    }

    // Getters
    public int getValue() {return value;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " value='" + value + "'>";
    }

}
