package mz.org.fgh.vmmc.model;

import java.io.Serializable;

public class District implements Serializable {

       /**
        * 
        */
       private static final long serialVersionUID = 1L;
       private long id;
       private String code;
       private String description;
       private String provinceId;
       private transient String option;

       public District() {
	     super();

       }

       public String getCode() {
	     return code;
       }

       public void setCode(String code) {
	     this.code = code;
       }

       public String getDescription() {
	     return description;
       }

       public String getProvinceId() {
	     return provinceId;
       }

       public static long getSerialversionuid() {
	     return serialVersionUID;
       }

       public void setDescription(String description) {
	     this.description = description;
       }

       public long getId() {
	     return id;
       }

       public void setId(long id) {
	     this.id = id;
       }

       public void setProvinceId(String provinceId) {
	     this.provinceId = provinceId;
       }

       public String getOption() {
	     return option;
       }

       public void setOption(String option) {
	     this.option = option;
       }

}
