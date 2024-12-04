package mz.org.fgh.vmmc.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.service.SessionDataService;

public class AddressMenuUtils {

	private List<District> districtList;
	private static AddressMenuUtils instance = new AddressMenuUtils();
	private int lastIndex;
	private int startIndex;
	private Map<String, Clinic> mapClinics;
	private List<Clinic> clinicsList = new ArrayList<Clinic>();
	private final int pagingSize = 4;
	private Map<String, Province> mapProvinces = new HashMap<>();
	private Map<String, District> mapDistricts = new HashMap<>();
	private List<Province> allProvinces = new ArrayList<>();

	private AddressMenuUtils() {
	}

	public static AddressMenuUtils getInstance() {
		return instance;
	}

	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public Map<String, Province> getMapProvinces() {
		return mapProvinces;
	}

	public void setMapProvinces(Map<String, Province> mapProvinces) {
		this.mapProvinces = mapProvinces;
	}

	public void setMapDistricts(Map<String, District> mapDistricts) {
		this.mapDistricts = mapDistricts;
	}

	public Map<String, District> getMapDistricts() {
		return mapDistricts;
	}
	
	public List<Province> getAllProvinces() {
		return allProvinces;
	}
	public void setAllProvinces(List<Province> allProvinces) {
		this.allProvinces = allProvinces;
	}

	public String getProvincesMenu() {
		String provinces = StringUtils.EMPTY;
		mapProvinces = new HashMap<>();
		this.allProvinces = RestClient.getInstance().getAllProvinces();
		int key = 1;
		for (Province province : allProvinces) {
			provinces += key + ". " + province.getDescription() + "\n";
			mapProvinces.put(String.valueOf(key), province);
			key++;
		}
		return provinces;
	}

	public String getDistrictsMenu(int idProvince, List<Province> allProvinces, UssdIn ussdIn) {

		if (!ussdIn.getContent().equalsIgnoreCase("#") && !ussdIn.getContent().equalsIgnoreCase("0")) {
			startIndex = 0;
			lastIndex = pagingSize;
			districtList = new ArrayList<District>();
			districtList = allProvinces.stream().filter(p -> p.getId() == (idProvince)).findFirst().get().getDistricts()
					.stream().sorted(Comparator.comparing(District::getDescription)).collect(Collectors.toList());
			int key = 1;
			for (District dis : districtList) {
				dis.setOption(key + "");
				key++;
			}

			String menuText = getFormatedDistrictByList(districtList.subList(startIndex,
					pagingSize > districtList.size() ? districtList.size() : pagingSize));

			startIndex = lastIndex;
			lastIndex = startIndex + pagingSize;
			return menuText;

		} else {
			if (lastIndex > districtList.size()) {
				return getFormatedDistrictByList(districtList.subList(startIndex, districtList.size()));
			}
			String menuText = getFormatedDistrictByList(districtList.subList(startIndex, lastIndex));
			startIndex = lastIndex;
			lastIndex = startIndex + pagingSize;
			return menuText;
		}
	}

	public String getFormatedDistrictByList(List<District> list) {
		mapDistricts = new HashMap<String, District>();
		String menuDistricts = StringUtils.EMPTY;

		for (District item : list) {
			menuDistricts += item.getOption() + ". " + item.getDescription() + "\n";
			mapDistricts.put(String.valueOf(item.getOption()), item);
		}

		return menuDistricts;
	}

	public String getClinicsByDistrictMenu(UssdIn request, CurrentState currentState,
			SessionDataService sessionDataService, Menu menu) {
		long districtId = Long.parseLong(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
		return MessageFormat.format(MessageUtils.getMenuText(menu), getClinicsByDistrictId(districtId, request));
	}

	// Devolve a lista de clinicas, sobre uma paginacao definida
	public String getClinicsByDistrictId(long districtId, UssdIn ussdIn) {

		if (ussdIn == null || !ussdIn.getContent().equalsIgnoreCase("#")) {

			clinicsList = RestClient.getInstance().getClinicsByDistrict(districtId).getClinics().stream()
					.sorted(Comparator.comparing(Clinic::getName)).collect(Collectors.toList());
			int key = 1;
			for (Clinic dis : clinicsList) {
				dis.setOption(key + "");
				key++;
			}
			Integer lastElementIndex = pagingSize > clinicsList.size() ? clinicsList.size() : pagingSize;
			String menuText = getClinicsMenu(clinicsList.subList(0, lastElementIndex));

			startIndex = lastElementIndex;
			lastIndex = startIndex + pagingSize;
			return menuText;

		} else {
			if (lastIndex > clinicsList.size()) {
				return getClinicsMenu(clinicsList.subList(startIndex, clinicsList.size()));
			}
			String menuText = getClinicsMenu(clinicsList.subList(startIndex, lastIndex));
			startIndex = lastIndex;
			lastIndex = lastIndex + pagingSize;
			return menuText;
		}
	}

	public Map<String, Clinic> getMapClinics() {
		return mapClinics;
	}
	public void setMapClinics(Map<String, Clinic> mapClinics) {
		this.mapClinics = mapClinics;
	}
	public String getClinicsMenu(List<Clinic> list) {
		this.mapClinics = new HashMap<String, Clinic>();
		String menuClinics = StringUtils.EMPTY;

		for (Clinic item : list) {
			menuClinics += item.getOption() + ". " + item.getName() + "\n";
			this.mapClinics.put(String.valueOf(item.getOption()), item);
		}

		return menuClinics;
	}

}
