package mz.org.fgh.vmmc.controller;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

 



/**
 * Created by dinesh on 3/9/17.
 */
@Controller
public class UssdService {
	
	static String level0="Sample Tips\n1.GYM Tips\n2.Love Tips\n\n000.Exit";
	static String  level1= "YM Tips\n1.abs tip for men\n2.abs tip for women\n999.Back\n000.Exit";
    static String  level2="Love Tips\n1.Friendship Love\n2.Romantic Love\n999.Back\n000.Exit";
    static String level11="->Improve Your Posture\n->Think Whole-Body Exercise.\n999.Back\n000.Exit";
	static String level12="->Train your lower abs first\n->Constantly increase your intensity/work rate.\n999.Back\n000.Exit";
	static  String level21="->Be willing to forgive.\n->Be reliable.\n999.Back\n000.Exit";
    static  String level22="->Listen to them talk about an interest of theirs.\n->Write a love note.\n999.Back\n000.Exit";

    // Service messages
    private static final String SERVICE_EXIT_CODE = "000";
    private static final String SERVICE_PREV_CODE = "999";
    private static final String SERVICE_INIT_CODE = "#123#";
    private static final String SERVICE_ELECTRONICS_CODE = "1";
    private static final String SERVICE_COSMETICS_CODE = "2";
    private static final String SERVICE_HOUSEHOLDS_CODE = "3";
    private static final String REQUEST_SEND_URL = "http://localhost:7000/ussd/send";
    private static final String OPERATION_MT_CONT = "mt-cont";
    private static final String OPERATION_MT_FIN = "mt-fin";

    PropertyReader propertyReader = new PropertyReader("application.properties");

    // List to store the states of the menus
    private ArrayList<String> menuStates = new ArrayList<>();
/*
    @PostMapping(path = "springUssd")
    public void onReceivedUssd(@RequestParam String sessionId, @RequestParam String serviceCode,
			@RequestParam String phoneNumber, @RequestParam String text) {
    	 
       
            try {
				processRequest( sessionId, serviceCode,
						 phoneNumber,text);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
    }

    // Process all kinds of requests to customer
    private void processRequest(@RequestParam String sessionId, @RequestParam String serviceCode,
			@RequestParam String phoneNumber, @RequestParam String text) throws MalformedURLException {
    	UssdRequest mtUssdReq;
        String destinationAddress = moUssdReq.getProvider();
        if (menuStates.size() > 0) {
            switch (text) {
                case SERVICE_INIT_CODE:
                    mtUssdReq = generateMTRequest(propertyReader.readProperty("welcome.page"), sessionId, OPERATION_MT_CONT, destinationAddress);
                    menuStates.add("welcome.page");
                    break;
                case SERVICE_EXIT_CODE:
                    mtUssdReq = generateMTRequest(propertyReader.readProperty("exit.page"),sessionId, OPERATION_MT_FIN, destinationAddress);
                    menuStates.clear();
                    break;
                case SERVICE_ELECTRONICS_CODE:
                    mtUssdReq = generateMTRequest(propertyReader.readProperty("electronics.page"), sessionId, OPERATION_MT_CONT, destinationAddress);
                    menuStates.add("electronics.page");
                    break;
                case SERVICE_COSMETICS_CODE:
                    mtUssdReq = generateMTRequest(propertyReader.readProperty("cosmetics.page"), sessionId,, OPERATION_MT_CONT, destinationAddress);
                    menuStates.add("cosmetics.page");
                    break;
                case SERVICE_HOUSEHOLDS_CODE:
                    mtUssdReq = generateMTRequest(propertyReader.readProperty("households.page"),sessionId, OPERATION_MT_CONT, destinationAddress);
                    menuStates.add("households.page");
                    break;
                case SERVICE_PREV_CODE:
                    mtUssdReq = generateMTRequest(backOperation(),sessionId, OPERATION_MT_CONT, destinationAddress);
                    break;
                default:
                    mtUssdReq = generateMTRequest(propertyReader.readProperty("error.page"), moUssdReq.getSessionId(), OPERATION_MT_CONT, destinationAddress);
                    menuStates.add("error.page");
            }
        } else {
            mtUssdReq = generateMTRequest(propertyReader.readProperty("error.page"), moUssdReq.getSessionId(), OPERATION_MT_CONT, destinationAddress);
            menuStates.add("error.page");
        }

        //UssdRequestSender ussdRequestSender = new UssdRequestSender(new URL(REQUEST_SEND_URL));
        //ussdRequestSender.sendUssdRequest(mtUssdReq);
        //System.out.println(menuStates);
    }

    // Generate request to the customer
    private UssdRequest generateMTRequest(String message, String sessionId, String operation, String destinationAddress) {
    	UssdRequest mtUssdReq = new UssdRequest(destinationAddress, destinationAddress, sessionId, message);
       /* mtUssdReq.setApplicationId("APP_000001");
        mtUssdReq.setPassword("dfc0333b82a8e01f500e7e37188f97eo");
        mtUssdReq.setMessage(message);
        mtUssdReq.setSessionId(sessionId);
        mtUssdReq.setUssdOperation(operation);
        mtUssdReq.setDestinationAddress(destinationAddress);
        return mtUssdReq;
    }
*/
    // Functionality of the back command
    private String backOperation() {
        String prevState = propertyReader.readProperty("welcome.page");
        System.out.println(menuStates.size());
        if (menuStates.size() > 0 && (menuStates.size() - 1) != 0) {
            prevState = propertyReader.readProperty(menuStates.get(menuStates.size() - 2));
            menuStates.remove(menuStates.size() - 1);
        }
        return prevState;
    }
    
}
