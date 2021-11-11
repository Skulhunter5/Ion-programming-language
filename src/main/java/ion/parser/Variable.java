package ion.parser;

import ion.parser.ast.AST_Expression;

public class Variable {

    private static int nextId = 0;

    private int id;
    private String type; // Unused for now
    private String identifier;

    public Variable(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    // Getters
    public int getId() {return id;}
    public String getType() {return type;}
    public String getIdentifier() {return identifier;}

}
