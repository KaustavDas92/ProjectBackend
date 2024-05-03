package com.example.springbootlibrary.service;

import com.example.springbootlibrary.dao.PaymentRepository;
import com.example.springbootlibrary.entity.Payment;
import com.example.springbootlibrary.requestModels.PaymentInfoRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PaymentService {

    private PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey=secretKey;
    }

    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException{
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder().setAmount(paymentInfoRequest.getAmount()).setCurrency(paymentInfoRequest.getCurrency()).build();

        return PaymentIntent.create(params);
    }

    public ResponseEntity<String> stripePayment(String userEmail) throws Exception {
        Payment payment= paymentRepository.findByUserEmail(userEmail);
        if(payment == null){
            throw  new Exception("Payment info is missing");
        }

        payment.setAmount(0.00);
        paymentRepository.save(payment);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
