package mz.org.fgh.vmmc.inout;

public class UtenteRegisterResponse extends GenericResponse {

    private String systemNumber;

    public UtenteRegisterResponse() {
	super();
    }

    public UtenteRegisterResponse(int status, String message) {
	super(status, message);
    }

    public String getSystemNumber() {
	return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
	this.systemNumber = systemNumber;
    }

    @Override
    public String toString() {
	return "UtenteRegisterResponse [systemNumber=" + systemNumber + "]";
    }
    
    

}
