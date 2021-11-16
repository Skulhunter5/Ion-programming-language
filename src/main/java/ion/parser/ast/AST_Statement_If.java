package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Statement_If extends AST_Statement {

    private static int nextId = 0;

    private int id;
    private AST_Expression condition;
    private AST_Block ifBlock;
    private AST_Block elseBlock;

    public AST_Statement_If(AST_Expression condition, AST_Block ifBlock, AST_Block elseBlock) {
        super(ASTType.STATEMENT_IF);
        this.id = AST_Statement_If.nextId++;

        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    // Getters and Setters
    public int getId() {return id;}
    public AST_Expression getCondition() {return condition;}
    public AST_Block getIfBlock() {return ifBlock;}
    public AST_Block getElseBlock() {return elseBlock;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " condition='" + condition + "' block_if='" + ifBlock + "' block_else='" + elseBlock + "'>";
    }

}
