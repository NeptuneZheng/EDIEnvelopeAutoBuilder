package utility.process;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLModifier {

	public void modify(String xmlFilePath, String xpath, String newValue) {
		try {
			FileReader fr;
			fr = new FileReader(new File(xmlFilePath));
			SAXReader reader = new SAXReader();
			HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
			if (xpath.startsWith("//") || (!xpath.startsWith("/"))) {
				System.out
						.println(xpath
								+ " is not absolute xpath ,please modify it to absolute xpath format ! Sample : /A/B/C ");
				return;
			}

			String[] paths = xpath.split("/");
			Document document = reader.read(fr);
			fr.close();
			List<Element> list;
			StringBuffer xmlPathSB = new StringBuffer();
			Element element;
			for (int i = 0; i < paths.length; i++) {
				if (paths[i].trim().equals("")) {
					continue;
				}
				if (i == 1) {
					element = document.getRootElement();
					nameSpaceMap.put("ns" + i, element.getNamespaceURI());
					reader.getDocumentFactory().setXPathNamespaceURIs(
							nameSpaceMap);
				} else {
					list = document.selectNodes(xmlPathSB.toString());
					if (list.size() > 0) {
						element = (Element) list.get(0);
						nameSpaceMap.put("ns" + i, element.element(
								paths[i].trim()).getNamespaceURI());
						reader.getDocumentFactory().setXPathNamespaceURIs(
								nameSpaceMap);
					}
				}
				xmlPathSB.append("/ns" + i + ":" + paths[i]);

			}
			list = document.selectNodes(xmlPathSB.toString());
			if (list.size() == 0) {
				System.out
						.println("Can't find the specified element by xpath : "
								+ xpath+". Please check if you are using absolute xpath ! Absolute xpath is expected !");
				return;
			}
			for (int j = 0; j < list.size(); j++) {
				element = (Element) list.get(j);
				element.clearContent();
				element.setText(newValue);
			}

			XMLWriter output = new XMLWriter(new FileWriter(new File(
					xmlFilePath)));
			output.write(document);
			output.close();
			System.out.println("Replace "+xpath + " in "+xmlFilePath+ " with new value '"+newValue+"' successfully !");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String GetXmlValue(String xml, String xpath) throws UnsupportedEncodingException {
		String value = null;
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			Element element = (Element) document.selectSingleNode(xpath);
			value = element.getText();

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value; 
	}
	
	public String replaceEDImsgID(String s){
		if(s.contains("EDI2")){
			int start = s.indexOf("EDI2");
			int end = start +22;
			s=s.replace(s.substring(start, end), "#");
		}
		return s;
	}
	public static void main(String[] args) throws UnsupportedEncodingException {

		XMLModifier xmlModifier = new XMLModifier();
//		File file = new File("D:\\test.txt");
//		String content = FunctionHelper.readContent(file);
//		String xpath = "/ns0:Msg/ns0:Body";
//		System.out.println(xmlModifier.GetXmlValue(content, xpath));
//		xmlModifier.modify("CT.xml",
//				"/ContainerMovement/Body/Event/CSEvent/CSEventCode", "cntr_transferred");
//		xmlModifier
//				.modify(
//						"SI.xml",
//						"/ShippingInstruction/Body/GeneralInformation/ShipmentTrafficMode/OutBound",
//						"xmlmodifier test   ");
		System.out.println(xmlModifier.replaceEDImsgID("aaaaaEDI2016123003234020-61.request"));
	
	}

}
