package org.openmrs.module.ehraccounting.web.controller.payment;

import org.openmrs.module.ehraccounting.api.model.Payee;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class PayeeValidator implements Validator {

	@Override
    public boolean supports(Class<?> arg0) {
		return Payee.class.equals(arg0);
    }

	@Override
    public void validate(Object arg0, Errors arg1) {
	    // TODO Auto-generated method stub
	    
    }
	
}
