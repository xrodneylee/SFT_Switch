package sft.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;

import sft.model.Config;
import sft.model.ConnectDB;
import sft.model.CopyFolder;
import sft.model.RegInstall;
import sft.model.SFTStart;
import sft.model.UnZipBean;
import sft.model.Util;
import sft.model.XMLConstructor;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class ConfigPage extends JPanel implements ActionListener,ItemListener, WindowListener{
	protected JFrame sftFrame;
	protected JTextField SFTVersion,Source,target,user,SFTcommonDB;//ip modi
	protected JTextField SFTDB;
	protected JPasswordField password;
	protected JLabel ipLabel,userLabel,passwordLabel,SFTcommonDBLabel,ERPVerLabel,GP31VerLabel;
	protected JLabel CompanyLabel,SFTDBLabel,ERPcommonDBLabel;
	protected JLabel VersionLabel,installPathLabel,SFTVersionLabel,SourceLabel,targetLabel,switchLabel,tipLabel;
	protected JButton connectTest,openFile,swichEnv,startSFT,copy,delete,save,exit,cmp,centerPage,HMI,troubleshooting;
	protected JTabbedPane sftTab;
	protected JPanel config,console,commonPanel,dataBasePanel,ERPPanel,VersionPanel,tipPanel;
	protected JComboBox installPath,VersionList,CompanyList,ipList,ERPcommonDB,ERPVer,GP31Ver;
	protected JRadioButton SFT_3745,SFT_376,WIP,SFT_377;
	protected ButtonGroup group;
	protected JCheckBox start_SFT;
	protected JFileChooser file;
	protected static JTextArea switchProcess;
	protected JScrollPane jscrollpanel,jscrollsftFrame;
	JCheckBox IntegrateHMI = new JCheckBox();
	CopyFolder copyfolder;
	JOptionPane worning = new JOptionPane();
	Border eborder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	Border filePathSet,dataBaseSet,ERPSet,VersionSet,tipSet;
	ConnectDB conn = new ConnectDB();
	XMLConstructor xml = new XMLConstructor();
	XMLConstructor verRecord = new XMLConstructor();
	Map dataMap = new HashMap();
	boolean saveflag = false;
	String ErpVerNum,Gp31VerNum;
	RegInstall reg = new RegInstall();
	String port = "8100";
	
	public ConfigPage(){
		Config.loadProperties();
		
		sftFrame = new JFrame();
		sftFrame.setTitle("SFT_Switch");
		sftFrame.setBounds(100, 100, 1000, 750);
		sftFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sftFrame.getContentPane().setLayout(null);
		sftFrame.addWindowListener(this);				
		
		ImageIcon img = new ImageIcon("img/Smiley 32 h p8.png");
		sftFrame.setIconImage(img.getImage());
		
		sftTab = new JTabbedPane(JTabbedPane.TOP);
		sftTab.setBounds(0, 0, 725, 700);
		sftFrame.getContentPane().add(sftTab);
		
		dataBaseSet = BorderFactory.createTitledBorder(eborder,"資料庫連線設定");
		ERPSet = BorderFactory.createTitledBorder(eborder,"ERP連線資料設定");
		VersionSet = BorderFactory.createTitledBorder(eborder,"版本");
		filePathSet = BorderFactory.createTitledBorder(eborder,"檔案路徑設定");
		tipSet = BorderFactory.createTitledBorder(eborder,"溫馨小提醒");
		
		//頁籤---一般設定&切換過程
		config = new JPanel();
		config.setLayout(null);
		sftTab.addTab("一般設定", config);				
		
		console = new JPanel();
		console.setLayout(null);
		sftTab.addTab("切換過程", console);
		
		//一般設定---切割四等分
		commonPanel = new JPanel();
		commonPanel.setLayout(null);
		commonPanel.setBounds(10, 10, 650, 200);
		commonPanel.setBorder(filePathSet);
		config.add(commonPanel);
		
		dataBasePanel = new JPanel();
		dataBasePanel.setLayout(null);
		dataBasePanel.setBounds(10, 210, 650, 150);
		dataBasePanel.setBorder(dataBaseSet);
		config.add(dataBasePanel);
		
		ERPPanel = new JPanel();
		ERPPanel.setLayout(null);
		ERPPanel.setBounds(10, 360, 650, 175);
		ERPPanel.setBorder(ERPSet);
		config.add(ERPPanel);
		
		VersionPanel = new JPanel();
		VersionPanel.setLayout(null);
		VersionPanel.setBounds(10, 535, 650, 80);
		VersionPanel.setBorder(VersionSet);
		config.add(VersionPanel);
		
		tipLabel = new JLabel("切換SFT個案版本前，請務必先進行ERP個案版本切換！");
		tipLabel.setForeground(Color.RED);
		tipLabel.setFont(new Font("新細明體", Font.PLAIN, 18));
		tipLabel.setBounds(15, 20, 500, 21);
		tipPanel = new JPanel();
		tipPanel.setLayout(null);
		tipPanel.setBounds(10, 615, 650, 50);
		tipPanel.setBorder(tipSet);
		config.add(tipPanel);	
		tipPanel.add(tipLabel);
		//第一區塊---文字設定
		VersionLabel = new JLabel("客戶版本別");
		VersionLabel.setBounds(10, 30, 110, 21);
		commonPanel.add(VersionLabel);
		
		SourceLabel = new JLabel("個案程式來源");
		SourceLabel.setBounds(10, 60, 110, 21);
		commonPanel.add(SourceLabel);
		
		installPathLabel = new JLabel("SFT安裝路徑");
		installPathLabel.setBounds(10, 90, 110, 21);
		commonPanel.add(installPathLabel);
		
		SFTVersionLabel = new JLabel("SFT版號");
		SFTVersionLabel.setBounds(10, 120, 110, 21);
		commonPanel.add(SFTVersionLabel);
						
		targetLabel = new JLabel("目標路徑");
		targetLabel.setBounds(10, 150, 110, 21);
		commonPanel.add(targetLabel);
		
		//第一區塊---顯示設定
		VersionList = new JComboBox();
		VersionList.setBounds(120, 30, 240, 21);
		VersionList.addItemListener(this);
		commonPanel.add(VersionList);
		
		Source = new JTextField();
		Source.setBounds(120, 60, 240, 21);
		Source.setColumns(10);
		commonPanel.add(Source);
		
		installPath = new JComboBox();
		installPath.setBounds(120, 90, 240, 21);
		installPath.addItemListener(this);
		commonPanel.add(installPath);
		
//		installPath = new JTextField();
//		installPath.setBounds(120, 60, 240, 21);
//		installPath.setColumns(10);
//		commonPanel.add(installPath);
			
		SFTVersion = new JTextField();
		SFTVersion.setBounds(120, 120, 240, 21);
		SFTVersion.setColumns(10);
		commonPanel.add(SFTVersion);
						
		target = new JTextField();
		target.setBounds(120, 150, 240, 21);
		target.setColumns(10);
		commonPanel.add(target);
		
		//第二區塊---文字設定	
		SFTcommonDBLabel = new JLabel("公用資料庫");
		SFTcommonDBLabel.setBounds(10, 20, 110, 21);
		dataBasePanel.add(SFTcommonDBLabel);
		
		ipLabel = new JLabel("資料庫主機IP");
		ipLabel.setBounds(10, 50, 110, 21);
		dataBasePanel.add(ipLabel);
		
		userLabel = new JLabel("sa帳號");
		userLabel.setBounds(10, 80, 110, 21);
		dataBasePanel.add(userLabel);
		
		passwordLabel = new JLabel("sa密碼");
		passwordLabel.setBounds(10, 110, 110, 21);
		dataBasePanel.add(passwordLabel);
		
		//第二區塊---顯示設定
		SFTcommonDB = new JTextField();
		SFTcommonDB.setBounds(120, 20, 240, 21);
		SFTcommonDB.setColumns(10);
		dataBasePanel.add(SFTcommonDB);
		
		
		if(Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
			Object[] ip = {"10.40.200.225","10.40.71.60"};//,"10.20.87.12","10.20.86.58","10.20.86.36","10.20.86.39","10.40.13.151","10.40.15.46"
			ipList = new JComboBox(ip);
		}else{
				ipList = new JComboBox();
//				if(conductor.get("MainServerName") == null){
//					ipList.addItem("請用ErpSwitch切換該公司ERP");
//				}else{
//					ipList.addItem(conductor.get("MainServerName"));
//				}				
		}		
		ipList.setBounds(120, 50, 240, 21);
		//ipList.addItemListener(this);
		dataBasePanel.add(ipList);
//		ip = new JTextField();
//		ip.setBounds(120, 50, 240, 21);
//		ip.setColumns(10);
//		dataBasePanel.add(ip);
				
		user = new JTextField("sa");
		user.setBounds(120, 80, 240, 21);
		user.setColumns(10);
		dataBasePanel.add(user);
				
		password = new JPasswordField();
		password.setBounds(120, 110, 240, 21);
		password.setColumns(10);
		dataBasePanel.add(password);
		
		//第三區塊---文字設定
		ERPcommonDBLabel = new JLabel("公用資料庫");
		ERPcommonDBLabel.setBounds(10, 20, 110, 21);
		ERPPanel.add(ERPcommonDBLabel);
		
		CompanyLabel = new JLabel("選擇公司別");
		CompanyLabel.setBounds(10, 50, 110, 21);
		ERPPanel.add(CompanyLabel);
		
		SFTDBLabel = new JLabel("SFT資料庫");
		SFTDBLabel.setBounds(10, 80, 110, 21);
		ERPPanel.add(SFTDBLabel);
		
		ERPVerLabel = new JLabel("ERP版本");
		ERPVerLabel.setBounds(10, 110, 110, 21);
		ERPPanel.add(ERPVerLabel);
		
		GP31VerLabel = new JLabel("GP3.X以上版本");
		GP31VerLabel.setBounds(10, 140, 110, 21);
		ERPPanel.add(GP31VerLabel);
				
		//第三區塊---顯示設定
		ERPcommonDB = new JComboBox();
		ERPcommonDB.setBounds(120, 20, 240, 21);
		ERPcommonDB.addItemListener(this);
		ERPPanel.add(ERPcommonDB);
//		ERPcommonDB = new JTextField();
//		ERPcommonDB.setBounds(120, 20, 240, 21);
//		ERPcommonDB.setColumns(10);
//		ERPPanel.add(ERPcommonDB);
		
		
		CompanyList = new JComboBox();
		CompanyList.setBounds(120, 50, 240, 21);
		CompanyList.addItemListener(this);
		ERPPanel.add(CompanyList);
		
		SFTDB = new JTextField();
		SFTDB.setBounds(120, 80, 240, 21);
		SFTDB.setColumns(10);
		ERPPanel.add(SFTDB);
		
		Object[] erpver = {"GP2.X","7X","SM","GP3.X or larter Version","COSMOS"};
		ERPVer = new JComboBox(erpver);
		ERPVer.setBounds(120, 110, 240, 21);
		ERPVer.addItemListener(this);
		ERPPanel.add(ERPVer);
		
		Object[] gp31ver = {"","GP3.1.4 or larter Version","GP3.1.3 or past Version"};
		GP31Ver = new JComboBox(gp31ver);
		GP31Ver.setBounds(120, 140, 240, 21);
		GP31Ver.setEnabled(false);
		//ipList.addItemListener(this);
		ERPPanel.add(GP31Ver);
		//第四區塊
		SFT_3745 = new JRadioButton("SFT 3.7.4.5");
		SFT_3745.setBounds(10, 20, 110, 21);
		SFT_3745.setActionCommand("3.7.4.5");
		SFT_3745.setEnabled(false);
		VersionPanel.add(SFT_3745);

		SFT_376 = new JRadioButton("SFT 3.7.6");
		SFT_376.setBounds(10, 50, 110, 21);
		SFT_376.setActionCommand("3.7.6");
		SFT_376.setEnabled(false);
		VersionPanel.add(SFT_376);
		
		SFT_377 = new JRadioButton("SFT 3.7.7");
		SFT_377.setBounds(120, 20, 110, 21);
		SFT_377.setActionCommand("3.7.7");
		SFT_377.setEnabled(false);
		VersionPanel.add(SFT_377);
		
//		WIP = new JRadioButton("WIP 1.0.0");
//		WIP.setBounds(10, 80, 110, 21);
//		WIP.setActionCommand("WIP");
//		VersionPanel.add(WIP);
		
		group = new ButtonGroup();
		group.add(SFT_3745);
		group.add(SFT_376);
		group.add(SFT_377);
//		group.add(WIP);
		
		//按鈕設定
		ImageIcon openImg = new ImageIcon("img/open.png");
		openFile = new JButton("開啟檔案路徑",openImg);
		openFile.setBounds(360, 60, 150, 25);		
		openFile.setActionCommand("open");
		openFile.addActionListener(this);
		commonPanel.add(openFile);
		
		if(VersionList.getSelectedItem() == null){
			openFile.setEnabled(false);
		}
		
		ImageIcon testImg = new ImageIcon("img/Link.png");
		connectTest = new JButton("連線測試",testImg);
		connectTest.setBounds(360, 80, 115, 25);
		connectTest.setActionCommand("connect");
		connectTest.addActionListener(this);
		dataBasePanel.add(connectTest);
		
		swichEnv = new JButton("3.切換環境");
		swichEnv.setBounds(750, 150, 150, 40);
		swichEnv.setActionCommand("switch");
		swichEnv.addActionListener(this);
		sftFrame.getContentPane().add(swichEnv);
		
		centerPage = new JButton("5.開啟SFT登入首頁");
		centerPage.setBounds(750, 250, 150, 40);
		centerPage.setActionCommand("home");
		centerPage.addActionListener(this);
		sftFrame.getContentPane().add(centerPage);
		
		startSFT = new JButton("4.啟動SFT");
		startSFT.setBounds(750, 200, 150, 40);
		startSFT.setActionCommand("start");
		startSFT.addActionListener(this);
		sftFrame.getContentPane().add(startSFT);
		
		copy = new JButton("1.複製");
		copy.setBounds(750, 50, 150, 40);
		copy.setActionCommand("copy");
		copy.addActionListener(this);
		sftFrame.getContentPane().add(copy);
		
		HMI = new JButton("6.啟動HMI");
		HMI.setBounds(750, 300, 150, 40);
		HMI.setActionCommand("HMI");
		HMI.addActionListener(this);
		HMI.setEnabled(false);
		sftFrame.getContentPane().add(HMI);
		
		delete = new JButton("7.刪除");
		delete.setBounds(750, 350, 150, 40);
		delete.setActionCommand("delete");
		delete.addActionListener(this);
		sftFrame.getContentPane().add(delete);
		
		save = new JButton("2.存檔");
		save.setBounds(750, 100, 150, 40);
		save.setActionCommand("save");
		save.addActionListener(this);
		sftFrame.getContentPane().add(save);
		
		exit = new JButton("8.離開");
		exit.setBounds(750, 400, 150, 40);
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		sftFrame.getContentPane().add(exit);
		
		troubleshooting = new JButton("常見問題排除");
		troubleshooting.setBounds(750, 550, 150, 40);
		troubleshooting.setActionCommand("troubleshooting");
		troubleshooting.addActionListener(this);
		troubleshooting.setForeground(Color.red);
		sftFrame.getContentPane().add(troubleshooting);
		
		ImageIcon cmpImg = new ImageIcon("img/query.jpg");
		cmp = new JButton("查詢公用資料庫",cmpImg);
		cmp.setBounds(360, 20, 160, 25);
		cmp.setActionCommand("cmp");
		cmp.addActionListener(this);
		ERPPanel.add(cmp);
		
//		tipLabel = new JLabel("訊息：");
//		tipLabel.setBounds(750, 400, 110, 21);
//		sftFrame.getContentPane().add(tipLabel);
//		tip = new JTextArea();
//		tip.setEditable(false);
//		tip.setForeground(Color.RED);
//		tip.setFont(new Font("新細明體", Font.PLAIN, 18));
//		tip.append("訊息：\n");
//		tip.append("切換SFT個案版本前，請務必先進行ERP個案版本切換！");
//		tip.setBounds(750, 400, 450, 50);
//		sftFrame.getContentPane().add(tip);
		//切換過程
		switchLabel = new JLabel("切換過程記錄");
		switchLabel.setBounds(30, 50, 110, 21);
		console.add(switchLabel);
		
		switchProcess = new JTextArea();
		switchProcess.setWrapStyleWord(true);
		switchProcess.setLineWrap(false);
		switchProcess.setEditable(true);
		switchProcess.setFont(new Font("新細明體", Font.PLAIN, 15));
		jscrollpanel = new JScrollPane(switchProcess);
		jscrollpanel.setBounds(30, 70, 650, 500);
		jscrollpanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		console.add(jscrollpanel);
		
		IntegrateHMI.setText("整合HMI");
		IntegrateHMI.setActionCommand("IntegrateHMI");
		IntegrateHMI.addActionListener(this);
		IntegrateHMI.setBounds(750, 450, 150, 40);
		sftFrame.getContentPane().add(IntegrateHMI);
		
		Map conductor = reg.getERPConductor();
		if(!Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
			ERPcommonDB.removeAllItems();
			ERPcommonDB.addItem(conductor.get("MainDBName"));
			ipList.removeAllItems();
			if(conductor.get("MainServerName") == null){
				ipList.addItem("請用ErpSwitch切換該公司ERP");
			}else{
				ipList.addItem(transToIP(conductor.get("MainServerName").toString()));
			}	
		}
		
		if(new File("ver.xml").exists()){
			Map dataMap = verRecord.XMLParser("ver.xml");
			for(Object key:dataMap.keySet()){
				VersionList.addItem(dataMap.get(key));
				verRecord.addElement("ver-"+dataMap.get(key).toString(), null, dataMap.get(key).toString());
				verRecord.saveToFile(new File("ver.xml"));
			}
		}
		
//		if(!Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
//			ERPcommonDB.removeAllItems();
//			ERPcommonDB.addItem(conductor.get("MainDBName"));
//			ipList.removeAllItems();
//			if(conductor.get("MainServerName") == null){
//				ipList.addItem("請用ErpSwitch切換該公司ERP");
//			}else{
//				ipList.addItem(conductor.get("MainServerName"));
//			}	
//		}
		//installPath.setText(reg.getReg());
		
		String erpreg = reg.getERPreg();
		if(!erpreg.equals("None")){
			if(erpreg.contains("COSMOS")){
				ERPVer.setSelectedItem("COSMOS");
			}else if(erpreg.contains("SM")){
				ERPVer.setSelectedItem("SM");
			}else if(erpreg.contains("GP3")){
				ERPVer.setSelectedItem("GP3.X or larter Version");
			}else if(erpreg.contains("GP2")){
				ERPVer.setSelectedItem("GP2.X");
			}else if(erpreg.startsWith("7")){
				ERPVer.setSelectedItem("7X");
			}
		}
		
		if(Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
			installPath.addItem(Config.getConfig("8100"));
			installPath.addItem(Config.getConfig("8110"));
			installPath.addItem(Config.getConfig("8120"));
			installPath.addItem(Config.getConfig("8130"));
		}else{
			installPath.addItem(reg.getReg());
			installPath.setEnabled(false);
		}
		
		(new File("log")).mkdirs();
		File logtxt = new File("log/"+getDate()+".txt");
        if(!logtxt.exists()){
        	try {
        		logtxt.createNewFile();
			} catch (IOException e) {
				log(exceptionStacktraceToString(e));
				e.printStackTrace();
			}
        }
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		Map dataMap = null;
		System.out.println(cmd);
		xml.createXmlDocument();
		if(cmd.equals("connect")){
			connectSet();
			dataMap = conn.Connect();
			if(!(Boolean) dataMap.get("flag")){			
				worning.showMessageDialog(null, dataMap.get("msg"), "警告", JOptionPane.ERROR_MESSAGE);
			}else{
				worning.showMessageDialog(null, dataMap.get("msg"), null, JOptionPane.INFORMATION_MESSAGE);
			}		
		}else if(cmd.equals("open")){
			String targetTemp = null,pathTemp; 
			int countTemp,pathCountTemp;
			file = new JFileChooser(Config.getConfig("sourcePath"));//路徑讀取config.properties
			file.setDialogTitle("開啟檔案");
			if(file.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
				Source.setText(file.getSelectedFile().toString());
				pathCountTemp = installPath.getSelectedItem().toString().replace("\\", "/").lastIndexOf("/");
				pathTemp = installPath.getSelectedItem().toString().substring(0, pathCountTemp);
				System.out.println("pathTemp="+pathCountTemp);
				countTemp = Source.getText().replace("\\", "/").split("/").length;
				targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
				target.setText(pathTemp+"_"+targetTemp);
				SFTcommonDB.setText("SFTSYS_"+targetTemp);
				log("解壓縮個案程式來源中，請稍候...");

				Thread t1 = new Thread(new Runnable() {
					  @Override
					  public void run() {
						  JFrame f = new JFrame("警告");
							JPanel p = new JPanel();				
							JLabel a = new JLabel("下載並解壓縮中，請稍候...");
							p.setLayout(null);
							f.getContentPane().add(p);
							f.getContentPane().setLayout(null);
							f.setBounds(350, 350, 150, 50);
							p.setBounds(50, 10, 150, 50);
							a.setBounds(0, 10, 250, 50);
							p.add(a);
							f.setSize(300, 100);
							f.setVisible(true);
							if(new File("SFTSWITCHtmp").exists()){
								copyfolder = new CopyFolder();
								copyfolder.deleteSubFile(new File("SFTSWITCHtmp"));
							}
						  UnZipBean unzip = new UnZipBean(Source.getText().replace("\\","/"),"SFTSWITCHtmp");
						  unzip.unzip();
						  
						  f.dispose();
						  log("解壓縮完成");
						  
						  if(new File("SFTSWITCHtmp/patchVer.xml").exists()){
								Map verMap = verRecord.XMLParser("SFTSWITCHtmp/patchVer.xml");
								String ver = verMap.get("SFTVer").toString();
								SFTVersion.setText(verMap.get("SFTVer").toString()+" (build:"+verMap.get("BuildID").toString()+")");
								if(ver.equals("3.7.4.5") || ver == "3.7.4.5"){
									SFT_3745.setSelected(true);
								}else if(ver.equals("3.7.6") || ver == "3.7.6"){
									SFT_376.setSelected(true);
								}else if(ver.equals("3.7.7") || ver == "3.7.7"){
									SFT_377.setSelected(true);
								}
							}
					  }
					 });
				t1.start();
			}
			
			
		}else if(cmd.equals("switch")){	
			System.out.println(group.getSelection().getActionCommand());
			Thread t1 = new Thread(new Runnable() {
				@Override
				  public void run() {
					
					try{
						swichEnv.setEnabled(false);
						sftTab.setSelectedIndex(1);
						
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String targetTemp;
						int countTemp;
						countTemp = Source.getText().replace("\\", "/").split("/").length;
						targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
						String cmpNameString = CompanyList.getSelectedItem().toString();
						String sysbak = null,sftbak = null,ver = group.getSelection().getActionCommand();
						if(ver.equals("3.7.4.5")){
							sysbak = "3.7.4.5\\SFTSYS.BAK";
							sftbak = "3.7.4.5\\SFT_STD.BAK";
						}else{
							sysbak = "3.7.6\\SFTSYS.BAK";
							sftbak = "3.7.6\\SFT_STD.BAK";
						}
						
						log("----------------開始切換----------------");
						copyfolder = new CopyFolder();
						log("檢查SFT共用資料庫是否存在");
						if(!conn.exist(SFTcommonDB.getText())){
							log("SFT共用資料庫不存在，還原資料庫中，請稍候...");
							try {
								conn.restore("SFTSYS_"+targetTemp,sysbak,ipList.getSelectedItem().toString());
								log("還原資料庫完成");
							} catch (SQLException e1) {
								log("資料庫"+SFTcommonDB.getText()+"還原失敗");
								log(exceptionStacktraceToString(e1));
								e1.printStackTrace();
							}//bak擋路徑需修改				
						}else{
							log("資料庫"+SFTcommonDB.getText()+"已存在");
						}
						//-----------------------------------------
						try {
							ResultSet rs;
							String MB001 = null;
							rs=conn.QueryMB001(cmpNameString);
							while(rs.next()){
								MB001 = rs.getString("MB001").trim();
							}
							if(!conn.checkCmp(SFTcommonDB.getText(),MB001)){
								log("建立公司別中，請稍候");
								conn.InsertCompany(SFTcommonDB.getText(),MB001,SFTDB.getText(),user.getText(),Util.convertString(password.getPassword()),cmpNameString);
								log("建立公司別完成");
							}
						} catch (SQLException e1) {
							log(exceptionStacktraceToString(e1));
							e1.printStackTrace();
						}	
						//-----------------------------------------
						log("檢查SFT資料庫是否存在");
						if(!conn.exist(SFTDB.getText())){
							log("SFT資料庫不存在，還原資料庫中，請稍候...");
							try {
								conn.restore(SFTDB.getText(),sftbak,ipList.getSelectedItem().toString());
								log("還原資料庫完成");
							} catch (SQLException e1) {
								log("資料庫"+SFTDB.getText()+"還原失敗");
								log(exceptionStacktraceToString(e1));
								e1.printStackTrace();
							}//bak擋路徑需修改
						}else{
							log("資料庫"+SFTDB.getText()+"已存在");
						}	
						if(!new File("SFTSWITCHtmp").exists()){
							log("解壓縮個案程式來源中，請稍候...");
							UnZipBean unzip = new UnZipBean(Source.getText().replace("\\","/"),"SFTSWITCHtmp");
							unzip.unzip();
							log("解壓縮完成");
						}		
						log("檢查目標路徑是否有"+target.getText()+"資料夾");
						if(!(new File(target.getText()).exists())){
							log("目標路徑無此資料夾，複製中，請稍候...");
							if(new File(installPath.getSelectedItem().toString()+"JBoss\\server\\default\\tmp").exists()){//刪tmp
								log("刪除tmp檔");
								copyfolder.deleteSubFile(new File(installPath.getSelectedItem().toString()+"JBoss\\server\\default\\tmp"));
								log("刪除tmp檔完成");
							}
							if(new File(installPath.getSelectedItem().toString()+"JBoss\\server\\default\\work").exists()){//刪work
								log("刪除work檔");
								copyfolder.deleteSubFile(new File(installPath.getSelectedItem().toString()+"JBoss\\server\\default\\work"));
								log("刪除work檔完成");
							}						
							copyfolder.copyFolder(installPath.getSelectedItem().toString(),target.getText());
							log("複製完成");
						}else{
							log("目標路徑已有"+target.getText()+"資料夾");
						}
						log("刪除jar檔和war檔");
						try {
							Process jar = Runtime.getRuntime().exec("cmd /C del "+target.getText()+"\\JBoss\\server\\default\\deploy\\*.jar");
							jar.waitFor();
							Process war = Runtime.getRuntime().exec("cmd /C del "+target.getText()+"\\JBoss\\server\\default\\deploy\\*.war");
							war.waitFor();
							log("刪除jar檔和war檔完成");
						} catch (IOException e2) {
							log(exceptionStacktraceToString(e2));
							e2.printStackTrace();
						} catch (InterruptedException e1) {
							log(exceptionStacktraceToString(e1));
							e1.printStackTrace();
						}
						
						log("複製個案程式來源到目標路徑中，請稍候...");
						copyfolder.copyFolder("SFTSWITCHtmp/server/",target.getText()+"/JBoss/server");
						log("複製完成");
						
						if(IntegrateHMI.isSelected()){
							log("複製HMI個案程式來源到目標路徑中，請稍候...");
							if(new File(target.getText()+"/HMI").exists())
								copyfolder.deleteSubFile(new File(target.getText()+"/HMI"));
							log(Source.getText().substring(0, Source.getText().lastIndexOf("\\")).replace("\\", "/")+"/HMI/");
//							String HMIsource = getHMIzip(Source.getText().substring(0, Source.getText().lastIndexOf("\\")).replace("\\", "/")+"/HMI/");
//							log(HMIsource);
							copyfolder.copyFolder(Source.getText().substring(0, Source.getText().lastIndexOf("\\")).replace("\\", "/")+"/HMI/",target.getText()+"/HMI");
							log("複製完成");
						}
						
						//執行patch
						log("執行patch中，請稍候...");
						runpatch();	
						//新增授權碼
						log("授權中");
						try {
							conn.authorize(SFTcommonDB.getText());
						} catch (SQLException e1) {
							log("授權失敗");
							log(exceptionStacktraceToString(e1));
							e1.printStackTrace();
						}
						log("授權完成");
						editDatabase();//修改database.conf.xml
						editmssql();//修改mssql-ds.xml
						editSFTSystem();//修改SFT_System.xml
						//if(ver.equals("3.7.6")){
							GuardService();
							//log("GS處理完成");
						//}
						
						try {
							conn.updateSFTSYS(SFTcommonDB.getText(),user.getText(),Util.convertString(password.getPassword()));
						} catch (SQLException e1) {
							log(exceptionStacktraceToString(e1));
							e1.printStackTrace();
						}
						log("刪除解壓縮檔案中，請稍候...");
						copyfolder.deleteSubFile(new File("SFTSWITCHtmp"));
						log("刪除完成");	
						log("----------------切換成功----------------");
						int confirm = worning.showConfirmDialog(null, "切換完成是否啟動SFT", null, JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
						if(confirm == JOptionPane.OK_OPTION){
							runbat();
						}						
					}catch(Exception e){
						log(exceptionStacktraceToString(e));
						e.printStackTrace();
					}finally{
						swichEnv.setEnabled(true);
					}
				}
					
			});
			t1.start();
			
			
		}else if(cmd.equals("start")){
			runbat();
		}else if(cmd.equals("cmp")){
			String targetTemp = null,MB002;
			ResultSet rs;
			int countTemp;
//			countTemp = Source.getText().replace("\\", "/").split("/").length;
//			targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
			//ERPcommonDB.removeAllItems();
			List dscsys;
			try {
				connectSet();
				conn.Connect();
//				System.out.println("targetTemp="+targetTemp);
				if(Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
					ERPcommonDB.addItem("DSCSYS");
				}else{
//					dscsys = conn.queryDSCSYS(targetTemp);
//					for(int i=0;i<dscsys.size();i++){
//						ERPcommonDB.addItem(dscsys.get(i));
//					}
//					log(String.valueOf(dscsys.isEmpty()));
//					if(dscsys.isEmpty()){
//						log("test>>>"+ERPcommonDB.getSelectedItem().toString());
						rs = conn.QueryCompany(ERPcommonDB.getSelectedItem().toString());
						CompanyList.removeAllItems();
						while (rs.next()) {
							MB002 = rs.getString("MB002");						
							CompanyList.addItem(MB002);
						}	
//					}
				}				
			} catch (SQLException e1) {
				log(exceptionStacktraceToString(e1));
				e1.printStackTrace();
			}

		}else if(cmd.equals("copy")){
			if(!SFTVersion.getText().isEmpty()){
				if(!saveflag){
					int confirm = worning.showConfirmDialog(null, "是否儲存此設定檔", "警告", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
					if(confirm == JOptionPane.OK_OPTION){
						save();						
					}
				}
			}	
			saveflag = false;
			int index = VersionList.getSelectedIndex();
			String key = worning.showInputDialog(null, null, "請輸入客戶版本別", JOptionPane.INFORMATION_MESSAGE);		
			if(key.equals("")){
				worning.showMessageDialog(null, "客戶版本別不可為空", "警告", JOptionPane.ERROR_MESSAGE);
				return;
			}else{
				VersionList.addItem(key);
				VersionList.setSelectedItem(key);
				if(Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
					ERPcommonDB.removeAllItems();
				}
				CompanyList.removeAllItems();
				openFile.setEnabled(true);
			}
			
			Map conductor = reg.getERPConductor();
			if(!Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
				ERPcommonDB.removeAllItems();
				ERPcommonDB.addItem(conductor.get("MainDBName"));
				ipList.removeAllItems();
				if(conductor.get("MainServerName") == null){
					ipList.addItem("請用ErpSwitch切換該公司ERP");
				}else{
					ipList.addItem(transToIP(conductor.get("MainServerName").toString()));
				}	
			}
			
		}else if(cmd.equals("delete")){
			int confirm = worning.showConfirmDialog(null, "確定刪除此設定檔", "警告", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if(confirm == JOptionPane.OK_OPTION){
				new File(VersionList.getSelectedItem().toString()+"_Config.xml").delete();
				//清空各欄位
				verRecord.delElement("ver-"+VersionList.getSelectedItem().toString());
				verRecord.saveToFile(new File("ver.xml"));
				VersionList.removeItem(VersionList.getSelectedItem().toString());
				VersionList.setSelectedIndex(-1);		
				clear();
				SFTDB.setText("");
				openFile.setEnabled(false);
			}	
		}else if(cmd.equals("save")){
			if(VersionList.getSelectedItem() == null){
				worning.showMessageDialog(null, "請輸入客戶版本別", "警告", JOptionPane.ERROR_MESSAGE);
				return;
			}
			save();	
		}else if(cmd.equals("exit")){
			if(!saveflag){
				int confirm = worning.showConfirmDialog(null, "是否儲存此設定檔", "警告", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if(confirm == JOptionPane.OK_OPTION){
					if(VersionList.getSelectedItem() == null){
						worning.showMessageDialog(null, "請輸入客戶版本別", "警告", JOptionPane.ERROR_MESSAGE);
						return;
					}
					save();
					sftFrame.dispose();
				}else{
					sftFrame.dispose();
				}
			}
			sftFrame.dispose();
		}else if(cmd.equals("home")){
			SFTHome();
		}else if(cmd.equals("HMI")){
			if(!new File(target.getText()+"/HMI").exists()){
				worning.showMessageDialog(null, "無HMI資料夾", "警告", JOptionPane.ERROR_MESSAGE);
				return;
			}else if(new File(target.getText()+"/HMI/").list().length == 0){
				worning.showMessageDialog(null, "無HMI相關程式", "警告", JOptionPane.ERROR_MESSAGE);
				return;
			}else{
				try {
					log("呼叫SFT_RunHMI.exe");
					Process p = Runtime.getRuntime().exec("cmd /c start "+System.getProperty("user.dir")+"\\SFT_RunHMI.exe "+target.getText()+"\\HMI\\");
				} catch (IOException e1) {
					e1.printStackTrace();
					log(exceptionStacktraceToString(e1));
				} 
			}
		}else if(cmd.equals("IntegrateHMI")){
			if(IntegrateHMI.isSelected()){
				HMI.setEnabled(true);
			}else{
				HMI.setEnabled(false);
			}
		}else if(cmd.equals("troubleshooting")){
			TipWindow tipWindow = new TipWindow();
			tipWindow.setVisible(true);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getSource();
		if(obj == VersionList){
			System.out.println("VersionList");
			if (e.getStateChange() == ItemEvent.SELECTED) {	
				String fileName = e.getItem().toString() + "_Config.xml";
				if(new File(fileName).exists()){
					dataMap = xml.XMLParser(fileName);
					installPath.setSelectedItem(dataMap.get("INSTALLPATH").toString());
					SFTVersion.setText(dataMap.get("SFTVERSION").toString());
					Source.setText(dataMap.get("SOURCE").toString());
					target.setText(dataMap.get("TARGET").toString());
					SFTcommonDB.setText(dataMap.get("SFTCOMMONDB").toString());
					ipList.removeAllItems();
					ipList.addItem(dataMap.get("IP").toString());
					user.setText(dataMap.get("USER").toString());
					password.setText(dataMap.get("PASSWORD").toString());					
					SFTDB.setText(dataMap.get("SFTDB").toString());
					ERPVer.setSelectedItem(dataMap.get("ERPVER").toString());
					GP31Ver.setSelectedItem(dataMap.get("GP31VER").toString());
					IntegrateHMI.setSelected(Boolean.valueOf(dataMap.get("HMI").toString()));
					String ver = dataMap.get("VER").toString();
					if(ver.equals("3.7.4.5")){
						SFT_3745.setSelected(true);
					}else if(ver.equals("3.7.6")){
						SFT_376.setSelected(true);
					}else{
						//WIP.setSelected(true);
					}
					connectSet();
					if(Util.convertString(password.getPassword())!=""){
						conn.Connect();
					}				
					ERPcommonDB.removeAllItems();
					ERPcommonDB.addItem(dataMap.get("ERPCOMMONDB").toString());
					CompanyList.removeAllItems();
					CompanyList.addItem(dataMap.get("COMPANY").toString());
						
					System.out.println("test "+VersionList.getSelectedItem());
					if(VersionList.getSelectedItem() == null){
						openFile.setEnabled(false);
					}else{
						openFile.setEnabled(true);
					}
					if(IntegrateHMI.isSelected()){
						HMI.setEnabled(true);
					}else{
						HMI.setEnabled(false);
					}
					saveflag = false;
				}else{
					
				}	
			}
		}else if(obj == CompanyList){
			String targetTemp;
			ResultSet rs;
			String CS004 = null,MB001 = null;
			int countTemp;
			if(Util.convertString(password.getPassword())!=""){
				countTemp = Source.getText().replace("\\", "/").split("/").length;
				targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
				System.out.println("SFTcommonDB.getText()="+SFTcommonDB.getText().toString());
				String cmpNameString = e.getItem().toString();
				
				if(e.getStateChange() == ItemEvent.SELECTED){
					try {
						conn.setERP(ERPcommonDB.getSelectedItem().toString());
						rs = conn.QueryMB001(cmpNameString);
						while (rs.next()) {
							MB001 = rs.getString("MB001").trim();
						}
						SFTDB.setText("SFT_" + targetTemp + "_" + MB001);
					} catch (SQLException e1) {
						log(exceptionStacktraceToString(e1));
						e1.printStackTrace();
					}
				}
			}
			
		}else if(obj == ERPcommonDB){
			System.out.println("ERPcommonDB");
			String MB002; 
			ResultSet rs;
			if(!Source.getText().equals("")){
				CompanyList.removeAllItems();

				connectSet();
				if(Util.convertString(password.getPassword())!=""){
					conn.Connect();
				}
				try {
					rs = conn.QueryCompany(ERPcommonDB.getSelectedItem().toString());
					while (rs.next()) {
						MB002 = rs.getString("MB002");
						CompanyList.addItem(MB002);
					}	
				} catch (SQLException e1) {
					//worning.showMessageDialog(null, ERPcommonDB.getSelectedItem().toString()+ "不是共用資料庫", "警告", JOptionPane.ERROR_MESSAGE);
					log(exceptionStacktraceToString(e1));
					e1.printStackTrace();
				} catch (NullPointerException e2){
					
				}
			}
			
		}else if(obj == ERPVer){
			if(ERPVer.getSelectedItem().equals("GP3.X or larter Version")){
				GP31Ver.setEnabled(true);
			}else{
				GP31Ver.setSelectedItem("");
				GP31Ver.setEnabled(false);
			}			
		}		
	}
	
	public void clear(){
		//installPath.setText("");
		SFTVersion.setText("");
		Source.setText("");
		target.setText("");
		SFTcommonDB.setText("");
		//ip.setText("");
		//user.setText("");
		password.setText("");
		SFTDB.setText("");
		ERPcommonDB.removeAllItems();
		CompanyList.removeAllItems();
		if(!Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
			ipList.removeAllItems();
		}
	}
	
	public void connectSet(){
		conn.setURL(ipList.getSelectedItem().toString());
		conn.setUSER(user.getText());
		conn.setPASSWORD(password.getPassword());
		conn.setSFTSYS(SFTcommonDB.getText());
		//conn.setERP(ERPcommonDB.getSelectedItem().toString());
	}
	
	public static void log(String record){
        try {
        	FileWriter dataFile = new FileWriter("log/"+getDate()+".txt",true);
            BufferedWriter input = new BufferedWriter(dataFile);
			input.write(getTime()+"  "+record+System.getProperty("line.separator"));
	        input.close();
		} catch (IOException e) {
			log(exceptionStacktraceToString(e));
			e.printStackTrace();
		}
		switchProcess.append(getTime()+"  "+record+"\n");
		switchProcess.paintImmediately(switchProcess.getBounds());
	}
	
	public static String exceptionStacktraceToString(Exception e)
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    e.printStackTrace(ps);
	    ps.close();
	    return baos.toString();
	}
	
	public static String getTime(){
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		Date current = new Date();
		return sdFormat.format(current);
	}
	public static String getDate(){
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date current = new Date();
		return sdFormat.format(current);
	}
	public void editDatabase(){
		XMLConstructor databaseXml = new XMLConstructor();
		databaseXml.createXmlDocument("config");
		String targetTemp,pathTemp,path;
		int countTemp,pathCountTemp;
		pathCountTemp = installPath.getSelectedItem().toString().replace("\\", "/").lastIndexOf("/");
		pathTemp = installPath.getSelectedItem().toString().substring(0, pathCountTemp);
		countTemp = Source.getText().replace("\\", "/").split("/").length;
		targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
		path = pathTemp+"_"+targetTemp+"\\SFT\\database.conf.xml";
		System.out.println("path="+path);
		if (new File(path).exists()) {
			log("修改database.conf.xml");
			dataMap = xml.XMLParser(path);
			databaseXml.addElement("DatabaseDriver", null, dataMap.get("DatabaseDriver").toString());
			databaseXml.addElement("DatabaseUri", null, "jdbc:jtds:sqlserver://sqlserver:1433/SFTSYS_"+targetTemp);
			databaseXml.addElement("DatabaseUsername", null, user.getText());
			databaseXml.addElement("DatabasePassword", null, Util.convertString(password.getPassword()));
			databaseXml.addElement("DatabaseCatalog", null, "SFTSYS_"+targetTemp);
			databaseXml.addElement("DatabaseHome", null, dataMap.get("DatabaseHome").toString());
			databaseXml.addElement("DatabaseRestore", null, dataMap.get("DatabaseRestore").toString());
			databaseXml.addElement("DatabaseMaxActive", null, dataMap.get("DatabaseMaxActive").toString());
			databaseXml.addElement("DatabaseMaxIdle", null, dataMap.get("DatabaseMaxIdle").toString());
			databaseXml.addElement("DatabaseMinIdle", null, dataMap.get("DatabaseMinIdle").toString());
			databaseXml.addElement("DatabaseMaxWait", null, dataMap.get("DatabaseMaxWait").toString());
			databaseXml.addElement("DatabaseRoot", null, dataMap.get("DatabaseRoot").toString());
			databaseXml.addElement("DefaultDatabaseDriver", null, dataMap.get("DefaultDatabaseDriver").toString());
			databaseXml.addElement("DefaultDatabaseUri", null, dataMap.get("DefaultDatabaseUri").toString());
			databaseXml.addElement("DefaultDatabaseUsername", null, user.getText());
			databaseXml.addElement("DefaultDatabasePassword", null, Util.convertString(password.getPassword()));
			databaseXml.addElement("DatabaseIp", null, ipList.getSelectedItem().toString());
			databaseXml.saveToFile(new File(path));
			log("修改database.conf.xml完成");
		}
	}

	public void editmssql(){
		XMLConstructor mssqlXml = new XMLConstructor();
		mssqlXml.createXmlDocument("datasources");
		String targetTemp,pathTemp,path;
		int countTemp,pathCountTemp;
		pathCountTemp = installPath.getSelectedItem().toString().replace("\\", "/").lastIndexOf("/");
		pathTemp = installPath.getSelectedItem().toString().substring(0, pathCountTemp);
		countTemp = Source.getText().replace("\\", "/").split("/").length;
		targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
		path = pathTemp+"_"+targetTemp+"\\JBoss\\server\\default\\deploy\\mssql-ds.xml";
		System.out.println("path="+path);
		
		SAXReader reader = new SAXReader();  
        Document document;
		try {
			log("修改mssql-ds.xml");
			document = reader.read(new File(path));
			Element root = document.getRootElement();  

	        @SuppressWarnings("unchecked")  
	        Iterator<Element> it = root.elementIterator();  
	        
	        while (it.hasNext()) {  
	        	Element e = it.next();
	        	e.element("connection-url").setText("jdbc:sqlserver://"+ipList.getSelectedItem().toString()+":1433;DatabaseName="+"SFTSYS_"+targetTemp);
	        	e.element("user-name").setText(user.getText());
	        	e.element("password").setText(Util.convertString(password.getPassword()));
	        }  
	 
	        OutputFormat format = OutputFormat.createPrettyPrint();  
	        format.setEncoding("utf-8"); 
	        XMLWriter writer = new XMLWriter(new FileOutputStream(path),format);  
	        writer.write(document);  
	        writer.close();
	        log("修改mssql-ds.xml完成");
		} catch (Exception e) {
			log(exceptionStacktraceToString(e));
			e.printStackTrace();
		}        
	}
	
	public void editSFTSystem(){
		transfer();
		XMLConstructor databaseXml = new XMLConstructor();
		databaseXml.createXmlDocument("config");
		String targetTemp,pathTemp,path;
		int countTemp,pathCountTemp;
		pathCountTemp = installPath.getSelectedItem().toString().replace("\\", "/").lastIndexOf("/");
		pathTemp = installPath.getSelectedItem().toString().substring(0, pathCountTemp);
		countTemp = Source.getText().replace("\\", "/").split("/").length;
		targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
		path = pathTemp+"_"+targetTemp+"\\SFT\\WebContent\\xmldata\\config\\SFT_system.xml";
		System.out.println("path="+path);
		if (new File(path).exists()) {
			log("修改SFT_system.xml");
			dataMap = xml.XMLParser(path);
			databaseXml.addElement("SopRoute", null, dataMap.get("SopRoute").toString());
			databaseXml.addElement("PowrRoute", null, dataMap.get("PowrRoute").toString());
			databaseXml.addElement("ConfigRoute", null, dataMap.get("ConfigRoute").toString());
			databaseXml.addElement("REPORT_HOME", null, dataMap.get("REPORT_HOME").toString());
			databaseXml.addElement("REPORTIMG_HOME", null, dataMap.get("REPORTIMG_HOME").toString());
			databaseXml.addElement("ERPVer", null, ErpVerNum);
			databaseXml.addElement("ERPSYSDB", null, ERPcommonDB.getSelectedItem().toString());
			databaseXml.addElement("ERP_ROUTE", null, dataMap.get("ERP_ROUTE").toString());
			databaseXml.addElement("B01_ROUTE", null, dataMap.get("B01_ROUTE").toString());
			databaseXml.addElement("Erpws", null, dataMap.get("Erpws").toString());
			databaseXml.addElement("Portalws", null, dataMap.get("Portalws").toString());
			databaseXml.addElement("GP31Ver", null, Gp31VerNum);
			databaseXml.saveToFile(new File(path));
			log("修改SFT_system.xml完成");
		}
	}
	
	public void runbat(){
		String run = "";
		ServerOnOff();
		GuardService();
		try {
			if(Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
				Process p1 = Runtime.getRuntime().exec("mshta VBScript:Execute(\"Set a=CreateObject(\"\"WScript.Shell\"\")"+
						":Set b=a.CreateShortcut(\"\""+target.getText()+"\\run_"+port+".lnk\"\"):b.TargetPath=\"\""+target.getText()+"\\JBoss\\bin\\run.bat\"\""+
						":b.WorkingDirectory=\"\"D:\\\"\":b.Description=\"\"1\"\":b.Save:close\")");
				Thread.sleep(2000);
				run = target.getText()+"\\run_"+port+".lnk";
			}else if(Config.getConfig("MultiJBoss").equalsIgnoreCase("off")){
				run = target.getText()+"\\JBoss\\bin\\run.bat";
			}
			Process p2 = Runtime.getRuntime().exec("cmd /c start "+run);
		} catch (IOException e1) {
			log(exceptionStacktraceToString(e1));
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void runpatch() {
		try { 
			String targetTemp;
			int countTemp;
			String sqlScript = null;
			
			countTemp = Source.getText().replace("\\", "/").split("/").length;
			targetTemp = Source.getText().replace("\\", "/").split("/")[countTemp-2].split("_")[0];
			if(ERPVer.getSelectedItem().toString().equals("SM")){
				sqlScript = "SFTSWITCHtmp/sql/2-1. COMPANY_SM.sql";
			}else{
				sqlScript = "SFTSWITCHtmp/sql/2-2. COMPANY_WF.sql";
			}

			String str;
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			File SFTSYS = new File("SFTSWITCHtmp/sql/1. SFTSYS.sql");
			File COMPANY = new File(sqlScript);
			BufferedReader sys_in = new BufferedReader(new InputStreamReader(new FileInputStream(SFTSYS), "UTF-16"));
			BufferedReader cmp_in = new BufferedReader(new InputStreamReader(new FileInputStream(COMPANY), "UTF-16"));
			
			//-----執行sys patch
			log("執行sys patch");
			while ((str = sys_in.readLine()) != null) {
				//System.out.println(str);
				sb1.append(str+ "\n");
			}
			sys_in.close();
			//System.out.println(sb1.toString().replaceAll("SFTSYS", SFTcommonDB.getText()).replaceAll("GO", " "));
			conn.executeSQL(sb1.toString().replaceAll("SFTSYS", SFTcommonDB.getText()).replaceAll("GO", " ").replaceAll("DSCSYS", ERPcommonDB.getSelectedItem().toString()).replaceAll("SMARTDSCSYS", ERPcommonDB.getSelectedItem().toString()));
			log("執行sys patch完成");
			//-----執行公司別patch
			log("執行公司別patch");
			while ((str = cmp_in.readLine()) != null) {
				//System.out.println(str);
				sb2.append(str+ "\n");
			}
			cmp_in.close();
			//System.out.println(sb2.toString().replaceAll("SFTSYS", SFTcommonDB.getText()));
			conn.executeSQL(sb2.toString().replaceAll("SFTSYS", SFTcommonDB.getText()).replaceAll("DSCSYS", ERPcommonDB.getSelectedItem().toString()).replaceAll("SMARTDSCSYS", ERPcommonDB.getSelectedItem().toString()));
			log("執行公司別patch完成");
			log("執行patch完成");
		} catch (FileNotFoundException e1) {
			log(exceptionStacktraceToString(e1));
			e1.printStackTrace();
		} catch (IOException e) {
			log(exceptionStacktraceToString(e));
			e.printStackTrace();
		}catch (SQLException e) {
			log(exceptionStacktraceToString(e));
			e.printStackTrace();
		}
	}
	
	public void save(){
		if(VersionList.getSelectedItem() == null){
			xml.addElement("VERSION", null, "");
		}else{
			xml.addElement("VERSION", null, VersionList.getSelectedItem().toString());
		}		
		xml.addElement("INSTALLPATH", null, installPath.getSelectedItem().toString());
		xml.addElement("SFTVERSION", null, SFTVersion.getText());
		xml.addElement("SOURCE", null, Source.getText());
		xml.addElement("TARGET", null, target.getText());
		xml.addElement("SFTCOMMONDB", null, SFTcommonDB.getText());
		if(ERPcommonDB.getSelectedItem() == null){
			xml.addElement("ERPCOMMONDB", null, "");
		}else{
			xml.addElement("ERPCOMMONDB", null, ERPcommonDB.getSelectedItem().toString());
		}		
		if(CompanyList.getSelectedItem() == null){
			xml.addElement("COMPANY", null, "");
		}else{
			xml.addElement("COMPANY", null, CompanyList.getSelectedItem().toString());
		}
		
		xml.addElement("SFTDB", null, SFTDB.getText());
		xml.addElement("IP", null, ipList.getSelectedItem().toString());
		xml.addElement("USER", null, user.getText());
		xml.addElement("PASSWORD", null, Util.convertString(password.getPassword()));
		if(group.getSelection() == null){
			xml.addElement("VER",null,"");
		}else{
			xml.addElement("VER",null,group.getSelection().getActionCommand());
		}
		
		xml.addElement("ERPVER",null,ERPVer.getSelectedItem().toString());
		xml.addElement("GP31VER",null,GP31Ver.getSelectedItem().toString());
		xml.addElement("HMI",null,String.valueOf(IntegrateHMI.isSelected()));
		xml.saveToFile(new File(VersionList.getSelectedItem().toString()+"_Config.xml"));
		verRecord.addElement("ver-"+VersionList.getSelectedItem().toString(), null, VersionList.getSelectedItem().toString());
		verRecord.saveToFile(new File("ver.xml"));
		saveflag = true;
	}

	public boolean ServerOnOff(){
		boolean flag = false;
		String process;
		String PID = null;
		try {
			Process p = Runtime.getRuntime().exec("tasklist /V /FO CSV");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) {
				if(Config.getConfig("MultiJBoss").equalsIgnoreCase("off")){
					if(process.split("\",\"")[8].contains("\\JBoss")){
						PID = process.split("\",\"")[1];
						flag = true;
						break;
					}
				}else if(Config.getConfig("MultiJBoss").equalsIgnoreCase("on")){
					port =installPath.getSelectedItem().toString().replace("\\", "/").split("/")[2].split("_")[1];
					if(process.split("\",\"")[8].matches("(.*)"+port+"(.*)")){
						PID = process.split("\",\"")[1];
						flag = true;
						break;
					}
				}
				
			}
			System.out.println(PID);
			Process kill = Runtime.getRuntime().exec("TASKKILL /PID "+PID);
			input.close();
		} catch (IOException e) {
			log(exceptionStacktraceToString(e));
			e.printStackTrace();
		}
		return flag;
	}
	
	public void transfer(){
		String erpver = ERPVer.getSelectedItem().toString();
		String gp31ver = GP31Ver.getSelectedItem().toString();
		
		if(erpver == "GP2.X" || erpver.equals("GP2.X")){
			ErpVerNum = "1";
		}else if(erpver == "7X" || erpver.equals("7X")){
			ErpVerNum = "3";
		}else if(erpver == "SM" || erpver.equals("SM")){
			ErpVerNum = "4";
		}else if(erpver == "GP3.X or larter Version" || erpver.equals("GP3.X or larter Version")){
			ErpVerNum = "5";
		}else if(erpver == "COSMOS" || erpver.equals("COSMOS")){
			ErpVerNum = "6";
		}
		
		if(gp31ver == "GP3.1.4 or larter Version" || gp31ver.equals("GP3.1.4 or larter Version")){
			Gp31VerNum = "1";
		}else if(gp31ver == "GP3.1.3 or past Version" || gp31ver.equals("GP3.1.3 or past Version")){
			Gp31VerNum = "2";
		}else{
			Gp31VerNum = "";
		}		
	}
	
	public String transToIP(String instance){
		String ip = null;
		if(instance.equalsIgnoreCase("SERVER201")){
			ip = "10.20.87.12";
		}else if(instance.equalsIgnoreCase("SERVER106")){
			ip = "10.20.86.58";
		}else if(instance.equalsIgnoreCase("SERVER100")){
			ip = "10.20.86.36";
		}else if(instance.equalsIgnoreCase("SERVER99")){
			ip = "10.20.86.39";
		}else if(instance.equalsIgnoreCase("SERVER101")){
			ip = "10.40.13.151";
		}else if(instance.equalsIgnoreCase("SERVER102")){
			ip = "10.40.15.46";
		}else{
			ip = instance;
		}
		return ip;
	}

	public void GuardService(){
		String ip,user,sys,IPCD003 = null,HardwardCD003 = null;
		char[] password;
		Map dataMap = null;
		XMLConstructor getDatabaseXml = new XMLConstructor();
		dataMap = getDatabaseXml.XMLParser(installPath.getSelectedItem().toString()+"SFT\\database.conf.xml");
		user = dataMap.get("DatabaseUsername").toString();
		password = dataMap.get("DatabasePassword").toString().toCharArray();
		ip = dataMap.get("DatabaseIp").toString();
		sys = dataMap.get("DatabaseCatalog").toString();
		conn.setURL(ip);
		conn.setPASSWORD(password);
		conn.setUSER(user);
		conn.Connect();
		try {
			log("GS處理中");
			IPCD003 = conn.queryGuardManagerIP(sys);
			HardwardCD003 = conn.queryHardwareKey(sys);
			connectSet();
			conn.Connect();
			conn.updateGuardManagerIP(IPCD003);
			conn.updateHardwareKey(HardwardCD003);
			conn.updateGuardManagerNetCard();
			log("GS處理完成");
		} catch (Exception e) {
			log(exceptionStacktraceToString(e));
			e.printStackTrace();
		}		
	}
	
	public void SFTHome(){
		try {
			Process p = Runtime.getRuntime().exec("cmd /c start http://127.0.0.1:"+port+"/SFT");//port參數化
		} catch (IOException e) {
			log(e.toString());
			e.printStackTrace();
		}
	}
	
	public String getHMIzip(String HMIpath){
		String zip = "";
		File HMIFile = new File(HMIpath);
        String[] zipFile = HMIFile.list();
        zip = HMIpath + zipFile[0];
		return zip;
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(!saveflag){
			int confirm = worning.showConfirmDialog(null, "是否儲存此設定檔", "警告", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if(confirm == JOptionPane.OK_OPTION){
				if(VersionList.getSelectedItem() == null){
					worning.showMessageDialog(null, "請輸入客戶版本別", "警告", JOptionPane.ERROR_MESSAGE);
					return;
				}
				save();
				sftFrame.dispose();
			}else{
				sftFrame.dispose();
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
