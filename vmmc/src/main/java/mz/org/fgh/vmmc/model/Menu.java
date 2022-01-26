package mz.org.fgh.vmmc.model;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "Menu")
public class Menu {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "code")
	private String code;

	@Column(name = "description")
	private String description;

	@Column(name = "nextMenuId")
	private long nextMenuId;

	@OneToMany(mappedBy = "parentMenu")
	private List<Menu> menuItems;

	@ManyToOne
	@JoinColumn(name = "parent_menu_id")
	private Menu parentMenu;

	public Menu() {
	}

	public Menu(String code, String description, long nextMenuId) {
		super();
		this.code = code;
		this.description = description;
		this.nextMenuId = nextMenuId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getId() {
		return id;
	}

	public long getNextMenuId() {
		return nextMenuId;
	}

	public void setNextMenuId(long nextMenu) {
		this.nextMenuId = nextMenu;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Menu> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<Menu> menuItems) {
		this.menuItems = menuItems;
	}

	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}

}
