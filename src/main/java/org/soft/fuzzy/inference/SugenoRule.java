package org.soft.fuzzy.inference;

import org.soft.fuzzy.core.*;
import org.soft.fuzzy.core.operators.LogicalOperator;
import org.soft.fuzzy.core.operators.Max;
import org.soft.fuzzy.core.operators.Min;

import java.util.List;

public class SugenoRule extends FuzzyRule {

    private final double outputValue; // constant output (zero-order)

    public SugenoRule(List<FuzzyCondition> conditions, double outputValue, LogicalOperator operator) {
        super(conditions, null, operator); // consequent is not a fuzzy set
        this.outputValue = outputValue;
    }

    public double getOutputValue() {
        return outputValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IF ");
        for (int i = 0; i < super.conditions().size(); i++) {
            sb.append(super.conditions().get(i));
            if (i < super.conditions.size() - 1) {
                if (super.operator instanceof Max) sb.append(" OR ");
                if (super.operator instanceof Min) sb.append(" AND ");
            }
        }

        sb.append(" THEN ").append("Thrust adjustment = ").append(outputValue);
        return sb.toString();
    }

}
