//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.10.24 at 03:35:39 PM CEST 
//


package generated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionSummary"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="creditSummary" type="{}Summary"/&gt;
 *         &lt;element name="debitSummary" type="{}Summary"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSummary", propOrder = {
    "creditSummary",
    "debitSummary"
})
public class TransactionSummary {

    @XmlElement(required = true)
    protected Summary creditSummary;
    @XmlElement(required = true)
    protected Summary debitSummary;

    /**
     * Gets the value of the creditSummary property.
     * 
     * @return
     *     possible object is
     *     {@link Summary }
     *     
     */
    public Summary getCreditSummary() {
        return creditSummary;
    }

    /**
     * Sets the value of the creditSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Summary }
     *     
     */
    public void setCreditSummary(Summary value) {
        this.creditSummary = value;
    }

    /**
     * Gets the value of the debitSummary property.
     * 
     * @return
     *     possible object is
     *     {@link Summary }
     *     
     */
    public Summary getDebitSummary() {
        return debitSummary;
    }

    /**
     * Sets the value of the debitSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Summary }
     *     
     */
    public void setDebitSummary(Summary value) {
        this.debitSummary = value;
    }

}
