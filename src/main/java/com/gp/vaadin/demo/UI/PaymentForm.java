package com.gp.vaadin.demo.UI;

import com.gp.vaadin.demo.Entity.Payment;
import com.gp.vaadin.demo.Services.PaymentService;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import static com.gp.vaadin.demo.Services.StaticFields.*;

public class PaymentForm extends CustomField<Payment> {
    private PaymentService paymentService = PaymentService.getInstance();
    private VerticalLayout layout = new VerticalLayout();
    private RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
    private TextField textField = new TextField();
    private Label label = new Label(PAYMENT_WILL_BE_MADE_DIRECTLY_IN_THE_HOTEL);
    private Payment payment = new Payment();
    private Byte lastPayment;

    @Override
    protected Component initContent() {
        setCaption(PAYMENT_METHOD);
        layout.addComponents(radioButtonGroup, textField, label);
        layout.setWidth(100, Unit.PERCENTAGE);
        radioButtonGroup.setItems(CREDIT_CARD, CASH);
        radioButtonGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        textField.setPlaceholder("Guaranty Deposit");
        radioButtonGroup.setWidth(100, Unit.PERCENTAGE);
        textField.setWidth(100, Unit.PERCENTAGE);
        label.setWidth(100, Unit.PERCENTAGE);
        textField.setVisible(false);
        label.setVisible(false);

        radioButtonGroup.addSelectionListener((SingleSelectionListener<String>) singleSelectionEvent -> {
            ValidationResult result = paymentService.validateType(radioButtonGroup);
            paymentService.showValidation(this, result);
            if (!result.isError()) {
                String paymentType = singleSelectionEvent.getSelectedItem().get();
                if (CREDIT_CARD.equals(paymentType)) {
                    textField.setVisible(true);
                    label.setVisible(false);
                    payment.setType(TYPE_CARD);

                } else if (CASH.equals(paymentType)) {
                    textField.setVisible(false);
                    label.setVisible(true);
                    payment.setType(TYPE_CASH);
                    payment.setPrePayment(null);
                    textField.setValue("");

                }
            }
        });
        textField.addValueChangeListener((ValueChangeListener<String>) valueChangeEvent -> {
            ValidationResult result = paymentService.validatePayment(radioButtonGroup, textField);
            paymentService.showValidation(this, result);
            if (paymentService.isValidPayment(textField)) {
                Byte newPrePayment = Byte.parseByte(textField.getValue());
                payment.setPrePayment(newPrePayment);
                paymentService.showPaymentChange(lastPayment, newPrePayment);
            }
        });

        return layout;
    }

    private void updateValues() {
        Byte type = payment.getType();
        if (type == null) {
            radioButtonGroup.setSelectedItem(null);
            textField.setVisible(false);
            label.setVisible(false);
            lastPayment = null;
            textField.setValue("");
        } else {
            String selected = (type == TYPE_CARD) ? CREDIT_CARD : CASH;
            radioButtonGroup.setSelectedItem(selected);
            Byte prePayment = payment.getPrePayment();
            if (prePayment != null) {
                lastPayment = prePayment;
                textField.setValue(prePayment.toString());
            }
        }
    }

    @Override
    protected boolean isDifferentValue(Payment newValue) {
        return true;
    }

    @Override
    public Validator<Payment> getDefaultValidator() {
        return (Validator<Payment>) (payment, valueContext) -> {
            ValidationResult typeValidation = paymentService.validateType(radioButtonGroup);
            if (typeValidation.isError()) {
                return ValidationResult.error(typeValidation.getErrorMessage());
            }
            ValidationResult priceValidation = paymentService.validatePayment(radioButtonGroup, textField);
            if (priceValidation.isError()) {
                return ValidationResult.error(priceValidation.getErrorMessage());
            }
            return ValidationResult.ok();
        };
    }

    @Override
    protected void doSetValue(Payment payment) {
        this.payment = new Payment(payment);
        updateValues();
    }


    @Override
    public Payment getValue() {
        return payment;
    }
}