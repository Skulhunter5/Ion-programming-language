package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_Assignment extends AST_Expression {

    private String identifier;
    private AST_Expression value;

    public AST_Assignment(String identifier, AST_Expression value) {
        super(ExpressionType.ASSIGNMENT);
        this.identifier = identifier;
        this.value = value;
    }

    // Getters
    public String getReturnType() {return null;} // TODO: implement
    public String getIdentifier() {return identifier;}
    public AST_Expression getValue() {return value;}

    //Print
    @Override
    public String toString() {
        return "<AST-" + type + "' identifier='" + identifier + "' value='" + value + "'>";
    }

}
