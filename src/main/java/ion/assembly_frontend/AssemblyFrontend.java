package ion.assembly_frontend;

import ion.parser.AST;
import ion.parser.ASTType;
import ion.parser.Parser;
import ion.parser.Variable;
import ion.parser.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AssemblyFrontend {

    private final Parser parser;

    private HashMap<Byte, String> definitionSizes, operationSizes;

    public AssemblyFrontend(Parser parser) {
        this.parser = parser;

        initDefinitionSizes();
        initOperationSizes();
    }

    private void initDefinitionSizes() {
        definitionSizes = new HashMap<>();
        definitionSizes.put((byte) 1, "db");
        definitionSizes.put((byte) 2, "dw");
        definitionSizes.put((byte) 4, "dd");
        definitionSizes.put((byte) 8, "dq");
    }
    private void initOperationSizes() {
        operationSizes = new HashMap<>();
        operationSizes.put((byte) 1, "byte");
        operationSizes.put((byte) 2, "word");
        operationSizes.put((byte) 4, "dword");
        operationSizes.put((byte) 8, "qword");
    }

    public String generate(String asmType) {
        if(asmType.equals("nasm linux x86_64")) {
            return generate_nasm_linux_x86_64();
        }
        return null;
    }

    private String asm;

    private String generate_nasm_linux_x86_64() {
        asm = """
                BITS 64
                segment .data
                """;
        for(AST_String str : parser.strings) {
            asm += String.format("    str_%s: db %s\n", str.getId(), formatString(str.getValue()));
        }
        for(Variable var : parser.variables) {
            asm += String.format("    var_%d: %s %d\n", var.getId(), definitionSizes.get(var.getBytesize()), 0);
        }
        asm += """
                segment .text
                print:
                    mov     r9, -3689348814741910323
                    sub     rsp, 40
                    mov     BYTE [rsp+31], 10
                    lea     rcx, [rsp+30]
                .L1:
                    mov     rax, r10
                    lea     r8, [rsp+32]
                    mul     r9
                    mov     rax, r10
                    sub     r8, rcx
                    shr     rdx, 3
                    lea     rsi, [rdx+rdx*4]
                    add     rsi, rsi
                    sub     rax, rsi
                    add     eax, 48
                    mov     BYTE [rcx], al
                    mov     rax, r10
                    mov     r10, rdx
                    mov     rdx, rcx
                    sub     rcx, 1
                    cmp     rax, 9
                    ja      .L1
                    lea     rax, [rsp+32]
                    mov     edi, 1
                    sub     rdx, rax
                    xor     eax, eax
                    lea     rsi, [rsp+32+rdx]
                    mov     rdx, r8
                    mov     rax, 1
                    syscall
                    add     rsp, 40
                    ret
                global _start
                _start:
                """;
        // Write program
        generateBlock(parser.getRoot());
        // Exit
        asm += """
                exit:
                    mov rax, 60
                    mov rdi, 0
                    syscall
                """;

        return asm;
    }

    private void generateBlock(AST_Block block) {
        for(AST uncastAST : block.getChildren()) {
            if(uncastAST instanceof AST_Expression) {
                generateExpression((AST_Expression) uncastAST);
            } else if(uncastAST instanceof AST_Statement) {
                generateStatement((AST_Statement) uncastAST);
            }
        }
    }

    private void generateExpression(AST_Expression expression) {
        switch(expression.getType()) {
            case DECREMENT -> {
                AST_Decrement ast = (AST_Decrement) expression;
                Variable var = getVariable(ast.getIdentifier());
                asm += String.format("    mov rax, %s [var_%d]\n", operationSizes.get(var.getBytesize()), var.getId());
                asm += String.format("    dec %s [var_%d]\n", operationSizes.get(var.getBytesize()), var.getId());
            }
            case PRINT -> { // TODO: make bytesize variable
                AST_Print ast = (AST_Print) expression;
                asm += String.format("    mov r10, qword [var_%d]\n", getVariable(ast.getIdentifier()).getId());
                asm += "    call print\n";
            }
            case ASSIGNMENT -> { // TODO: make bytesize variable
                AST_Assignment ast = (AST_Assignment) expression;
                AST_Expression value = ast.getValue();
                if(value.getType() == ASTType.INTEGER) {
                    AST_Integer integer = (AST_Integer) value;
                    asm += String.format("    mov qword [var_%d], %d\n", getVariable(ast.getIdentifier()).getId(), integer.getValue());
                }
            }
            case VARIABLE -> {
                AST_Variable ast = (AST_Variable) expression;
                Variable var = getVariable(ast.getIdentifier());
                asm += String.format("    mov rax, %s [var_%d]\n", operationSizes.get(var.getBytesize()), var.getId());
            }
            default -> {
                System.err.println("[AssemblyFrontend] Unimplemented expression.");
                System.exit(1);
            }
        }
    }

    private void generateStatement(AST_Statement statement) {
        switch(statement.getType()) {
            case STATEMENT_IF -> {
                AST_Statement_If ast = (AST_Statement_If) statement;
                generateExpression(ast.getCondition());
                asm += "    cmp rax, 0\n";
                asm += String.format("    jne if_%d\n", ast.getId());
                asm += String.format("else_%d:\n", ast.getId());
                generateBlock(ast.getElseBlock());
                asm += String.format("    jmp end_if_%d\n", ast.getId());
                asm += String.format("if_%d:\n", ast.getId());
                generateBlock(ast.getIfBlock());
                asm += String.format("end_if_%d:\n", ast.getId());
            }
            case STATEMENT_WHILE -> {
                // TODO
            }
            default -> {
                System.err.println("[AssemblyFrontend] Unimplemented statement.");
                System.exit(1);
            }
        }
    }

    private Variable getVariable(String identifier) {
        for(Variable var : parser.variables) {
            if(var.getIdentifier().equals(identifier)) return var;
        }
        System.err.println("[AssemblyFrontend]: Trying to access non-existent variable");
        System.exit(1);
        return null; // Unreachable
    }

    private String formatString(String str) { // Might not work
        ArrayList<String> parts = new ArrayList<>();
        String tmp = "";
        for(char c : str.toCharArray()) {
            if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ') {
                tmp += c;
            } else {
                if(tmp.length() > 0) {
                    parts.add(tmp);
                    tmp = "";
                }
                parts.add("" + ((byte) c));
            }
        }
        return String.join(",", parts);
    }

}
