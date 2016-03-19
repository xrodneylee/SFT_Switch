package sft.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.ini4j.Ini;

import ca.beq.util.win32.registry.RegistryKey;
import ca.beq.util.win32.registry.RegistryValue;
import ca.beq.util.win32.registry.RootKey;

public class RegInstall {
	
	static {
		try {
			LoadJarDll.loadJarDll("jRegistryKey.dll");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.load("C:\\Users\\GuanPu\\Desktop\\switch_ver_1\\jRegistryKey.dll");
	}
	
	RegistryKey sftKey,erpKey,conductorKey;
	RegistryValue sftValue,erpValue,conductorValue;	
	JOptionPane worning = new JOptionPane();
	public String getReg(){
		String WinBit1,WinBit2;
		WinBit1 = System.getenv("PROCESSOR_ARCHITEW6432");
		WinBit2 = System.getenv("PROCESSOR_ARCHITECTURE");
		System.out.print(System.getenv());
		try{
			if(WinBit1.equals("AMD64") || WinBit2.equals("AMD64")){
				sftKey = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,"Software\\Wow6432Node\\DCI\\SFT");
			}else{
				sftKey = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,"Software\\DCI\\SFT");
			}
			
			if(sftKey.hasValue("InstallPath")){
				sftValue = sftKey.getValue("InstallPath");
			}
			System.out.println(sftValue.getData().toString());
			return sftValue.getData().toString();
		}catch(Exception e){
			return "此電腦無安裝SFT";
		}
	}
	public String getERPreg(){
		String WinBit1,WinBit2;
		WinBit1 = System.getenv("PROCESSOR_ARCHITEW6432");
		WinBit2 = System.getenv("PROCESSOR_ARCHITECTURE");
		try{
			if(WinBit1.equals("AMD64") || WinBit2.equals("AMD64")){
				erpKey = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,"Software\\DigiWinSoftFactory\\WF_Tools\\ErpSwitch");
			}else{
				erpKey = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,"Software\\DigiWinSoftFactory\\WF_Tools\\ErpSwitch");
			}

			if(erpKey.hasValue("PreRunningBPL")){
				erpValue = erpKey.getValue("PreRunningBPL");
			}
			return erpValue.getData().toString();
		}catch(Exception e){
			return "None";
		}
	}
	public Map getERPConductor(){
		String WinBit1,WinBit2;
		Map dataMap = new HashMap();
		WinBit1 = System.getenv("PROCESSOR_ARCHITEW6432");
		WinBit2 = System.getenv("PROCESSOR_ARCHITECTURE");
		try{
			if(WinBit1.equals("AMD64") || WinBit2.equals("AMD64")){
				conductorKey = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,"SOFTWARE\\Wow6432Node\\DSC\\CONDUCTOR");
			}else{
				conductorKey = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,"SOFTWARE\\Wow6432Node\\DSC\\CONDUCTOR");
			}

			if(conductorKey.hasValue("clientpath")){
				conductorValue = conductorKey.getValue("clientpath");
			}
			
			String strLine;
			BufferedReader bufferedReader = new BufferedReader(new FileReader(conductorValue.getData().toString()+"ConductorS.INI"));
			StringBuffer sb = new StringBuffer();
			while ((strLine = bufferedReader.readLine()) != null) {
			    if(strLine.contains(";") && strLine.replace(";", "").length()>0) {
			    	System.out.println(strLine);
			     strLine = strLine.split(";")[0];
			    }
			    strLine = strLine.trim();
			    sb.append(strLine);
			    sb.append("\n");
			   }
			Ini ini = new Ini(new StringReader(sb.toString()));
			Ini.Section DBESetting = ini.get("DBE Setting");
			dataMap.put("MainServerName", DBESetting.get("MainServerName").toString());
			dataMap.put("MainDBName", DBESetting.get("MainDBName").toString());
			return dataMap;
		}catch(Exception e){
			dataMap.put("error", "None");
			return dataMap;
		}
	}
}