package org.openmrs.module.ehraccounting.web.controller.budget;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.BudgetItem;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class BudgetItemValidator implements Validator{

	@Override
    public boolean supports(Class<?> arg0) {
	    return arg0.isInstance(BudgetItem.class);
    }

	@Override
    public void validate(Object arg0, Errors error) {
	    BudgetItem item = (BudgetItem) arg0;
	    EhraccountingService service = Context.getService(EhraccountingService.class);
	    if (item.getAccount() == null) {
	    	error.reject("ehraccounting.account.required");
	    } 
	    if ( item.getId() == null) {
		    if (service.isBudgetItemOverlap(item.getAccount().getId(), item.getStartDate(), item.getEndDate())){
			    	error.reject("overlap"," Budget period is overlap");
			}
	    } else if (item.getId() != null) {
	    	// EDIT
//	    	BudgetItem persitedItem = service.getBudgetItem(item.getId());
	    	if (!service.isEditableBudget(item) ) {
	    		error.reject("cannotEdit"," Can not edit this Budget because there are Payments linked");
	    	} 
	    	
	    }
    }
	
}
