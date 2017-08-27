package org.oa.websocket.jni;

import org.oa.websocket.model.JniParametrs;

public class ArServerDetector {
	   static {
		      System.load("arserverdetector.so"); // hello.dll (Windows) or libhello.so (Unixes)
		      //System.out.println(System.getProperty("java.library.path"));
		   }

		public static native int initSession(String pyramidFile, String dbowFile, int[] ids, String [] markers);

		public static native int detect(int sessionId, byte[] image);

		public static native void closeSession(int sessionId);
}
