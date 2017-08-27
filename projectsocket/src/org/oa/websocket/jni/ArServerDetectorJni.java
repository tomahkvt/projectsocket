package org.oa.websocket.jni;

import org.oa.websocket.model.JniParametrs;

public class ArServerDetectorJni {
	   static {
		      System.load("arserverdetector.so"); // hello.dll (Windows) or libhello.so (Unixes)
		      //System.out.println(System.getProperty("java.library.path"));
		   }
		public static void main(String[] args) {
			callMeethodJni();
		}

		
		public static native void arServerDetectorJni(JniParametrs jniParametrs);
		static public void callMeethodJni() {
			JniParametrs val = new JniParametrs();
			System.out.println("Before call jni");
			arServerDetectorJni(val);
			System.out.println("After call jni");
		}
}
