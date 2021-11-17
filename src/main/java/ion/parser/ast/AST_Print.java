package ion.parser.ast;

import ion.parser.AST_Statement;
import ion.parser.StatementType;

public class AST_Print extends AST_Statement { // TODO: make print a statement instead of an expression

    private String identifier;

    public AST_Print(String identifier) {
        super(StatementType.PRINT);

        this.identifier = identifier;
    }

    // Getters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return "<AST-" + type + " identifier='" + identifier + "'>";
    }

}
