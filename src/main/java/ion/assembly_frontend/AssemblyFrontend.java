package ion.assembly_frontend;

import ion.parser.*;
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
                    mov     rax, rdi
                    lea     r8, [rsp+32]
                    mul     r9
                    mov     rax, rdi
                    sub     r8, rcx
                    shr     rdx, 3
                    lea     rsi, [rdx+rdx*4]
                    add     rsi, rsi
                    sub     rax, rsi
                    add     eax, 48
                    mov     BYTE [rcx], al
                    mov     rax, rdi
                    mov     rdi, rdx
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
        switch(expression.getExpressionType()) {
            case INTEGER -> {
                AST_Integer ast = (AST_Integer) expression;
                asm += String.format("    mov %s, %d\n", "rax", ast.getValue());
            }
            case COMPARISON -> {
                AST_Comparison ast = (AST_Comparison) expression;
                generateExpression(ast.getA());
                asm += "    push rax\n";
                generateExpression(ast.getB());
                // Mark: look for a better way to compare in assembly
                asm += """
                            pop rbx
                            cmp rbx, rax
                            mov rax, 0
                        """;
                switch(ast.getCompareType()) { // Mark: difference in setae<->setge and similar things
                    case EQ -> asm += "    sete al\n";
                    case NEQ -> asm += "    setne al\n";
                    case LT -> asm += "    setb al\n";
                    case GT -> asm += "    seta al\n";
                    case LTEQ -> asm += "    setbe al\n";
                    case GTEQ -> asm += "    setae al\n";
                    default -> {
                        System.err.println("[AssemblyFrontend] Invalid compareType of AST_Comparison.");
                        System.exit(1);
                    }
                }
            }
            case DECREMENT -> {
                AST_Decrement ast = (AST_Decrement) expression;
                Variable var = getVariable(ast.getIdentifier());
                String reg = getSizedRegister("rax", var.getBytesize());
                String opSize = operationSizes.get(var.getBytesize());
                if(var.getBytesize() < 8) asm += "    xor rax, rax\n";
                if(ast.isBefore()) asm += String.format("    dec %s [var_%d]\n", opSize, var.getId());
                asm += String.format("    mov %s, %s [var_%d]\n", reg, opSize, var.getId());
                if(ast.isAfter()) asm += String.format("    dec %s [var_%d]\n", opSize, var.getId());
            }
            case INCREMENT -> {
                AST_Increment ast = (AST_Increment) expression;
                Variable var = getVariable(ast.getIdentifier());
                String reg = getSizedRegister("rax", var.getBytesize());
                String opSize = operationSizes.get(var.getBytesize());
                if(var.getBytesize() < 8) asm += "    xor rax, rax\n";
                if(ast.isBefore()) asm += String.format("    inc %s [var_%d]\n", opSize, var.getId());
                asm += String.format("    mov %s, %s [var_%d]\n", reg, opSize, var.getId());
                if(ast.isAfter()) asm += String.format("    inc %s [var_%d]\n", opSize, var.getId());
            }
            case ASSIGNMENT -> { // TODO: make bytesize variable
                AST_Assignment ast = (AST_Assignment) expression;
                Variable var = getVariable(ast.getIdentifier());
                generateExpression(ast.getValue());
                asm += String.format("    mov %s [var_%d], %s\n", operationSizes.get(var.getBytesize()), var.getId(), getSizedRegister("rax", var.getBytesize()));
            }
            case VARIABLE_ACCESS -> {
                AST_Variable ast = (AST_Variable) expression;
                Variable var = getVariable(ast.getIdentifier());
                if(var.getBytesize() < 8) asm += "    xor rax, rax\n";
                asm += String.format("    mov %s, %s [var_%d]\n", getSizedRegister("rax", var.getBytesize()), operationSizes.get(var.getBytesize()), var.getId());
            }
            default -> {
                System.err.println("[AssemblyFrontend] Unimplemented expression.");
                System.exit(1);
            }
        }
    }

    private void generateStatement(AST_Statement statement) {
        switch(statement.getStatementType()) {
            case IF -> {
                AST_If ast = (AST_If) statement;
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
            case WHILE -> {
                AST_While ast = (AST_While) statement;
                asm += String.format("while_%d:\n", ast.getId());
                generateExpression(ast.getCondition());
                asm += "    cmp rax, 0\n";
                asm += String.format("    je end_while_%d\n", ast.getId());
                generateBlock(ast.getBlock());
                asm += String.format("    jmp while_%d\n", ast.getId());
                asm += String.format("end_while_%d:\n", ast.getId());
            }
            case DO_WHILE -> {
                AST_DoWhile ast = (AST_DoWhile) statement;
                asm += String.format("doWhile_%d:\n", ast.getId());
                generateBlock(ast.getBlock());
                generateExpression(ast.getCondition());
                asm += "    cmp rax, 0\n";
                asm += String.format("    jne doWhile_%d\n", ast.getId());
            }
            case PRINT -> { // TODO: make bytesize variable
                AST_Print ast = (AST_Print) statement;
                generateExpression(ast.getExpression());
                asm += """
                            mov rdi, rax
                            call print
                        """;
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

    private String getSizedRegister(String reg, byte bytesize) {
        if(reg.equals("rax")) {
            if(bytesize == 1) return "al";
            else if(bytesize == 2) return "ax";
            else if(bytesize == 4) return "eax";
            else if(bytesize == 8) return "rax";
        } else if(reg.equals("rbx")) {
            if(bytesize == 1) return "bl";
            else if(bytesize == 4) return "ebx";
            else if(bytesize == 8) return "rbx";
        } else if(reg.equals("rcx")) {
            if(bytesize == 1) return "cl";
            else if(bytesize == 4) return "ecx";
            else if(bytesize == 8) return "rcx";
        } else if(reg.equals("rdx")) {
            if(bytesize == 1) return "dl";
            else if(bytesize == 4) return "edx";
            else if(bytesize == 8) return "rdx";
        } else if(reg.equals("rdi")) {
            if(bytesize == 2) return "di";
            else if(bytesize == 4) return "edi";
            else if(bytesize == 8) return "rdi";
        }

        System.err.println("[AssemblyFrontend] Error in getSmallerRegister.");
        System.exit(1);
        return null; // Unreachable
    }

}
