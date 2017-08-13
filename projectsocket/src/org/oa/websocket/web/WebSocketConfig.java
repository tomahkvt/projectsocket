package org.oa.websocket.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import javax.websocket.CloseReason;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import org.oa.websocket.model.JsonResponse;
import org.oa.websocket.model.User;
import org.oa.websocket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.server.standard.SpringConfigurator;

/**
 * Class description goes here.
 *
 * @version 0.03 14 Aug 2017
 * @author Tomakh Konstatin
 */
@Service
@ServerEndpoint(value = "/receive/fileserver", configurator = SpringConfigurator.class)
public class WebSocketConfig {
	@Autowired
	private UserRepository userRepository;
	private static final String COMMAND = "cmd /c dir /a ";
	final static String filePath = "e:/3";
	private static final String MESSAGE_START_LOGIN = "{\"login\":";
	private static final String MESSAGE_START_FILENAME = "filename:";
	private File uploadedFile = null;
	private FileOutputStream fos = null;
	private boolean auth = false;
	private static Logger log = Logger.getLogger(WebSocketConfig.class);
	
	public WebSocketConfig() {

	}

	@OnOpen
	public void open(Session session, EndpointConfig conf) {
		log.info("Websocket opened session=" + session.getId());
		System.out.println("chat ws server open");
	}

	
	 /**
	  * This method uses for upload file to server
	  * @param msg messgae
	  * @param last if this last message last = true
	  * @param session session of WebSocket
	  */
	@OnMessage
	public void processUpload(ByteBuffer msg, boolean last, Session session) {
		if (isAuth() == true) {
			while (msg.hasRemaining()) {
				try {
					fos.write(msg.get());
				} catch (IOException e) {
					log.error("Error write file" + e.getMessage());
				}
			}
		}

		if (last == true) {
			log.info("Binary Data session=" + session.getId() + " Auth=" + isAuth());
			this.closeFile(session);
			this.excuteCommand(session);
			if (isAuth() == true) {
				sendMessageToClient(session, "OK", "File transfer");
			} else {
				sendMessageToClient(session, "Error", "No Access");
			}
		}
	}


	 /**
	  * This method execute command in shell
	  * var command set in constant COMMAND
	  * @param session session of WebSocket
	  */
	private void excuteCommand(Session session) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			System.out.println(COMMAND + uploadedFile.getAbsolutePath());
			p = Runtime.getRuntime().exec(COMMAND + uploadedFile.getAbsolutePath());
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (Exception e) {
			log.error("Websocket error executing comand session=" + session.getId() + 
					" Error:" + e.getMessage());
		}

		log.info("Result executing command:" + output.toString());
		sendMessageToClient(session, "Result Execute comand", output.toString());
	}

	
	 /**
	  * This method get message and check begin sequence and call the corresponding method 
	  * @param session session of WebSocket
 	  * @param message
	  */
	@OnMessage
	public void message(Session session, String msg) {
		log.info("Web socket session=" + session.getId() + " Auth=" + isAuth());
		log.info("Web socket got msg: " + msg);
		if (msg.startsWith(MESSAGE_START_LOGIN) == true) {
			check_authentication(session, msg);
		}

		if (msg.startsWith(MESSAGE_START_FILENAME) == true) {
			create_file(session, msg);
		}

	}

	
	 /**
	  * This method get message and check begin sequence and call the corresponding method 
	  * @param session session of WebSocket
	  * @param reason of closing Websocket
	  */
	@OnClose
	public void close(Session session, CloseReason reason) {
		File uploadDirectory = new File(filePath + "/" + session.getId());
		if (uploadDirectory.exists() == true) {
			File[] contents = uploadDirectory.listFiles();
			for (File f : contents) {
				f.delete();
			}
			uploadDirectory.delete();
		}
		log.info("Websocket closed session=" + session.getId());
	}

	@OnError
	public void error(Session session, Throwable t) {
		log.error("Websocket closed session=" + session.getId() + 
				" Error:" + t.getMessage());
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	
	 /**
	  * This method check user authentication 
	  * @param session session of WebSocket
	  * @param message
	  */
	private void check_authentication(Session session, String msg) {
		log.info("Websocket authentication session=" + session.getId());
		ObjectMapper mapper = new ObjectMapper();
		User user = null;

		try {
			user = mapper.readValue(msg, User.class);
		} catch (JsonParseException e) {
			log.error("Websocket session=" + session.getId() + "JsonParseException:" +e.getMessage());
		} catch (JsonMappingException e) {
			log.error("Websocket session=" + session.getId() + "JsonMappingException:" +e.getMessage());
		} catch (IOException e) {
			log.error("Websocket session=" + session.getId() + "IOException:" +e.getMessage());
		}

		boolean resultAuth = userRepository.checkAuthentication(user);
		if (resultAuth == true) {
			log.info("Websocket authentication true session=" + session.getId());
			this.setAuth(true);
			sendMessageToClient(session, "OK", "Authentication OK");
		} else {
			log.info("Websocket authentication false session=" + session.getId());
			this.setAuth(false);
			sendMessageToClient(session, "Error", "Authentication Error");
		}
	}

	 /**
	  * This method close file 
	  * @param session session of WebSocket
	  */	
	private void closeFile(Session session) {

		try {
			if (fos != null) {
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			log.error("Websocket session=" + session.getId() + "on file close IOException:" +e.getMessage());
		}
	}


	 /**
	  * This method close file 
	  * @param session session of WebSocket
	  * @param message
	  */	
	private void create_file(Session session, String msg) {
		File uploadDirectory = null;
		if (isAuth() == true) {
			System.out.println("get file:");
			String fileName = msg.substring(msg.indexOf(':') + 1);
			uploadDirectory = new File(filePath + "/" + session.getId());
			if (uploadDirectory.exists() == false) {
				uploadDirectory.mkdirs();
			}
			uploadedFile = new File(filePath + "/" + session.getId() + "/" + fileName);
			try {
				fos = new FileOutputStream(uploadedFile);
			} catch (FileNotFoundException e) {
				log.error("Websocket session=" + session.getId() + "file not found IOException:" +e.getMessage());
			}
			sendMessageToClient(session, "OK", "File Created");
		} else {
			sendMessageToClient(session, "Error", "No Access");
		}
	}

	 /**
	  * This method for send message to Websocket client
	  * @param session session of WebSocket
	  * @param status
	  * @param response
	  */	
	private void sendMessageToClient(Session session, String status, String response) {
		JsonResponse jsonResponse = new JsonResponse(status, response);
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(jsonResponse);
			session.getBasicRemote().sendText(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}