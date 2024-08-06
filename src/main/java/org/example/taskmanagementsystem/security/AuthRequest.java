package org.example.taskmanagementsystem.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @Email(message = "Некорректный формат электронной почты")
    private String email;
    @Size(min = 3, message = "Пароль должен содержать минимум 3 символов")
    private String password;
}
