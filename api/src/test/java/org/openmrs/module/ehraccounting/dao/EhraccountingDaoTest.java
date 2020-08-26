/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ehraccounting.api.dao;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ehraccounting.api.db.EhraccountingDAO;
import org.openmrs.module.ehraccounting.api.model.Account;
import org.openmrs.module.ehraccounting.api.model.AccountType;
import org.openmrs.module.ehraccounting.api.model.BalanceStatus;
import org.openmrs.module.ehraccounting.api.model.Budget;
import org.openmrs.module.ehraccounting.api.model.BudgetItem;
import org.openmrs.module.ehraccounting.api.model.FiscalPeriod;
import org.openmrs.module.ehraccounting.api.model.FiscalYear;
import org.openmrs.module.ehraccounting.api.model.GeneralStatus;
import org.openmrs.module.ehraccounting.api.model.IncomeBalance;
import org.openmrs.module.ehraccounting.api.model.IncomeReceipt;
import org.openmrs.module.ehraccounting.api.model.IncomeReceiptItem;
import org.openmrs.module.ehraccounting.api.utils.DateUtils;
import org.openmrs.module.ehraccounting.Item;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * It is an integration test (extends BaseModuleContextSensitiveTest), which verifies DAO methods
 * against the in-memory H2 database. The database is initially loaded with data from
 * standardTestDataset.xml in openmrs-api. All test methods are executed in transactions, which are
 * rolled back by the end of each test method.
 */
public class EhraccountingDaoTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	EhraccountingDao dao;
	
	@Autowired
	UserService userService;
	
	@Test
	@Ignore("Unignore if you want to make the Item class persistable, see also Item and liquibase.xml")
	public void shouldSaveAccount() throws Exception {
		Account acc = new Account("Account 1");
		acc.setAccountType(AccountType.INCOME);
		acc.setCreatedDate(Calendar.getInstance().getTime());
		acc = dao.saveAccount(acc);
		Context.flushSession();
		Context.clearSession();

		Account persitedAccount = dao.getAccount(acc.getId());
		Assert.assertNotNull(persitedAccount);

	}

	@Test
	public void shouldSaveFiscalYear() throws Exception {
		FiscalYear fy = new FiscalYear("Fiscal Year 1",Calendar.getInstance().getTime(),1);
		fy = dao.saveFiscalYear(fy);
		Context.flushSession();
		Context.clearSession();

		FiscalYear persitedFiscalYear = dao.getFiscalYear(fy.getId());
		Assert.assertNotNull(persitedFiscalYear);
	}

	@Test
	public void shouldSaveFiscalPeriod() throws Exception {
		FiscalYear fy = new FiscalYear("Fiscal Year 1",Calendar.getInstance().getTime(),1);
		fy = dao.saveFiscalYear(fy);
		Context.flushSession();
		Context.clearSession();

		FiscalYear persitedFiscalYear = dao.getFiscalYear(fy.getId());
		Assert.assertNotNull(persitedFiscalYear);

		FiscalPeriod fp = new FiscalPeriod("Q1", Calendar.getInstance().getTime(), 1);
		fp.setFiscalYear(fy);
		fp = dao.saveFiscalPeriod(fp);
		Context.flushSession();
		Context.clearSession();
		FiscalPeriod persitedFiscalPeriod = dao.getFiscalPeriod(fp.getId());
		Assert.assertNotNull(persitedFiscalPeriod);
	}

	@Test
	public void shouldSaveAccountPeriod() throws Exception{
		/**
		 * Create Account
		 */
		Account acc = new Account("Account 1");
		acc.setAccountType(AccountType.EXPENSE);
		acc.setCreatedDate(Calendar.getInstance().getTime());

		acc = dao.saveAccount(acc);
		Context.flushSession();
		Context.clearSession();

		Account persitedAccount = dao.getAccount(acc.getId());
		Assert.assertNotNull(persitedAccount);

		/**
		 * Create Fiscal Year
		 */
		FiscalYear fy = new FiscalYear("Fiscal Year 1",Calendar.getInstance().getTime(),1);
		fy = dao.saveFiscalYear(fy);
		Context.flushSession();
		Context.clearSession();

		FiscalYear persitedFiscalYear = dao.getFiscalYear(fy.getId());
		Assert.assertNotNull(persitedFiscalYear);


		/**
		 * Create FiscalPeriod
		 */
		FiscalPeriod fp = new FiscalPeriod("Q1", Calendar.getInstance().getTime(), 1);
		fp.setFiscalYear(fy);
		fp = dao.saveFiscalPeriod(fp);
		Context.flushSession();
		Context.clearSession();
		FiscalPeriod persitedFiscalPeriod = dao.getFiscalPeriod(fp.getId());
		Assert.assertNotNull(persitedFiscalPeriod);

		/**
		 * Create Account Period
		 */
		IncomeBalance ap = new IncomeBalance();
		ap.setAccount(acc);
		ap.setCreatedBy(1);
		ap.setCreatedDate(Calendar.getInstance().getTime());
		ap.setPeriod(fp);
		ap.setStatus(BalanceStatus.ACTIVE);
		ap = dao.saveAccountBalance(ap);

		Context.flushSession();
		Context.clearSession();

		Assert.assertNotNull(ap);
		IncomeBalance persitedAP = dao.getAccountBalance(ap.getId());
		Assert.assertNotNull(persitedAP);

	}

	@Test
	public void shouldGetIncomeReceipt() throws Exception{

		Date curDate = Calendar.getInstance().getTime();

		IncomeReceipt receipt = new IncomeReceipt();
		receipt.setCreatedDate(curDate);
		receipt.setCreatedBy(1);
		receipt.setDescription("testing");
		receipt.setReceiptDate(DateUtils.getDateFromStr("21/05/2014"));
		receipt = dao.saveIncomeReceipt(receipt);
		Context.flushSession();
		Context.clearSession();

		IncomeReceipt persitedReceipt = dao.getIncomeReceipt(receipt.getId());
		Assert.assertNotNull(persitedReceipt);

		IncomeReceipt receipt2 = new IncomeReceipt();
		receipt2.setCreatedDate(curDate);
		receipt2.setCreatedBy(1);
		receipt2.setDescription("testing");
		receipt2.setReceiptDate(DateUtils.getDateFromStr("22/05/2014"));
		receipt2 = dao.saveIncomeReceipt(receipt2);

		Context.flushSession();
		Context.clearSession();

		IncomeReceipt persitedReceipt2 = dao.getIncomeReceipt(receipt2.getId());
		Assert.assertNotNull(persitedReceipt2);

		List<IncomeReceipt> list = dao.getListIncomeReceiptByDate("21/05/2014", "22/05/2014", true);
		List<IncomeReceipt> list2 = dao.getListIncomeReceipt(true);
		Assert.assertNotNull(list);
		Assert.assertNotNull(list2);
		Assert.assertEquals(2,list.size());
		Assert.assertEquals(2,list2.size());

	}


	@Test
	public void shouldSaveBudget() {
		Date curDate = Calendar.getInstance().getTime();

		/**
		 * Create Account
		 */
		Account acc = new Account("Account 1");
		acc.setAccountType(AccountType.EXPENSE);
		acc.setCreatedDate(curDate);
		acc = dao.saveAccount(acc);
		Context.flushSession();
		Context.clearSession();


		Budget budget = new Budget();
		budget.setCreatedBy(1);
		budget.setCreatedDate(curDate);
		budget.setDescription("test");
		budget.setName("test");

		BudgetItem item1 = new BudgetItem();
		item1.setAccount(acc);
		item1.setBudget(budget);
		item1.setAmount(new BigDecimal("1000.00"));
		item1.setCreatedBy(1);
		item1.setCreatedDate(curDate);
		budget.addBudgetItem(item1);

		BudgetItem item2 = new BudgetItem();
		item2.setAccount(acc);
		item2.setBudget(budget);
		item2.setAmount(new BigDecimal("2000.00"));
		item2.setCreatedBy(1);
		item2.setCreatedDate(curDate);
		budget.addBudgetItem(item2);



		Budget persitedBudget = dao.saveBudget(budget);

		Context.flushSession();
		Context.clearSession();

		Assert.assertNotNull(persitedBudget);

	}

	@Test
	public void shouldGetAccountBudget(){
		Date curDate = Calendar.getInstance().getTime();
		Account acc = new Account("Account 1");
		acc.setAccountType(AccountType.EXPENSE);
		acc.setCreatedDate(curDate);
		acc = dao.saveAccount(acc);
		Context.flushSession();
		Context.clearSession();

		Date from = DateUtils.getDateFromStr("1/6/2014");
		Date to = DateUtils.getDateFromStr("30/6/2014");

		Budget budget = new Budget();
		budget.setCreatedBy(1);
		budget.setCreatedDate(curDate);
		budget.setDescription("test");
		budget.setName("test");

		BudgetItem item1 = new BudgetItem();
		item1.setAccount(acc);
		item1.setBudget(budget);
		item1.setAmount(new BigDecimal("1000.00"));
		item1.setCreatedBy(1);
		item1.setCreatedDate(curDate);
		item1.setStartDate(from);
		item1.setEndDate(to);
		budget.addBudgetItem(item1);


		//from = DateUtils.getDateFromStr("1/7/2014");
		// to = DateUtils.getDateFromStr("31/7/2014");



		Budget persitedBudget = dao.saveBudget(budget);

		Context.flushSession();
		Context.clearSession();

		BudgetItem item2 = new BudgetItem();
		item2.setAccount(acc);
		item2.setBudget(budget);
		item2.setAmount(new BigDecimal("2000.00"));
		item2.setCreatedBy(1);
		item2.setCreatedDate(curDate);
		item1.setStartDate(from);
		item1.setEndDate(to);
		//budget.addBudgetItem(item2);

		System.out.println(dao.isBudgetItemOverlap(acc, from, to));
		Assert.assertTrue(dao.isBudgetItemOverlap(acc, from, to));
//		BudgetItem result = da÷o.getB÷÷udgetItem(DateUtils.getDateFromStr("31/7/2014"),acc);
//		System.out.println÷("========"+result);


	}

	@Test
	public void shouldAggregateIncomeReceipt() {
		Date curDate = Calendar.getInstance().getTime();
		Account acc = new Account("Account 1");
		acc.setAccountType(AccountType.EXPENSE);
		acc.setCreatedDate(curDate);
		acc = dao.saveAccount(acc);
		Context.flushSession();
		Context.clearSession();

		Account acc2 = new Account("Account 1");
		acc2.setAccountType(AccountType.EXPENSE);
		acc2.setCreatedDate(curDate);
		acc2 = dao.saveAccount(acc);
		Context.flushSession();
		Context.clearSession();

		IncomeReceipt receipt = new IncomeReceipt();
		receipt.setCreatedDate(curDate);
		receipt.setCreatedBy(1);
		receipt.setDescription("testing");
		receipt.setReceiptDate(curDate);
		receipt = dao.saveIncomeReceipt(receipt);
		Context.flushSession();
		Context.clearSession();

		IncomeReceiptItem item = new IncomeReceiptItem();
		item.setAccount(acc);
		item.setReceipt(receipt);
		item.setCreatedBy(1);
		item.setCreatedDate(curDate);
		item.setAmount(new BigDecimal("100"));
		item = dao.saveIncomeReceiptItem(item);
		Context.flushSession();
		Context.clearSession();

		IncomeReceiptItem item2 = new IncomeReceiptItem();
		item2.setAccount(acc2);
		item2.setReceipt(receipt);
		item2.setCreatedBy(1);
		item2.setCreatedDate(curDate);
		item2.setAmount(new BigDecimal("100"));
		item2 = dao.saveIncomeReceiptItem(item);
		Context.flushSession();
		Context.clearSession();

		Date from = DateUtils.getDateFromStr("1/7/2014");
		Date to = DateUtils.getDateFromStr("30/7/2014");
		Map<Integer, Object[]> result = dao.aggregateIncomeReceiptItem(from, to);
		System.out.println(result);
		Assert.assertNotEquals(result.values().size(), 2);
	}

	@Test
	public void shouldBeOverlapFiscalYear(){
		FiscalYear fy = new FiscalYear("Fiscal Year 1",Calendar.getInstance().getTime(),1);

		fy.setStartDate(DateUtils.getDateFromStr("1/1/2014"));
		fy.setEndDate(DateUtils.getDateFromStr("31/12/2014"));
		fy.setStatus(GeneralStatus.ACTIVE);
		fy = dao.saveFiscalYear(fy);

		Context.flushSession();
		Context.clearSession();

		Date from = DateUtils.getDateFromStr("1/6/2013");
		Date to = DateUtils.getDateFromStr("1/6/2014");
		List<FiscalYear> list = dao.getOverlapFiscalYears(fy.getId(),from, to);
		boolean flag = false;;
		for (FiscalYear year : list) {
			if (year.getId().equals(fy.getId()) && year.getStartDate().compareTo(from) == 0
					&& year.getEndDate().compareTo(to) == 0 ) {
				continue;
			} else {
				flag = true;
			}
		}
		Assert.assertTrue(flag);
	}
}