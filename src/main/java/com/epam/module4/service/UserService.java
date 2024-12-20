package com.epam.module4.service;

import com.epam.module4.dao.UserRepository;
import com.epam.module4.domain.Authority;
import com.epam.module4.domain.Role;
import com.epam.module4.domain.User;
import com.epam.module4.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper mapper;

    public User createUser(String username, String password, Role role, Authority... authorities) {
        return createUser(new UserDto()
                .setUsername(username)
                .setPassword(password)
                .setCreatedBy(username)
                .setUpdatedBy(username)
                .setRole(role)
                .setAuthorities(Arrays.asList(authorities)));
    }

    public User createUser(UserDto dto) {
        User user = mapper.convertValue(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return saveUser(user);
    }

    public User getUser(String username) {
        String sanitizedUsername = username.replaceAll("[^a-zA-z0-9\\-.]", "");
        return repository.findByUsername(sanitizedUsername).orElseThrow(() -> new UserNotFoundException(username));
    }

    public User getUser(Integer id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Page<User> getAllUsers(int page, int size) {
        return repository.findAll(Pageable.ofSize(size).withPage(page));
    }

    public User updateUser(Integer id, UserDto dto) {
        User user = getUser(id);
        user.setUpdatedBy(dto.getUpdatedBy());
        Optional.ofNullable(dto.getUsername()).ifPresent(user::setUsername);
        Optional.ofNullable(dto.getPassword()).map(passwordEncoder::encode).ifPresent(user::setPassword);
        Optional.ofNullable(dto.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(dto.getComment()).ifPresent(user::setComment);
        Optional.ofNullable(dto.getRole()).ifPresent(user::setRole);
        Optional.ofNullable(dto.getAuthorities()).ifPresent(user::setAuthorities);
        return saveUser(user);
    }

    private User saveUser(User user) {
        return repository.save(user);
    }

    public User deleteUser(Integer id) {
        User user = getUser(id);
        repository.delete(user);
        return user;
    }

}
