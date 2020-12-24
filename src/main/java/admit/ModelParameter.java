package admit;

import java.io.Serializable;

/**
 * Created by Dimitrios Zisis <zdimitris@outlook.com>
 * Date: 03/12/2020
 */
public class ModelParameter implements Serializable{
    private String name;
    private Equation equation;
    private String project;
    private String decision;
    private String type;

    public ModelParameter(String modelName, String projectName, String decisionName, String modelType, Equation equation){
        this.name = modelName;
        this.project = projectName;
        this.decision = decisionName;
        this.type = modelType;
        this.equation = equation;
    }
    
    public ModelParameter(String modelName, String modelType, Equation equation){
        this.name = modelName;
        this.type = modelType;
        this.equation = equation;
    }
    
    public void clearPreviousTuning(){
        this.equation.tuneList.clear();
    }

    public String getName() { return this.name; }

    public String getType() { return this.type; }

    public Equation getEquation() { return this.equation; }

    public String getProject() { return this.project; }

    public String getDecision() { return this.decision; }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ModelParameter)) return false;

        ModelParameter other = (ModelParameter)o;
        return other.getName().equals(this.getName());
    }

}
