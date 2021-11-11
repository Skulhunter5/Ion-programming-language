package ion.parser;

import java.util.ArrayList;

public class AST {

    protected ASTType type;

    public AST(ASTType type) {
        this.type = type;
    }

    public ASTType getType() {
        return type;
    }

    // Printing

    @Override
    public String toString() {
        return "<AST type='" + type + "'>";
    }

}
