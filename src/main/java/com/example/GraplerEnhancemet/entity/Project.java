 package com.example.GraplerEnhancemet.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
	public class Project {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id ;

		@NotBlank(message = "Name is required")
		@Size(max = 255, message = "Name should not exceed 255 characters")
		private String name;

		@NotBlank(message = "Type is required")
		@Size(max = 255, message = "Type should not exceed 255 characters")
		private String type;

		@NotBlank(message = "SubType is required")
		@Size(max = 255, message = "SubType should not exceed 255 characters")
		private String subType;

		private LocalDateTime creationTime ;


		@JsonBackReference
		@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH} , fetch = FetchType.EAGER)
		private Workspace workspace;

	    @JsonManagedReference
		@OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	    private List<Task> tasks;


	    @OneToMany(mappedBy = "parentProject", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	    private List<Folder> subFolders;

//	   			@JsonBackReference
		@JsonIgnore
		@ManyToMany(fetch = FetchType.EAGER)
		private List<User> users;


}
