package com.example.repository;

import com.example.models.User;
import com.example.models.WorkerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByEmailIgnoreCase(String email);

    List<User> findAllByWorkerInfoIsNotNull();

    List<User> findAllByFirstNameStartingWithOrLastNameStartingWithOrPatronymicStartingWith(String firstName, String lastName, String patronymic);
}
