package br.com.alm.workflow.infraestructure;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**

 * Java class for anonymous complex type.
 *

 * The following schema fragment specifies the expected content
 * contained within this class.
 *
 * <complexType>
 *   <complexContent>
 *     <restriction base=
 *         "{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element ref=
 *             "{}Attribute" maxOccurs="unbounded"
 *             minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 *
 *

 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "entity" })
@XmlRootElement(name = "Entities")
public class Entities {
	
	@XmlAttribute(name = "TotalResults", required = true)
   protected int totalResults;

    @XmlElement(name = "Entity")
    protected List<Entity> entity;

    /**
     * Gets the value of the entity property.
     *
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object. This is
     * why there is not a <CODE>set</CODE> method for the entity property.
     *
     * For example, to add a new item, do as follows:
     *
     * getEntity().add(newItem);
      *
     * Objects of the following type(s) are allowed in the
     * list {@link Entity }
     *
     *
     */
    public List<Entity> getEntity() {
        if (entity == null) {
        	entity = new ArrayList<Entity>();
        }
        return this.entity;
    }

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

}
