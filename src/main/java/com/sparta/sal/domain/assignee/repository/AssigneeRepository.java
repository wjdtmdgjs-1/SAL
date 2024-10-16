package com.sparta.sal.domain.assignee.repository;

import com.sparta.sal.domain.assignee.entity.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
    Assignee findByCard_IdAndMember_Id(Long card_Id, Long member_Id);
    List<Assignee> findByCard_Id(Long card_Id);
}
