package com.epam.module4.dto;

import com.epam.module4.controller.validation.OnCreate;
import com.epam.module4.controller.validation.OnManageAccess;
import com.epam.module4.controller.validation.OnUpdate;
import com.epam.module4.domain.Authority;
import com.epam.module4.domain.Role;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserDto extends RepresentationModel<UserDto> {

    public static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-z0-9\\-.]+";

    @Null(groups = {OnCreate.class}, message = "id cannot be explicitly set")
    Integer id;
    @NotNull(groups = OnCreate.class, message = "username is required")
    @Null(groups = OnManageAccess.class, message = "unexpected username param")
    @Pattern(regexp = USERNAME_REGEX, groups = {OnCreate.class, OnUpdate.class, OnManageAccess.class}, message = "illegal username")
    String username;
    @JsonIgnore
    @NotNull(groups = OnCreate.class, message = "password is required")
    @Null(groups = OnManageAccess.class, message = "unexpected password param")
    String password;
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "email not valid")
    @Null(groups = OnManageAccess.class, message = "unexpected email param")
    String email;
    @Null(groups = {OnCreate.class, OnUpdate.class, OnManageAccess.class}, message = "createdAt cannot be explicitly set")
    LocalDateTime createdAt;
    @Null(groups = {OnCreate.class, OnUpdate.class, OnManageAccess.class}, message = "createdBy cannot be explicitly set")
    String createdBy;
    @Null(groups = {OnCreate.class, OnUpdate.class, OnManageAccess.class}, message = "lastUpdatedAt cannot be explicitly set")
    LocalDateTime lastUpdatedAt;
    @Null(groups = {OnCreate.class, OnUpdate.class, OnManageAccess.class}, message = "updatedBy cannot be explicitly set")
    String updatedBy;
    @Null(groups = OnManageAccess.class, message = "unexpected comment param")
    String comment;
    @Null(groups = {OnCreate.class, OnUpdate.class}, message = "role cannot be updated")
    Role role;
    @Null(groups = {OnCreate.class, OnUpdate.class}, message = "authorities cannot be updated")
    List<Authority> authorities;

    @JsonGetter
    public String getCreatedBy() {
        return Optional.ofNullable(createdBy).orElse(username);
    }

    @JsonGetter
    public String getUpdatedBy() {
        return Optional.ofNullable(updatedBy).orElse(username);
    }

}
