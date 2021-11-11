package ion.assembly_frontend;

import ion.parser.AST;
import ion.parser.ASTType;
import ion.parser.Parser;
import ion.parser.Variable;
import ion.parser.ast.*;

import java.util.ArrayList;

public class AssemblyFrontend {

    private Parser parser;

    public AssemblyFrontend(Parser parser) {
        this.parser = parser;
    }

    public String generate(String asmType) {
        if(asmType == "nasm linux x86_64") {
            return generate_nasm_linux_x86_64();
        }
        return null;
    }

    private String asm;

    private String generate_nasm_linux_x86_64() {
        asm =    "BITS 64\n"+
                 "segment .data\n";
        for(AST_String str : parser.strings) {
            asm += String.format("    str_%s: db %s\n", str.getId(), formatString(str.getValue()));
        }
        for(Variable var : parser.variables) {
            asm += String.format("    var_%d: dd %d\n", var.getId(), 0);
        }
        asm +=  "segment .text\n"+
                "print:\n"+
                "    mov     r9, -3689348814741910323\n"+
                "    sub     rsp, 40\n"+
                "    mov     BYTE [rsp+31], 10\n"+
                "    lea     rcx, [rsp+30]\n"+
                ".L2:\n"+
                "    mov     rax, r10\n"+
                "    lea     r8, [rsp+32]\n"+
                "    mul     r9\n"+
                "    mov     rax, r10\n"+
                "    sub     r8, rcx\n"+
                "    shr     rdx, 3\n"+
                "    lea     rsi, [rdx+rdx*4]\n"+
                "    add     rsi, rsi\n"+
                "    sub     rax, rsi\n"+
                "    add     eax, 48\n"+
                "    mov     BYTE [rcx], al\n"+
                "    mov     rax, r10\n"+
                "    mov     r10, rdx\n"+
                "    mov     rdx, rcx\n"+
                "    sub     rcx, 1\n"+
                "    cmp     rax, 9\n"+
                "    ja      .L2\n"+
                "    lea     rax, [rsp+32]\n"+
                "    mov     edi, 1\n"+
                "    sub     rdx, rax\n"+
                "    xor     eax, eax\n"+
                "    lea     rsi, [rsp+32+rdx]\n"+
                "    mov     rdx, r8\n"+
                "    mov     rax, 1\n"+
                "    syscall\n"+
                "    add     rsp, 40\n"+
                "    ret\n"+
                "global _start\n"+
                "_start:\n";
        // Write program
        generateBlock(parser.getRoot());
        // Exit
        asm +=  "    mov rax, 60\n"+
                "    mov rdi, 0\n"+
                "    syscall\n";

        return asm;
    }

    private void generateBlock(AST_Block block) {
        for(AST uncastAST : block.getChildren()) {
            if(uncastAST instanceof AST_Expression) {
                if(uncastAST.getType() == ASTType.PRINT) {
                    AST_Print ast = (AST_Print) uncastAST;
                    asm += "    xor r10, r10\n";
                    asm += String.format("    mov r10d, dword [var_%d]\n", getVariable(ast.getIdentifier()).getId());
                    asm += "    call print\n";
                } else if(uncastAST.getType() == ASTType.ASSIGNMENT) {
                    AST_Assignment ast = (AST_Assignment) uncastAST;
                    AST_Expression value = ast.getValue();
                    if(value.getType() == ASTType.INTEGER) {
                        AST_Integer integer = (AST_Integer) value;
                        asm += String.format("    mov dword [var_%d], %d\n", getVariable(ast.getIdentifier()).getId(), integer.getValue());
                    }
                }
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
            byte i = (byte) c;
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
