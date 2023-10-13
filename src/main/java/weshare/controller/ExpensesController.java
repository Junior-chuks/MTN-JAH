package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.jetbrains.annotations.NotNull;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static weshare.model.MoneyHelper.ZERO_RANDS;
import static weshare.model.MoneyHelper.amountOf;

public class ExpensesController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);

        Collection<Expense> expenses1 = new HashSet<>();
        Map<String, Object> viewModel = new HashMap<>();
//        System.out.println(expenses.size());
        for(Expense expense: expenses){
            if(!expense.isFullyPaidByOthers()){
                expenses1.add(expense);
            }
        }
//        System.out.println(expenses.size());
        MonetaryAmount Total = total(expenses);

        viewModel.put("expenses",expenses1);
        viewModel.put("netTotal",Total);
        context.render("expenses.html", viewModel);
    };

    public static MonetaryAmount total(Collection<Expense> expenses){
        MonetaryAmount amountTotal = amountOf(0);
        for(Expense amount : expenses){
            amountTotal = amountTotal.add(amount.amountLessPaymentsReceived());
        }
        return amountTotal;
    };
}
