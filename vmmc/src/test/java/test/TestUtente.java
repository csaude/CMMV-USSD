package test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.Address;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Login;
import mz.org.fgh.vmmc.model.Utente;

class TestUtente {

    static RestTemplate restTemplate = new RestTemplate();

 
    
    
    public String login() throws Exception {

	
	try {
	    
	    Map<String, String> param = new HashMap<>();
	    param.put("username", "admin");
	    param.put("password", "admin");
	    ResponseEntity<Login> postForEntity = restTemplate.postForEntity("http://dev.fgh.org.mz:4110/api/login", param, Login.class);
	    ObjectMapper mapper = new ObjectMapper();
	    Object objects = postForEntity.getBody();
	    mapper.convertValue(objects, Login.class);
	    // System.out.println("token output: " + ((Login) objects).getAccess_token());
	    return ((Login) objects).getAccess_token();

	} catch (Exception e) {
	    throw new Exception(" [RestClient.login]Ocorreu um erro de autenticacao do servico:", e);
	}

    }

    
    public UtenteRegisterResponse registerUtente(@RequestBody UtenteRegisterRequest request) throws Exception {

	
 	try {
 	    String token =  login(); 
 	    HttpHeaders headers = new HttpHeaders();
 	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
 	    headers.put("X-Auth-Token", Arrays.asList(token));

 	    Map<String, String> param = new HashMap<>();
 	   // param.put("firstNames", request.getFirstNames());
 	    //param.put("password", System.getProperty("password"));
 	    ResponseEntity<String> postForEntity = restTemplate.postForEntity("http://dev.fgh.org.mz:4110/api/utente", request, String.class);
 	    ObjectMapper mapper = new ObjectMapper();
 	    Object objects = postForEntity.getBody();
 	    mapper.convertValue(objects, UtenteRegisterResponse.class);
 	    // System.out.println("token output: " + ((Login) objects).getAccess_token());
 	    return ((UtenteRegisterResponse) objects) ;

 	} catch (Exception e) {
 	    throw new Exception(" [RestClient.login]Ocorreu um erro de autenticacao do servico:", e);
 	}

     }
    
    private UtenteRegisterRequest getUtenteRegisterRequest(Utente utente) {
	UtenteRegisterRequest request = new UtenteRegisterRequest();
	request.setFirstNames(utente.getFirstNames());
	request.setLastNames(utente.getLastNames());

	District district = new District();
	district.setId(utente.getDistrictId());
	district.setProvinceId(utente.getProvinceId());
	Address address = new Address();
	address.setResidence(utente.getAddress());
	address.setDistrict(district);

	request.setAddress(address);
	request.setCellNumber(utente.getCellNumber());
	request.setWhatsappNumber(utente.getCellNumber());
	request.setHaspartner(utente.getHaspartner());
	request.setAge(utente.getAge());

	return request;

    }
    
   /* @Test
    void test() throws Exception {
	Utente utente = new Utente("", " lastNames", "birthDate", " cellNumber", "true", " age", "provinceId", " districtId");

	  UtenteRegisterRequest utenteRegisterRequest = getUtenteRegisterRequest(utente);
	 
	UtenteRegisterResponse response =  registerUtente(utenteRegisterRequest);
	assertEquals(response, null);
    }
    */
    @Test
    void test() throws Exception {
	LocalDateTime dateTime = LocalDateTime.of(2022, 7, 4, 0, 0);
	LocalDateTime dateTime2 = LocalDateTime.now();

	int diffInNano = java.time.Duration.between(dateTime, dateTime2).getNano();
	long diffInSeconds = java.time.Duration.between(dateTime, dateTime2).getSeconds();
	long diffInMilli = java.time.Duration.between(dateTime, dateTime2).toMillis();
	long diffInMinutes = java.time.Duration.between(dateTime, dateTime2).toMinutes();
	long diffInHours = java.time.Duration.between(dateTime, dateTime2).toHours();
	
	System.out.println(diffInSeconds);
    }
    
     
    private long test(LocalDateTime startDate, LocalDateTime thruDate) {
	 

	   return startDate.until(thruDate, ChronoUnit.SECONDS);

 }
    
    

}
