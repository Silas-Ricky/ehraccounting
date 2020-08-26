package org.openmrs.module.ehraccounting.web.controller.budget;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Budget;
import org.openmrs.module.ehraccounting.api.model.BudgetItem;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;


public class BudgetValidator implements Validator {

	@Override
    public boolean supports(Class<?> arg0) {
	    return BudgetCommand.class.equals(arg0);
    }

	@Override
    public void validate(Object command, Errors error) {
		BudgetCommand com = (BudgetCommand) command;
		Budget budget = com.getBudget();
		EhraccountingService service = Context.getService(EhraccountingService.class);
		
		if (StringUtils.isBlank(budget.getName())) {
			error.reject("ehraccounting.name.required");
		}
		
		if (budget.getStartDate() == null) {
			error.reject("ehraccounting.startDate.required");
		}
		
		if (budget.getEndDate() == null) {
			error.reject("ehraccounting.endDate.required");
		}
		
		Budget persitedBudget = service.getBudgetByName(budget.getName()); 
		if (budget.getId() == null) {
			if (persitedBudget != null) {
				error.reject("name.invalid","Name is already exist");
			}
		} else {
			// Update
			if (persitedBudget != null && persitedBudget.getId() != budget.getId()) {
				error.reject("name.invalid","Name is already exist");
			}
		}
		
		
		List<BudgetItem> items = com.getBudgetItems();
		if (items != null && !items.isEmpty()) {
			for (BudgetItem item : items ){
				if( service.isBudgetItemOverlap(item.getAccount().getId(), item.getStartDate(), item.getEndDate())) {
					error.reject("budgetItem.overlap","Budget Item period is overlap");
				}
			}
		}
	}
	
}
