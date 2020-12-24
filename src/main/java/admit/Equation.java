package admit;

import java.io.Serializable;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dimitrios Zisis <zdimitris@outlook.com>
 * Date: 03/12/2020
 */
public class Equation implements Serializable{

    private Map<String, Double> parameters;
    private String id;
    private String equationStr;
    public Map<Double, Double> tuneList;

    public Equation(String id, String equationStr){
        this.id = id;
        this.equationStr = equationStr;
        this.parameters = new HashMap<>();
        this.tuneList = new HashMap<>();
        initializeParameters(parseParameterNames());
    }

    public Equation(String equationStr){
        this.equationStr = equationStr;
        this.parameters = new HashMap<>();
        this.tuneList = new HashMap<>();
        initializeParameters(parseParameterNames());
    }

    /**
     * Updates a parameter's weight, according to a value given.
     * Returns true if addition was successful, false otherwise
     *
     * @param  parameterName  the parameter's name
     * @param  weight  the new parameter's weight
     *
     * @return  true if addition was successful, false otherwise
     */
    public void putParameterWeight(String parameterName, double weight) { this.parameters.put(parameterName, weight); }

    private void initializeParameters(Set<String> paramNames){ paramNames.forEach(name -> this.parameters.put(name, 0.0)); }

    private Set<String> parseParameterNames(){
        Set<String> names = new HashSet<>();
        Matcher m = Pattern.compile("\\b([A-Za-z]\\w*)\\b").matcher(this.equationStr);
        while (m.find())
            names.add(m.group());
        return names;
    }

    /**
     * Computes the equation with the last updated parameter's weights
     * and returns the final cost.
     *
     * @return  the final cost
     */
    public double computeEquation() {
        double finalCost = 0.0;
        Expression e = new ExpressionBuilder(this.equationStr)
                .variables(this.parameters.keySet())
                .build()
                .setVariables(this.parameters);
        try { finalCost = e.evaluate(); } catch (Exception ignored){}
        return finalCost;
    }

    /**
     * Computes the equation with the last updated parameter's weights, but
     * substituting the one parameter given (key-value map entry: parameterName, parameterVal),
     * and returns the final cost
     *
     * @param  param  the parameter (key-value map entry: parameterName, parameterVal)
     *                use ' new AbstractMap.SimpleEntry<>(name, val) '
     *
     * @return  the final cost
     */
    public double computeEquation(Map.Entry<String, Double> param) {
        double finalCost = 0.0;
        Map<String, Double> tmpParams = new HashMap<>(this.parameters);
        tmpParams.put(param.getKey(), param.getValue());
        Expression e = new ExpressionBuilder(this.equationStr)
                .variables(tmpParams.keySet())
                .build()
                .setVariables(tmpParams);
        try { finalCost = e.evaluate(); } catch (Exception ignored){}
        tuneList.put(param.getValue(),finalCost);
        return finalCost;
    }

    public Map<String, Double> getParameters() { return this.parameters; }

    @Override
    public String toString() { return this.equationStr; }
}
