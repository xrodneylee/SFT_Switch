package sft.model;

public class Util {
	public static String convertString(char[] password){
		String passwordString = "";
		int num = password.length;
		for(int i=0 ; i<num ; i++){
			passwordString += password[i];
		}
		return passwordString;
	}
}
