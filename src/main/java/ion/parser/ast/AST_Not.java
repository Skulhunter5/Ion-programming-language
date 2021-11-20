package ion.parser.ast;

import ion.parser.AST;
import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Not extends AST_Expression {

    private final AST_Expression expression;

    public AST_Not(AST_Expression expression) {
        super(ExpressionType.NOT);

        this.expression = expression;
    }

    // Getters and Setters
    public AST_Expression getExpression() {return expression;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " expression=" + expression + ">";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\texpression=\n" + AST.indent(expression, 2) + "\n>";
    }

}
