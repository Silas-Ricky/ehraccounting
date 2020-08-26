package org.openmrs.module.ehraccounting.web.controller.bankStatement;

import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.BankAccount;
import org.openmrs.module.ehraccounting.api.model.BankStatement;
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
public class BankStatementController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"),true));
		binder.registerCustomEditor(BankAccount.class, new BankAccountPropertySupport());
	}
	

	@RequestMapping(value="/module/ehraccounting/bankStatement.form",method=RequestMethod.GET)
	public String get(@RequestParam(value="id", required=false) Integer id, Model model) {
		BankStatement command = null;
		if (id == null) {
			command = new BankStatement();
		} else {
			command = Context.getService(EhraccountingService.class).getBankStatement(id);
		}
		model.addAttribute("command",command);
		
		model.addAttribute("bankAccounts",Context.getService(EhraccountingService.class).getListBankAccounts());
		
		return "/module/ehraccounting/bankStatement/bankStatementForm";
	} 
	
	@RequestMapping(value="/module/ehraccounting/bankStatement.form",method=RequestMethod.POST)
	public String post(@ModelAttribute("command") BankStatement bs, BindingResult bindingResult) {
		
		new BankStatementValidator().validate(bs, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/bankStatement/bankStatementForm";
		}
		EhraccountingService as = Context.getService(EhraccountingService.class);
		as.saveBankStatement(bs);
		
		
		return "redirect:/module/ehraccounting/bankStatement.list";
	}
	
	@RequestMapping(value="/module/ehraccounting/bankStatement.list",method=RequestMethod.GET)
	public String list(Model model) {

		List<BankStatement> listStatements = Context.getService(EhraccountingService.class).getListBankStatements();
		
		model.addAttribute("listStatements",listStatements);
		
		return "/module/ehraccounting/bankStatement/bankStatementList";
	}
	
	
}
