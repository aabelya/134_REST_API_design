package com.epam.module4.domain;


import com.epam.module4.dao.AuthoritiesListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class User {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    int id;

    @Column(unique = true, nullable = false)
    String username;

    @JsonIgnore
    @Column(nullable = false)
    @ToString.Exclude
    String password;

    @Column(unique = true)
    String email;

    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    String createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime lastUpdatedAt;

    @Column(nullable = false)
    String updatedBy;

    @Column
    String comment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Role role = Role.USER;

    @Column(nullable = false)
    @Convert(converter = AuthoritiesListConverter.class)
    List<Authority> authorities = new ArrayList<>(Arrays.asList(Authority.READ, Authority.UPDATE));
}
