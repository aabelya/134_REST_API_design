package com.epam.module4.dao;

import com.epam.module4.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
