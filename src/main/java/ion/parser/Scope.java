package ion.parser;

import java.util.HashMap;

public class Scope {

    private final HashMap<String, Variable> variables;
    private String lastIdentifier;
    private final Scope parent;
    private long baseOffset = 0;

    public Scope(Scope parent) {
        this.variables = new HashMap<>();
        this.parent = parent;
        if(parent != null) baseOffset = parent.getTotalOffset();
    }

    // Getters and Setters
    public Scope getParent() {return parent;}
    public HashMap<String, Variable> getAllVariables() {
        HashMap<String, Variable> res = new HashMap<>();
        for(Variable var : variables.values()) res.put(var.getIdentifier(), var);
        if(parent != null) for(Variable var : parent.getAllVariables().values()) res.put(var.getIdentifier(), var);
        return res;
    }
    public void addVariable(Variable variable) {
        if(lastIdentifier != null) variable.setOffset(variables.get(lastIdentifier).getOffset() + variable.getBytesize());
        variables.put(variable.getIdentifier(), variable);
        lastIdentifier = variable.getIdentifier();
    }
    public Variable getVariable(String identifier) {
        for(String varName : variables.keySet()) if(varName.equals(identifier)) return variables.get(varName);
        if(parent != null) return parent.getVariable(identifier);
        return null;
    }
    public boolean isVariableDeclared(String identifier) {
        for(String varName : variables.keySet()) if(varName.equals(identifier)) return true;
        if(parent != null) return parent.isVariableDeclared(identifier);
        return false;
    }
    public long getTotalOffset() {
        if(lastIdentifier != null) return variables.get(lastIdentifier).getOffset();
        if(parent != null) return parent.getTotalOffset();
        return 0;
    }

    // Print
    @Override
    public String toString() {
        String res = "Scope{variables=";
        if(variables.size() == 0) res += "null";
        else {
            res += "{";
            for(String identifier : variables.keySet()) {
                res += identifier + "=" + variables.get(identifier) + ",";
            }
            res = res.substring(0, res.length() - 1) + "}";
        }
        res +=  ", lastIdentifier='" + lastIdentifier + '\'' +
                ", parent=" + parent +
                ", baseOffset=" + baseOffset +
                '}';
        return res;
    }
}
