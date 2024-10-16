package com.sparta.sal.domain.assignee.repository;

import com.sparta.sal.domain.assignee.entity.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
}
