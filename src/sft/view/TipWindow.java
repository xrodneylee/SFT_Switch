package sft.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TipWindow extends JFrame implements WindowListener{
	private JTextArea tip;
	private JScrollPane jscrollpanel;
	public TipWindow(){
		
		this.setTitle("常見問題排除");
		this.setBounds(100, 100, 600, 300);
		this.setLocationRelativeTo(null);
		
		tip = new JTextArea();
		tip.setFont(new Font("新細明體", Font.PLAIN, 18));
		tip.setBackground(Color.BLACK);
		tip.setForeground(Color.WHITE);
		jscrollpanel = new JScrollPane(tip);
		jscrollpanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.getContentPane().add(jscrollpanel);
		
		tip.append("1.進入127.0.0.1:8100/SFT/InitSet.jsp \n");
		tip.append("2.點選環境設定 \n");
		tip.append("3.切換虛擬機器安裝設定頁籤，主機IP選擇該台IP \n");
		tip.append("4.按下執行並儲存 \n");
		tip.append("5.至ERP主機IP位址頁籤，將是否整合CROSS改為【否】 \n");
		tip.append("6.重啟Server即可 \n");
	}
	
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		this.dispose();
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
