package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.*;

import static weshare.model.MoneyHelper.amountOf;

public class paymentRequest {

    public static String expenseId =null;

    public static UUID UIDExpenseId = null;

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        expenseId = context.queryParam("expenseId");
        UIDExpenseId = UUID.fromString(expenseId);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);

        Map<String, Object> viewModel = new HashMap<>();
        Expense expense = null;

        for(Expense personExpense: expenses){
            if(personExpense.getId().equals(UIDExpenseId)){
                expense = personExpense;
            }
        }
        MonetaryAmount Total = total(expense.listOfPaymentRequests());

        viewModel.put("expense",expense);
        viewModel.put("payment_requests",expense.listOfPaymentRequests());
        viewModel.put("netTotal",Total);
        context.render("paymentrequests.html", viewModel);
    };

    public static final Handler submitPaymentRequest = context -> {
        // Retrieve form data submitted by the user
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        Person studentEmail = new Person(context.formParamAsClass("email", String.class).get());
        LocalDate due_date = LocalDate.parse(Objects.requireNonNull(
                context.formParamAsClass("due_date",String.class).get()));

        MonetaryAmount amount = amountOf(Integer.parseInt(Objects.requireNonNull(
                context.formParamAsClass("amount",String.class).get()))); // Assuming "amount" is a double


        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
//
        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Expense expense = null;

        for(Expense personExpense: expenses){
            if(personExpense.getId().equals(UIDExpenseId)){
                expense = personExpense;
            }
        }


        expense.requestPayment(studentEmail,amount,due_date);
        expensesDAO.save(expense);

        context.redirect("/paymentrequest?expenseId="+UIDExpenseId);
    };

    public static MonetaryAmount total(Collection<PaymentRequest> expenses){
        MonetaryAmount amountTotal = amountOf(0);
        for(PaymentRequest amount : expenses){
            amountTotal = amountTotal.add(amount.getAmountToPay());
        }
        return amountTotal;
    };
}
