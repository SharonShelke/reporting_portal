package com.reporting.portal.dto;


public record OrderRequest(
        String zone,
        String magazineType,
        Integer quantity,
        Double totalAmount,
        String orderedBy,
        String deliveryAddress,
        String country,
        String stateProvince,
        String postalCode,
        String contactEmail,
        String contactPhone
) {}