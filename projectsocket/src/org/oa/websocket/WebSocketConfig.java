package org.oa.websocket;




import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.CloseReason;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/receive/fileserver")
public class WebSocketConfig {
    static File uploadedFile = null;
    static String fileName = null;
    static FileOutputStream fos = null;
    final static String filePath="e:/3/";
  

    @OnOpen
    public void open(Session session, EndpointConfig conf) {
    	
        System.out.println("chat ws server open");
    }

    @OnMessage
    public void processUpload(ByteBuffer msg, boolean last, Session session) {
        System.out.println("Binary Data");      

        while(msg.hasRemaining()) {         
            try {
                fos.write(msg.get());
            } catch (IOException e) {               
                e.printStackTrace();
            }
        }

    }

    @OnMessage
    public void message(Session session, String msg) {
        System.out.println("got msg: " + msg);
        if (msg.contains("login") == true){
        	System.out.println("get login");
        }
        
        if(!msg.equals("end")) {
            fileName=msg.substring(msg.indexOf(':')+1);
            uploadedFile = new File(filePath+fileName);
            try {
                fos = new FileOutputStream(uploadedFile);
            } catch (FileNotFoundException e) {     
                e.printStackTrace();
            }
        }else {
            try {
            	if (fos != null){
                fos.flush();
                fos.close();}
            	
            } catch (IOException e) {       
                e.printStackTrace();
            }
        }
        
       
        JsonResponse jsonResponse = new JsonResponse("OK","123");
       ObjectMapper mapper = new ObjectMapper();
        
        String jsonString = "1234";
       
        try {
        	jsonString = mapper.writeValueAsString(jsonResponse);
			session.getBasicRemote().sendText(jsonString);;
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        
        
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
        System.out.println("socket closed: "+ reason.getReasonPhrase());
    }

    @OnError
    public void error(Session session, Throwable t) {
        t.printStackTrace();

    }
}