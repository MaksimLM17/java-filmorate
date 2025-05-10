package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;

    @NotNull(message = "E-mail не может быть null")
    @Email(message = "Некорректный e-mail")
    private String email;

    @NotNull(message = "Логин не может быть null")
    @NotEmpty(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s).+$", message = "Логин не должен быть пустым или содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата Рождения указана в будущем времени")
    private LocalDate birthday;
}
