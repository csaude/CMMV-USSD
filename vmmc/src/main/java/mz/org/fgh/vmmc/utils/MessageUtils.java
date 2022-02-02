package mz.org.fgh.vmmc.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
	    sb.append(menu.getDescription()).append("\n");
	    for (Menu item : menu.getMenuItems()) {
		sb.append(item.getDescription()).append("\n");
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

	String[] textsArray = StringUtils.split(inputText, "*");
	String formatedText = ArrayUtils.isNotEmpty(textsArray) ? textsArray[textsArray.length - 1] : inputText;
	return removeAccent(formatedText);
    }

}
