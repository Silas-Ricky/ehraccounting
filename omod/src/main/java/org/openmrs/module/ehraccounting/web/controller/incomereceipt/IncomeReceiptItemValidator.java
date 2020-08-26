package org.openmrs.module.ehraccounting.web.controller.incomereceipt;

import org.openmrs.module.ehraccounting.api.model.IncomeReceiptItem;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class IncomeReceiptItemValidator implements Validator{

	@Override
    public boolean supports(Class<?> clazz) {
	    return IncomeReceiptItem.class.equals(clazz);
    }

	@Override
    public void validate(Object obj, Errors e) {
		IncomeReceiptItem item = (IncomeReceiptItem) obj;
		
		if (item.getAccount() == null ) {
			e.reject("accounting.incomeReceiptItem.account.required");
		} 
		
    }
	
}
