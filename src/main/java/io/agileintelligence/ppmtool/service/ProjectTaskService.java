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
	private ProjectService projectService;
	
	@Autowired
	public ProjectTaskService(BacklogRepository backlogRepository,
				ProjectTaskRepository projectTaskRepository,
				ProjectRepository projectRepository,
				ProjectService projectService) {
		
		this.backlogRepository = backlogRepository;
		this.projectTaskRepository = projectTaskRepository;
		this.projectRepository = projectRepository;
		this.projectService = projectService;
		
	}
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask,
					String username) {
		
		// PTs to be added to a specific project, project != null, BL exists
		Backlog backlog = 
				projectService.findProjectByIdentifier(
						projectIdentifier, username).getBacklog();
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
		
		// Fix bug with priority in Spring Boot Server, need to check null first
		if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {  
			projectTask.setPriority(3);          
		}
		
		return projectTaskRepository.save(projectTask);
		
	}
	
	public Iterable<ProjectTask> findBacklogById(String projectIdentifier, String username) {
		
		projectService.findProjectByIdentifier(projectIdentifier, username);
	
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectIdentifier);
		
	}
	
	public ProjectTask findProjectTaskByProjectSequence(String backlog_id, 
				String projectTask_id, String username) {
		
		// make sure we are searching on the right backlog
		projectService.findProjectByIdentifier(backlog_id, username);
		
		// make sure that our task exists
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(projectTask_id);
		
		if (projectTask == null) {
			throw new ProjectNotFoundException(
						"Project Task '" + projectTask_id + "' not found!");
		} 
		
		// make sure that the backlog/project id in the path corresponds to the right project
		if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
			
			throw new ProjectNotFoundException("Project Task '"
					+ projectTask_id + "' does not exist in project: '" + backlog_id + "'");
			
		}
		
		return projectTask;
		
	}
	
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id,
					String projectTask_id, String username) {
		
		ProjectTask projectTask = 
				findProjectTaskByProjectSequence(backlog_id, projectTask_id, username);
		
		projectTask = updatedTask;
		
		return projectTaskRepository.save(projectTask);
				
	}
	
	public void deleteProjectTaskByProjectSequence(String backlog_id, String projectTask_id,
					String username) {
		
		ProjectTask projectTask = 
				findProjectTaskByProjectSequence(backlog_id, projectTask_id, username);
		
		projectTaskRepository.delete(projectTask);
		
	}
	
}
