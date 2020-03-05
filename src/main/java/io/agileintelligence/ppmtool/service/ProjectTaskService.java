package io.agileintelligence.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.repository.BacklogRepository;
import io.agileintelligence.ppmtool.repository.ProjectTaskRepository;

@Service
public class ProjectTaskService {
	
	private BacklogRepository backlogRepository;
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	public ProjectTaskService(BacklogRepository backlogRepository, 
						ProjectTaskRepository projectTaskRepository) {
		
		this.backlogRepository = backlogRepository;
		this.projectTaskRepository = projectTaskRepository;
		
	}
	
	public ProjectTask addProjectTask() {
		
		// PTs to be added to a specific project, project != null, BL exists
		// set the BL to PT
		// we want our project sequence to be like this: IDPRO-1, IDPRO-2 ...100 101
		// Update the BL SEQUENCE
		
		// INITIAL priority when priority null
		// INITIAL status when status is null
		
		return null;
	}

}
