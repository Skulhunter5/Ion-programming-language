package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.AST_Statement;
import ion.parser.StatementType;

public class AST_If extends AST_Statement {

    private static int nextId = 0;

    private final int id;
    private final AST_Expression condition;
    private final AST_Block ifBlock;
    private final AST_Block elseBlock;

    public AST_If(AST_Expression condition, AST_Block ifBlock, AST_Block elseBlock) {
        super(StatementType.IF);
        this.id = AST_If.nextId++;

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
        return super.toString() + " condition=" + condition + " block_if=" + ifBlock + (elseBlock != null ? " block_else=" + elseBlock : "") + ">";
    }

}
