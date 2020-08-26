package org.openmrs.module.ehraccounting.web.controller.budget;

import org.apache.commons.lang.math.NumberUtils;
import org.openmrs.module.ehraccounting.api.model.Account;

import java.beans.PropertyEditorSupport;


public class AccountPropertySupport extends PropertyEditorSupport{
	
	// Converts a String to a Account (when submitting form)
    @Override
    public void setAsText(String text) {
        Account acc = new Account();
        acc.setId(NumberUtils.createInteger(text));
        this.setValue(acc);
    }

    // Converts a Account to a String (when displaying form)
    @Override
    public String getAsText() {
    	Account c = (Account) this.getValue();
        return c != null ? c.getId().toString() : "";
    }
	
}
