package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Statement_While extends AST_Statement {

    private static int nextId = 0;

    private int id;
    private AST_Expression condition;
    private AST_Block block;

    public AST_Statement_While(AST_Expression condition, AST_Block block) {
        super(ASTType.STATEMENT_WHILE);
        this.id = AST_Statement_While.nextId++;

        this.condition = condition;
        this.block = block;
    }

    // Getters and Setters
    public int getId() {return id;}
    public AST_Expression getCondition() {return condition;}
    public AST_Block getBlock() {return block;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " condition='" + condition + "' block='" + block + "'>";
    }

}
