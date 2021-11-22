package ion.parser;

public enum ExpressionType {

    VARIABLE_ACCESS, ARRAY_ACCESS,
    ASSIGNMENT,
    STRING, INTEGER, FLOAT, BOOLEAN,
    COMPARISON,
    DECREMENT, INCREMENT,
    NOT,
    // Temp
    ALLOC,

}
