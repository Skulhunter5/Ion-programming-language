package ion.lexer;

public enum TokenType {

    IDENTIFIER,
    INTEGER, FLOAT, STRING,
    LT, GT, EQ, LTEQ, GTEQ, NEQ, NOT,
    ASSIGN, RIGHT_ARROW_SINGLE, RIGHT_ARROW_DOUBLE,
    OPERATOR,
    SEMICOLON, COLON, DOT, COMMA, LPAREN, RPAREN, LBRACE, RBRACE, LBRACK, RBRACK,
    KEYWORD,
    EOF;

    public boolean isKeyword() {
        return this.toString().startsWith("KEY_");
    }

}
