package br.com.iuriraredu.ecommerce.dto;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}
