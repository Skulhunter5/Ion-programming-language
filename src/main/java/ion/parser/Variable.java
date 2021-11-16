package ion.parser;

public class Variable {

    private static int nextId = 0;

    private int id;
    private String type;
    private byte bytesize;
    private String identifier;

    public Variable(String type, byte bitsize, String identifier) {
        this.id = Variable.nextId++;

        this.type = type;
        this.bytesize = bitsize;
        this.identifier = identifier;
    }

    // Getters
    public int getId() {return id;}
    public String getType() {return type;}
    public byte getBytesize() {return bytesize;}
    public String getIdentifier() {return identifier;}

}
