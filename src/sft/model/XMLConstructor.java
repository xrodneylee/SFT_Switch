package sft.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLConstructor {
	private Document document = null;
	private Element rootNodeElement = null;

	/**
	　* Create XML document
	*/
	public void createXmlDocument() {
		this.document = DocumentHelper.createDocument();
		this.document.setXMLEncoding("utf-8");
		this.rootNodeElement = this.document.addElement("root");
	}
	public void createXmlDocument(String root) {
		this.document = DocumentHelper.createDocument();
		this.document.setXMLEncoding("utf-8");
		this.rootNodeElement = this.document.addElement(root);
	}
	
	/**
	　　 * Add an child node to root node.
	　　 * @param tagName The name of the tag.
	　　 * @param attr The value of attribute.
	　　 * @param text The value of text.
	　　 * @return The element of the new created node.
	　　 */
	public Element addElement(String tagName, String attr, String text) {
		if (this.document == null)
			this.createXmlDocument();

		Element element = this.rootNodeElement.addElement(tagName);
		element.addAttribute("attribute", attr);
		element.setText(text);
		return element;
	}
	public void delElement(String tag){
		Element root = document.getRootElement();  
		Element ver=root.element(tag);  
		root.remove(ver);
	}
	public boolean hasElement(String tag){
		boolean flag = false;
		Element root = document.getRootElement();
		Iterator<Element> it = root.elementIterator();
		while (it.hasNext()) {  
        	Element e = it.next();
        	if(e.element(tag)!=null){
        		flag = true;
        		break;
        	}
		}
		return flag;
	}
	
	/**
	　　 * Save the XML document to <code>storedFile</code> using UTF-8 encoding.
	　　 * @param storedFile The stored file path.
	　　 */
	public void saveToFile (File storedFile) {
		if(storedFile.exists())
			storedFile.delete();

		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		XMLWriter writer = null;
		try {
			storedFile.createNewFile();

			OutputFormat format = OutputFormat.createPrettyPrint();  
	        format.setEncoding("utf-8");
	        fos = new FileOutputStream(storedFile);
	        osw = new OutputStreamWriter(fos, Charset.forName("utf-8"));
	        writer = new XMLWriter(osw, format);
	        writer.write(this.document);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) writer.close();
				if(osw != null) osw.close();
				if(fos != null) fos.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	　　 * 讀取XML
	　　 * @param xmlPath
	　　 * @author GuanPu
	　　 */
	public Map XMLParser(String xmlPath){
		String Path = xmlPath;
		Map dataMap = new HashMap();
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(xmlPath);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element rootElement = document.getRootElement();
		List<Element> elements= rootElement.elements();
		for(Element element : elements){
			dataMap.put(element.getName(),element.getText());
		}

		return dataMap;
	}  
}
