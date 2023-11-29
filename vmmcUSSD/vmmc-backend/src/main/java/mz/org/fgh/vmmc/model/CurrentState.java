package mz.org.fgh.vmmc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CurrentState")
public class CurrentState implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sessionId")
    private String sessionId;

    @Column(name = "idMenu")
    private long idMenu;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "location")
    private String location;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "createdDate", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    public CurrentState(String sessionId, long idMenu, boolean isActive, String location, String phoneNumber, LocalDateTime createdDate) {
	super();
	this.sessionId = sessionId;
	this.idMenu = idMenu;
	this.isActive = isActive;
	this.location = location;
	this.phoneNumber = phoneNumber;
	this.createdDate = createdDate;
    }

    public CurrentState() {
	super();
    }

    public Long getId() {
	return id;
    }

    public String getSessionId() {
	return sessionId;
    }

    public boolean isActive() {
	return isActive;
    }
    public void setId(Long id) {
	this.id = id;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
    }

    public long getIdMenu() {
	return idMenu;
    }

    public void setIdMenu(long idMenu) {
	this.idMenu = idMenu;
    }

    public void setActive(boolean isActive) {
	this.isActive = isActive;
    }
    
    

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
	this.createdDate = createdDate;
    }

    @Override
    public String toString() {
	return "CurrentState [id=" + id + ", sessionId=" + sessionId + ", idMenu=" + idMenu + ", isActive=" + isActive + ", location=" + location + ", phoneNumber=" + phoneNumber
		+ ", createdDate=" + createdDate + "]";
    }

}
