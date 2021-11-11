package ion.parser.ast;

import ion.parser.ASTType;

public class AST_VariableDeclaration extends AST_Expression {

    private static int nextId = 0;

    private String varType;
    private String identifier;
    private AST_Expression startValue;
    // Temp
    public int id;

    public AST_VariableDeclaration(String varType, String identifier, AST_Expression startValue) {
        super(ASTType.VARIABLE_DECLARATION);
        this.varType = varType;
        this.identifier = identifier;
        this.startValue = startValue;
        // Temp
        this.id = nextId++;
    }

    // Getters
    public String getVarType() {return varType;}
    public String getIdentifier() {return identifier;}
    public AST_Expression getStartValue() {return startValue;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + "' identifier='" + identifier + "' type='" + varType + (startValue != null ? "' startValue='" + startValue : "") + "'>";
    }

}
