package com.bloatit.framework;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bloatit.common.FatalErrorException;
import com.bloatit.common.PageIterable;
import com.bloatit.common.UnauthorizedOperationException;
import com.bloatit.model.data.DaoBankTransaction;
import com.experian.payline.ws.impl.DoWebPaymentRequest;
import com.experian.payline.ws.impl.DoWebPaymentResponse;
import com.experian.payline.ws.impl.GetWebPaymentDetailsRequest;
import com.experian.payline.ws.impl.WebPaymentAPI_Service;
import com.experian.payline.ws.obj.Order;
import com.experian.payline.ws.obj.Payment;
import com.experian.payline.ws.obj.Result;

public class Payline extends Unlockable {

    private static final String ACCEPTED_CODE = "00000";
    private static final String ORDER_ORIGINE = "payline";
    /**
     * 978 : euros 840 : dollars US
     */
    private static final String CURRENCY = "978";
    // Numero de contrat de vente à distance (VAD) found on payline administration page.
    private static final String CONTRACT_NUMBER = "42";
    // immediate payment: Action = 101; mode "CPT"
    private static final String MODE = "CPT";
    private static final String ACTION = "101";

    public static class Reponse {
        private final String code;
        private final String token;
        private final String message;
        private final String redirectUrl;

        Reponse(DoWebPaymentResponse reponse) {
            code = reponse.getResult().getCode();
            token = reponse.getToken();
            message = reponse.getResult().getShortMessage() + "\n" + reponse.getResult().getLongMessage();
            this.redirectUrl = reponse.getRedirectURL();
        }

        Reponse(Result result, String token) {
            code = result.getCode();
            this.token = token;
            message = result.getShortMessage() + "\n" + result.getLongMessage();
            this.redirectUrl = "";
        }

        public final String getToken() {
            return token;
        }

        public final String getMessage() {
            return message;
        }

        public final String getCode() {
            return code;
        }

        public boolean isAccepted() {
            return code.equals(ACCEPTED_CODE);
        }

        public final String getRedirectUrl() {
            return redirectUrl;
        }
    }

    public static class TokenNotfoundException extends Exception {
        private static final long serialVersionUID = 4891304798361361776L;

        public TokenNotfoundException() {
            super();
        }

        public TokenNotfoundException(String message) {
            super(message);
        }
    }

    public Payline() {
        // identifiant commerçant : 54652391742591
        // Votre clé d’accès au service Payline : ar6NsH8gOFdAxFXnm568
        // Les certificats serveur Payline* : homologation et production
        // Votre ou vos contrats de vente à distance : 1234567
        // Votre ou vos contrats de vente à distance : 987654
        // Votre ou vos contrats de vente à distance : 42
    }

    public boolean canMakePayment() {
        return getAuthToken() != null;
    }

    public void validatePayment(String token) throws TokenNotfoundException {
        BankTransaction transaction = BankTransaction.getByToken(token);
        if (transaction != null) {
            transaction.authenticate(getAuthToken());
            if (!transaction.setValidated()) {
                throw new TokenNotfoundException("Cannot validate the BankTransaction.");
            }
        }
        throw new TokenNotfoundException("Token is not found in DB: " + token);
    }

    public Reponse getPaymentDetails(String token) throws TokenNotfoundException {
        BankTransaction transaction = BankTransaction.getByToken(token);
        if (transaction == null) {
            throw new TokenNotfoundException("Token is not found in DB: " + token);
        }

        WebPaymentAPI_Service paylineApi = new WebPaymentAPI_Service();
        GetWebPaymentDetailsRequest parameters = new GetWebPaymentDetailsRequest();
        parameters.setToken(token);
        Result result = paylineApi.getWebPaymentAPI().getWebPaymentDetails(parameters).getResult();
        return new Reponse(result, token);
    }

    public Reponse doPayment(BigDecimal amount, String cancelUrl, String returnUrl, String notificationUrl) {
        DoWebPaymentRequest paymentRequest = new DoWebPaymentRequest();
        paymentRequest.setCancelURL(cancelUrl);
        paymentRequest.setReturnURL(returnUrl);
        paymentRequest.setNotificationURL(notificationUrl);

        if (getAuthToken() == null) {
            throw new UnauthorizedOperationException();
        }
        if (amount.scale() > 2) {
            throw new FatalErrorException("The amount cannot have more than 2 digit after the '.'.");
        }

        BigDecimal amountX100 = amount.scaleByPowerOfTen(2);

        addPaymentDetails(amountX100, paymentRequest);
        String orderReference = addOrderDetails(amountX100, paymentRequest);

        // paymentRequest.setCustomPaymentPageCode("");
        paymentRequest.setLanguageCode(Locale.FRENCH.getISO3Language());
        paymentRequest.setSecurityMode("ssl");

        WebPaymentAPI_Service paylineService = new WebPaymentAPI_Service();
        DoWebPaymentResponse apiReponse = paylineService.getWebPaymentAPI().doWebPayment(paymentRequest);

        Reponse reponse = new Reponse(apiReponse);
        createBankTransaction(amount, orderReference, reponse);
        return reponse;
    }

    private void createBankTransaction(BigDecimal amount, String orderReference, Reponse reponse) {
        if (reponse.getToken() != null && !reponse.getToken().isEmpty()) {
            BankTransaction bankTransaction = new BankTransaction(reponse.getMessage(),//
                                                                    reponse.getToken(),//
                                                                    getAuthToken().getMember().getDao(),//
                                                                    amount, //
                                                                    orderReference);
            bankTransaction.setProcessInformations(reponse.getCode());
            if (reponse.isAccepted()) {
                bankTransaction.setAccepted();
            } else {
                bankTransaction.setRefused();
            }
        }
    }

    private String addOrderDetails(BigDecimal amountX100, DoWebPaymentRequest paymentRequest) {
        // Order details
        Order order = new Order();
        String orderReference = createOrderRef(getAuthToken().getMember());
        order.setRef(orderReference);
        order.setOrigin(ORDER_ORIGINE);
        order.setCountry(Locale.FRANCE.getCountry());
        order.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        order.setCurrency(CURRENCY);
        order.setAmount(amountX100.toPlainString()); // entier * 100
        order.setTaxes(amountX100.divide(new BigDecimal("0.21"), 0, RoundingMode.HALF_EVEN).toPlainString());
        paymentRequest.setOrder(order);
        return orderReference;
    }

    private void addPaymentDetails(BigDecimal amountX100, DoWebPaymentRequest paymentRequest) {
        // Payment details
        Payment payement = new Payment();
        payement.setAction(ACTION);
        payement.setMode(MODE);
        payement.setAmount(amountX100.toPlainString());
        payement.setContractNumber(CONTRACT_NUMBER);
        payement.setCurrency(CURRENCY);
        paymentRequest.setPayment(payement);
    }

    /**
     * Return a unique ref.
     *
     * @param member
     * @return
     */
    private String createOrderRef(Member member) {
        StringBuilder ref = new StringBuilder();
        ref.append("PAYLINE-");
        ref.append(member.getId());
        ref.append("-");
        PageIterable<DaoBankTransaction> bankTransaction = DaoBankTransaction.getAllTransactionsOf(member.getDao());
        if (bankTransaction.size() == 0) {
            ref.append("0");
        } else {
            ref.append(bankTransaction.iterator().next().getId() + 1);
        }
        return ref.toString();
    }

}