package com.example.springbootlibrary.controller;

import com.example.springbootlibrary.requestModels.PaymentInfoRequest;
import com.example.springbootlibrary.service.PaymentService;
import com.example.springbootlibrary.utils.ExtractJWTToken;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/payments/secure")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent( @RequestBody PaymentInfoRequest paymentInfoRequest) throws StripeException {
        PaymentIntent paymentIntent=paymentService.createPaymentIntent(paymentInfoRequest);

        String paymentIntentStr=paymentIntent.toJson();

        return new ResponseEntity<>(paymentIntentStr, HttpStatus.OK);
    }

    @PutMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestHeader(value = "Authorization") String token) throws Exception{
        String userEmail=ExtractJWTToken.payloadExtract(token,"\"sub\"");
        if(userEmail == null){
            throw new Exception("User not found");
        }

        return paymentService.stripePayment(userEmail);
    }
}
