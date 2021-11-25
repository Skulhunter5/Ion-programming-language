package ion.parser.ast.code;

import ion.parser.*;

public class AST_While extends AST_Statement {

    private static int nextId = 0;

    private int id;
    private AST_Expression condition;
    private AST_Block block;

    public AST_While(AST_Expression condition, AST_Block block, Scope scope) {
        super(StatementType.WHILE, scope);
        this.id = AST_While.nextId++;

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
    @Override
    public String readableString() {
        return super.toString() + "\n\tcondition=\n" + AST.indent(condition, 2) + "\n\tblock=\n" + AST.indent(block, 2) + "\n>";
    }

}
