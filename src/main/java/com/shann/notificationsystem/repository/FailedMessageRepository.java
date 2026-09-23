package com.shann.notificationsystem.repository;

import com.shann.notificationsystem.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {
    public List<FailedMessage> findByReplayedFalse();
}
