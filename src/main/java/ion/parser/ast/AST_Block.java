package ion.parser.ast;

import ion.parser.AST;
import ion.parser.ASTType;

import java.util.ArrayList;

public class AST_Block extends AST {

    private final ArrayList<AST> children;

    public AST_Block() {
        super(ASTType.BLOCK);
        children = new ArrayList<>();
    }

    // Getters
    public void addChild(AST child) {children.add(child);}
    public ArrayList<AST> getChildren() {return children;}

    // Print
    @Override
    public String toString() {
        String res = super.toString() + " children=[\n";
        for(AST child : children) res += AST.indent(child) + ",\n";
        res = res.substring(0, res.length() - 2);
        res += "\n>";
        return res;
    }

}
