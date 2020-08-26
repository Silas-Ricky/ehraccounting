package org.openmrs.module.ehraccounting.web.controller.bankAccount;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.BankAccount;
import org.openmrs.module.ehraccounting.api.model.Budget;
import org.openmrs.module.ehraccounting.web.controller.budget.AccountPropertySupport;
import org.openmrs.module.ehraccounting.web.controller.budget.BudgetPropertySupport;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@SessionAttributes("command")
public class BankAccountController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"),true));
		binder.registerCustomEditor(Account.class, new AccountPropertySupport());
		binder.registerCustomEditor(Budget.class, new BudgetPropertySupport());
	}
	

	@RequestMapping(value="/module/ehraccounting/bankAccount.form",method=RequestMethod.GET)
	public String get(@RequestParam(value="id", required=false) Integer id, Model model) {
		BankAccount command = null;
		if (id == null) {
			command = new BankAccount();
		} else {
			command = Context.getService(EhraccountingService.class).getBankAccount(id);
		}
		model.addAttribute("command",command);
		
		model.addAttribute("bankAccounts",Context.getService(EhraccountingService.class).getListBankAccounts());
		
		return "/module/ehraccounting/bankAccount/bankAccountForm";
	} 
	
	@RequestMapping(value="/module/ehraccounting/bankAccount.form",method=RequestMethod.POST)
	public String post(@ModelAttribute("command") BankAccount bs, BindingResult bindingResult) {
		
		new BankAccounttValidator().validate(bs, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/bankAccount/bankAccountForm";
		}
		EhraccountingService as = Context.getService(EhraccountingService.class);
		as.saveBankAccount(bs);
		
		
		return "redirect:/module/ehraccounting/bankAccount.list";
	}
	
	@RequestMapping(value="/module/ehraccounting/bankAccount.list",method=RequestMethod.GET)
	public String list(Model model) {

		List<BankAccount> listAccounts = Context.getService(EhraccountingService.class).getListBankAccounts();
		
		model.addAttribute("listAccounts",listAccounts);
		
		return "/module/ehraccounting/bankAccount/bankAccountList";
	}
	
	
}
