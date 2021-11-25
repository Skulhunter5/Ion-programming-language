package ion.parser.ast.declaration;

import ion.parser.AST;
import ion.parser.ASTType;
import ion.parser.Scope;
import ion.parser.ast.code.AST_Block;

public class AST_Function extends AST {

    private final String identifier;
    private final AST_Block body;
    private final Scope scope;

    public AST_Function(String identifier, Scope scope, AST_Block body) {
        super(ASTType.FUNCTION);

        this.identifier = identifier;
        this.scope = scope;
        this.body = body;
    }

    // Getters and Setters
    public AST_Block getBody() {return body;}
    public String getIdentifier() {return identifier;}
    public Scope getScope() {return scope;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " identifier='" + identifier + "' body=" + body + ">";
    }
    @Override
    public String readableString() {
        return super.toString() + "\n\tidentifier='" + identifier + "'\n\tbody=\n" + AST.indent(body) + "\n>";
    }
}
