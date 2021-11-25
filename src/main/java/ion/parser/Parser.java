package ion.parser;

import ion.lexer.Lexer;
import ion.lexer.Token;
import ion.lexer.TokenType;
import ion.parser.ast.code.*;
import ion.parser.ast.declaration.AST_Declaration;
import ion.parser.ast.declaration.AST_Function;
import ion.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

    private final Lexer lexer;
    private Token token;
    private final ArrayList<Token> peekTokens;

    public AST_Declaration root; // TODO: implement functions and change root from block to declarationSpace
    public ArrayList<AST_String> strings;

    public HashMap<String, AST_Function> functions;
    private final HashMap<String, GlobalVariable> globalVariables;
    private Scope currentScope;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        peekTokens = new ArrayList<>();
        token = lexer.nextToken();

        strings = new ArrayList<>();
        functions = new HashMap<>();

        globalVariables = new HashMap<>();
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
     * retrieves the next token and returns whether the old token fits the specified TokenType for custom Error handling;
     *
     * @param type TokenType to check for
     * @return true if wrong; false if correct
     */
    private boolean eatWithFeedback(TokenType type) {
        if(token.getType() != type) return true;
        eat();
        return false;
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

    public AST_Declaration parse() {
        root = parseDeclaration();
        return root;
    }

    public AST_Declaration parseDeclaration() {
        AST_Declaration declaration = new AST_Declaration();
        while(token.getType() != TokenType.EOF) {
            if(peek(1).getType() == TokenType.LPAREN) declaration.addDeclaration(parseFunction());
            else parseGlobalVariable();
        }
        return declaration;
    }

    public AST_Function parseFunction() {
        String val1 = token.getValue();
        eat(TokenType.IDENTIFIER);
        eat(TokenType.LPAREN);
        eat(TokenType.RPAREN);
        currentScope = new Scope(null);
        AST_Function function = registerFunction(val1, parseBlock(), currentScope);
        currentScope = null;
        return function;
    }
    
    public void parseGlobalVariable() {
        String type = eat(TokenType.IDENTIFIER).getValue();
        String identifier = eat(TokenType.IDENTIFIER).getValue();
        if(token.getType() == TokenType.ASSIGN) {
            eat(); // TokenType.ASSIGN
            registerGlobalVariable(type, identifier, parseExpressionConjunction(ExpressionEnd.SEMICOLON));
        } else {
            if(eatWithFeedback(TokenType.SEMICOLON)) {
                System.err.println("[Parser] Unexpected token: Was expecting ASSIGN or SEMICOLON, but got " + token);
                System.exit(1);
            }
            registerGlobalVariable(type, identifier, null);
        }
    }

    private AST_Block parseBlock() { // Mark: remove the root flag if a declaration becomes the root
        AST_Block block = new AST_Block();
        if(token.getType() != TokenType.LBRACE) {
            AST ast = parseStatement();
            if(ast != null) block.addChild(ast);
            return block;
        }

        eat(TokenType.LBRACE); // TokenType.LBRACE
        while(token.getType() != TokenType.RBRACE && token.getType() != TokenType.EOF) {
            AST ast = parseStatement();
            if(ast != null) block.addChild(ast);
        }
        eat(TokenType.RBRACE);

        return block;
    }

    private AST parseStatement() {
        if(token.getType() == TokenType.KEYWORD) {
            AST_Statement statement = null;
            Scope oldScope = currentScope;
            currentScope = new Scope(oldScope);
            switch(token.getValue()) {
                case "if" -> {
                    eat(); // TokenType.KEYWORD "if"
                    if(eatWithFeedback(TokenType.LPAREN)) { // eat(TokenType.LPAREN);
                        System.err.println("[Parser] Expected LPAREN after 'if' keyword, instead got: " + token);
                        System.exit(1);
                    }
                    AST_Expression condition = parseExpressionConjunction(0);
                    if(eatWithFeedback(TokenType.RPAREN)) { // eat(TokenType.RPAREN);
                        System.err.println("[Parser] Expected RPAREN to close condition block of if-statement, instead got: " + token);
                        System.exit(1);
                    }
                    AST_Block ifBlock = parseBlock();
                    AST_Block elseBlock = null;
                    if(token.getType() == TokenType.KEYWORD && token.getValue().equals("else")) {
                        eat(); // TokenType.KEYWORD "else"
                        elseBlock = parseBlock();
                    }
                    statement = new AST_If(condition, ifBlock, elseBlock, currentScope);
                }
                case "while" -> {
                    eat(); // TokenType.KEYWORD "while"
                    if(eatWithFeedback(TokenType.LPAREN)) { // eat(TokenType.LPAREN);
                        System.err.println("[Parser] Expected LPAREN after 'while' keyword, instead got: " + token);
                        System.exit(1);
                    }
                    AST_Expression condition = parseExpressionConjunction(0);
                    if(eatWithFeedback(TokenType.RPAREN)) { // eat(TokenType.RPAREN);
                        System.err.println("[Parser] Expected RPAREN to close condition block of while-statement, instead got: " + token);
                        System.exit(1);
                    }
                    AST_Block block = parseBlock();
                    statement = new AST_While(condition, block, currentScope);
                }
                case "do" -> {
                    eat(); // TokenType.KEYWORD "do"
                    AST_Block block = parseBlock();
                    String val = eat(TokenType.KEYWORD).getValue();
                    if(!val.equals("while")) {
                        System.err.println("[Parser] Expected 'while' keyword after block of do-statement, instead got: '" + token + "'");
                        System.exit(1);
                    }
                    if(eatWithFeedback(TokenType.LPAREN)) { // eat(TokenType.LPAREN);
                        System.err.println("[Parser] Expected LPAREN after 'while' keyword in do-while-statement, instead got: " + token);
                        System.exit(1);
                    }
                    AST_Expression condition = parseExpressionConjunction(0);
                    if(eatWithFeedback(TokenType.RPAREN)) { // eat(TokenType.RPAREN);
                        System.err.println("[Parser] Expected RPAREN to close condition block of do-while-statement, instead got: " + token);
                        System.exit(1);
                    }
                    if(eatWithFeedback(TokenType.SEMICOLON)) { // eat(TokenType.SEMICOLON);
                        System.err.println("[Parser] Expected SEMICOLON after do-while-statement, instead got: " + token);
                        System.exit(1);
                    }
                    return new AST_DoWhile(condition, block, currentScope);
                }
                case "print" -> {
                    eat(); // TokenType.KEYWORD "print"
                    AST_Expression expression = parseExpressionConjunction(0);
                    if(eatWithFeedback(TokenType.SEMICOLON)) { // eat(TokenType.SEMICOLON);
                        System.err.println("[Parser] Expected SEMICOLON after print-statement, instead got: " + token);
                        System.exit(1);
                    }
                    return new AST_Print(expression, currentScope);
                }
                default -> {
                    System.err.println("[Parser] Invalid keyword: " + token.getValue());
                    System.exit(1);
                }
            }
            currentScope = oldScope;
            return statement;
        } else return parseExpressionConjunction(ExpressionEnd.SEMICOLON);
    }

    private static final TokenType[] conjunctions = new TokenType[]{
            TokenType.EQ,
            TokenType.NEQ,
            TokenType.GT,
            TokenType.LT,
            TokenType.GTEQ,
            TokenType.LTEQ,
    };
    private static boolean isConjunction(TokenType type) {
        for(TokenType t : conjunctions) if(t == type) return true;
        return false;
    }
    private AST_Expression parseExpressionConjunction(int expressionEnd) {
        ArrayList<AST_Expression> expressions = new ArrayList<>();
        expressions.add(parseExpression());
        ArrayList<TokenType> conjunctions = new ArrayList<>();

        while(isConjunction(token.getType())) {
            conjunctions.add(eat().getType()); // eat -> some kind of conjunction
            expressions.add(parseExpression());
        }

        if(expressions.size() == 0) {
            System.err.println("[Parser parseExpressionConjunction(int)] expressions shouldn't be empty.");
            System.exit(1);
        }
        while(expressions.size() > 1) { // TODO: implement operator precedence
            TokenType ttype = conjunctions.remove(0);
            switch(ttype) {
                case EQ, NEQ, GT, LT, GTEQ, LTEQ -> expressions.set(0, new AST_Comparison(expressions.get(0), expressions.remove(1), ttype));
            }
        }

        if((expressionEnd & ExpressionEnd.PARENTHESIS) > 0) if(eatWithFeedback(TokenType.RPAREN)) {
            System.err.println("[Parser] Expected RPAREN to close expression: " + expressions.get(0));
            System.exit(1);
        }
        if((expressionEnd & ExpressionEnd.SEMICOLON) > 0) if(eatWithFeedback(TokenType.SEMICOLON)) {
            System.err.println("[Parser] Expected SEMICOLON to close expression: " + expressions.get(0));
            System.exit(1);
        }

        return expressions.get(0);
    }

    private AST_Expression parseExpression() { // Mark: might not work correctly
        AST_Expression expression = null;

        boolean useParenthesis = false;
        if(token.getType() == TokenType.LPAREN) {
            eat(); // TokenType.LPAREN
            expression = parseExpressionConjunction(0);
            useParenthesis = true;
        }

        if(token.getType() == TokenType.IDENTIFIER && token.getValue().equals("alloc")) {
            eat(); // TokenType.KEYWORD "alloc"
            if(eatWithFeedback(TokenType.LPAREN)) {
                System.err.println("[Parser] Expected LPAREN after 'alloc'.");
                System.exit(1);
            }
            expression = new AST_Alloc(parseExpressionConjunction(ExpressionEnd.PARENTHESIS));
            return expression;
        }

        if(useParenthesis) {
            if(eatWithFeedback(TokenType.RPAREN)) {
                System.err.println("[Parser] Expected RPAREN to close expression: " + expression);
                System.exit(1);
            }
            return expression;
        }

        return parseUnaryExpression();
    }

    private AST_Expression parseUnaryExpression() {
        AST_Expression expression = null;

        switch(token.getType()) {
            case NOT -> {
                eat(); // TokenType.NOT
                expression = new AST_Not(parseExpression());
            }
        }

        if(expression != null) return expression;
        return parseExpressionParticle();
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
                    case LPAREN:
                        eat(); // TokenType.LPAREN
                        ArrayList<AST_Expression> arguments = parseCallArguments();
                        eat(TokenType.RPAREN);
                        expression = new AST_FunctionCall(val1, arguments);
                        break;
                    case LBRACK:
                        eat(); // TokenType.LBRACK
                        AST_Expression indexExpression = parseExpressionConjunction(0);
                        //expression = new AST_Array(val1, parseExpressionConjunction(0));
                        eat(TokenType.RBRACK);
                        if(token.getType() == TokenType.ASSIGN) {
                            eat(); // TokenType.ASSIGN
                            expression = new AST_Assignment_Array(val1, indexExpression, parseExpressionConjunction(0));
                        } else {
                            expression = new AST_ArrayAccess(val1, indexExpression);
                        }
                        break;
                    case IDENTIFIER:
                        String val2 = token.getValue();
                        eat(); // TokenType.IDENTIFIER
                        switch(token.getType()) { // TODO: implement declaration and assignment of variable in one go
                            case ASSIGN: // TODO: add capability of declaration/definition inside of a condition
                                eat(); // TokenType.ASSIGN
                                expression = registerVariable(val1, val2, parseExpressionConjunction(0));
                                break;
                            default:
                                expression = registerVariable(val1, val2, null);
                        }
                        break;
                    case ASSIGN:
                        eat(); // TokenType.ASSIGN
                        expression = new AST_Assignment(val1, parseExpressionConjunction(0));
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
                        if(!isVariableDeclared(val1, currentScope)) {
                            System.err.println("[Parser] Trying to access an undeclared variable: '" + val1 + "'");
                            System.exit(1);
                        }
                        expression = new AST_Variable(val1);
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

    private ArrayList<AST_Expression> parseCallArguments() {
        ArrayList<AST_Expression> arguments = new ArrayList<>();

        while(token.getType() != TokenType.RPAREN) {
            arguments.add(parseExpressionConjunction(0));
            if(token.getType() != TokenType.RPAREN) eat(TokenType.COMMA);
        }

        return arguments;
    }

    private void registerGlobalVariable(String type, String identifier, AST_Expression startValue) {
        if(globalVariables.containsKey(identifier)) {
            System.err.println("[Parser] Trying to redeclare a variable (global).");
            System.exit(1);
        }

        byte bytesize = Utils.getByteSize(type);
        GlobalVariable var = new GlobalVariable(type, bytesize, identifier, 0, startValue);
        globalVariables.put(identifier, var);
    }

    private AST_Expression registerVariable(String type, String identifier, AST_Expression startValue) { // Remove AST_VariableDeclaration
        if(isVariableDeclared(identifier, currentScope)) {
            System.err.println("[Parser] Trying to redeclare a variable (scoped).");
            System.exit(1);
        }

        byte bytesize = Utils.getByteSize(type);
        currentScope.addVariable(new Variable(type, bytesize, identifier, currentScope.getTotalOffset() + bytesize));
        if(startValue == null) return new AST_Assignment(identifier, new AST_Integer(0));
        return new AST_Assignment(identifier, startValue);
    }

    private AST_Function registerFunction(String identifier, AST_Block body, Scope scope) {
        if(functions.containsKey(identifier)) {
            System.err.println("[Parser] Trying to redefine a function.");
            System.exit(1);
        }

        AST_Function function = new AST_Function(identifier, scope, body);
        functions.put(identifier, function);
        return function;
    }

    private boolean isVariableDeclared(String identifier, Scope scope) {
        boolean isDeclared = globalVariables.containsKey(identifier);
        if(!isDeclared) isDeclared = scope.isVariableDeclared(identifier);
        return isDeclared;
    }

    // Getters
    public AST_Declaration getRoot() {return root;}
    public Scope getScope() {return currentScope;}
    public HashMap<String, GlobalVariable> getGlobalVariables() {return globalVariables;}
    public Variable getVariable(String identifier, Scope scope) {
        Variable var = globalVariables.get(identifier);
        if(var == null) var = scope.getVariable(identifier);
        return var;
    }

}
