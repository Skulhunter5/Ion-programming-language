package ion.parser.ast;

import ion.parser.AST_Expression;
import ion.parser.AST_Statement;
import ion.parser.StatementType;

public class AST_DoWhile extends AST_Statement {

    private static int nextId = 0;

    private final int id;
    private final AST_Expression condition;
    private final AST_Block block;

    public AST_DoWhile(AST_Expression condition, AST_Block block) {
        super(StatementType.DO_WHILE);
        this.id = AST_DoWhile.nextId++;

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
        return super.toString() + " condition=" + condition + " block=" + block + ">";
    }

}
