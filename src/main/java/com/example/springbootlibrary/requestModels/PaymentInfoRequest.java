package com.example.springbootlibrary.requestModels;

import lombok.Data;

@Data
public class PaymentInfoRequest {
    private long amount;
    private String currency;
    private String receiptEmail;
}
