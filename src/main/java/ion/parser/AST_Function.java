package ion.parser;

import ion.parser.ast.AST_Block;

public class AST_Function extends AST {

    private final String identifier;
    private final AST_Block body;

    public AST_Function(String identifier, AST_Block body) {
        super(ASTType.FUNCTION);

        this.identifier = identifier;
        this.body = body;
    }

    // Getters and Setters
    public AST_Block getBody() {return body;}
    public String getIdentifier() {return identifier;}

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
