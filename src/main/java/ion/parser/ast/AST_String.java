package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

public class AST_String extends AST_Expression {

    private static int nextId = 0;

    private int id;
    private String value;

    public AST_String(String value) {
        super(ExpressionType.STRING);
        this.id = AST_String.nextId++;

        this.value = value;
    }

    // Getters
    public String getReturnType() {return null;} // TODO: implement
    public int getId() {return id;}
    public String getValue() {return value;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " value='" + value + "' id='" + id + "'>";
    }

}
