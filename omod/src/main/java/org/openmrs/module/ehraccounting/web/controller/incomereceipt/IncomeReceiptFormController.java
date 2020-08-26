package org.openmrs.module.ehraccounting.web.controller.incomereceipt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.IncomeReceipt;
import org.openmrs.module.ehraccounting.api.model.IncomeReceiptType;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Controller
@RequestMapping("/module/ehraccounting/incomereceipt.form")
public class IncomeReceiptFormController {
	Log log = LogFactory.getLog(getClass());
	
	@ModelAttribute("itemTypes")
	public IncomeReceiptType[] registerAccountTypes() {
		return IncomeReceiptType.values();
	}
	
	@ModelAttribute("accounts")
	public String registerAccounts() {
		Collection<Account> accounts = Context.getService(EhraccountingService.class).listAccount(AccountType.INCOME,false);
		if (accounts != null ) {
			return buildJSONAccounts(new ArrayList<Account>(accounts));
		} else {
			return "";
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView(@ModelAttribute("incomeReceipt") IncomeReceipt incomeReceipt,
	                        @RequestParam(value = "id", required = false) Integer id, Model model) {
		EhraccountingService service = Context.getService(EhraccountingService.class);
		if (id != null) {
			incomeReceipt = service.getIncomeReceipt(id);
			model.addAttribute(incomeReceipt);
		} else {
			incomeReceipt = new IncomeReceipt();
			model.addAttribute(incomeReceipt);
		}
		
		return "/module/ehraccounting/incomereceipt/form";
	}

	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(IncomeReceipt incomeReceipt, BindingResult bindingResult,
	                       HttpServletRequest request) {
		
		new IncomeReceiptValidator().validate(incomeReceipt, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/incomereceipt/form";
		}
		
		if ( incomeReceipt.getId() == null ) {
			/** Return to form for adding receipt item **/
			incomeReceipt = Context.getService(EhraccountingService.class).saveIncomeReceipt(incomeReceipt);
			return "redirect:/module/ehraccounting/incomereceipt.form?id="+incomeReceipt.getId();
		} else {
			return "redirect:/module/ehraccounting/incomereceipt.list";
		}
		
	}
	
	
	private String buildJSONAccounts(List<Account> accounts) {
		if (accounts == null || accounts.size() == 0)  return null;
		StringBuffer s = new StringBuffer();
		for (Account acc : accounts) {
			s.append(acc.getName()+",");
		}
		s.deleteCharAt(s.length() - 1);
		return s.toString();
	}
	
	
	
}
