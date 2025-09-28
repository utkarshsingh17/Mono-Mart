package com.sb_ecom.backend_ecom.service;

import com.sb_ecom.backend_ecom.payload.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {

    PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDto) throws StripeException;
}
