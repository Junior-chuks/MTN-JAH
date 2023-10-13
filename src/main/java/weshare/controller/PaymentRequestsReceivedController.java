package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static weshare.model.MoneyHelper.amountOf;

public class PaymentRequestsReceivedController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> requests_received = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        Map<String, Object> viewModel = new HashMap<>();
        MonetaryAmount Total = total(requests_received);
        viewModel.put("requests_received",requests_received);
        viewModel.put("unpaidTotal",Total);
        context.render("paymentrequests_received.html", viewModel);
    };

    public static MonetaryAmount total(Collection<PaymentRequest> requests_received){
        MonetaryAmount amountTotal = amountOf(0);
        for(PaymentRequest amount : requests_received){
            amountTotal = amountTotal.add(amount.getAmountToPay());
        }
        return amountTotal;
    };
}
