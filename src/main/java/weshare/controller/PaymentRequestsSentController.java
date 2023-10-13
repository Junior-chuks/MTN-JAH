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

public class PaymentRequestsSentController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> requests_sent = expensesDAO.findPaymentRequestsSent(personLoggedIn);
        Map<String, Object> viewModel = new HashMap<>();
        MonetaryAmount Total = total(requests_sent);
        viewModel.put("requests_sent",requests_sent);
        viewModel.put("unpaidTotal",Total);
        context.render("paymentrequests_sent.html", viewModel);
    };

    public static MonetaryAmount total(Collection<PaymentRequest> requests_sent){
        MonetaryAmount amountTotal = amountOf(0);
        for(PaymentRequest amount : requests_sent){
            amountTotal = amountTotal.add(amount.getAmountToPay());
        }
        return amountTotal;
    };
}
