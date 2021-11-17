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
     */
    private Token eat(TokenType type) {
        if(token.getType() != type) {
            System.err.println("[Parser]: Unexpected Token: " + token + ", was expecting: " + type);
            System.exit(1);
        }
        return eat();
    }

    private Token peek(int offset) { // might not work correctly
        if(offset == 0) return token;

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
            if(token.getValue().equals("if")) {
                eat(); // TokenType.KEYWORD "if"
                eat(TokenType.LPAREN);
                AST_Expression condition = parseExpression();
                eat(TokenType.RPAREN);
                AST_Block ifBlock = parseBlock(false);
                AST_Block elseBlock = null;
                if(token.getType() == TokenType.KEYWORD && token.getValue().equals("else")) {
                    eat(); // TokenType.KEYWORD "else"
                    elseBlock = parseBlock(false);
                }
                return new AST_If(condition, ifBlock, elseBlock);
            } else if(token.getValue().equals("while")) {
                eat(); // TokenType.KEYWORD "while"
                eat(TokenType.LPAREN);
                AST_Expression condition = parseExpression();
                eat(TokenType.RPAREN);
                AST_Block block = parseBlock(false);
                return new AST_While(condition, block);
            } else if(token.getValue().equals("do")) {
                eat(); // TokenType.KEYWORD "do"
                AST_Block block = parseBlock(false);
                String val = token.getValue();
                eat(TokenType.KEYWORD);
                if(!val.equals("while")) {
                    System.err.println("[Parser] Unexpected keyword: '" + val + "'");
                    System.exit(1);
                }
                eat(TokenType.LPAREN);
                AST_Expression condition = parseExpression();
                eat(TokenType.RPAREN);
                eat(TokenType.SEMICOLON);
                return new AST_DoWhile(condition, block);
            } else {
                System.err.println("[Parser]: Invalid keyword: " + token.getValue());
                System.exit(1);
            }
        } else {
            if(token.getType() == TokenType.IDENTIFIER && token.getValue().equals("print")) {
                eat(); // TokenType.IDENTIFIER
                String val2 = token.getValue();
                eat(TokenType.IDENTIFIER);
                eat(TokenType.SEMICOLON);
                return new AST_Print(val2);
            }
            return parseExpression();
        }

        // Should be unreachable
        System.err.println("[Parser]: Unreachable");
        System.exit(1);
        return null; // Unreachable
    }

    private AST_Expression parseExpression() {
        switch(token.getType()) {
            case SEMICOLON:
                eat(); // TokenType.SEMICOLON
                return null;
            case INTEGER:
                // TODO: Implement enough sized unsigned parsing
                // TODO: Check if there has to be an upper/lower bound safety or implement more
                return new AST_Integer(Long.parseLong(eat()/*TokenType.INTEGER*/.getValue()));
            case FLOAT:
                // TODO: Check if there has to be an upper/lower bound safety or implement more
                return new AST_Float(Float.parseFloat(eat()/*TokenType.FLOAT*/.getValue()));
            case IDENTIFIER:
                String val1 = token.getValue();
                eat(); // TokenType.IDENTIFIER
                switch(token.getType()) {
                    case SEMICOLON:
                        eat(); // TokenType.SEMICOLON
                    case RPAREN:
                        return new AST_Variable(val1);
                    case IDENTIFIER:
                        String val2 = token.getValue();
                        eat(); // TokenType.IDENTIFIER
                        switch(token.getType()) { // TODO: implement declaration and assignment of variable in one go
                            case SEMICOLON:
                                eat(); // TokenType.SEMICOLON
                                return registerVariable(val1, val2, null);
                            case ASSIGN: // TODO: add capability of declaration inside of a condition
                                eat(); // TokenType.ASSIGN
                                AST_Expression expr = registerVariable(val1, val2, parseExpression());
                                if(token.getType() != TokenType.RPAREN)
                                    eat(TokenType.SEMICOLON); // Mark: rethink this
                                return expr;
                            default:
                                System.err.println("[Parser]: Unexpected token: " + token.getType());
                                System.exit(1);
                        }

                        // Should be unreachable
                        System.err.println("[Parser]: Unreachable");
                        System.exit(1);
                        return null; // Unreachable
                    case ASSIGN: // TODO: add capability of declaration inside of a condition
                        eat(); // TokenType.ASSIGN
                        return new AST_Assignment(val1, parseExpression());
                    case DECREMENT:
                        eat();
                        if(token.getType() != TokenType.RPAREN)
                            eat(TokenType.SEMICOLON);
                        return new AST_Decrement(val1, true);
                    case INCREMENT:
                        eat();
                        if(token.getType() != TokenType.RPAREN)
                            eat(TokenType.SEMICOLON);
                        return new AST_Increment(val1, true);
                    default:
                        System.err.println("[Parser]: Unexpected token: " + token.getType());
                        System.exit(1);
                }
                // Temp
                break;
            case DECREMENT: {
                eat(); // TokenType.DECREMENT
                String val = eat(TokenType.IDENTIFIER).getValue();
                return new AST_Decrement(val, false);
            }
            case INCREMENT: {
                eat(); // TokenType.INCREMENT
                String val = eat(TokenType.IDENTIFIER).getValue();
                return new AST_Increment(val, false);
            }
            default:
                System.err.println("[Parser]: Unexpected token: " + token.getType());
                System.exit(1);
        }

        // TODO: Idea: Fallthrough full expressions until here and then try to find expression that contain the current expression (also eat RPAREN if necessary, ...)

        // Should be unreachable
        System.err.println("[Parser]: Unreachable");
        System.exit(1);
        return null; // Unreachable
    }

    private AST_Expression registerVariable(String type, String identifier, AST_Expression startValue) { // Remove AST_VariableDeclaration
        byte bytesize = 0;
        if(type.equals("uint64")) bytesize = 8;
        else if(type.equals("uint32")) bytesize = 4;
        else if(type.equals("uint16")) bytesize = 2;
        else if(type.equals("uint8")) bytesize = 1;
        else {
            System.err.println("[Parser] Unknown bytesize.");
            System.exit(1);
        }
        variables.add(new Variable(type, bytesize, identifier)); // TODO
        if(startValue == null) return null;
        return new AST_Assignment(identifier, startValue);
    }

    // Getters
    public AST_Block getRoot() {return root;}

}
