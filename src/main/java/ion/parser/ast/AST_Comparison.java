package ion.parser.ast;

import ion.lexer.TokenType;
import ion.parser.AST;
import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Comparison extends AST_Expression {

    private final AST_Expression a, b;
    private final TokenType compareType;

    public AST_Comparison(AST_Expression a, AST_Expression b, TokenType compareType) {
        super(ExpressionType.COMPARISON);

        this.a = a;
        this.b = b;
        this.compareType = compareType;
    }

    // Getters and Setters
    public AST_Expression getA() {return a;}
    public AST_Expression getB() {return b;}
    public TokenType getCompareType() {return compareType;}

    // Print
    @Override
    public String toString() {
        return super.toString() + "\n\ta=\n" + AST.indent(a, 2) + "\n\tb=\n" + AST.indent(b, 2) + "\n\tcompareType='" + compareType + "'\n>";
    }

}
