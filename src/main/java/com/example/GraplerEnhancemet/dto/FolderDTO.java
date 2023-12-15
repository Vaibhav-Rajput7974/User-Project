package com.example.GraplerEnhancemet.dto;

import com.example.GraplerEnhancemet.entity.Folder;
import com.example.GraplerEnhancemet.entity.Project;
import com.example.GraplerEnhancemet.entity.enums.FolderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {
    private Long id;
    String name;
    private FolderType FolderType;
    private Project parentProject;
    private Folder parentfolder;
}
