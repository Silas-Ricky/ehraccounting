/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.ehraccounting.web.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class AccountValidator implements Validator {
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	public boolean supports(Class clazz) {
		return Account.class.equals(clazz);
	}

	/**
	 * @see Validator#validate(java.lang.Object,
	 *      Errors)
	 */
	public void validate(Object cmd, Errors error) {
    	AccountCommand command= (AccountCommand) cmd;
    	Account account = command.getAccount();
    	EhraccountingService ehraccountingService = Context.getService(EhraccountingService.class);
    	
    	if (StringUtils.isBlank(account.getName())) {
    		error.reject("accounting.name.required");
    	} 
    	
    	if (account.getAccountType() == null){
    		error.reject("accounting.type.required");
    	}
    	
    	
    	if (StringUtils.isBlank(account.getAccountNumber())) {
    		error.reject("accounting.accountNumber.required");
    	}
    	
    	if (account.getId() != null) {
    		// UPDATE
    		Account acc = ehraccountingService.getAccountByAccountNumber(account.getAccountNumber());
    		if (acc != null && acc.getId() != account.getId()) {
    			error.reject("accounting.accountNumber.existed");
    		}
    		
    		acc = ehraccountingService.getAccountByNameAndType(account.getName(),account.getAccountType());
    		if (acc != null && acc.getId() != account.getId()) {
    			error.reject("accounting.name.existed");
    		}
    		
    	} else {
    		// ADD NEW
    		/* Dont need this
    		if (command.getPeriod() == null){
        		error.reject("accounting.period.required");
        	}
    		*/
    		// CREATE NEW
    		Account acc = ehraccountingService.getAccountByNameAndType(account.getName(),account.getAccountType());
    		if (acc != null){
    			error.reject("accounting.name.existed");
    		}
    		
    	}
    	
    	
    }
}
