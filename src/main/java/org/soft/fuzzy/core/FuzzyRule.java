package org.soft.fuzzy.core;

import org.soft.fuzzy.core.operators.*;

import java.io.Serializable;
import java.util.*;

public class FuzzyRule implements Serializable {

    protected final List<FuzzyCondition> conditions;
    private final FuzzyCondition consequent;
    protected final LogicalOperator operator;
    private double weight = 1.0;
    private boolean enabled = true;

    public FuzzyRule(List<FuzzyCondition> conditions, FuzzyCondition consequent, LogicalOperator operator) {
        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("Rule must have at least one condition.");
        }
        this.conditions = new ArrayList<>(conditions);
        this.consequent = consequent;
        this.operator = operator;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public List<FuzzyCondition> conditions() {
        return Collections.unmodifiableList(conditions);
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public double evaluate(Map<String, Double> inputs) {
        double result = 0;
        boolean first = true;

        for (FuzzyCondition condition : conditions) {
            Double inputValue = inputs.get(condition.variable().getName());

            if (inputValue == null) {
                // handle missing input: use midpoint
                inputValue = (condition.variable().getMinRange() + condition.variable().getMaxRange()) / 2.0;
            }

            // clamp to domain
            inputValue = Math.max(condition.variable().getMinRange(),
                    Math.min(inputValue, condition.variable().getMaxRange()));

            double degree = condition.evaluate(inputValue);

            if (first) {
                result = degree;
                first = false;
            } else {
                result = operator.apply(result, degree);
            }
        }

        return result * weight;
    }

    public FuzzyCondition getConsequent() {
        return consequent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IF ");
        for (int i = 0; i < conditions.size(); i++) {
            sb.append(conditions.get(i));
            if (i < conditions.size() - 1) {
                if (operator instanceof Max) sb.append(" OR ");
                if (operator instanceof Min) sb.append(" AND ");
            }
        }
        sb.append(" THEN ").append(consequent);
        return sb.toString();
    }
}
