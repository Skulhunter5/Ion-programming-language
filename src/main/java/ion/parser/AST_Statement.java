package ion.parser;

public abstract class AST_Statement extends AST {

    private StatementType statementType;
    private Scope scope;

    public AST_Statement(StatementType statementType, Scope scope) {
        super(ASTType.STATEMENT);

        this.statementType = statementType;
        this.scope = scope;
    }

    // Getters and Setters
    public StatementType getStatementType() {return statementType;}
    public Scope getScope() {return scope;}

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
