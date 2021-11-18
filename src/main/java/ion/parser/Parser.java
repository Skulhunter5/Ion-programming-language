package ion.parser;

import ion.lexer.Lexer;
import ion.lexer.Token;
import ion.lexer.TokenType;
import ion.parser.ast.*;

import java.util.ArrayList;

public class Parser {

    private final Lexer lexer;
    private Token token;
    private final ArrayList<Token> peekTokens;

    public AST_Block root; // TODO: implement functions and change root from block to declarationSpace
    public ArrayList<Variable> variables;
    public ArrayList<AST_String> strings;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        peekTokens = new ArrayList<>();
        token = lexer.nextToken();

        strings = new ArrayList<>();
        variables = new ArrayList<>();
    }

    /**
     * retrieves the next token and returns the old token
     *
     * @return old token
     */
    private Token eat() {
        Token old = token;
        if(peekTokens.size() == 0) token = lexer.nextToken();
        else token = peekTokens.remove(0);
        return old;
    }

    /**
     * retrieves the next token and returns the old token;
     * exits the program with error code 1 if the current tokens type doesn't match the specified type
     *
     * @param type TokenType to check for
     * @return old token
     */
    private Token eat(TokenType type) {
        if(token.getType() != type) {
            System.err.println("[Parser]: Unexpected Token: " + token + ", was expecting: " + type);
            System.exit(1);
        }
        return eat();
    }

    /**
     * Retrieves the token at the given offset
     *
     * @param offset the offset to peek at
     * @return old token
     */
    private Token peek(int offset) {
        if(offset == 0) return token;
        if(offset == -1) { // Better this way because of otherwise having to handle a returned null whenever peek(int) is used
            System.err.println("[Parser] can't peek at a negative offset");
            System.exit(1);
        }

        if(offset <= peekTokens.size()) return peekTokens.get(offset - 1);

        for(int i = peekTokens.size(); i < offset; i++) peekTokens.add(lexer.nextToken());
        return peekTokens.get(peekTokens.size() - 1);
    }

    public AST parse() {
        root = parseBlock(true);
        return root;
    }

    private AST_Block parseBlock(boolean root) { // Mark: remove the root flag if a declaration becomes the root
        AST_Block block = new AST_Block();
        if(token.getType() != TokenType.LBRACE && !root) {
            AST ast = parseStatement();
            if(ast != null) block.addChild(ast);
            return block;
        }
        if(!root || token.getType() == TokenType.LBRACE) eat(); // TokenType.LBRACE

        while(token.getType() != TokenType.RBRACE && token.getType() != TokenType.EOF) {
            AST ast = parseStatement();
            if(ast != null) block.addChild(ast);
        }
        if(!root || token.getType() == TokenType.RBRACE) eat(TokenType.RBRACE);

        return block;
    }

    private AST parseStatement() {
        if(token.getType() == TokenType.KEYWORD) {
            switch(token.getValue()) {
                case "if" -> {
                    eat(); // TokenType.KEYWORD "if"
                    eat(TokenType.LPAREN);
                    AST_Expression condition = parseExpression(ExpressionEnd.NONE);
                    eat(TokenType.RPAREN);
                    AST_Block ifBlock = parseBlock(false);
                    AST_Block elseBlock = null;
                    if(token.getType() == TokenType.KEYWORD && token.getValue().equals("else")) {
                        eat(); // TokenType.KEYWORD "else"
                        elseBlock = parseBlock(false);
                    }
                    return new AST_If(condition, ifBlock, elseBlock);
                }
                case "while" -> {
                    eat(); // TokenType.KEYWORD "while"
                    eat(TokenType.LPAREN);
                    AST_Expression condition = parseExpression(ExpressionEnd.NONE);
                    eat(TokenType.RPAREN);
                    AST_Block block = parseBlock(false);
                    return new AST_While(condition, block);
                }
                case "do" -> {
                    eat(); // TokenType.KEYWORD "do"
                    AST_Block block = parseBlock(false);
                    String val = eat(TokenType.KEYWORD).getValue();
                    if(!val.equals("while")) {
                        System.err.println("[Parser] Unexpected keyword: '" + val + "'");
                        System.exit(1);
                    }
                    eat(TokenType.LPAREN);
                    AST_Expression condition = parseExpression(ExpressionEnd.NONE);
                    eat(TokenType.RPAREN);
                    eat(TokenType.SEMICOLON);
                    return new AST_DoWhile(condition, block);
                }
                case "print" -> {
                    eat(); // TokenType.KEYWORD "print"
                    AST_Expression expression = parseExpression(ExpressionEnd.NONE);
                    eat(TokenType.SEMICOLON);
                    return new AST_Print(expression);
                }
                default -> {
                    System.err.println("[Parser]: Invalid keyword: " + token.getValue());
                    System.exit(1);
                }
            }
        } else return parseExpression(ExpressionEnd.SEMICOLON);

        // Should be unreachable
        System.err.println("[Parser]: Unreachable");
        System.exit(1);
        return null; // Unreachable
    }

    private AST_Expression parseExpression(int expressionEnd) {
        AST_Expression expression = parseExpressionConjunction(null);

        if((expressionEnd & ExpressionEnd.SEMICOLON) > 0) eat(TokenType.SEMICOLON);

        return expression;
    }

    private AST_Expression parseExpressionParticle() {
        AST_Expression expression = null;
        switch(token.getType()) {
            case INTEGER:
                // TODO: Implement enough sized unsigned parsing
                // TODO: Check if there has to be an upper/lower bound safety or implement more
                expression = new AST_Integer(Long.parseUnsignedLong(eat()/*TokenType.INTEGER*/.getValue()));
                break;
            case FLOAT:
                // TODO: Check if there has to be an upper/lower bound safety or implement more
                expression = new AST_Float(Float.parseFloat(eat()/*TokenType.FLOAT*/.getValue()));
                break;
            case IDENTIFIER:
                String val1 = token.getValue();
                eat(); // TokenType.IDENTIFIER
                switch(token.getType()) {
                    case IDENTIFIER:
                        String val2 = token.getValue();
                        eat(); // TokenType.IDENTIFIER
                        switch(token.getType()) { // TODO: implement declaration and assignment of variable in one go
//                            case SEMICOLON:
//                                eat(); // TokenType.SEMICOLON
//                                expression = registerVariable(val1, val2, null);
//                                break;
                            case ASSIGN: // TODO: add capability of declaration/definition inside of a condition
                                eat(); // TokenType.ASSIGN
                                expression = registerVariable(val1, val2, parseExpression(0));
                                break;
                            default:
                                registerVariable(val1, val2, null);
//                                System.err.println("[Parser]: Unexpected token: " + token.getType());
//                                System.exit(1);
                        }
                        break;
                    case ASSIGN: // TODO: add capability of declaration/definition inside of a condition
                        eat(); // TokenType.ASSIGN
                        expression = new AST_Assignment(val1, parseExpression(0));
                        break;
                    case DECREMENT:
                        eat();
                        expression = new AST_Decrement(val1, true);
                        break;
                    case INCREMENT:
                        eat();
                        expression = new AST_Increment(val1, true);
                        break;
                    default:
                        expression = new AST_Variable(val1);
                        //                    default:
                        //                        System.err.println("[Parser]: Unexpected token: " + token.getType());
                        //                        System.exit(1);
                }
                break;
            case DECREMENT: {
                eat(); // TokenType.DECREMENT
                String val = eat(TokenType.IDENTIFIER).getValue();
                expression = new AST_Decrement(val, false);
                break;
            }
            case INCREMENT: {
                eat(); // TokenType.INCREMENT
                String val = eat(TokenType.IDENTIFIER).getValue();
                expression = new AST_Increment(val, false);
                break;
            }
            default:
                System.err.println("[Parser]: Unexpected token: " + token.getType());
                System.exit(1);
        }

        return expression;
    }

    private AST_Expression parseUnaryExpression() {
        if(token.getType() == TokenType.NOT) {
            eat(); // TokenType.NOT
            return new AST_Not(parseExpression(0));
        }
        return parseExpressionParticle();
    }

    private AST_Expression parseExpressionConjunction(AST_Expression expression) {
        if(expression == null) expression = parseUnaryExpression();

        TokenType ttype = token.getType();
        switch(ttype) {
            case EQ, NEQ, GT, LT, GTEQ, LTEQ -> {
                eat(); // some kind of EQ
                return parseExpressionConjunction(new AST_Comparison(expression, parseUnaryExpression(), ttype));
            }
        }

        return expression;
    }

    private AST_Expression registerVariable(String type, String identifier, AST_Expression startValue) { // Remove AST_VariableDeclaration
        byte bytesize = 0;
        switch(type) {
            case "uint64" -> bytesize = 8;
            case "uint32" -> bytesize = 4;
            case "uint16" -> bytesize = 2;
            case "uint8" -> bytesize = 1;
            default -> {
                System.err.println("[Parser] Unknown bytesize.");
                System.exit(1);
            }
        }
        variables.add(new Variable(type, bytesize, identifier)); // TODO
        if(startValue == null) return null;
        return new AST_Assignment(identifier, startValue);
    }

    // Getters
    public AST_Block getRoot() {
        return root;
    }

}
