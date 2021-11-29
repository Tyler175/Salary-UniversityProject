package com.example.repository;

import com.example.models.Position;
import com.example.models.User;
import com.example.models.WorkerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerInfoRepository extends JpaRepository<WorkerInfo, Long> {
    Optional<WorkerInfo> findById(Long id);

    List<WorkerInfo> findAllByUser(User user);

    List<WorkerInfo> findAllByPosition(Position position);
}
