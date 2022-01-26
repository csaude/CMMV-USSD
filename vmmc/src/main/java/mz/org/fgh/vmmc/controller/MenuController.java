package mz.org.fgh.vmmc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.repository.MenuRepository;
import mz.org.fgh.vmmc.service.MenuManagerService;

@RestController
public class MenuController {

	@Autowired
	MenuRepository menuRepo;

	@Autowired
	MenuManagerService service;

	@GetMapping(value = "menus")
	@ResponseBody
	public ResponseEntity<List<Menu>> getAllItems() {
		return new ResponseEntity<List<Menu>>(service.getAllMenus(), HttpStatus.OK);
	}


	@PostMapping(value = "add", consumes = { "application/json" }, produces = { "application/json" })
	@ResponseBody
	public ResponseEntity<Menu> addItem(@RequestBody Menu menu, UriComponentsBuilder builder) {
		menuRepo.save(menu);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("/addItem/{id}").buildAndExpand(menu.getId()).toUri());
		return new ResponseEntity<Menu>(headers, HttpStatus.CREATED);
	}

}
