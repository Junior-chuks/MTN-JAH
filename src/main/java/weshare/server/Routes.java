package weshare.server;

import weshare.controller.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";

    public static final String PAYMENT_REQUEST_SENT ="/paymentrequests_sent";

    public static final String PAYMENT_REQUEST_RECEIVED ="/paymentrequests_received";

    public static final String NEW_EXPENSE ="/newexpense";

    public static final String EXPENSE_ACTION ="/expense.action";

    public static final String REQUEST_ACTION ="/paymentrequest.action";
    public static final String REQUEST_FORM ="/paymentrequest";
    public static final String PAYMENT_ACTION ="/payment.action";


    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,  PersonController.login);
            get(LOGOUT,         PersonController.logout);
            get(EXPENSES,           ExpensesController.view);
            get(PAYMENT_REQUEST_SENT,           PaymentRequestsSentController.view);
            get(PAYMENT_REQUEST_RECEIVED,           PaymentRequestsReceivedController.view);
            post(EXPENSE_ACTION,        AddExpenseController.addExpense);
            get(NEW_EXPENSE,           AddExpenseController.view);
            get(REQUEST_FORM,           paymentRequest.view);
            post(REQUEST_ACTION,           paymentRequest.submitPaymentRequest);
            post(PAYMENT_ACTION,           paymentController.pay);

        });
    }
}
