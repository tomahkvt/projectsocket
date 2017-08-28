public class ArServerDetectorJni {

	static {
		System.load("/opt/arserverdetector.so");
		// System.out.println(System.getProperty("java.library.path"));
	}

	public static void main(String[] args) {
		ArServerDetectorJni detector = new ArServerDetectorJni();
		detector.initSessionLocal();
		detector.detectLocal();
		detector.closeSessionLocal();
	}
	
	public native int initSession(String pyramidFile, String dbowFile, int[] ids, String [] markers);
	public void initSessionLocal() {
		String lpyramidFile = "pyramidFile";
		String ldbowFile = "dbowFile";
		int[] lids = {1,2,3};
		String[] lmarkers = {"s1","s2"};
		int sessionid = this.initSession(lpyramidFile, ldbowFile,lids, lmarkers);
		System.out.println("sessionid = " + sessionid);
	}
	public static native int detect(int sessionId, byte[] image);
	public void detectLocal() {
		int sessionIdl = 24;
		byte[] imagel = {1,2,3};
		int detectresult = this.detect(sessionIdl, imagel);
		System.out.println("detectresult = " + detectresult);
	}

	public static native void closeSession(int sessionId);
	public void closeSessionLocal() {
		this.closeSession(54);
		
	}

}
