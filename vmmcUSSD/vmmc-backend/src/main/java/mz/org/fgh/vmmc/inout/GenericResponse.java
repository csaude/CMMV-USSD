package mz.org.fgh.vmmc.inout;

public class GenericResponse {

    private int statusCode;
    private String message;

    public GenericResponse() {
	super();
    }

    public GenericResponse(int status, String message) {
	super();
	this.statusCode = status;
	this.message = message;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    @Override
    public String toString() {
	return "GenericResponse [statusCode=" + statusCode + ", message=" + message + "]";
    }

}
