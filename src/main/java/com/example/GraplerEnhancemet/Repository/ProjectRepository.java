package com.example.GraplerEnhancemet.Repository;

import com.example.GraplerEnhancemet.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByWorkspace_Id(Long workspaceId);
    Project findByWorkspace_IdAndId(Long workspaceId, Long projectId);
    void deleteByWorkspace_Id(Long workspaceId);
    }
