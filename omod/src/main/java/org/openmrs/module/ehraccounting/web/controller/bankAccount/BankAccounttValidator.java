package org.openmrs.module.ehraccounting.web.controller.bankAccount;

import org.openmrs.module.ehraccounting.api.model.BankAccount;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class BankAccounttValidator implements Validator{
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class clazz) {
		return BankAccount.class.equals(clazz);
	}


	@Override
    public void validate(Object arg0, Errors arg1) {

    }

	/**
	 * @see Validator#validate(java.lang.Object,
	 *      Errors)
	 */
	
	
}
