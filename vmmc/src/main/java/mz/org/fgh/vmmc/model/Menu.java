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

	@Column(name = "level")
	private String level;

	/*
	 * @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY, cascade =
	 * CascadeType.ALL) private List<MenuItem> menuItems;
	 */

	@OneToMany(mappedBy = "parentMenu")
	private List<Menu> menuItems;

	@ManyToOne
	 @JoinColumn(name="parent_menu_id")
	private Menu parentMenu;
	
	

	
	public Menu() {
	}

	public Menu(String code, String description, String level) {
		super();
		this.code = code;
		this.description = description;
		this.level = level;
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
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
