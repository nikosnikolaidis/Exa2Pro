package admit;

import java.io.Serializable;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by Dimitrios Zisis <zdimitris@outlook.com>
 * Date: 03/12/2020
 */
public class AdmitProject implements Serializable{
    private String name;
    private int loc;
    private int marketSize;
    private String marketShare;
    private double price;
    private Set<Decision> decisions;

    public AdmitProject(String name){
        this.name = name;
        decisions = new HashSet<>();
    }

    /**
     * Adds a decision to project's decision list
     * Returns true if addition was successful, false otherwise
     *
     * @param  decision  the decision to be added to project's model list
     *
     * @return  true if addition was successful, false otherwise
     */
    public boolean addDecision(Decision decision) {
        try {
            this.decisions.add(decision);
            return true;
        } catch (Exception e){ return false; }
    }

    /**
     * Finds a specific decision and returns its instance.
     * If no decision exists with the name given, returns null
     *
     * @param  decisionName  the decision's name that is being searched
     *
     * @return  the decision's instance (Decision) if exists, null otherwise
     */
    public Decision findDecision(String decisionName) {
        try {
            return this.decisions.stream().filter(d -> d.getName().equals(decisionName)).findFirst().get();
        } catch (NoSuchElementException e){ return null; }
    }

    /**
     * Removes a decision from project's decision list
     * Returns true if removal was successful, false otherwise
     *
     * @param  decision  the decision to be removed from project's decision list
     *
     * @return  true if removal was successful, false otherwise
     */
    public boolean removeDecision(Decision decision){
        try { this.decisions.remove(decision); } catch (Exception e){ return false; }
        return true;
    }

    /* TODO: 19/12/2020 */
    public boolean insertAllDecisionsToFile() {
        return true;
    }

    /**
     * Tunes cost & benefit analysis, given a specific decision,
     * a min & a max value for a specific parameter.
     * Returns true if analysis tuned successfully, false otherwise.
     *
     * @param  min  the parameter's value minimum threshold
     * @param  max  the parameter's value maximum threshold
     * @param  dec  the decision provided (containing the parameter)
     *
     * @return true if analysis tuned successfully, false otherwise.
     */
    public boolean tuneCostBenefitAnalysis(Decision dec, int min, int max, String parameter) {
        try {
            Decision currentDecision = findDecision(dec.getName());
            return currentDecision.tuneCostBenefitAnalysis(min, max, parameter);
        } catch (Exception e) { return false; }
    }

    public String getName() {
        return this.name;
    }

    public Set<Decision> getDecisions() { return this.decisions; }

    public String toString(){
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AdmitProject)) return false;

        AdmitProject other = (AdmitProject)o;
        return other.getName().equals(this.getName());
    }
}
