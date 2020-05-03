package io.agileintelligence.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.agileintelligence.ppmtool.domain.Backlog;
import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.exception.ProjectIdException;
import io.agileintelligence.ppmtool.repository.BacklogRepository;
import io.agileintelligence.ppmtool.repository.ProjectRepository;
import io.agileintelligence.ppmtool.repository.UserRepository;

@Service
public class ProjectService {
	
	private ProjectRepository projectRepository;
	private BacklogRepository backlogRepository;
	private UserRepository userRepository;
	
	@Autowired
	public ProjectService(ProjectRepository projectRepository,
					BacklogRepository backlogRepository,
					UserRepository userRepository) {
		
		this.projectRepository = projectRepository;
		this.backlogRepository = backlogRepository;
		this.userRepository = userRepository;
		
	}
	
	public Project saveOrUpdateProject(Project project, String username) {
		
		try {
			
			User user = userRepository.findByUsername(username);
			
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if (project.getId() == null) {
				Backlog backlog = new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}
			
			if (project.getId() != null) {
				project.setBacklog(backlogRepository.findByProjectIdentifier(
											project.getProjectIdentifier().toUpperCase()));
			}
			
			return projectRepository.save(project);
			
		} catch (Exception e) {
			throw new ProjectIdException("Project ID '" + 
					project.getProjectIdentifier().toUpperCase() + "' already exists");
		}
	}
	
	public Project findProjectByIdentifier(String projectId) {
		
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if (project == null) {
			
			throw new ProjectIdException("Project ID '" + projectId + "' does not exist");
			
		}
		
		return project;
	}
	
	public Iterable<Project> findAllProject() {
		
		return projectRepository.findAll();
	}
	
	public void deleteProjectByIdentifier(String projectId) {
		
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if (project == null) {
			
			throw new ProjectIdException("Cannot delete Project with ID '" + projectId +
					"'. This project does not exist!");
			
		}
		
		projectRepository.delete(project);
	}
}
