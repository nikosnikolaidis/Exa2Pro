/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro.xml_rules;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Nikos
 */
/*
* Root elemet for all rules in xml
*/
@XmlRootElement(name="icodelint-rules")
public class IcodelintRrules {

    private List<Rule> rule;

    public IcodelintRrules(){}
    public IcodelintRrules(List<Rule> rule) {
        this.rule = rule;
    }

    @XmlElement
    public List<Rule> getRule() {
        return rule;
    }

    public void setRule(List<Rule> rule) {
        this.rule = rule;
    }
}
