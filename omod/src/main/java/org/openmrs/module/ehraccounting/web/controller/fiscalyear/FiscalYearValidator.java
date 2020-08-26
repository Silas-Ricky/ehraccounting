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

package org.openmrs.module.ehraccounting.web.controller.fiscalyear;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.FiscalYear;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 */
public class FiscalYearValidator implements Validator {
	
	/**
	 * @see Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
    public boolean supports(Class clazz) {
		return FiscalYear.class.equals(clazz);
	}

	/**
	 * @see Validator#validate(java.lang.Object,
	 *      Errors)
	 */
	public void validate(Object obj, Errors error) {
		FiscalYear fiscalYear = (FiscalYear) obj;
		EhraccountingService ehraccountingService = (EhraccountingService) Context.getService(EhraccountingService.class);
		
		if (StringUtils.isBlank(fiscalYear.getName())) {
			error.reject("accounting.name.required");
		} 
		
		if (fiscalYear.getStatus() == null) {
			error.reject("accounting.type.required");
		}
		
		if (fiscalYear.getStartDate() == null) {
			error.reject("accounting.startDate.required");
		}
		
		if (fiscalYear.getEndDate() == null) {
			error.reject("accounting.endDate.required");
		}
		
		if (ehraccountingService.isOverlapFiscalYear(fiscalYear.getId(), fiscalYear.getStartDate(), fiscalYear.getEndDate())) {
			error.reject("accounting.overlap");
		}
		
		if (fiscalYear.getId() != null) {

			FiscalYear year = ehraccountingService.getFiscalYearByName(fiscalYear.getName());
			if (year != null && !year.getId().equals(fiscalYear.getId())){
				error.reject("accounting.name.exisited");
			}
		
		} else {
			// ADD NEW
			/* as above
			if (fiscalYear.getStatus().equals(GeneralStatus.ACTIVE)) {
				FiscalYear year = accountingService.getActiveFiscalYear() ;
				if (year != null ) {
					// Only one active fiscal year allow
					error.reject("accounting.active.exisited");
				}
			}*/
		}
	}
}
