package ion.parser;

public enum ExpressionType {

    VARIABLE_ACCESS, ARRAY_ACCESS,
    ASSIGNMENT, ASSIGNMENT_ARRAY,
    STRING, INTEGER, FLOAT, BOOLEAN,
    COMPARISON,
    DECREMENT, INCREMENT,
    NOT,
    FUNCTION_CALL,
    // Temp
    ALLOC,

}
