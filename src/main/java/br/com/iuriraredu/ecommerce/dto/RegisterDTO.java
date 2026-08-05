package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.enums.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
}
