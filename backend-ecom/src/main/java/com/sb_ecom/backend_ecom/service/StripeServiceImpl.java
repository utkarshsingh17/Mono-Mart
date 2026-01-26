package com.sb_ecom.backend_ecom.service;

import com.sb_ecom.backend_ecom.payload.StripePaymentDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;

import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDto) throws StripeException {
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(stripePaymentDto.getAmount())
                .setCurrency(stripePaymentDto.getCurrency())
                .setDescription(stripePaymentDto.getDescription())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build());

        if (stripePaymentDto.getEmail() != null && !stripePaymentDto.getEmail().isEmpty()) {
            Customer customer;
            // Retrieve and check if customer exist
            com.stripe.param.CustomerListParams listParams = com.stripe.param.CustomerListParams.builder()
                    .setEmail(stripePaymentDto.getEmail())
                    .setLimit(1L)
                    .build();
            com.stripe.model.CustomerCollection customers = Customer.list(listParams);

            if (customers.getData().isEmpty()) {
                // Create new customer
                CustomerCreateParams.Builder customerParamsBuilder = CustomerCreateParams.builder()
                        .setEmail(stripePaymentDto.getEmail())
                        .setName(stripePaymentDto.getName());

                if (stripePaymentDto.getAddress() != null) {
                    customerParamsBuilder.setAddress(
                            CustomerCreateParams.Address.builder()
                                    .setLine1(stripePaymentDto.getAddress().getStreet())
                                    .setCity(stripePaymentDto.getAddress().getCity())
                                    .setState(stripePaymentDto.getAddress().getState())
                                    .setPostalCode(stripePaymentDto.getAddress().getPincode())
                                    .setCountry(stripePaymentDto.getAddress().getCountry())
                                    .build()
                    );
                }

                CustomerCreateParams customerParams = customerParamsBuilder.build();
                customer = Customer.create(customerParams);
            } else {
                // Fetch the customer that exist
                customer = customers.getData().get(0);
            }

            paramsBuilder.setCustomer(customer.getId());
        }

        return PaymentIntent.create(paramsBuilder.build());
    }
}
