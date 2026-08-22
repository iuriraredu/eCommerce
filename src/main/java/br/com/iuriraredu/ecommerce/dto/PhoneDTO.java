package br.com.iuriraredu.ecommerce.dto;

import br.com.iuriraredu.ecommerce.entity.Phone;
import jakarta.validation.constraints.NotBlank;

public record PhoneDTO(
        Long id,

        @NotBlank(message = "Phone number is required")
        String number
) {
    public static PhoneDTO fromEntity(Phone phone) {
        return new PhoneDTO(phone.getId(), phone.getNumber());
    }

    public Phone toEntity() {
        Phone phone = new Phone();
        phone.setNumber(number());
        return phone;
    }
}
