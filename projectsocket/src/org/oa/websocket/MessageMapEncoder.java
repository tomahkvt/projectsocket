package org.oa.websocket;
 
import org.codehaus.jackson.map.ObjectMapper;
 
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.util.Map;
 
public class MessageMapEncoder implements Encoder.Text<JsonResponse> {
 
    @Override
    public String encode(JsonResponse map) throws EncodeException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(map);
        } catch (IOException e) {
            System.out.print("Problem with Encoder: " + e.getMessage());
            return "-";
        }
    }
 
    @Override
    public void init(EndpointConfig config) {
        System.out.println("MessageMapEncoder - init method called");
    }
 
    @Override
    public void destroy() {
        System.out.println("MessageMapEncoder - destroy method called");
    }
}