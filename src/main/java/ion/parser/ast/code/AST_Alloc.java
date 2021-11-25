package ion.parser.ast.code;

import ion.parser.*;

public class AST_Alloc extends AST_Expression { // TODO: make print a statement instead of an expression

    private final AST_Expression expression;

    public AST_Alloc(AST_Expression expression) {
        super(ExpressionType.ALLOC);

        this.expression = expression;
    }

    // Getters
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
