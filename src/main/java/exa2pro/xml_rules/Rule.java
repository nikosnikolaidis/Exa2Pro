/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro.xml_rules;

/**
 *
 * @author Nikos
 */
/*
* Rule class for rules from xml
*/
public class Rule {

    private String key;
    private String name;
    private String internalKey;
    private String description;
    private String severity;
    private String cardinality;
    private String status;
    private String type;
    private String remediationFunction;
    private String remediationFunctionBaseEffort;

    public Rule(){}
    public Rule(String key, String name, String internalKey, String description,
            String severity, String cardinality, String status, String type,
            String remediationFunction, String remediationFunctionBaseEffort) {
        this.key = key;
        this.name = name;
        this.internalKey = internalKey;
        this.description = description;
        this.severity = severity;
        this.cardinality = cardinality;
        this.status = status;
        this.type = type;
        this.remediationFunction = remediationFunction;
        this.remediationFunctionBaseEffort = remediationFunctionBaseEffort;
    }

    //Getters
    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getInternalKey() {
        return internalKey;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public String getCardinality() {
        return cardinality;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getRemediationFunction() {
        return remediationFunction;
    }

    public String getRemediationFunctionBaseEffort() {
        return remediationFunctionBaseEffort;
    }

    //Setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInternalKey(String internalKey) {
        this.internalKey = internalKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRemediationFunction(String remediationFunction) {
        this.remediationFunction = remediationFunction;
    }

    public void setRemediationFunctionBaseEffort(String remediationFunctionBaseEffort) {
        this.remediationFunctionBaseEffort = remediationFunctionBaseEffort;
    }
}
