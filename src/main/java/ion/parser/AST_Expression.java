package ion.parser;

import ion.parser.AST;
import ion.parser.ASTType;

public abstract class AST_Expression extends AST {

    private ExpressionType expressionType;

    public AST_Expression(ExpressionType expressionType) {
        super(ASTType.EXPRESSION);
        this.expressionType = expressionType;
    }

    // Getters and Setters
    public ExpressionType getExpressionType() {return expressionType;}

    @Override
    public String toString() {
        return super.toString() + "-" + expressionType;
    }
    @Override
    public String readableString() {
        return super.toString() + "-" + expressionType;
    }

}
