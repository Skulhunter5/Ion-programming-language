package ion.lexer;

import ion.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class Lexer {

    private String src;
    private int src_size;
    private int i;
    private char c;

    private static final List<String> KEYWORDS = Arrays.asList(new String[] {"if", "else", "while", "for", "switch", "case", "continue", "break", "default"});

    public Lexer(String src) {
        this.src = src;
        i = 0;
        src_size = src.length();
        c = src.charAt(i);
    }

    private void advance() {
        i += 1;
        if(i < src_size) c = src.charAt(i);
        else c = (char) 0;
    }
    private void advance(int n) {
        i += n;
        if(i < src_size) c = src.charAt(i);
        else c = (char) 0;
    }

    private Token advanceWith(Token token) {
        advance();
        return token;
    }

    private char peek(int offset) {
        if(i + offset < src_size) return src.charAt(i + offset);
        else return (char) 0;
    }

    private void skipWhitespace() {
        while(c == ' ' || c == '\n' || c == '\t' || c == '\r') {
            advance();
        }
    }

    private Token parseIdentifier() {
        String value = "";
        while(Utils.isAlpha(c) || c == '_' || Utils.isDigit(c)) {
            value += c;
            advance();
        }
        return new Token(TokenType.IDENTIFIER, value);
    }

    private Token parseNumber() { // TODO: rework exception handling for multiple dots
        String value = "";
        byte dotCount = 0;
        while(Utils.isDigit(c) || c == '.') {
            if(c == '.') {
                if(dotCount == 1) break;
                dotCount++;
            }
            value += c;
            advance();
        }
        if(dotCount == 0) return new Token(TokenType.INTEGER, value);
        else return new Token(TokenType.FLOAT, value);
    }

    private Token parseOperator() {
        String value = "";
        while(Utils.isOperatorChar(c)) {
            value += c;
            advance();
        }
        return new Token(TokenType.OPERATOR, value);
    }

    private Token parseString() {
        advance();
        String value = "";
        while(c != '"' && c != (char) 0) {
            value += c;
            advance();
        }
        if(c == (char) 0) {
            System.err.println("[Lexer] String expands to end of file");
            System.exit(1);
        }
        advance();
        return new Token(TokenType.STRING, value);
    }

    private void skipComments() {
        while((c == '/' && peek(1) == '/') || (c == '/' && peek(1) == '*')) {
            if(c == '/' && peek(1) == '/') {
                advance(2);
                while(c != '\n') advance();
                advance();
            } else if(c == '/' && peek(1) == '*') {
                advance(2);
                while(!(c == '*' && peek(1) == '/')) advance();
                advance(2);
            }
            skipWhitespace();
        }
    }

    public Token nextToken() {
        skipWhitespace();
        skipComments();

        if(c == (char) 0) return new Token(TokenType.EOF, null);

        if(Utils.isAlpha(c) || c == '_') {
            Token id = parseIdentifier();

            if(Lexer.KEYWORDS.contains(id.getValue())) return new Token(TokenType.KEYWORD, id.getValue());
            else return id;
        }
        if(Utils.isDigit(c)) return parseNumber();
        if(c == '"') return parseString();

        switch(c) {
            case '=':
                advance();
                if(c == '>') return advanceWith(new Token(TokenType.RIGHT_ARROW_DOUBLE, null));
                else if(c == '=') return advanceWith(new Token(TokenType.EQ, null));
                else return new Token(TokenType.ASSIGN, null);
            case '<':
                advance();
                if(c == '=') return advanceWith(new Token(TokenType.LTEQ, null));
                else return new Token(TokenType.LT, null);
            case '>':
                advance();
                if(c == '=') return advanceWith(new Token(TokenType.GTEQ, null));
                else return new Token(TokenType.GT, null);
            case '!':
                advance();
                if(c == '=') return advanceWith(new Token(TokenType.NEQ, null));
                else return new Token(TokenType.NOT, null);
            case ';':
                return advanceWith(new Token(TokenType.SEMICOLON, null));
            case '(':
                return advanceWith(new Token(TokenType.LPAREN, null));
            case ')':
                return advanceWith(new Token(TokenType.RPAREN, null));
            case '[':
                return advanceWith(new Token(TokenType.LBRACK, null));
            case ']':
                return advanceWith(new Token(TokenType.RBRACK, null));
            case '{':
                return advanceWith(new Token(TokenType.LBRACE, null));
            case '}':
                return advanceWith(new Token(TokenType.RBRACE, null));
            case '-':
                advance();
                if(c == '>') return advanceWith(new Token(TokenType.RIGHT_ARROW_SINGLE, null));
            case '+':
            case '*':
            case '/':
                parseOperator();
            case ':':
                return advanceWith(new Token(TokenType.COLON, null));
            case '.':
                if(Utils.isDigit(peek(1))) return parseNumber();
                else return advanceWith(new Token(TokenType.DOT, null));
            case ',':
                return advanceWith(new Token(TokenType.COMMA, null));
            default:
                System.err.println("[Lexer] Unexpected character: '" + c + "'");
                System.exit(1);
        }

        return null;
    }

    public void run() {
        if(this.src.length() == 0) return;

        Token token;
        do {
            token = nextToken();
            Token.print(token);
        } while(token.getType() != TokenType.EOF);
    }

}
