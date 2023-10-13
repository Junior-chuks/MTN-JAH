package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static weshare.model.MoneyHelper.amountOf;

public class AddExpenseController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Map<String, Object> viewModel = Map.of("expenses", expenses);
        context.render("new_expense.html", viewModel);
    };

    public static final Handler addExpense = context -> {
        // Retrieve form data submitted by the user
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        String description = context.formParamAsClass("description", String.class).get();
        LocalDate dateString = LocalDate.parse(Objects.requireNonNull(
                context.formParamAsClass("date",String.class).get()));

        MonetaryAmount amount = amountOf(Integer.parseInt(Objects.requireNonNull(
                context.formParamAsClass("amount",String.class).get()))); // Assuming "amount" is a double

        // Convert the date string to a Date object (you may need to handle date parsing and formatting)
        // Example:
        // Date date = parseDate(dateString);

        // Create a new Expense object with the submitted data
        Expense newExpense = new Expense(personLoggedIn,description, amount,dateString); // Assuming you have an appropriate Expense constructor

        // Add the new Expense to the ExpenseDAO to persist it
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        expensesDAO.save(newExpense);

        // Redirect the user to "expense.html"

        context.redirect("/expenses");
    };

}
