package org.openmrs.module.ehraccounting.web.controller.payment;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.ExpenseBalance;
import org.openmrs.module.ehraccounting.api.model.Payee;
import org.openmrs.module.ehraccounting.api.model.Payment;
import org.openmrs.module.ehraccounting.api.model.PaymentStatus;
import org.openmrs.module.ehraccounting.api.utils.DateUtils;
import org.openmrs.module.ehraccounting.web.controller.budget.AccountPropertySupport;
import org.openmrs.module.hospitalcore.util.PagingUtil;
import org.openmrs.module.hospitalcore.util.RequestUtil;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@SessionAttributes("payment")
public class PaymentController {
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"),true));
		binder.registerCustomEditor(Account.class, new AccountPropertySupport());
		binder.registerCustomEditor(Payee.class, new PayeePropertySupport());
	}
	
	@ModelAttribute("listPayees")
	public String registerPayee() {
		List<Payee> listPayees = Context.getService(EhraccountingService.class).listActivePayees();
		if (listPayees != null ) {
			return buildJSON(listPayees);
		} else {
			return "";
		}
	}
	
	
	@ModelAttribute("accounts")
	public String registerAccounts() {
		List<Account> accounts = Context.getService(EhraccountingService.class).listAccount(AccountType.EXPENSE,false);
		if (accounts != null ) {
			return buildJSONAccounts(accounts);
		} else {
			return "";
		}
	}
	
	
	
	
	@RequestMapping("/module/ehraccounting/payment.list")
	public String showPaymentList(  @RequestParam(value="accountId",required=false) Integer accountId, 
	                                @RequestParam(value="pageSize",required=false) Integer pageSize,
	                                @RequestParam(value="currentPage",required=false) Integer currentPage,
	                                Model model,HttpServletRequest request){
		EhraccountingService service = Context.getService(EhraccountingService.class);
		if (accountId != null) {
			Account account = service.getAccount(accountId);
			int count = service.countListPaymentsByAccount(account);
			
			PagingUtil pagingUtil = new PagingUtil( RequestUtil.getCurrentLink(request) , pageSize, currentPage, count );
			
			List<Payment> payments = Context.getService(EhraccountingService.class).listPaymentsByAccount(account, pagingUtil.getStartPos(), pagingUtil.getPageSize());
			model.addAttribute("payments",payments);
			model.addAttribute("pagingUtil",pagingUtil);
		} else {
			int count = service.countAllPayments();
			PagingUtil pagingUtil = new PagingUtil( RequestUtil.getCurrentLink(request) , pageSize, currentPage, count );
			List<Payment> payments = Context.getService(EhraccountingService.class).listAllPayments(pagingUtil.getStartPos(), pagingUtil.getPageSize());
			model.addAttribute("payments",payments);
			model.addAttribute("pagingUtil",pagingUtil);
		}
		
		List<Account> accounts = service.listAccount(AccountType.EXPENSE, false);
		model.addAttribute("accounts", accounts);
		model.addAttribute("accountId", accountId);
		
		return "/module/ehraccounting/payment/listPayment";
	}
	
	// show add payment from
	@RequestMapping(value="/module/ehraccounting/payment.form", method=RequestMethod.GET)
	public String showPaymentForm(@RequestParam(value="id", required=false) Integer id
	                             , Model model) {
		Payment payment = null;
		if (id == null) {
			payment = new Payment();
			PaymentStatus[] statues = { PaymentStatus.COMMITTED,PaymentStatus.PAID};
			model.addAttribute("paymentStatuses",statues);
		} else {
			payment = Context.getService(EhraccountingService.class).getPayment(id);
			if (payment.getStatus().equals(PaymentStatus.COMMITTED)) {
				PaymentStatus[] statues = {PaymentStatus.COMMITTED,PaymentStatus.PAID, PaymentStatus.DELETED};
				model.addAttribute("paymentStatuses",statues);
			}
			
			if (payment.getStatus().equals(PaymentStatus.PAID)) {
				PaymentStatus[] statues = {PaymentStatus.PAID,PaymentStatus.DELETED};
				model.addAttribute("paymentStatuses",statues);
			} 
			
			ExpenseBalance balance = Context.getService(EhraccountingService.class).findExpenseBalance(payment.getAccount().getId(), payment.getPaymentDate());
			if (balance != null) {
				model.addAttribute("accountBudget",balance.getAvailableBalance().toString());
			} 
		}
		
		model.addAttribute("payment",payment);
		
		
		return "/module/ehraccounting/payment/paymentForm";
	}
	
	
	
	@RequestMapping(value="/module/ehraccounting/payment.form", method=RequestMethod.POST)
	public String postPaymentForm(@ModelAttribute("payment") Payment payment, BindingResult bindingResult, Model model) throws Exception {
		
		new PaymentValidator().validate(payment, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("erors",bindingResult.getAllErrors());
			return "/module/ehraccounting/payment/paymentForm";
		}
		Context.getService(EhraccountingService.class).savePayment(payment);
		return "redirect:/module/ehraccounting/payment.list";
	}
	
	@RequestMapping(value="/module/ehraccounting/getPaymentAIE.htm", method=RequestMethod.GET)
	@ResponseBody
	public String getPaymentAIE(@RequestParam("accountId") Integer accountId, @RequestParam("paymentDate") String paymentDate) {
		Date date = DateUtils.getDateFromStr(paymentDate);
//		BudgetItem item = Context.getService(AccountingService.class).getBudgetItem(accountId, date);
		ExpenseBalance balance = Context.getService(AccountingService.class).findExpenseBalance(accountId, date);
		if (balance != null) {
			return balance.getAvailableBalance().toString();
		} else {
			return "NA";
		}
	}
	private String buildJSON(List<Payee> payees) {
		
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		try {
            mapper.writeValue(writer, payees);
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
