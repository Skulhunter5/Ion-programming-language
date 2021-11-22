package ion.assembly_frontend;

import ion.parser.*;
import ion.parser.ast.*;
import ion.utils.Utils;

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
            return generate_nasm_linux_x86_64(); // Wrong due to blocks/expressions not being executed in the order they appear in
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
            asm += String.format("    str_%d: db %s, 0\n", str.getId(), formatString(str.getValue()));
        }
        for(String identifier : parser.variables.keySet()) {
            Variable var = getVariable(identifier);
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
                alloc:
                    mov rsi, rdi
                    mov rax, 9 ; syscall: mmap
                    mov rdi, 0 ; location hint: os chooses
                    mov rdx, 3 ; PROT_READ | PROT_WRITE
                    mov r10, 0x21 ; MAP_ANONYMOUS | MAP_SHARED
                    mov r8, -1 ; required: fd=-1
                    mov r9, 0 ; required: offset=0
                    syscall
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
        if(block == null) return;
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
            case ARRAY_ACCESS -> {
                AST_Array ast = (AST_Array) expression;
                Variable var = getVariable(ast.getIdentifier());
                if(!var.getType().endsWith("*")) {
                    System.err.println("Array access operator can only be used on pointers.");
                    System.exit(1);
                }
                generateExpression(ast.getIndexExpression());
                asm += String.format("    mov rbx, %d\n", Utils.getByteSize(var.getType().substring(0, var.getType().length() - 1)));
                asm += "    mul rbx\n";
                asm += String.format("    add rax, var_%d\n", var.getId());
                asm += String.format("    mov %s, %s [rax]\n", getSizedRegister("rax", var.getBytesize()), operationSizes.get(var.getBytesize()));
            }
            case NOT -> {
                AST_Not ast = (AST_Not) expression;
                generateExpression(ast.getExpression());
                asm += """
                            cmp rax, 0
                            mov rax, 0
                            sete al
                        """;
            }
            case INTEGER -> {
                AST_Integer ast = (AST_Integer) expression;
                asm += String.format("    mov %s, %s\n", "rax", Long.toUnsignedString(ast.getValue()));
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
            case ALLOC -> {
                AST_Alloc ast = (AST_Alloc) expression;
                generateExpression(ast.getExpression());
                asm += """
                            mov rdi, rax
                            call alloc
                        """;
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
        return parser.variables.get(identifier);
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
        switch(reg) {
            case "rax":
                switch(bytesize) {
                    case 1 -> {
                        return "al";
                    }
                    case 2 -> {
                        return "ax";
                    }
                    case 4 -> {
                        return "eax";
                    }
                    case 8 -> {
                        return "rax";
                    }
                }
                break;
            case "rbx":
                switch(bytesize) {
                    case 1 -> {
                        return "bl";
                    }
                    case 2 -> {
                        return "bx";
                    }
                    case 4 -> {
                        return "ebx";
                    }
                    case 8 -> {
                        return "rbx";
                    }
                }
                break;
            case "rcx":
                switch(bytesize) {
                    case 1 -> {
                        return "cl";
                    }
                    case 2 -> {
                        return "cx";
                    }
                    case 4 -> {
                        return "ecx";
                    }
                    case 8 -> {
                        return "rcx";
                    }
                }
                break;
            case "rdx":
                switch(bytesize) {
                    case 1 -> {
                        return "dl";
                    }
                    case 2 -> {
                        return "dx";
                    }
                    case 4 -> {
                        return "edx";
                    }
                    case 8 -> {
                        return "rdx";
                    }
                }
                break;
            case "rdi":
                switch(bytesize) {
                    case 2 -> {
                        return "di";
                    }
                    case 4 -> {
                        return "edi";
                    }
                    case 8 -> {
                        return "rdi";
                    }
                }
                break;
        }

        System.err.println("[AssemblyFrontend] Error in getSmallerRegister.");
        System.exit(1);
        return null; // Unreachable
    }

}
