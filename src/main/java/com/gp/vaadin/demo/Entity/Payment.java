package com.gp.vaadin.demo.Entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Payment {

    @Column(name = "PAYMENT_TYPE")
    private Byte type;
    @Column(name = "PRE_PAYMENT")
    private Byte prePayment;

    public Payment() {
    }

    public Payment(Payment payment) {
        if (payment == null) return;
        type = payment.getType();
        prePayment = payment.getPrePayment();
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getPrePayment() {
        return prePayment;
    }

    public void setPrePayment(Byte prePayment) {
        this.prePayment = prePayment;
    }
}
