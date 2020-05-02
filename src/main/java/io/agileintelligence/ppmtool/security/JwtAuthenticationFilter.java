package io.agileintelligence.ppmtool.security;

import static io.agileintelligence.ppmtool.security.SecurityConstants.HEADER_STRING;
import static io.agileintelligence.ppmtool.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.service.CustomUserDetailsService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private JwtTokenProvider jwtTokenProvider;
	
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
		
		this.jwtTokenProvider = jwtTokenProvider;
		
	}
	
	@Autowired
	public void setCustomUserDetailsService(
				CustomUserDetailsService customUserDetailsService) {
		
		this.customUserDetailsService = customUserDetailsService;
		
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, 
				HttpServletResponse httpServletResponse, 
				FilterChain filterChain) throws ServletException, IOException {
		
		try {
			
			String jwt = getJWTFromHttpRequest(httpServletRequest);
			
			if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
				
				Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);
				
				User userDetails = customUserDetailsService.loadUserById(userId);
				
				UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(
									userDetails, null, Collections.emptyList()
							);
				
				authentication.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			}
			
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}
		
		filterChain.doFilter(httpServletRequest, httpServletResponse);
		
	}
	
	private String getJWTFromHttpRequest(HttpServletRequest httpServletRequest) {
		
		String bearerToken = httpServletRequest.getHeader(HEADER_STRING);
		
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
			
			return bearerToken.substring(7, bearerToken.length());
			
		}
		
		return null;
		
	}

}
