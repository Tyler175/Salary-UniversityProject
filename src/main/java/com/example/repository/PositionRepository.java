package com.example.repository;

import com.example.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName (String name);
    List<Position> findAllByNameStartingWith(String name);
}
