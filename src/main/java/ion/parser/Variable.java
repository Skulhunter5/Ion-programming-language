package ion.parser;

public class Variable {

    private static int nextId = 0;

    private final int id;
    private final String type;
    private final byte bytesize;
    private final String identifier;

    public Variable(String type, byte bytesize, String identifier) {
        this.id = Variable.nextId++;

        this.type = type;
        this.bytesize = bytesize;
        this.identifier = identifier;
    }

    // Getters
    public int getId() {return id;}
    public String getType() {return type;}
    public byte getBytesize() {return bytesize;}
    public String getIdentifier() {return identifier;}

}
