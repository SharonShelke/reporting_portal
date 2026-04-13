package com.reporting.portal.dto;


public record OrderRequest(
        String zone,
        String magazineType,
        Integer quantity,
        Double totalAmount,
        String orderedBy
)
{

}