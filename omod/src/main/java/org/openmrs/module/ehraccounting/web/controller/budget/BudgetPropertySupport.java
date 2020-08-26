package org.openmrs.module.ehraccounting.web.controller.budget;

import org.apache.commons.lang3.math.NumberUtils;
import org.openmrs.module.ehraccounting.api.model.Budget;

import java.beans.PropertyEditorSupport;


public class BudgetPropertySupport extends PropertyEditorSupport {

	@Override
    public String getAsText() {
		Budget budget = (Budget) this.getValue();
	    return budget != null ? budget.getName() : "";
    }

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
		Budget budget = new Budget();
		budget.setId(NumberUtils.createInteger(text));
	    this.setValue(budget);
    }
	
	
}
