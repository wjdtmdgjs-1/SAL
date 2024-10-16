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
    List<Member> findByUserIdAndMemberRole(long id, MemberRole memberRole);

    @Query("SELECT m.workSpace FROM Member m WHERE m.user.id = :userId")
    List<WorkSpace> findWorkSpaceIdByUserId(@Param("userId") long userId);

    @Query("SELECT m FROM Member m WHERE m.user.id = :userId AND m.memberRole = :memberRole AND m.workSpace.id = :workSpaceId")
    Optional<Member> findByUserIdAndMemberRoleAndWorkSpaceId(@Param("userId") long userId,
                                                             @Param("memberRole") MemberRole memberRole,
                                                             @Param("workSpaceId") long workSpaceId);

    @Query("SELECT m FROM Member m WHERE m.user.id = :userId AND m.workSpace.id = :workSpaceId")
    Optional<Member> findMemberWithUserIdAndWorkSpaceId(@Param("userId") long userId, @Param("workSpaceId") long workSpaceId);

    @Query("SELECT m FROM Member m WHERE m.workSpace.id = :workSpaceId AND m.user.id = :userId")
    Optional<Member> checkDuplicate(@Param("workSpaceId") long workSpaceId, @Param("userId") long userId);

    Optional<Member> findByWorkSpace_IdAndUser_Id(long workSpaceId, long userId);
}
