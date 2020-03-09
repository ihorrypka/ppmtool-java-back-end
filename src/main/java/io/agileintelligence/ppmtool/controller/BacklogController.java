package io.agileintelligence.ppmtool.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agileintelligence.ppmtool.domain.ProjectTask;
import io.agileintelligence.ppmtool.service.MapValidationErrorService;
import io.agileintelligence.ppmtool.service.ProjectTaskService;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {
	
	private ProjectTaskService projectTaskService;
	private MapValidationErrorService mapValidationErrorService;
	
	@Autowired
	public BacklogController(ProjectTaskService projectTaskService,
							MapValidationErrorService mapValidationErrorService) {
		
		this.projectTaskService = projectTaskService;
		this.mapValidationErrorService = mapValidationErrorService;
	}
	
	@PostMapping("/{backlog_id}")
	public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask,
											BindingResult result, @PathVariable String backlog_id) {
		
		ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
		if (errorMap != null) {
			return errorMap;
		}
		
		ProjectTask theProjectTask = 
				projectTaskService.addProjectTask(backlog_id, projectTask);
		
		return new ResponseEntity<ProjectTask>(theProjectTask, HttpStatus.CREATED);
	}
	
	@GetMapping("/{backlog_id}")
	public Iterable<ProjectTask>getProjectBacklog(@PathVariable String backlog_id) {
		
		return projectTaskService.findBacklogById(backlog_id);
		
	}
	
	@GetMapping("/{backlog_id}/{projectTask_id}")
	public ResponseEntity<?> getProjectTask(@PathVariable String backlog_id,
											@PathVariable String projectTask_id) {
		
		ProjectTask projectTask = 
				projectTaskService.findProjectTaskByProjectSequence(backlog_id, projectTask_id);
		
		return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
		
	}
	
	@PatchMapping("/{backlog_id}/{projectTask_id}")
	public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask,
												BindingResult result,
												@PathVariable String backlog_id,
												@PathVariable String projectTask_id) {
		
		ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
		if (errorMap != null) {
			return errorMap;
		}
		
		ProjectTask updatedProjectTask = 
				projectTaskService.updateByProjectSequence(projectTask, backlog_id, projectTask_id);
		
		return new ResponseEntity<ProjectTask>(updatedProjectTask, HttpStatus.OK);
		
	}
	
	@DeleteMapping("/{backlog_id}/{projectTask_id}") 
	public ResponseEntity<?> deleteProjectTask(@PathVariable String backlog_id,
												@PathVariable String projectTask_id) {
		
		projectTaskService.deleteProjectTaskByProjectSequence(backlog_id, projectTask_id);
		
		return new ResponseEntity<String>(
				"Project Task '" + projectTask_id + "' was deleted successfully!", HttpStatus.OK);
		
	}
	
}
