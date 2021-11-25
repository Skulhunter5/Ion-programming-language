package ion.parser.ast.declaration;

import ion.parser.AST;
import ion.parser.ASTType;
import ion.parser.ast.declaration.AST_Function;

import java.util.ArrayList;

public class AST_Declaration extends AST {

    private final ArrayList<AST_Function> declarations;

    public AST_Declaration() {
        super(ASTType.DECLARATION);
        declarations = new ArrayList<>();
    }

    // Getters
    public void addDeclaration(AST_Function child) {declarations.add(child);}
    public ArrayList<AST_Function> getDeclarations() {return declarations;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " children=" + declarations + ">";
    }
    @Override
    public String readableString() {
        String res = super.readableString() + " declarations=[\n";
        for(AST_Function declaration : declarations) res += AST.indent(declaration) + ",\n";
        res = res.substring(0, res.length() - 2);
        res += "]\n>";
        return res;
    }

}
