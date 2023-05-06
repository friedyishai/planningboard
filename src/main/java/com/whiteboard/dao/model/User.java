package com.whiteboard.dao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static com.whiteboard.constants.Validation.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "[User]")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @NotBlank
    @Length(min = MIN_USER_NAME_LEN, max = MAX_USER_NAME_LEN)
    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String name;

    @NotBlank
    @Pattern(regexp = EMAIL_REGEX)
    @Length(min = MIN_EMAIL_LEN, max = MAX_EMAIL_LEN)
    @Column(nullable = false, columnDefinition = "varchar(45)")
    private String email;

    @NotBlank
    @Length(min = MIN_PASSWORD_LEN, max = MAX_PASSWORD_LEN)
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String password;

    @Column(name = "is_active", nullable = false, columnDefinition = "bit")
    private Boolean isActive;
}
