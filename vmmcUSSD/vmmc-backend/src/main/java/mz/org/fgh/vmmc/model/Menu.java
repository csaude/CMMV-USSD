package mz.org.fgh.vmmc.model;

import java.util.Collections;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "Menu")
public class Menu implements Comparable<Menu> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "code")
	private String code;

	@Column(name = "option")
	private String option;

	@Column(name = "description")
	private String description;

	@Column(name = "nextMenuId")
	private long nextMenuId;

	@OneToMany(mappedBy = "parentMenu")
	private List<Menu> menuItems;

	@Column(name = "menuField")
	private String menuField;

	@ManyToOne
	@JoinColumn(name = "parent_menu_id")
	private Menu parentMenu;

	@Column(name = "orderMenu")
	private Integer orderMenu;

	@Column(name = "action")
	private String action;

	public Menu() {
	}

	public Menu(String code, String option, String description, long nextMenuId, List<Menu> menuItems, String menuField,
			Menu parentMenu) {
		super();
		this.code = code;
		this.option = option;
		this.description = description;
		this.nextMenuId = nextMenuId;
		this.menuItems = menuItems;
		this.menuField = menuField;
		this.parentMenu = parentMenu;
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
		Collections.sort(this.menuItems);
		return this.menuItems;
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

	public String getMenuField() {
		return menuField;
	}

	public void setMenuField(String menuField) {
		this.menuField = menuField;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public int compareTo(Menu otherMenu) {
		return Integer.compare(this.orderMenu, otherMenu.orderMenu);
	}

}
