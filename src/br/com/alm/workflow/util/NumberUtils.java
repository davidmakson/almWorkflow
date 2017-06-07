package br.com.alm.workflow.util;

public class NumberUtils {
	
	/**
	 * Implementar Integer.parseInt retornando 0 (zero) em caso de exceção
	 * 
	 * @param str
	 * @return
	 */
	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

}
