package mz.org.fgh.vmmc.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.Login;
import mz.org.fgh.vmmc.model.Province;

public class RestClient {

    private static  String ENDPOINT = System.getProperty("endpoint");
 

    static RestTemplate restTemplate = new RestTemplate();

    private static RestClient instance = new RestClient();

    private RestClient() {

    }

    public static RestClient getInstance() {
	return instance;
    }

    public String login() throws Exception {

	
	try {
	    
	    Map<String, String> param = new HashMap<>();
	    param.put("username", System.getProperty("username"));
	    param.put("password", System.getProperty("password"));
	    ResponseEntity<Login> postForEntity = restTemplate.postForEntity(ENDPOINT.concat("login"), param, Login.class);
	    ObjectMapper mapper = new ObjectMapper();
	    Object objects = postForEntity.getBody();
	    mapper.convertValue(objects, Login.class);
	    // System.out.println("token output: " + ((Login) objects).getAccess_token());
	    return ((Login) objects).getAccess_token();

	} catch (Exception e) {
	    throw new Exception(" [RestClient.login]Ocorreu um erro de autenticacao do servico:", e);
	}

    }

    public List<Province> getAllProvinces() throws Exception {
	try {
	    
	    String token = RestClient.getInstance().login();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.put("X-Auth-Token", Arrays.asList(token));
	    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
	    ResponseEntity<Province[]> result = restTemplate.exchange(ENDPOINT.concat("province"), HttpMethod.GET, entity, Province[].class);
	    ObjectMapper mapper = new ObjectMapper();
	    Object objects = result.getBody();
	    mapper.convertValue(objects, Province[].class);
	    return Arrays.asList((Province[]) objects);
	} catch (Exception e) {
	    throw new Exception("[RestClient.getAllProvinces] Ocorreu um erro ao invocar o servico que lista provincias", e);
	}

    }

    public UtenteRegisterResponse registerUtente(@RequestBody UtenteRegisterRequest request) throws Exception {

	
	try {
	    String token = RestClient.getInstance().login(); 
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    headers.put("X-Auth-Token", Arrays.asList(token));  
	    ResponseEntity<String> postForEntity = restTemplate.postForEntity(ENDPOINT.concat("utente"), request, String.class);
	    ObjectMapper mapper = new ObjectMapper();
	    Object objects = postForEntity.getBody();
	    mapper.convertValue(objects, UtenteRegisterResponse.class); 
	    return ((UtenteRegisterResponse) objects) ;

	} catch (Exception e) {
	    throw new Exception(" [RestClient.login]Ocorreu um erro de autenticacao do servico:", e);
	}

    }

}
