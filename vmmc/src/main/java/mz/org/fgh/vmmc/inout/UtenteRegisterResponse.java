package mz.org.fgh.vmmc.inout;

public class UtenteRegisterResponse extends GenericResponse {
    private String registerCode;

    public UtenteRegisterResponse(String registerCode) {
	super();
	this.registerCode = registerCode;
    }

    public UtenteRegisterResponse() {
	super();

    }

    public String getRegisterCode() {
	return registerCode;
    }

    public void setRegisterCode(String registerCode) {
	this.registerCode = registerCode;
    }

    @Override
    public String toString() {
	return "UtenteRegisterResponse [registerCode=" + registerCode + "]";
    }

    
}
