package ion.parser;

public abstract class AST_Statement extends AST {

    private StatementType statementType;

    public AST_Statement(StatementType statementType) {
        super(ASTType.STATEMENT);
        this.statementType = statementType;
    }

    // Getters and Setters
    public StatementType getStatementType() {return statementType;}

}
