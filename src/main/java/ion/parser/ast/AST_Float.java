package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Float extends AST_Expression {

    private float value;

    public AST_Float(float value) {
        super(ASTType.FLOAT);
        this.value = value;
    }

    // Getters
    public float getValue() {return value;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " value='" + value + "'>";
    }

}
