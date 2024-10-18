package com.sparta.sal.domain.workspace.repository;

import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace,Long> {
}
