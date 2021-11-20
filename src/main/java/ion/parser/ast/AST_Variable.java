package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Variable extends AST_Expression {

    private String identifier;

    public AST_Variable(String identifier) {
        super(ExpressionType.VARIABLE_ACCESS);

        this.identifier = identifier;
    }

    // Getters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " identifier='" + identifier + "'>";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\tidentifier='" + identifier + "'\n>";
    }

}
