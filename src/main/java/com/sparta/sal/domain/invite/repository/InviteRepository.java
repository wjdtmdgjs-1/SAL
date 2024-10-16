package com.sparta.sal.domain.invite.repository;

import com.sparta.sal.domain.invite.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite,Long> {

    @Query("SELECT i FROM Invite i WHERE i.member.workSpace.id = :workSpaceId AND i.user.id = :userId")
    Optional<Invite> checkDuplicate(@Param("workSpaceId") long workSpaceId,@Param("userId") long userId);

    List<Invite> findByUserId(Long id);
}
