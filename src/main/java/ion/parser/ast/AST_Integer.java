package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Integer extends AST_Expression {

    private final long value;

    public AST_Integer(long value) {
        super(ExpressionType.INTEGER);
        this.value = value;
    }

    // Getters
    public long getValue() {return value;}

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
