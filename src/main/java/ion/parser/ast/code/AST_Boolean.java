package ion.parser.ast.code;

import ion.parser.ASTType;
import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Boolean extends AST_Expression {

    private final boolean value;

    public AST_Boolean(boolean value) {
        super(ExpressionType.BOOLEAN);

        this.value = value;
    }

    // Getters and Setters
    public boolean getValue() {return value;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " value='" + value + "'>";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\tvalue='" + value + "'\n>";
    }

}
