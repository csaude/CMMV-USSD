package test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import mz.org.fgh.vmmc.inout.ClinicsResponse;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.Address;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Login;
import mz.org.fgh.vmmc.model.LoginRequest;
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

    public String registerUtente(@RequestBody UtenteRegisterRequest request) throws Exception {

	/*
	 * String token = login(); HttpHeaders headers = new HttpHeaders();
	 * headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	 * headers.put("X-Auth-Token", Arrays.asList(token));
	 * headers.set("Authorization", "Bearer " + token);
	 * 
	 * HttpEntity<String> entity = new HttpEntity<String>("{}", headers); return
	 * restTemplate.postForObject("http://dev.fgh.org.mz:4110/api/utente", entity,
	 * String.class);
	 */

	System.out.println("iniciandoo....");
	Utente utente = new Utente("Ercilio", " lastNames", "birthDate", "cellNumber", true, " age", "provinceId", " districtId");

	UtenteRegisterRequest utenteRegisterRequest = getUtenteRegisterRequest(utente);

	ResteasyClient client = new ResteasyClientBuilder().build();
	ResteasyWebTarget target = client.target("http://dev.fgh.org.mz:4110/api/utente");
	Response response = target.request().post(Entity.entity(utenteRegisterRequest, "application/json"));
	System.out.println(response.getStatus());
	response.close();
	return "";

    }

    private UtenteRegisterRequest getUtenteRegisterRequest(Utente utente) {
	UtenteRegisterRequest request = new UtenteRegisterRequest();
	request.setFirstNames(utente.getFirstNames());
	request.setLastNames(utente.getLastNames());
	request.setWhatsappNumber(utente.getCellNumber());
	request.setCellNumber(utente.getCellNumber());
	request.setRegisterDate(ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ));
	request.setBirthDate(ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ));
	request.setStatus("CONFIRMADO");

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

    @Test
    void test() throws Exception {

	System.out.println("iniciandoo....");

	LoginRequest req = new LoginRequest("admi", "admin1");
	ResteasyClient client = new ResteasyClientBuilder().build();
	ResteasyWebTarget target = client.target("http://dev.fgh.org.mz:4110/api/login");
	Response response = target.request().post(Entity.entity(req, "application/json"));

	String value = response.readEntity(String.class);
	Gson gson = new Gson();
	Login login = gson.fromJson(value, Login.class);

	System.out.println("StatussHTTP: " + response.getStatus());
	// System.out.println("Testing:"+
	// response.status(200).entity(response).build().toString());
	System.out.println("COMMEHHH:" + login.getToken_type());
	response.close();

    }
    
    @Test
    void testListClinicByDistrict() throws Exception {
	 
	
	String token = login();;
	ResteasyClient client = new ResteasyClientBuilder().build();
	ResteasyWebTarget target = client.target("http://dev.fgh.org.mz:4110/api/clinic/district/").path( "1");
	Response response = target.request().header("X-Auth-Token", token).get();
	String jsonValue = response.readEntity(String.class);
	if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != Response.Status.CREATED.getStatusCode()) {
	    System.out.println("[RestClient.makeAppointment] response: " + jsonValue);
	}
	Gson gson = new Gson();
	Clinic[] responseReg = gson.fromJson(jsonValue, Clinic[].class);
	ClinicsResponse resp = new ClinicsResponse();
	resp.setClinics(Arrays.asList(responseReg));
	resp.setStatusCode(response.getStatus());
	response.close();
	System.out.println("A resposta: "+resp.toString());
	System.out.println("A resposta: "+resp.getClinics().toString());
    }
 

    @Test
    void testCadastroUtente() throws Exception {
 
	
	System.out.println("CURRENTDATE..: "+ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ));

	System.out.println("iniciandoo....");
	String token = login();
	Utente utente = new Utente("Ercilioo", " lastNames", ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT ), " 258847630629", true, " age", "provinceId", " districtId");

	UtenteRegisterRequest utenteRegisterRequest = getUtenteRegisterRequest(utente);
	ResteasyClient client = new ResteasyClientBuilder().build();
	ResteasyWebTarget target = client.target("http://dev.fgh.org.mz:4110/api/utente");
	Response response = target.request().header("X-Auth-Token", token).post(Entity.entity(utenteRegisterRequest, "application/json")); 
	String jsonValue = response.readEntity(String.class);
	
	System.out.println("RESPONSEEE:  "+jsonValue);
	Gson gson = new Gson();
	UtenteRegisterResponse responseReg = gson.fromJson(jsonValue, UtenteRegisterResponse.class);
	responseReg.setStatusCode(response.getStatus());
	response.close();
	 System.out.println(responseReg.toString());

    }
    
    @Test
    void testFormatDate() throws ParseException {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS");
	String timeString = "2022-04-12T00:00:00Z";
			   
	Date date = dateFormat.parse(timeString);
	System.out.println(date);
	 
	LocalDateTime ldti= LocalDateTime.ofInstant(Instant.parse(timeString), ZoneOffset.UTC);
	 System.out.println(ldti.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
	 
    }
}
