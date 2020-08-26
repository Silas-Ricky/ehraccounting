package org.openmrs.module.ehraccounting.web.controller.payment;

import org.apache.commons.lang3.math.NumberUtils;
import org.openmrs.module.ehraccounting.api.model.Payee;

import java.beans.PropertyEditorSupport;


public class PayeePropertySupport extends PropertyEditorSupport {

	@Override
    public String getAsText() {
		Payee payee = (Payee) this.getValue();
	    return payee != null ?  payee.getId().toString() : "";
    }

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
		Payee payee = new Payee();
		payee.setId(NumberUtils.createInteger(text));
	    this.setValue(payee);
    }
	
	
}
