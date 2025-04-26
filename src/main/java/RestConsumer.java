import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RestConsumer {
    public static void main(String[] args) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> jsonToSend = new HashMap<>();
        jsonToSend.put("name", "Test name");
        jsonToSend.put("job", "Test job");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonToSend);


        // String url = "https://reqres.in/api/users/2";
        String url = "https://reqres.in/api/users/";
        //String response = restTemplate.getForObject(url, String.class);
        String response = restTemplate.postForObject(url, request, String.class);
        System.out.println("response: " + response);

        //Парсим JSON с помощью Jackson
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);//уже распаршенный

        String name = jsonNode.get("name").asText();

        System.out.println("имя: " + name);
    }
}
