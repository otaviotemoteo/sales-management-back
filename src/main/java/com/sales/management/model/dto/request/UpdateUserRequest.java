package com.sales.management.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    private Boolean active;

    @Pattern(regexp = "^\\+?[0-9 ()\\-]{8,20}$", message = "Telefone inválido")
    private String phone;

    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter 11 dígitos numéricos")
    private String cpf;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String city;

    @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ser a sigla de 2 letras (ex: SP)")
    private String state;

    @Size(max = 1000, message = "Biografia deve ter no máximo 1000 caracteres")
    private String bio;

    @Size(max = 500, message = "URL do avatar deve ter no máximo 500 caracteres")
    private String avatarUrl;
}