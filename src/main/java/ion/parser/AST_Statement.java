package ion.parser;

public abstract class AST_Statement extends AST {

    private StatementType statementType;

    public AST_Statement(StatementType statementType) {
        super(ASTType.STATEMENT);
        this.statementType = statementType;
    }

    // Getters and Setters
    public StatementType getStatementType() {return statementType;}

    // Print
    @Override
    public String toString() {
        return super.toString() + "-" + statementType;
    }
    @Override
    public String readableString() {
        return super.toString() + "-" + statementType;
    }

}
