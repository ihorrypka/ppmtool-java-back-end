package io.agileintelligence.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.agileintelligence.ppmtool.domain.Backlog;
import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.exception.ProjectNotFoundException;
import io.agileintelligence.ppmtool.repository.BacklogRepository;
import io.agileintelligence.ppmtool.repository.ProjectRepository;
import io.agileintelligence.ppmtool.repository.ProjectTaskRepository;

@Service
public class ProjectTaskService {
	
	private BacklogRepository backlogRepository;
	private ProjectTaskRepository projectTaskRepository;
	private ProjectRepository projectRepository;
	
	@Autowired
	public ProjectTaskService(BacklogRepository backlogRepository, 
						ProjectTaskRepository projectTaskRepository,
						ProjectRepository projectRepository) {
		
		this.backlogRepository = backlogRepository;
		this.projectTaskRepository = projectTaskRepository;
		this.projectRepository = projectRepository;
		
	}
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		
		try {
			// Exceptions: Project not found
			
			// PTs to be added to a specific project, project != null, BL exists
			Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
			// set the BL to PT
			projectTask.setBacklog(backlog);
			// we want our project sequence to be like this: IDPRO-1, IDPRO-2 ...100 101
			Integer backlogSequence = backlog.getPTSequence();
			// Update the BL SEQUENCE
			backlogSequence++;
			
			backlog.setPTSequence(backlogSequence);
			
			// Add Sequence to Project Task
			projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
			projectTask.setProjectIdentifier(projectIdentifier);
			
			// INITIAL priority when priority null

			// INITIAL status when status is null
			if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
				projectTask.setStatus("TO_DO");
			}
			
			if (projectTask.getPriority() == null) { // In the future we need projectTask 
				projectTask.setPriority(3);          // .getPriority() == 0 to handle the form
			}
			
			return projectTaskRepository.save(projectTask);
			
		} catch (Exception e) {
			throw new ProjectNotFoundException("Project not Found!");
		}
		
	}
	
	public Iterable<ProjectTask> findBacklogById(String projectIdentifier) {
		
		Project project = projectRepository.findByProjectIdentifier(projectIdentifier);
		
		if (project == null) {
			throw new ProjectNotFoundException(
							"Project with ID: '" + projectIdentifier + "' does not exist!");
		}
	
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectIdentifier);
		
	}

}
