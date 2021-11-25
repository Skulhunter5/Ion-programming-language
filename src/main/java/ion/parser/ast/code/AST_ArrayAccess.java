package ion.parser.ast.code;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_ArrayAccess extends AST_Expression {

    private final String identifier;
    private final AST_Expression indexExpression;

    public AST_ArrayAccess(String identifier, AST_Expression indexExpression) {
        super(ExpressionType.ARRAY_ACCESS);

        this.identifier = identifier;
        this.indexExpression = indexExpression;
    }

    // Getters
    public String getIdentifier() {return identifier;}
    public AST_Expression getIndexExpression() {return indexExpression;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " identifier='" + identifier + " indexExpression='" + indexExpression + "'>";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\tidentifier='" + identifier + "\n\tindexExpression='" + indexExpression + "'\n>";
    }

}
