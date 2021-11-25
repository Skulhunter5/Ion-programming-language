package ion.parser;

public class Variable {

    private static int nextId = 0;

    private final int id;
    private final String type;
    private final byte bytesize;
    private final String identifier;
    private long offset;

    public Variable(String type, byte bytesize, String identifier, long offset) {
        this.id = Variable.nextId++;

        this.type = type;
        this.bytesize = bytesize;
        this.identifier = identifier;
        this.offset = offset;
    }

    // Getters and Setters
    public int getId() {return id;}
    public String getType() {return type;}
    public byte getBytesize() {return bytesize;}
    public String getIdentifier() {return identifier;}
    public long getOffset() {return offset;}
    public void setOffset(long offset) {
        this.offset = offset;
    }

    // Print
    @Override
    public String toString() {
        return "Variable{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", bytesize=" + bytesize +
                ", identifier='" + identifier + '\'' +
                ", offset=" + offset +
                '}';
    }
}
