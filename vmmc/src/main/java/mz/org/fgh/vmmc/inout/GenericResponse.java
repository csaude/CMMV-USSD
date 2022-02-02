package mz.org.fgh.vmmc.inout;

public abstract class GenericResponse {

    private long errorCode;
    private String errorMessage;

    public long getErrorCode() {
	return errorCode;
    }

    public void setErrorCode(long errorCode) {
	this.errorCode = errorCode;
    }

    @Override
    public String toString() {
	return "GenericResponse [errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
    }

    
}
