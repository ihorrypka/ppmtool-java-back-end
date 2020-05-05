package io.agileintelligence.ppmtool.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.service.MapValidationErrorService;
import io.agileintelligence.ppmtool.service.ProjectService;

@RestController
@RequestMapping("api/project")
@CrossOrigin
public class ProjectController {
	
	private ProjectService projectService;
	private MapValidationErrorService mapValidationErrorService;
	
	@Autowired
	public ProjectController(ProjectService projectService, 
							MapValidationErrorService mapValidationErrorService) {
		this.projectService = projectService;
		this.mapValidationErrorService = mapValidationErrorService;
	}
	
	@PostMapping("")
	public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, 
				BindingResult result, Principal principal) {
		
		ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
		if (errorMap != null) {
			return errorMap;
		}
		
		Project theProject = projectService.saveOrUpdateProject(
					project, principal.getName());
		return new ResponseEntity<Project>(project, HttpStatus.CREATED);
	} 
	
	@GetMapping("/{projectId}")
	public ResponseEntity<?> getProjectById(@PathVariable String projectId, 
					Principal principal) {
		
		Project project =
				projectService.findProjectByIdentifier(projectId, principal.getName());
		
		return new ResponseEntity<Project>(project, HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public Iterable<Project> getAllProject(Principal principal) {
		
		return projectService.findAllProject(principal.getName());
	}
	
	@DeleteMapping("/{projectId}")
	public ResponseEntity<?> deleteProjectById(@PathVariable String projectId, 
					Principal principal) {
		
		projectService.deleteProjectByIdentifier(projectId, principal.getName());
		
		return new ResponseEntity<String>("Project with ID: '" + projectId +
				"' was deleted!", HttpStatus.OK);
	}
}
