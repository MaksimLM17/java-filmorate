package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Objects;


public class User {
    private Integer id;

    @NotNull(message = "E-mail не может быть null")
    @Email(message = "Некорректный e-mail")
    private String email;

    @NotNull(message = "Логин не может быть null")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s).+$", message = "Логин не должен быть пустым или содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата Рождения указана в будущем времени")
    private LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
