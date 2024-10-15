package com.sparta.sal.domain.member.repository;

import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUserIdAndMemberRole(Long id, MemberRole memberRole);

    @Query("SELECT m.workSpace FROM Member m WHERE m.user.id = :userid")
    List<WorkSpace> findWorkSpaceIdByUserId(@Param("userid") long userid);

    @Query("SELECT m FROM Member m WHERE m.user.id=:userid AND m.memberRole = :memberRole AND m.workSpace.id = :workSpaceId")
    Optional<Member> findByUserIdAndMemberRoleAndWorkSpaceId(@Param("userid") Long userid,
                                                             @Param("memberRole")MemberRole memberRole,
                                                             @Param("workSpaceId")Long workSpaceId);

    @Query("SELECT m FROM Member m WHERE m.user.id = :userid AND m.workSpace.id = :workSpaceId")
    Optional<Member> findMemberWithUserIdAndWorkSpaceId(@Param("userid") Long userid,@Param("workSpaceId") Long workSpaceId);

}
