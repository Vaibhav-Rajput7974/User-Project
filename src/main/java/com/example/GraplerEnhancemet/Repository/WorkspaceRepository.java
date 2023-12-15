package com.example.GraplerEnhancemet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.GraplerEnhancemet.entity.Workspace;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long>{
    Optional<Workspace> findByIdAndCompany_Id(Long id, Long companyId);

    List<Workspace> findByCompany_Id(Long companyId);
    void deleteByCompany_Id(Long companyId);
}
