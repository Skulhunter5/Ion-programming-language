package ion.parser.ast.code;

import ion.parser.AST;
import ion.parser.AST_Expression;
import ion.parser.ExpressionType;

import java.util.ArrayList;

public class AST_FunctionCall extends AST_Expression {

    private final String identifier;
    private final ArrayList<AST_Expression> arguments;

    public AST_FunctionCall(String identifier, ArrayList<AST_Expression> arguments) {
        super(ExpressionType.FUNCTION_CALL);

        this.identifier = identifier;
        if(arguments.size() == 0) this.arguments = null;
        else this.arguments = arguments;
    }

    // Getter and Setters
    public String getIdentifier() {return identifier;}

    // Print
    @Override
    public String toString() {
        return super.toString() + " identifier='" + identifier + (arguments != null ? "' arguments=" + arguments : "'") + ">";
    }
    @Override
    public String readableString() {
        String res = super.readableString() + "\n\tidentifier='" + identifier + "'";
        if(arguments != null) {
            res += "\n\targuments=[\n";
            for(AST_Expression argument : arguments) res += AST.indent(argument, 2) + ",\n";
            res = res.substring(0, res.length() - 2);
            res += "\n\t]";
        }
        return res + "\n>";
    }
}
