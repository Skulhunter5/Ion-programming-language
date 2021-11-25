package ion.parser.ast.code;

import ion.parser.AST;
import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Assignment_Array extends AST_Assignment {

    private AST_Expression indexExpression;

    public AST_Assignment_Array(String identifier, AST_Expression indexExpression, AST_Expression value) {
        super(ExpressionType.ASSIGNMENT_ARRAY, identifier, value);

        this.indexExpression = indexExpression;
    }

    // Getters and Setters
    public AST_Expression getIndexExpression() {return indexExpression;}

    //Print
    @Override
    public String toString() {
        return super.toString() + " identifier='" + identifier + "' value=" + value + ">";
    }
    @Override
    public String readableString() {
        String res = super.toString();
        res += "\n\tidentifier='" + identifier + "'";
        res += "\n\tindexExpression=" + AST.indent(indexExpression, 2);
        res += "\n\tvalue=\n" + AST.indent(value, 2);
        res += "\n>";
        return res;
    }

}
