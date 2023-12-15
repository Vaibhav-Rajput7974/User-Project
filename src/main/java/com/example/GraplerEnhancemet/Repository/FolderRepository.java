package com.example.GraplerEnhancemet.Repository;

import com.example.GraplerEnhancemet.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder,Long> {
  List<Folder> findAllByParentProject_Id(Long projectId);
  @Modifying
  @Transactional
  @Query("DELETE FROM Folder f WHERE f.id = :id")
  void deleteFolderById(@Param("id") Long id);
//  @Modifying
//  @Query("DELETE FROM Folder f WHERE f.parentProject.id = :projectId AND f.parentFolder.id = :parentFolderId")
//  void deleteByParentProjectIdAndParentFolderId(
//          @Param("projectId") Long projectId,
//          @Param("parentFolderId") Long parentFolderId
//  );
//  @Modifying
//  @Query("DELETE FROM Folder f WHERE f.parentFolder.id = :parentFolderId")
//  void deleteByParentFolderId(@Param("parentFolderId") Long parentFolderId);  //  void deleteByParentProject_Id(Long ProjectId);
//  //  void deleteByParentFolder_Id(Long ParentFolder_Id);
}
