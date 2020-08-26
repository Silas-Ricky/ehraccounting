package org.openmrs.module.ehraccounting.web.controller.budget;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.Budget;
import org.openmrs.module.ehraccounting.api.model.BudgetItem;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
@SessionAttributes("budgetCommand")
public class BudgetFormController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"),true));
		binder.registerCustomEditor(Account.class, new AccountPropertySupport());
		binder.registerCustomEditor(Budget.class, new BudgetPropertySupport());
	}
	
	@ModelAttribute("accounts")
	public String registerAccounts() {
		Collection<Account> accounts = Context.getService(EhraccountingService.class).listAccount(AccountType.EXPENSE,false);
		if (accounts != null ) {
			return buildJSONAccounts(new ArrayList<Account>(accounts));
		} else {
			return "";
		}
	}
	
	
	@ModelAttribute("periods")
	public List<FiscalPeriod> registerPeriods() {
		return Context.getService(EhraccountingService.class).getCurrentYearPeriods();
	}
	
	@RequestMapping(value="/module/ehraccounting/budget.form",method=RequestMethod.GET)
	public String get(@RequestParam(value="id", required=false) Integer id, Model model) {
		BudgetCommand command =new BudgetCommand();
		if (id == null) {
			command.setBudget(new Budget());
		} else {
			command.setBudget(Context.getService(EhraccountingService.class).getBudget(id));
		}
		model.addAttribute("budgetCommand",command);
		
		return "/module/accounting/budget/form";
	}
	
	
	@RequestMapping(value="/module/ehraaccounting/budget.form",method=RequestMethod.POST)
	public String post(@ModelAttribute("budgetCommand") BudgetCommand command, BindingResult bindingResult,SessionStatus status) {
		new BudgetValidator().validate(command, bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/budget/form";
		}
		
		if (command.getBudget().getId() == null ) {
			for (BudgetItem item : command.getBudgetItems()) {
				item.setBudget(command.getBudget());
				item.setCreatedBy(command.getBudget().getCreatedBy());
				item.setCreatedDate(command.getBudget().getCreatedDate());
				command.getBudget().addBudgetItem(item);
			}
		}
		Budget budget = null;
		try {
	       budget =  Context.getService(EhraccountingService.class).saveBudget(command.getBudget());
        }
        catch (Exception e) {
	        e.printStackTrace();
        }
		status.setComplete();
		return "redirect:/module/ehraccounting/budget.form?id="+budget.getId();
	}
	
	@RequestMapping(value="/module/ehraccounting/budgetItem.form",method=RequestMethod.POST, params = "action=delete")
	@ResponseBody
	public String postDeleteItem(@RequestParam(value="budgetItemId",required=true) Integer budgetItemId,
	                   @RequestParam("action") String action,
	                   HttpServletRequest request, SessionStatus status) {
		try {
			Context.getService(AccountingService.class).retireBudgetItem(budgetItemId);
            return "success";
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } 
	}
	
	@RequestMapping(value="/module/ehraccounting/budgetItem.form",method=RequestMethod.POST, params = "action=update")
	@ResponseBody
	public String postUpdateItem(@ModelAttribute("budgetItem") BudgetItem item, BindingResult bindingResult) {
		new BudgetItemValidator().validate(item, bindingResult);
		if (bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			StringBuffer result = new StringBuffer();
			for(ObjectError err : errors){
				result.append(err.getDefaultMessage());
			}
			//return "/module/accounting/budget/form";
			return result.toString();
		}
		try {
			 item  =  Context.getService(EhraccountingService.class).saveBudgetItem(item);
			return item.getId().toString();
		} catch(Exception e) {
			 e.printStackTrace();
	         return "error";
		}
	}
	
	
	private String buildJSONAccounts(List<Account> accounts) {
		
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		try {
            mapper.writeValue(writer, accounts);
        }
        catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return writer.toString();
		
	}
	
}
