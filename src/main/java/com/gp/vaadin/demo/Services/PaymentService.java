package com.gp.vaadin.demo.Services;

import com.gp.vaadin.demo.UI.PaymentForm;
import com.vaadin.data.ValidationResult;
import com.vaadin.server.UserError;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;

import java.util.Locale;

import static com.gp.vaadin.demo.Services.StaticFields.CREDIT_CARD;

public class PaymentService {
    private static PaymentService instance;

    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    public void showValidation(PaymentForm paymentForm, ValidationResult result) {
        if (result.isError()) {
            paymentForm.setComponentError(new UserError(result.getErrorMessage()));
        } else {
            paymentForm.setComponentError(null);
        }
    }

    public ValidationResult validateType(RadioButtonGroup<String> radioButtonGroup) {
        if (!radioButtonGroup.getSelectedItem().isPresent()) {
            return ValidationResult.error("Please, select payment method");
        }
        return ValidationResult.ok();
    }

    public ValidationResult validatePayment(RadioButtonGroup<String> radioButtonGroup, TextField textField) {
        if (radioButtonGroup.getSelectedItem().isPresent() && radioButtonGroup.getSelectedItem().get().equals(CREDIT_CARD)) {
            if (!isValidPayment(textField)) {
                return ValidationResult.error("Payment must be in range 0 to 100");
            }
        }
        return ValidationResult.ok();
    }

    public boolean isValidPayment(TextField textField) {
        try {
            byte val = Byte.parseByte(textField.getValue());
            if (val < 0 || val > 100) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void showPaymentChange(Byte lastPayment, Byte newPayment) {
        if (lastPayment == null || lastPayment.equals(newPayment)) return;
        String message = String.format(Locale.ENGLISH, "Payment changed from %d%% to %d%%",
                lastPayment, newPayment);
        Notification.show(message, Notification.Type.HUMANIZED_MESSAGE);
    }
}
