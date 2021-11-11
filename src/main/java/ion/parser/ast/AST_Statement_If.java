package ion.parser.ast;

import ion.parser.ASTType;

public class AST_Statement_If extends AST_Statement {

    private AST_Expression condition;
    private AST_Block ifBlock;
    private AST_Block elseBlock;

    public AST_Statement_If(AST_Expression condition, AST_Block ifBlock, AST_Block elseBlock) {
        super(ASTType.STATEMENT_IF);
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " condition='" + condition + "' block_if='" + ifBlock + "' block_else='" + elseBlock + "'>";
    }

}
