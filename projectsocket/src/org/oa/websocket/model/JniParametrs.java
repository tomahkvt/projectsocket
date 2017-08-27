package org.oa.websocket.model;

public class JniParametrs {
	String dbowFile;
	String pyramidFile;
	String markerFiles;
	String pathFrame1;
	String pathFrame2;
	String id1;
	String id2;
	String detectSessionId1;
	String detectSessionId2;
	
	public JniParametrs(){
		
	}

	public String getDbowFile() {
		return dbowFile;
	}

	public void setDbowFile(String dbowFile) {
		this.dbowFile = dbowFile;
	}

	public String getPyramidFile() {
		return pyramidFile;
	}

	public void setPyramidFile(String pyramidFile) {
		this.pyramidFile = pyramidFile;
	}

	public String getMarkerFiles() {
		return markerFiles;
	}

	public void setMarkerFiles(String markerFiles) {
		this.markerFiles = markerFiles;
	}

	public String getPathFrame1() {
		return pathFrame1;
	}

	public void setPathFrame1(String pathFrame1) {
		this.pathFrame1 = pathFrame1;
	}

	public String getPathFrame2() {
		return pathFrame2;
	}

	public void setPathFrame2(String pathFrame2) {
		this.pathFrame2 = pathFrame2;
	}

	public String getId1() {
		return id1;
	}

	public void setId1(String id1) {
		this.id1 = id1;
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}

	public String getDetectSessionId1() {
		return detectSessionId1;
	}

	public void setDetectSessionId1(String detectSessionId1) {
		this.detectSessionId1 = detectSessionId1;
	}

	public String getDetectSessionId2() {
		return detectSessionId2;
	}

	public void setDetectSessionId2(String detectSessionId2) {
		this.detectSessionId2 = detectSessionId2;
	}

	@Override
	public String toString() {
		return "JniParametrs [getDbowFile()=" + getDbowFile() + ", getPyramidFile()=" + getPyramidFile()
				+ ", getMarkerFiles()=" + getMarkerFiles() + ", getPathFrame1()=" + getPathFrame1()
				+ ", getPathFrame2()=" + getPathFrame2() + ", getId1()=" + getId1() + ", getId2()=" + getId2()
				+ ", getDetectSessionId1()=" + getDetectSessionId1() + ", getDetectSessionId2()="
				+ getDetectSessionId2() + "]";
	}
	
}
