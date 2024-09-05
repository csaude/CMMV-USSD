package mz.org.fgh.vmmc.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import mz.org.fgh.vmmc.model.Menu;

public class MessageUtils {

       /**
        * Remover acentos na mensagem a ser devolvida
        * 
        * @param s
        * @return
        */
       public static String removeAccent(String s) {
	     String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
	     Pattern pattern = Pattern.compile("\\p{InCOMBINING_DIACRITICAL_MARKS}+");
	     return pattern.matcher(temp).replaceAll("");
       }

       public static String getMenuText(Menu menu) {
	     StringBuilder sb = new StringBuilder();

	     if (menu != null) {
		   sb.append(menu.getDescription()).append("\r\n");
		   for (Menu item : menu.getMenuItems()) {
			 sb.append(item.getDescription()).append("\r\n");
		   }
	     } else {
		   sb.append(ConstantUtils.MENU_PRINCIPAL_DESCRIPTION);
	     }

	     return removeAccent(sb.toString());

       }

       /**
        * retorna o valor corrente introduzido pelo user, No teste feito com o
        * AfricasTalking a cada interacao ele vai concatenando o que foi introduzido
        * Ex: ercilio*23*1*maputo
        * 
        * @param inputText
        */
       public static String formatInputText(String inputText) {

	     String[] textsArray = StringUtils.splitPreserveAllTokens(inputText, "*");
	     String formatedText = ArrayUtils.isNotEmpty(textsArray) ? textsArray[textsArray.length - 1] : StringUtils.EMPTY;
	     return removeAccent(formatedText);
       }

       public static boolean isValidOption(Menu menu, String inputText) {

	     return StringUtils.isNotBlank(inputText) && menu.getMenuItems().stream().filter(o -> o.getOption().equals(inputText)).findFirst().isPresent()
			 || (ConstantUtils.MENU_CLINICS_LIST_CODE.equalsIgnoreCase(menu.getCode()) || ConstantUtils.MENU_DISTRICTS_CODE.equalsIgnoreCase(menu.getCode())
				      || ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE.equalsIgnoreCase(menu.getCode()))
			 || menu.getMenuItems().size() == 1;

       }

       public static boolean isValidInput(String inputText) {

	     return StringUtils.isNotBlank(inputText) && StringUtils.length(inputText) > 0 && StringUtils.length(inputText) <= 140;
       }

       public static boolean isValidAge(String age) {
	     if (NumberUtils.isDigits(age)) {
		   return Integer.parseInt(age) > 14;
	     }
	     return false;
       }

       public static boolean isValidStringField(String value) {
	     return Pattern.compile("^[[a-zA-Z]+\\.?]{1,40}$").matcher(value).matches();
       }
}
