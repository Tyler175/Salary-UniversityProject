package com.example.repository;

import java.util.Optional;
import java.util.Set;

import com.example.models.ERole;
import com.example.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
  Optional<Role> findById(Integer id);
  boolean existsByName(ERole name);
}
