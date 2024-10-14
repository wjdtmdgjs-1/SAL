package com.sparta.sal.domain.member.repository;

import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUserIdAndMemberRole(Long id, MemberRole memberRole);

    @Query("SELECT m.workSpace FROM Member m WHERE m.user.id = :userid")
    List<WorkSpace> findWorkSpaceId(@Param("userid") long userid);
}
