package mz.org.fgh.vmmc.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import mz.org.fgh.vmmc.inout.AppointmentSearchResponse;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.AppointmentResponse;
import mz.org.fgh.vmmc.inout.ClinicsResponse;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.inout.UtenteSearchResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.Login;
import mz.org.fgh.vmmc.model.Province;

public class RestClient {

       Logger LOG = Logger.getLogger(RestClient.class);
       private static String ENDPOINT = System.getProperty("endpoint");

       static RestTemplate restTemplate = new RestTemplate();

       private static RestClient instance = new RestClient();

       private RestClient() {

       }

       public static RestClient getInstance() {
	     return instance;
       }

       public String login() {

	     Map<String, String> param = new HashMap<>();
	     param.put("username", System.getProperty("username"));
	     param.put("password", System.getProperty("password"));
	     ResponseEntity<Login> postForEntity = restTemplate.postForEntity(ENDPOINT.concat("login"), param, Login.class);
	     ObjectMapper mapper = new ObjectMapper();
	     Object objects = postForEntity.getBody();
	     mapper.convertValue(objects, Login.class);
	     return ((Login) objects).getAccess_token();

       }

       public List<Province> getAllProvinces() {
	     String token = RestClient.getInstance().login();
	     HttpHeaders headers = new HttpHeaders();
	     headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	     headers.put("X-Auth-Token", Arrays.asList(token));
	     HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
	     ResponseEntity<Province[]> result = restTemplate.exchange(ENDPOINT.concat("province"), HttpMethod.GET, entity, Province[].class);
	     ObjectMapper mapper = new ObjectMapper();
	     Object objects = result.getBody();
	     mapper.convertValue(objects, Province[].class);
	     List<Province> lista = Arrays.asList((Province[]) objects).stream().sorted(Comparator.comparing(Province::getCode)).collect(Collectors.toList());
	     return lista;
       }

       public UtenteRegisterResponse registerUtente(@RequestBody UtenteRegisterRequest request) {
	     String token = RestClient.getInstance().login();
	     ResteasyClient client = new ResteasyClientBuilder().build();
	     ResteasyWebTarget target = client.target(ENDPOINT.concat("utente"));
	     Response response = target.request().header("X-Auth-Token", token).post(Entity.entity(request, "application/json"));
	     String jsonValue = response.readEntity(String.class);
	     if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != Response.Status.CREATED.getStatusCode()) {
		   LOG.error("[RestClient.registerUtente] response: " + jsonValue);
	     }
	     Gson gson = new Gson();
	     UtenteRegisterResponse responseReg = gson.fromJson(jsonValue, UtenteRegisterResponse.class);
	     responseReg.setStatusCode(response.getStatus());
	     response.close();
	     return responseReg;

       }

       public AppointmentResponse makeAppointment(@RequestBody AppointmentRequest request) throws Throwable {

	     String token = RestClient.getInstance().login();
	     ResteasyClient client = new ResteasyClientBuilder().build();
	     ResteasyWebTarget target = client.target(ENDPOINT.concat("appointment"));
	     Response response = target.request().header("X-Auth-Token", token).post(Entity.entity(request, "application/json"));
	     String jsonValue = response.readEntity(String.class);
	     if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != Response.Status.CREATED.getStatusCode()) {
		   LOG.error("[RestClient.makeAppointment] response: " + jsonValue);
	     }
	     Gson gson = new Gson();
	     AppointmentResponse responseReg = gson.fromJson(jsonValue, AppointmentResponse.class);
	     responseReg.setStatusCode(response.getStatus());
	     response.close();
	     return responseReg;
       }

       public ClinicsResponse getClinicsByDistrict(long districtId) {

	     String token = RestClient.getInstance().login();
	     ResteasyClient client = new ResteasyClientBuilder().build();
	     ResteasyWebTarget target = client.target(ENDPOINT.concat("clinic/district/")).path(districtId + "");
	     Response response = target.request().header("X-Auth-Token", token).get();
	     String jsonValue = response.readEntity(String.class);
	     if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != Response.Status.CREATED.getStatusCode()) {
		   LOG.error("[RestClient.getClinicsByDistrict] response: " + jsonValue);
	     }
	     Gson gson = new Gson();
	     Clinic[] clinics = gson.fromJson(jsonValue, Clinic[].class);
	     ClinicsResponse resp = new ClinicsResponse();
	     resp.setClinics(Arrays.asList(clinics));
	     resp.setStatusCode(response.getStatus());
	     response.close();
	     return resp;
       }

       public UtenteSearchResponse getUtenteBySystemNumber(String systemNumber) {

	     ResteasyClient client = new ResteasyClientBuilder().build();
	     ResteasyWebTarget target = client.target(ENDPOINT.concat("utente/search")).path(systemNumber);
	     Response response = target.request().get();
	     String jsonValue = response.readEntity(String.class);
	     if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != Response.Status.CREATED.getStatusCode()) {
		   LOG.error("[RestClient.getUtenteBySystemNumber] response: " + jsonValue);
	     }
	     Gson gson = new Gson();
	     UtenteSearchResponse responseUtente = gson.fromJson(jsonValue, UtenteSearchResponse.class);
	     responseUtente.setStatusCode(response.getStatus());
	     response.close();
	     return responseUtente;
       }

       public AppointmentSearchResponse getAppointmentByUtenteId(String utenteId) {

	     ResteasyClient client = new ResteasyClientBuilder().build();
	     ResteasyWebTarget target = client.target(ENDPOINT.concat("appointment/search")).path(utenteId);
	     Response response = target.request().get();
	     String jsonValue = response.readEntity(String.class);
	     if (response.getStatus() != Response.Status.OK.getStatusCode() && response.getStatus() != Response.Status.CREATED.getStatusCode()) {
		   LOG.error("[RestClient.getAppointmentByUtenteIdO] response: " + jsonValue);
	     }
	     Gson gson = new Gson();
	     AppointmentSearchResponse responseUtente = gson.fromJson(jsonValue, AppointmentSearchResponse.class);
	     responseUtente.setStatusCode(response.getStatus());
	     response.close();
	     return responseUtente;
       }

}
