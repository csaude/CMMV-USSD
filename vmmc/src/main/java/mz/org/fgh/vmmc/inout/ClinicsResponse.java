package mz.org.fgh.vmmc.inout;

import java.util.List;

import mz.org.fgh.vmmc.model.Clinic;

public class ClinicsResponse extends GenericResponse {

    private List<Clinic> clinics;
    

    public List<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(List<Clinic> clinics) {
        this.clinics = clinics;
    }
    
    
 }
