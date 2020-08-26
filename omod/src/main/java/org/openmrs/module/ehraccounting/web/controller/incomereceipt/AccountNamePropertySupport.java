package org.openmrs.module.ehraccounting.web.controller.incomereceipt;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;

import java.beans.PropertyEditorSupport;


public class AccountNamePropertySupport extends PropertyEditorSupport{
	
	// Converts a String to a Account (when submitting form)
    @Override
    public void setAsText(String text) {
    	Account acc = Context.getService(EhraccountingService.class).getAccountByName(text);
        this.setValue(acc);
    }

    // Converts a Account to a String (when displaying form)
    @Override
    public String getAsText() {
    	Account c = (Account) this.getValue();
        return c != null ? c.getName() : "";
    }
	
}
