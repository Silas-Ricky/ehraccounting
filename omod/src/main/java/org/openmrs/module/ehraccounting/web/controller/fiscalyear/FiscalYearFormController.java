package org.openmrs.module.ehraccounting.web.controller.fiscalyear;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.EhraccountingService;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;
import org.openmrs.module.ehraccounting.api.model.FiscalYear;
import org.openmrs.module.ehraccounting.api.model.GeneralStatus;
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
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/module/ehraccounting/fiscalyear.form")
//@SessionAttributes("command")
public class FiscalYearFormController {
	
	Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(java.lang.Boolean.class, new CustomBooleanEditor("true", "false", true));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String firstView(@RequestParam(value = "id", required = false) Integer id, Model model) {
		FiscalYearCommand fiscalYearCommand = new FiscalYearCommand();
		boolean editable = true;
		if (id != null) {
			FiscalYear fy = Context.getService(EhraccountingService.class).getFiscalYear(id);
			fiscalYearCommand.setFiscalYear(fy);
			model.addAttribute("listStatus", getStatues(fy.getStatus()));
			if (fy.getStatus().equals(GeneralStatus.DELETED)) {
				editable = false;
			}
		} else {
			fiscalYearCommand = new FiscalYearCommand();
			fiscalYearCommand.setFiscalYear(new FiscalYear());
			model.addAttribute("listStatus", getStatues(null));
		}
		model.addAttribute("command", fiscalYearCommand);
		
		model.addAttribute("editable", editable);
		return "/module/ehraccounting/fiscalyear/form";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(@ModelAttribute("command") FiscalYearCommand fiscalYearCommand, BindingResult bindingResult,
	                      HttpServletRequest request, SessionStatus status) {
		
		new FiscalYearValidator().validate(fiscalYearCommand.getFiscalYear(), bindingResult);
		if (bindingResult.hasErrors()) {
			return "/module/ehraccounting/fiscalyear/form";
		}
		
		EhraccountingService ehraccountingService = Context.getService(EhraccountingService.class);
		FiscalYear year = fiscalYearCommand.getFiscalYear();
		if (year.getId() == null ) {
			// Add new FiscalYear
			Date curDate = Calendar.getInstance().getTime();
			year.setCreatedDate(curDate);
			year.setCreatedBy(Context.getAuthenticatedUser().getId());
			for (FiscalPeriod period : fiscalYearCommand.getPeriods()) {
				period.setFiscalYear(year);
				period.setCreatedBy(year.getCreatedBy());
				period.setCreatedDate(year.getCreatedDate());
				period.setStatus(year.getStatus());
				year.addPeriod(period);
			}
			 ehraccountingService.saveFiscalYear(year);
		} else {
			// Update Fiscal Year
			FiscalYear persistedYear = ehraccountingService.getFiscalYear(year.getId());
			persistedYear.setName(year.getName());
			persistedYear.setStartDate(year.getStartDate());
			persistedYear.setEndDate(year.getEndDate());
			persistedYear.setStatus(year.getStatus());
			persistedYear.getPeriods().clear();
			for (FiscalPeriod period : fiscalYearCommand.getPeriods()) {
				period.setFiscalYear(persistedYear);
				period.setCreatedBy(persistedYear.getCreatedBy());
				period.setCreatedDate(persistedYear.getCreatedDate());
				period.setStatus(persistedYear.getStatus());
				persistedYear.addPeriod(period);
			}
			persistedYear.setUpdatedBy(Context.getAuthenticatedUser().getId());
			persistedYear.setUpdatedDate(Calendar.getInstance().getTime());
			ehraccountingService.saveFiscalYear(persistedYear);
		}
		
		
		
		
		
		
		
		/*
		if (StringUtils.isEmpty(jsonPeriods))  {
			System.out.println("===================json period == null return to list ");
			log.error("empty");
			status.setComplete();
			return "redirect:/module/accounting/fiscalyear.list";
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory f = new JsonFactory();
		JsonParser jp;
		try {
			log.error("pasrings");
			System.out.println("===================json period != parse list ");
			jp = f.createJsonParser(jsonPeriods);
			jp.nextToken();
			Date curDate = Calendar.getInstance().getTime();
			int userId = Context.getAuthenticatedUser().getId();
			while (jp.nextToken() == JsonToken.START_OBJECT) {
				FiscalPeriod fp = mapper.readValue(jp, FiscalPeriod.class);
				fp.setCreatedBy(userId);
				fp.setCreatedDate(curDate);
				fp.setFiscalYear(fy);
				fp.setStatus(GeneralStatus.INACTIVE);
				Context.getService(AccountingService.class).saveFiscalPeriod(fp);
			}
		}
		catch (JsonParseException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		*/
		status.setComplete();
		return "redirect:/module/accounting/fiscalyear.list";
	}
	
	
	private GeneralStatus[] getStatues(GeneralStatus status){
		if (status == null) {
			// Create new
			GeneralStatus[] statues = new GeneralStatus[1];
			statues[0] = GeneralStatus.OPEN;
			return statues;
		} else if (status.equals(GeneralStatus.OPEN)) {
			GeneralStatus[] statues = new GeneralStatus[2];
			statues[0] = GeneralStatus.OPEN;
			statues[1] = GeneralStatus.CLOSED;
			return statues;
		} else if (status.equals(GeneralStatus.DELETED)) {
			GeneralStatus[] statues = new GeneralStatus[1];
			statues[0] = GeneralStatus.DELETED;
			return statues;
		} else if (status.equals(GeneralStatus.CLOSED)) {
			GeneralStatus[] statues = new GeneralStatus[2];
			statues[0] = GeneralStatus.CLOSED;
			statues[1] = GeneralStatus.OPEN;
			return statues;
		} else return null;
		
	}
}
