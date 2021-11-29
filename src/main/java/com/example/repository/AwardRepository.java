package com.example.repository;

import com.example.models.Award;
import com.example.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {



}
