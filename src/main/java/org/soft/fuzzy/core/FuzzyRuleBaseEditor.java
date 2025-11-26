package org.soft.fuzzy.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.soft.fuzzy.inference.SugenoRule;
import org.soft.fuzzy.core.operators.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class FuzzyRuleBaseEditor {

    private final FuzzyRuleBase ruleBase;
    private final Map<Integer, FuzzyRule> ruleMapMamdani;
//    private final Map<Integer, SugenoRule> ruleMapSugeno;
    private int nextId;

    public FuzzyRuleBaseEditor(FuzzyRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.ruleMapMamdani = new LinkedHashMap<>();
        this.nextId = 0;

        for (FuzzyRule rule : ruleBase.getRules()) {
            int id = nextId++;
            ruleMapMamdani.put(id, rule);
        }
    }

    public int addRule(FuzzyRule rule) {
        int id = nextId++;
        rule.setEnabled(true);
        rule.setWeight(1.0);
        ruleMapMamdani.put(id, rule);
        ruleBase.addRule(rule);
        return id;
    }

    public void editRule(int id, FuzzyRule newRule) {
        checkRuleExists(id);
        FuzzyRule oldRule = ruleMapMamdani.get(id);

        ruleBase.removeRule(oldRule);
        ruleBase.addRule(newRule);
        ruleMapMamdani.put(id, newRule);
    }

    public void setRuleWeight(int id, double weight) {
        checkRuleExists(id);
        if (weight < 0.0 || weight > 1.0)
            throw new IllegalArgumentException("Weight must be in [0,1]");
        ruleMapMamdani.get(id).setWeight(weight);
    }

    public void removeRule(int id) {
        checkRuleExists(id);
        FuzzyRule rule = ruleMapMamdani.remove(id);
        ruleBase.removeRule(rule);
    }

    public Map<Integer, FuzzyRule> getAllRules() {
        return Collections.unmodifiableMap(ruleMapMamdani);
    }

    public void saveToJson(String filePath) throws IOException {
        List<RuleDTO> dtoList = new ArrayList<>();
        for (var entry : ruleMapMamdani.entrySet()) {
            int id = entry.getKey();
            FuzzyRule r = entry.getValue();
            RuleDTO dto = RuleDTO.fromRule(id, r);
            dtoList.add(dto);
        }
        PersistDTO p = new PersistDTO();
        p.rules = dtoList;
        p.nextId = nextId;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer w = new FileWriter(filePath)) {
            gson.toJson(p, w);
        }
    }

    public void loadFromJson(String filePath, Map<String, FuzzyVariable> variableRegistry) throws IOException {
        Gson gson = new Gson();
        try (Reader r = new FileReader(filePath)) {
            Type t = new TypeToken<PersistDTO>(){}.getType();
            PersistDTO p = gson.fromJson(r, t);
            if (p == null) throw new IOException("Invalid or empty JSON file: " + filePath);

            for (FuzzyRule rule : new ArrayList<>(ruleMapMamdani.values())) {
                ruleBase.removeRule(rule);
            }
            ruleMapMamdani.clear();

            for (RuleDTO dto : p.rules) {
                FuzzyRule rule = dto.toRule(variableRegistry);
                ruleBase.addRule(rule);
                ruleMapMamdani.put(dto.id, rule);
            }
            this.nextId = p.nextId;
        }
    }

    private void checkRuleExists(int id) {
        if (!ruleMapMamdani.containsKey(id))
            throw new IllegalArgumentException("Rule ID not found: " + id);
    }

    static class PersistDTO {
        List<RuleDTO> rules;
        int nextId;
    }

    static class RuleDTO {
        int id;
        String type;
        boolean enabled;
        double weight;
        String operator;
        List<CondDTO> conditions;
        CondDTO consequent;
        Double sugenoOutput;

        static RuleDTO fromRule(int id, FuzzyRule rule) {
            RuleDTO dto = new RuleDTO();
            dto.id = id;
            dto.enabled = rule.isEnabled();
            dto.weight = rule.getWeight();
            dto.operator = ruleOperatorName(rule);
            dto.conditions = new ArrayList<>();
            for (FuzzyCondition c : rule.conditions()) {
                dto.conditions.add(new CondDTO(c.variable().getName(), c.getSetName()));
            }
            if (rule instanceof SugenoRule) {
                dto.type = "sugeno";
                dto.consequent = null;
                dto.sugenoOutput = ((SugenoRule) rule).getOutputValue();
            } else {
                dto.type = "mamdani";
                FuzzyCondition cons = rule.getConsequent();
                dto.consequent = new CondDTO(cons.variable().getName(), cons.getSetName());
                dto.sugenoOutput = null;
            }
            return dto;
        }

        FuzzyRule toRule(Map<String, FuzzyVariable> vars) {
            LogicalOperator op = operatorFromName(this.operator);
            List<FuzzyCondition> conditions = new ArrayList<>();
            for (CondDTO cd : this.conditions) {
                FuzzyVariable var = vars.get(cd.variable);
                if (var == null) throw new IllegalArgumentException("Unknown variable: " + cd.variable);
                conditions.add(new FuzzyCondition(var, cd.setName));
            }
            if ("sugeno".equalsIgnoreCase(this.type)) {
                SugenoRule sr = new SugenoRule(conditions, this.sugenoOutput == null ? 0.0 : this.sugenoOutput, op);
                sr.setWeight(this.weight);
                sr.setEnabled(this.enabled);
                return sr;
            } else {
                if (this.consequent == null) throw new IllegalArgumentException("Mamdani rule without consequent");
                FuzzyVariable var = vars.get(this.consequent.variable);
                if (var == null) throw new IllegalArgumentException("Unknown variable: " + this.consequent.variable);
                FuzzyCondition cons = new FuzzyCondition(var, this.consequent.setName);
                FuzzyRule r = new FuzzyRule(conditions, cons, op);
                r.setWeight(this.weight);
                r.setEnabled(this.enabled);
                return r;
            }
        }

        static String ruleOperatorName(FuzzyRule r) {
            LogicalOperator op = r.operator;
            if (op == null) return "Min";
            return op.getClass().getSimpleName();
        }

        static LogicalOperator operatorFromName(String name) {
            if (name == null) return new Min();
            return switch (name) {
                case "Max" -> new Max();
                case "Product" -> new Product();
                case "Sum" -> new Sum();
                default -> new Min();
            };
        }
    }

    static class CondDTO {
        String variable;
        String setName;

        CondDTO(String variable, String setName) { this.variable = variable; this.setName = setName; }
    }
}