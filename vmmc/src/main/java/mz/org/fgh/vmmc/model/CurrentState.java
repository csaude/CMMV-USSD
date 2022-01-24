package mz.org.fgh.vmmc.model;

import java.io.Serializable;

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

	public CurrentState(String sessionId) {
		super();
		this.sessionId = sessionId;
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

	@Override
	public String toString() {
		return "CurrentState [id=" + id + ", sessionId=" + sessionId + ", idMenu=" + idMenu + ", isActive=" + isActive
				+ "]";
	}

}
