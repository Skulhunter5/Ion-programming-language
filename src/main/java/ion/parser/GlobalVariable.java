package ion.parser;

public class GlobalVariable extends Variable {

    private final AST_Expression startValue;

    public GlobalVariable(String type, byte bytesize, String identifier, long offset, AST_Expression startValue) {
        super(type, bytesize, identifier, offset);

        this.startValue = startValue;
    }

    // Getters and Setters
    public AST_Expression getStartValue() {return startValue;}

    // Print

    @Override
    public String toString() {
        return "GlobalVariable{" +
                "variable=" + super.toString() +
                "startValue=" + startValue +
                '}';
    }

}
