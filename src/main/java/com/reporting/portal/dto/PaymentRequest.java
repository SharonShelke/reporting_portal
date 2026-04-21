package com.reporting.portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    private String proofUrl;
    private String method;
    private String paymentRef;


}