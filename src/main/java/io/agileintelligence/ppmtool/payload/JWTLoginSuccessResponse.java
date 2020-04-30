package io.agileintelligence.ppmtool.payload;

public class JWTLoginSuccessResponse {
	
	private boolean succes;
	private String token;
	
	public JWTLoginSuccessResponse(boolean succes, String token) {
		this.succes = succes;
		this.token = token;
	}

	public boolean isSucces() {
		return succes;
	}

	public void setSucces(boolean succes) {
		this.succes = succes;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "JWTLoginSuccessResponse [succes=" + succes + ", token=" + token + "]";
	}
	
}
