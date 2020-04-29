package io.agileintelligence.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.exception.UsernameAlreadyExistsException;
import io.agileintelligence.ppmtool.repository.UserRepository;

@Service
public class UserService {
	
	private UserRepository userRepository;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserService(UserRepository userRepository,
								BCryptPasswordEncoder bCryptPasswordEncoder) {
		
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		
	}
	
	public User saveUser (User newUser) {
		
		try {
			
			newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
			
			// Username has to be unique(need custom exception)
			newUser.setUsername(newUser.getUsername());
			
			// Make sure that password and confirmPassword match
			
			// We don't persist or show the confirmPassword
			newUser.setConfirmPassword("");
			
			return userRepository.save(newUser);
			
		} catch(Exception e) {
			throw new UsernameAlreadyExistsException("Username '" + newUser.getUsername() 
																	+ "' already exists!");
		}
		
	} 

}
