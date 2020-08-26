package org.openmrs.module.ehraccounting.web.controller.bankStatement;

import org.openmrs.module.ehraccounting.api.model.BankStatement;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class BankStatementValidator implements Validator{
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class clazz) {
		return BankStatement.class.equals(clazz);
	}


	@Override
    public void validate(Object arg0, Errors arg1) {

    }

	/**
	 * @see Validator#validate(java.lang.Object,
	 *      Errors)
	 */
	
	
}
