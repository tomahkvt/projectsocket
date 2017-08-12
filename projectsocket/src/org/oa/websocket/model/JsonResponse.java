package org.oa.websocket.model;

public class JsonResponse {
	private String status;
	private String response;
	
	public JsonResponse(){
		
	}
	
	public JsonResponse(String status,String response){
		this.status = status;
		this.response = response;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "JsonResponse {getStatus()=" + getStatus() + ", getResponse()=" + getResponse() + "}";
	}
	
}
