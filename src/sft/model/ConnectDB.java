package sft.model;
import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sft.view.ConfigPage;
public class ConnectDB {
	private String URL;
	private String USER;
	private char[] PASSWORD;
	private String SFTSYS;
	private String SFT;
	private String ERP;
	private Connection conn=null;
	private SQLException ex=null;
	private Statement stmt;
	
	public void setURL(String URL){
		this.URL = URL;
	}
	public void setUSER(String USER){
		this.USER = USER;
	}
	public void setPASSWORD(char[] PASSWORD){
		this.PASSWORD = PASSWORD;
	}
	public void setSFTSYS(String SFTSYS){
		this.SFTSYS=SFTSYS;
	}
	public void setSFT(String SFT){
		this.SFT=SFT;
	}
	public void setERP(String ERP){
		this.ERP=ERP;
	}
	public String getURL(){
		return URL;
	}
	public String getUSER(){
		return USER;
	}
	public char[] getPASSWORD(){
		return PASSWORD;
	}
	public String getSFTSYS(){
		return SFTSYS;
	}
	public String getSFT(){
		return SFT;
	}
	public String getERP(){
		return ERP;
	}
	
	public static String convertString(char[] password){
		String passwordString = "";
		int num = password.length;
		for(int i=0 ; i<num ; i++){
			passwordString += password[i];
		}
		return passwordString;
	}
	
	public Map Connect(){
		Map dataMap = new HashMap();
		try {
			conn=DriverManager.getConnection("jdbc:sqlserver://"+getURL(),getUSER(),convertString(getPASSWORD()));
			System.out.printf("已%s資料庫連線\n",conn.isClosed()?"關閉":"開啟");
			dataMap.put("flag", true);
			dataMap.put("msg", "連線成功");
		} catch (SQLException e) {
			dataMap.put("flag", false);
			dataMap.put("msg", "連線失敗");
			ConfigPage.log(ConfigPage.exceptionStacktraceToString(e));
			e.printStackTrace();
		}
		return dataMap;
	}
	public ResultSet QueryCompany(String dscsys) throws SQLException{
		String sql = " SELECT * FROM DSCMB ";
		stmt=conn.createStatement();
		return stmt.executeQuery(" USE "+dscsys+sql);
	}
	public boolean exist(String sft){
		boolean flag = true;
		String sql = " select * from sys.sysdatabases where name='"+sft+"'";
		try {
			stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(!rs.next()){
				flag = false;
			}	
		} catch (SQLException e) {
			ConfigPage.log(ConfigPage.exceptionStacktraceToString(e));
			e.printStackTrace();
		}		
		return flag;
	}
	public void restore(String db,String bak,String ip) throws SQLException{
		String sql1,sql2;
		String SqlDBpath = null;
		String DBLNDAT = null,DBLNLOG = null;
		String convertBak = db.split("_")[0];
		Config.loadProperties();
		sql1 = " SELECT name, physical_name FROM sys.master_files where name ='master' ";		

		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql1);
		while (rs.next()) {
			SqlDBpath = rs.getString("physical_name").split("master")[0];
		}
		String mdf_path = SqlDBpath + db + "_dat.mdf";
		String ldf_path = SqlDBpath + db + "_log.ldf";
		if (convertBak.equals("SFTSYS")) {
			DBLNDAT = "SFT_common";
			DBLNLOG = "SFT_common_log";
		} else {
			DBLNDAT = "SFTSTANDARD_dat";
			DBLNLOG = "SFTSTANDARD_log";
		}
		sql2 = " RESTORE DATABASE " + db + " FROM DISK='"+ Config.getConfig(ip) + bak + "'";
		sql2 += " WITH  MOVE '" + DBLNDAT + "' TO '" + mdf_path + "' , ";
		sql2 += "       MOVE '" + DBLNLOG + "' TO '" + ldf_path + "'";

		System.out.println(sql2);
		ConfigPage.log(sql2);
		stmt.execute(sql2);
	}
	public ResultSet querySFTDB(String sftsys,String erp,String cmp) throws SQLException{	
		String sql = " SELECT CS004,MB001 FROM SFT_COMPANYSET LEFT JOIN ";
		sql += erp+"..DSCMB ON CS001=MB001 ";
		sql += " WHERE CS007='"+cmp+"'";
		stmt=conn.createStatement();
		return stmt.executeQuery(" USE "+sftsys+sql);
	}
	
	public ResultSet QueryMB001(String cmp) throws SQLException{
		String sql = " SELECT * FROM DSCMB 	WHERE MB002='"+cmp+"'";
		stmt=conn.createStatement();
		return stmt.executeQuery(" USE "+ERP+sql);
	}
	
	public void InsertCompany(String COMPANY,String CS001,String CS004,String CS005,String CS006,String CS007) throws SQLException{
		String sql;
		sql = " INSERT INTO SFT_COMPANYSET (COMPANY, CS001, CS002, CS003, CS004, CS005, CS006, CS007, CS008, CS009, CS010, CS011, CS012) ";
		sql += " VALUES ('"+COMPANY+"','"+CS001+"','net.sourceforge.jtds.jdbc.Driver','jdbc:jtds:sqlserver://sqlserver:1433/"+CS004+"','";
		sql += CS004+"','"+CS005+"','"+CS006+"','"+CS007+"','N','N','N','N','N')";
		stmt=conn.createStatement();
		stmt.executeUpdate(" USE "+COMPANY+sql);
	}
	public void updateSFTSYS(String COMPANY,String CS005,String CS006) throws SQLException{
		String sql;
		sql = " UPDATE SFT_COMPANYSET SET  CS005='"+CS005+"',CS006='"+CS006+"' WHERE CS001='SFTSYS' ";
		stmt=conn.createStatement();
		stmt.executeUpdate(" USE "+COMPANY+sql);
	}
	public void executeSQL(String sql) throws SQLException{
		stmt=conn.createStatement();
		stmt.executeUpdate(sql);
	}
	public boolean checkCmp(String sftsys,String CS001) throws SQLException{
		boolean flag = false;
		String sql;
		ResultSet rs;
		sql = " SELECT * FROM  SFT_COMPANYSET WHERE CS001='"+CS001+"'";
		stmt=conn.createStatement();
		rs = stmt.executeQuery(" USE "+sftsys+sql);
		while(rs.next()){
			flag = true;
		}
		return flag;
	}
	public void authorize(String sftsys) throws SQLException{
		String sql;
		sql = " INSERT INTO SFT_CONFIG (SC001, SC002, SC003, SC004, SC005) ";
		sql += " VALUES ('70614749','HGHGB93DMHQMV2X9','ffffffae:ffffffce:ffffffae:ffffff94:ffffffee:ffffff8f:61:20','2015-03-30 14:07:32.633','2015-03-29 14:07:32.633')";
		stmt=conn.createStatement();
		stmt.executeUpdate(" USE "+sftsys+sql);
	}
	public List queryDSCSYS(String cusName) throws SQLException{
		List dscsys = new ArrayList();
		String sql = " SELECT name FROM master.dbo.sysdatabases WHERE name like '%"+cusName+"%'";
		System.out.println(sql);
		stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(" USE master "+sql);
		while(rs.next()){
			dscsys.add(rs.getString("name").toString());
		}	
		return  dscsys;
	}
	
	public String queryGuardManagerIP(String sys) throws SQLException{
		String ip = null;
		String sql = " USE "+sys+" SELECT CD003 FROM CONFIG_DEF WHERE CD001='GuardManagerIP' ";
		stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			ip =rs.getString("CD003").toString();
		}
		return ip;
	}
	
	public String queryHardwareKey(String sys) throws SQLException{
		String hk = null;
		String sql = " USE "+sys+" SELECT CD003 FROM CONFIG_DEF WHERE CD001='HardwareKey' ";
		stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			hk =rs.getString("CD003").toString();
		}
		return hk;
	}
	
	public String queryGuardManagerNetCard(String sys) throws SQLException{
		String netCard = null;
		String sql = " USE "+sys+" SELECT CD003 FROM CONFIG_DEF WHERE CD001='GuardManagerNetCard' ";
		stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			netCard =rs.getString("CD003").toString();
		}
		return netCard;
	}
	
	public void updateGuardManagerIP(String ip) throws SQLException{
		String sql;
		sql = " UPDATE CONFIG_DEF SET  CD003='"+ip+"' WHERE CD001='GuardManagerIP' ";
		stmt=conn.createStatement();
		stmt.executeUpdate(" USE "+getSFTSYS()+sql);
	}
	public void updateHardwareKey(String hk) throws SQLException{
		String sql;
		sql = " UPDATE CONFIG_DEF SET  CD003='"+hk+"' WHERE CD001='HardwareKey' ";
		stmt=conn.createStatement();
		stmt.executeUpdate(" USE "+getSFTSYS()+sql);
	}
	public void updateGuardManagerNetCard() throws Exception{
		InetAddress myComputer = InetAddress.getLocalHost();
		String netCard = myComputer.getHostAddress().toString();
		String sql = " UPDATE CONFIG_DEF SET  CD003='"+netCard+"' WHERE CD001='GuardManagerNetCard' ";
		stmt=conn.createStatement();
		stmt.executeUpdate(" USE "+getSFTSYS()+sql);
	}
}
