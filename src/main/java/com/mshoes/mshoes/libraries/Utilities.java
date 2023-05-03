package com.mshoes.mshoes.libraries;

import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Utilities {

	/**
	 * Method get current date and format output to String <br>
	 * <u><i>Update: 26/02/2023</i></u>
	 *
	 * @author manhminh
	 */
	public String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

//	/**
//	 * Method encode password to hexMD5<br>
//	 * <u><i>Update: 02/03/2023</i></u>
//	 *
//	 * @param str
//	 * @return
//	 */
//	public String encodeToMD5(String str) {
//		return DigestUtils.md5Hex(str).toUpperCase();
//	}

//	/**
//	 * Method decode password to hexMD5<br>
//	 * <u><i>Update: 02/03/2023</i></u>
//	 *
//	 * @param str
//	 * @return
//	 */
//	public String decodeToMD5(String strMD5) {
//		return strMD5;
//	}
}
