package org.oa.websocket;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	private static final String MESSAGE_START_LOGIN = "{\"login\":";
	private static final String MESSAGE_START_END = "end";
	private static final String MESSAGE_START_FILENAME = "filename:";
	static File uploadedFile = null;

	static FileOutputStream fos = null;
	final static String filePath = "e:/3/";
	private boolean auth = false;

	@OnOpen
	public void open(Session session, EndpointConfig conf) {
		System.out.println("session=" + session.getId() + " Auth=" + isAuth());
		System.out.println("chat ws server open");
	}

	@OnMessage
	public void processUpload(ByteBuffer msg, boolean last, Session session) {
		JsonResponse jsonResponse = null;

		System.out.println("Binary Data");
		System.out.println("session=" + session.getId() + " Auth=" + isAuth());
		if (isAuth() == true) {
			if (isAuth() == false) {
				this.close(session, null);
			}
			while (msg.hasRemaining()) {
				try {
					fos.write(msg.get());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			jsonResponse = new JsonResponse("OK", "File transfer");
		} else {
			jsonResponse = new JsonResponse("Error", "No Access");
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {

			jsonString = mapper.writeValueAsString(jsonResponse);
			session.getBasicRemote().sendText(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnMessage
	public void message(Session session, String msg) {
		System.out.println("session=" + session.getId() + " Auth=" + isAuth());
		System.out.println("got msg: " + msg);
		if (msg.startsWith(MESSAGE_START_LOGIN) == true) {
			check_authentication(session, msg);
		}

		if (msg.startsWith(MESSAGE_START_FILENAME) == true) {
			create_file(session, msg);
		}

		if (msg.startsWith(MESSAGE_START_END) == true) {
			doIfGetEnd(session, msg);
		}

	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		System.out.println("socket closed: " + reason.getReasonPhrase());
	}

	@OnError
	public void error(Session session, Throwable t) {
		t.printStackTrace();

	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	private void check_authentication(Session session, String msg) {
		System.out.println("get login");
		ObjectMapper mapper = new ObjectMapper();
		AuthenticationData auth = null;
		JsonResponse jsonResponse;
		try {
			auth = mapper.readValue(msg, AuthenticationData.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean resultAuth = auth.equals(new AuthenticationData("user", "pass"));
		if (resultAuth == true) {
			System.out.println("Auth true");
			this.setAuth(true);
			jsonResponse = new JsonResponse("OK", "Authentication OK");
		} else {
			System.out.println("Auth false");
			this.setAuth(false);
			jsonResponse = new JsonResponse("Error", "Authentication Error");
		}

		mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(jsonResponse);
			session.getBasicRemote().sendText(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void doIfGetEnd(Session session, String msg) {
		System.out.println("get end:");
		try {
			if (fos != null) {
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void create_file(Session session, String msg) {
		JsonResponse jsonResponse = null;
		if (isAuth() == true) {
			System.out.println("get file:");
			String fileName = msg.substring(msg.indexOf(':') + 1);
			uploadedFile = new File(filePath + fileName);
			try {
				fos = new FileOutputStream(uploadedFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			jsonResponse = new JsonResponse("OK", "File Created");
		} else {
			jsonResponse = new JsonResponse("Error", "No Access");
		}
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