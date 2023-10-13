package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Payment;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class paymentController {

    public static final Handler pay = context -> {
        // Retrieve form data submitted by the user
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        UUID expenseId = UUID.fromString(context.formParamAsClass("expenseId", String.class).get());
        Person personPaying = new Person(Objects.requireNonNull(
                context.formParamAsClass("personPaying",String.class).get()));

        LocalDate date = LocalDate.parse(Objects.requireNonNull(
                context.formParamAsClass("date",String.class).get())); // Assuming "amount" is a double

        Payment thePayment = null;
        // Add the new Expense to the ExpenseDAO to persist it
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);

        // Create a new Expense object with the submitted data
        Collection<PaymentRequest> allPaymentRequestReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);; // Assuming you have an appropriate Expense constructor

        for( PaymentRequest expenseToPay: allPaymentRequestReceived){
            if(expenseToPay.getId().equals(expenseId)){
                thePayment = expenseToPay.pay(personPaying,LocalDate.now());

            }
        }
        expensesDAO.save(thePayment.getExpenseForPersonPaying());

        // Redirect the user to "expense.html"

        context.redirect("/paymentrequests_received");
    };
}
