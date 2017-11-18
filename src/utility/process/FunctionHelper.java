package utility.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class FunctionHelper {

	static Logger logger = Logger.getLogger(FunctionHelper.class.getName());


	public static boolean tpNeedCleanFolder(String tpID,String msgType,String dirID){
		boolean  flag= false;

		logger.info("--------------checking Invoice Special : clean up folder in FTP------------------");

		if((tpID.toUpperCase().equals("KNNTRADESHIFT" )|| tpID.toUpperCase().equals("KNNBETRADESHIFT" )
				|| tpID.toUpperCase().equals("KNNHKTRADESHIFT" ))&& msgType.toUpperCase().equals("IN")
				&&dirID.toUpperCase().equals("O")){
			flag = true;
		}
		return flag;
	}


	public static boolean fileExist(String tpID, int row, String filePath) {

		boolean existFlag = true;
		File file = new File(filePath);
		String lowerCaseFilePath = filePath.toLowerCase().trim();
		int i=lowerCaseFilePath.indexOf(" ");

	//	logger.info(filePath);
		if ((!lowerCaseFilePath.endsWith("n.a"))
				&& (!lowerCaseFilePath.endsWith("/"))
				&& ((!file.isFile()) || (!file.exists()))) {
			if(file.getName().toLowerCase().equals("1.ira")){
				File F = new File(file.getParent());
				if(!F.exists()){
					F.mkdir();
				}
				
				try {
					file.createNewFile();
					 existFlag = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			/*	File iraModelFile=new File("D:/SVN/Regression\\com\\differenceExcelModel\\1.ira");
				try {
					FileUtils.copyFile(iraModelFile,file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}else{
				logger.error("File doesn't exist: " + tpID + " ,Row " + row
						+ " , file: " + filePath);

				existFlag = false;
			}
			
		}
	/*	else if(i!=-1)
		{				
				logger.error("File can not contain blank space: " + tpID + " ,Row " + row
						+ " , file: " + filePath);
				existFlag = false;
		}*/
		
		return existFlag;
	}

	public static String selectEnvironment(String envConfigFile) {

		String env = null;

		String envList = null;
		try {
			Configuration config = new PropertiesConfiguration(envConfigFile);
			envList = config.getString("EnvironmentList");
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] options = null;
		if (envList != null) {
			options = envList.split(";");
		}

		int response = JOptionPane.showOptionDialog(null,
				"Please choose one environment:", "Choose environment",
				JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		if (response >= 0) {
			env = options[response].trim();

		} else if (response == -1) {
			logger.error("You didn't select any environment !");
			JOptionPane.showMessageDialog(null,
					"You didn't select any environment !", "Warning",
					JOptionPane.YES_OPTION);

			System.exit(0);

		} else {
			logger.error("Undefined environment !");
			JOptionPane.showMessageDialog(null, "Undefined environment !",
					"Warning", JOptionPane.YES_OPTION);

			System.exit(0);
		}

		return env;
	}
	
	public static String selectFunction(String envConfigFile) {

		String function = null;

		String functionList = null;
		try {
			Configuration config = new PropertiesConfiguration(envConfigFile);
			functionList = config.getString("FunctionList");
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] options = null;
		if (functionList != null) {
			options = functionList.split(";");
		}

		int response = JOptionPane.showOptionDialog(null,
				"Please choose one function:", "Choose Function Type",
				JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		if (response >= 0) {
			function = options[response].trim();

		} else if (response == -1) {
			logger.error("You didn't select any function !");
			JOptionPane.showMessageDialog(null,
					"You didn't select any function !", "Warning",
					JOptionPane.YES_OPTION);

			System.exit(0);

		} else {
			logger.error("Undefined function !");
			JOptionPane.showMessageDialog(null, "Undefined function !",
					"Warning", JOptionPane.YES_OPTION);

			System.exit(0);
		}

		return function;
	}
	
	

	public static boolean checkExcelAvailable(File excel) {
		if (!excel.renameTo(excel)) {
			logger.error("Auto-Log excel : " + excel.getAbsolutePath());
			logger
					.error("  Error! Please make sure your Auto-Log excel exists and it is closed !  ");

			return false;
		} else {
			return true;
		}

	}

	public static String readContent(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

			char[] buffer = new char[1024];
			int length = 0;
			while ((length = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, length);
			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String replaceParameter(String content, String regex,
			String replacement) {
		if (content.contains(regex)) {
//			logger.info("Replace " + regex + " with " + replacement);
			if (replacement.length() < 1000) {
				content = content.replaceAll(regex, replacement);
			} else {
				StringBuffer contentSB = new StringBuffer();
				int regexPosition = content.indexOf(regex);
				String head = content.substring(0, regexPosition);
				String tail = content.substring(regexPosition + regex.length());
				contentSB.append(head);
				contentSB.append(replacement);
				contentSB.append(tail);
				content = contentSB.toString();

			}

		}

		return content;
	}

	public static String generateUniqueString(int length) {
		String result = null;

		return result;
	}

	public static String getString(XSSFCell cell1) {
		String cell1value = "";
		if (cell1 != null) {

			switch (cell1.getCellType()) {

			case HSSFCell.CELL_TYPE_NUMERIC: {

				if (HSSFDateUtil.isCellDateFormatted(cell1)) {

					Date date = cell1.getDateCellValue();

					cell1value = date.toString();
				}

				else {

					// Double num = new Double((Double)
					// cell1.getNumericCellValue());
					cell1value = String.valueOf(cell1.getNumericCellValue()
							+ "");
				}
				break;
			}

			case HSSFCell.CELL_TYPE_STRING:

				cell1value = cell1.getStringCellValue().replaceAll("'", "''");
				break;

			default:
				cell1value = "";
			}
		} else {
			cell1value = "";
		}
		return cell1value.trim();
	}

	public static String getUniqueString(String type) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date time = new Date();
		SimpleDateFormat form = null;
		String systemString = null;
		String uniqueString = null;
		if (type.toLowerCase().equals("msg_req_num")) {
			form = new SimpleDateFormat("yyyyMMddHHmmss");
			systemString = form.format(time);
			uniqueString = systemString;
		} else if (type.toLowerCase().equals("lrn")
				|| type.toLowerCase().equals("divlrn")
				|| type.toLowerCase().equals("anlrn")) {
			form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			systemString = form.format(time);
			uniqueString = "ENS1" + systemString;
		} else if (type.toLowerCase().equals("mrn")) {
			form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			systemString = form.format(time);
			uniqueString = "10TT" + systemString;
		} else if (type.toLowerCase().equals("correlation_id")) {
			form = new SimpleDateFormat("yyMMddHHmmss");
			systemString = form.format(time);
			uniqueString = "CO" + systemString;
		}else if (type.toLowerCase().equals("blnum")) {
			form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			systemString = form.format(time);
			uniqueString = "BLN-" + systemString;
		}else if (type.toLowerCase().equals("relatebl")) {
			form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			systemString = form.format(time);
			uniqueString = "MBL-" + systemString;
		}else if (type.toLowerCase().equals("callsign")) {
			logger.info(type);
			form = new SimpleDateFormat("HHmmssSSS");
			systemString = form.format(time);
			uniqueString = systemString;
		}else if (type.toLowerCase().equals("filename")) {
			form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			systemString = form.format(time);
			uniqueString = systemString;
		}else if (type.toLowerCase().equals("pdffilename")) {
			form = new SimpleDateFormat("yyyyMMddHH");
			systemString = form.format(time);
			uniqueString = systemString;
		}
		else if (type.toLowerCase().equals("msg_req_id")) {
			form = new SimpleDateFormat("MMddHHmmssSS");
			systemString = form.format(time);
			uniqueString = systemString+"-"+getRandomString("0123456789",2);
		}else if (type.toLowerCase().equals("cs_ref_num")) {
			form = new SimpleDateFormat("HHmmssSS");
			systemString = form.format(time);
			uniqueString = systemString+getRandomString("0123456789",2);
		}else if(type.toLowerCase().equals("uuid")){
			uniqueString = UUID.randomUUID().toString().replaceAll("-", "");
		}else{
			form = new SimpleDateFormat("mmssSSS");
			systemString = form.format(time);
			uniqueString = systemString+getRandomString("0123456789",1);
		}
		// long randomNum = Math.round(Math.floor((Math.random() * 100)));
  //     logger.info(type+"-"+uniqueString);
		return uniqueString;
	}

	public static String getRandomString(String base, int length) {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getLinkageType(String countryName, String configFile) {

		String linkageType = null;

		try {
			Configuration config = new PropertiesConfiguration(configFile);
			linkageType = config.getString(countryName + ".LinkageType");
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return linkageType;
	}

	public static List<String> getListFromConfig(String key, String configFile) {

		List<String> result = null;

		try {
			Configuration config = new PropertiesConfiguration(configFile);
			result = config.getList(key);
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}

	public static boolean copyFile(String msgType, int index, String source,
			String targetFolderPath) {

		File sourceFile = new File(source);

		File targetFolder = new File(targetFolderPath);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		File targetFile = new File(targetFolder.getPath() + "/Row-" + index
				+ "-" + msgType + "-" + sourceFile.getName());
		// System.out.println(expectedFile.getParent());
		// System.out.println(expectedDataPath);

		if (sourceFile.exists()) {
			try {
				FileUtils.copyFile(sourceFile, targetFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// actualFile.renameTo(expectedFile);
			return true;
		}

		return false;

	}

	public static void modifyXMLForIgnore(String xmlFilePath, String xpath,
			String newValue) {
		try {
			FileReader fr;
			fr = new FileReader(new File(xmlFilePath));
			SAXReader reader = new SAXReader();

			logger.info("Modify XML");
			if (xpath.startsWith("//") || (!xpath.startsWith("/"))) {
				logger
						.error(xpath
								+ " is not absolute xpath ,please modify it to absolute xpath format ! Sample : /A/B/C ");
				return;
			}
			
			logger.info("xmlFilePath="+xmlFilePath);
			logger.info("xpath="+xpath);
			logger.info("newValue="+newValue);

			Document document = reader.read(fr);
			fr.close();
			Element root = document.getRootElement(); 
			List list = root.elements();    
			getAndModifyElement(root,xpath,newValue);   
			XMLWriter output = new XMLWriter(new FileWriter(new File(
					xmlFilePath)));
			output.write(document);
			output.close();
			logger.info("Replace " + xpath + " in " + xmlFilePath
					+ " with new value '" + newValue + "' successfully !");
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
	public static String getXMLValue(String xmlFilePath, String xpath) {
		String XMLValue=null;
		try {
			FileReader fr;
			fr = new FileReader(new File(xmlFilePath));
			SAXReader reader = new SAXReader();

			logger.info("Read XML");
			if (xpath.startsWith("//") || (!xpath.startsWith("/"))) {
				logger
						.error(xpath
								+ " is not absolute xpath ,please modify it to absolute xpath format ! Sample : /A/B/C ");
				//return;
			}
			
			
			logger.info("xmlFilePath="+xmlFilePath);
			logger.info("xpath="+xpath);

			Document document = reader.read(fr);
			fr.close();
			Element root = document.getRootElement(); 
			List list = root.elements();    
			XMLValue=getElement(root,xpath);   
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
		return XMLValue;

			
	}
	public static void modifyXML(String xmlFilePath, String xpath,
			String newValue) {
		try {
			boolean standalone=false;
			String preComment=null;
			File xml=new File(xmlFilePath);
			String fileContent = FunctionHelper.readContent(xml);
			if(fileContent.toLowerCase().contains("standalone=\"yes\"")){
				standalone=true;
				if(fileContent.trim().startsWith("<?xml")){
					preComment = fileContent.substring(0,fileContent.indexOf("\"?>")+3);
					logger.info("preComment="+preComment);
				}
			}
			FileReader fr;
			fr = new FileReader(xml);
			SAXReader reader = new SAXReader();
			HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
			boolean success = true;
			boolean isAttribute = false;
			logger.info("Modify XML");
			if (xpath.startsWith("//") || (!xpath.startsWith("/"))) {
				logger
						.error(xpath
								+ " is not absolute xpath ,please modify it to absolute xpath format ! Sample : /A/B/C ");
				return;
			}
			
			logger.info("newValue="+newValue);
			logger.info("xmlFilePath="+xmlFilePath);
			logger.info("xpath="+xpath);
		
			String[] paths = xpath.split("/");
			Document document = reader.read(fr);
			fr.close();
			List list;
			StringBuffer xmlPathSB = new StringBuffer();
			Element element;
			Element root = document.getRootElement(); 
			 list = root.elements();    
		//	 getAndModifyElement(root,xpath,newValue);   
			for (int i = 0; i < paths.length; i++) {
				if (paths[i].trim().equals("")) {
					continue;
				}
			//	logger.info("xmlPathSB="+xmlPathSB);
				if (i == 1) {
					element = document.getRootElement();
					nameSpaceMap.put("ns" + i, element.getNamespaceURI());
					reader.getDocumentFactory().setXPathNamespaceURIs(
							nameSpaceMap);
				} else {
					list = document.selectNodes(xmlPathSB.toString());
					if (list.size() > 0) {
						element = (Element) list.get(0);
						String nodeString = (paths[i].trim().contains("[") && paths[i]
								.trim().contains("]")) ? paths[i].trim()
								.substring(0, paths[i].trim().indexOf("["))
								: paths[i].trim();

						if (element.attribute(paths[i].trim().replaceFirst("@",
								"")) != null) {
							isAttribute = true;
						}

						else if (element.element(nodeString) == null) {
							logger
									.error("Please check your xpath and make sure you file have such element : "
											+ xpath);
							success = false;
							break;

						} else {
							nameSpaceMap.put("ns" + i, element.element(
									nodeString).getNamespaceURI());
							reader.getDocumentFactory().setXPathNamespaceURIs(
									nameSpaceMap);
						}
					} else {
						logger
								.error("Please check your xpath and make sure you file have such element : "
										+ xpath);
						success = false;
						break;
					}
				}
				if (isAttribute) {
					xmlPathSB.append("/" + paths[i]);

				} else {
					xmlPathSB.append("/ns" + i + ":" + paths[i]);
				}

			}
			if (success) {
				logger.info("Analyzed Xpath : " + xmlPathSB.toString());

				list = document.selectNodes(xmlPathSB.toString());
				if (list.size() == 0) {
					logger
							.warn("Can't find the specified element by xpath : "
									+ xpath
									+ ". Please check if you are using absolute xpath ! Absolute xpath is expected !");
					return;
				}

				if (isAttribute) {
					Attribute attribute;
					for (int j = 0; j < list.size(); j++) {
						attribute = (Attribute) list.get(j);
						attribute.setValue(newValue);
					}
				} else {
					for (int j = 0; j < list.size(); j++) {
						element = (Element) list.get(j);
						element.clearContent();
						element.setText(newValue);
					}
				}

				XMLWriter output = new XMLWriter(new FileWriter(new File(
						xmlFilePath)));

//				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(xmlFilePath),"UTF-8");
//				XMLWriter output = new XMLWriter(out);
				output.write(document);
				output.close();
				if(standalone && preComment!=null){
					logger.info("Add back for  <\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>");
					File newXml=new File(xmlFilePath);
					fileContent = FunctionHelper.readContent(newXml);
					fileContent = fileContent.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-8\"\\?>", preComment);
					fileContent = fileContent.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", preComment);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(xmlFilePath), "UTF-8"));
					bw.write(fileContent);
					bw.close();
				}
				logger.info("Replace " + xpath + " in " + xmlFilePath
						+ " with new value '" + newValue + "' successfully !");
			}
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
	
	
	public static String getValueFromXML(String xmlFilePath, String xpath) {
		String nodeValue=null;
		try {
			boolean standalone=false;
			String preComment=null;
			File xml=new File(xmlFilePath);
			String fileContent = FunctionHelper.readContent(xml);
			if(fileContent.toLowerCase().contains("standalone=\"yes\"")){
				standalone=true;
				if(fileContent.trim().startsWith("<?xml")){
					preComment = fileContent.substring(0,fileContent.indexOf("\"?>")+3);
					logger.info("preComment="+preComment);
				}
			}
			FileReader fr;
			fr = new FileReader(xml);
			SAXReader reader = new SAXReader();
			HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
			boolean success = true;
			boolean isAttribute = false;
			logger.info("get value from XML");
			if (xpath.startsWith("//") || (!xpath.startsWith("/"))) {
				logger
						.error(xpath
								+ " is not absolute xpath ,please modify it to absolute xpath format ! Sample : /A/B/C ");
				//return;
			}
						
			logger.info("xmlFilePath="+xmlFilePath);
			logger.info("xpath="+xpath);
		
			String[] paths = xpath.split("/");
			Document document = reader.read(fr);
			fr.close();
			List list;
			StringBuffer xmlPathSB = new StringBuffer();
			Element element;
			Element root = document.getRootElement(); 
			 list = root.elements();    
		//	 getAndModifyElement(root,xpath,newValue);   
			for (int i = 0; i < paths.length; i++) {
				if (paths[i].trim().equals("")) {
					continue;
				}
			//	logger.info("xmlPathSB="+xmlPathSB);
				if (i == 1) {
					element = document.getRootElement();
					nameSpaceMap.put("ns" + i, element.getNamespaceURI());
					reader.getDocumentFactory().setXPathNamespaceURIs(
							nameSpaceMap);
				} else {
					list = document.selectNodes(xmlPathSB.toString());
					if (list.size() > 0) {
						element = (Element) list.get(0);
						String nodeString = (paths[i].trim().contains("[") && paths[i]
								.trim().contains("]")) ? paths[i].trim()
								.substring(0, paths[i].trim().indexOf("["))
								: paths[i].trim();

						if (element.attribute(paths[i].trim().replaceFirst("@",
								"")) != null) {
							isAttribute = true;
						}

						else if (element.element(nodeString) == null) {
							logger
									.error("Please check your xpath and make sure you file have such element : "
											+ xpath);
							success = false;
							break;

						} else {
							nameSpaceMap.put("ns" + i, element.element(
									nodeString).getNamespaceURI());
							reader.getDocumentFactory().setXPathNamespaceURIs(
									nameSpaceMap);
						}
					} else {
						logger
								.error("Please check your xpath and make sure you file have such element : "
										+ xpath);
						success = false;
						break;
					}
				}
				if (isAttribute) {
					xmlPathSB.append("/" + paths[i]);

				} else {
					xmlPathSB.append("/ns" + i + ":" + paths[i]);
				}

			}
			if (success) {
				logger.info("Analyzed Xpath : " + xmlPathSB.toString());

				list = document.selectNodes(xmlPathSB.toString());
				if (list.size() == 0) {
					logger
							.warn("Can't find the specified element by xpath : "
									+ xpath
									+ ". Please check if you are using absolute xpath ! Absolute xpath is expected !");
					//return;
				}

				if (isAttribute) {
					Attribute attribute;
					for (int j = 0; j < list.size(); j++) {
						attribute = (Attribute) list.get(j);
						nodeValue=attribute.getValue();
					}
				} else {
					for (int j = 0; j < list.size(); j++) {
						element = (Element) list.get(j);
						//element.clearContent();
						nodeValue=element.getText();
					}
				}

				XMLWriter output = new XMLWriter(new FileWriter(new File(
						xmlFilePath)));
				
				output.write(document);
				output.close();
				if(standalone && preComment!=null){
					logger.info("Add back for  <\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>");
					File newXml=new File(xmlFilePath);
					fileContent = FunctionHelper.readContent(newXml);
					fileContent = fileContent.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-8\"\\?>", preComment);	
					fileContent = fileContent.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", preComment);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(xmlFilePath), "UTF-8"));
					bw.write(fileContent);
					bw.close();
				}
				logger.info("Get value of " + xpath + " in " + xmlFilePath
						+ "' successfully !");
			}
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
		return nodeValue;

	}
	public static void modifyEDI(String ediPath, String modifyDetailStr,
			String newValue) {
		String[] modifyDetail;
		if (!modifyDetailStr.contains("-")) {
			logger.warn("Can't modify edi field by config like "
					+ modifyDetailStr);
			return;
		} else {
			modifyDetail = modifyDetailStr.split("-");
			if (modifyDetail.length < 2) {
				logger.warn("Can't modify edi field by config like "
						+ modifyDetailStr);
				return;
			}
		}
		if (modifyDetail.length >= 2) {

			String fileContent = readContent(new File(ediPath));
			fileContent = fileContent.replaceAll("\r\n", "\n");
			fileContent = fileContent.replaceAll("\r", "\n");
			char delimiter = 0;
			String seperator = "+";
			String subSeperator = ":";
			String releaseIndicator = "?";
	
			if (fileContent.startsWith("ISA")) {
				delimiter = fileContent.charAt(105);
				seperator = String.valueOf(fileContent.charAt(3));
				subSeperator = fileContent.substring(104, 105);
			} else if (fileContent.startsWith("UNA")) {
				delimiter = fileContent.charAt(fileContent.indexOf("UNA") + 8);
				seperator = String.valueOf(fileContent.charAt(4));
				subSeperator = fileContent.substring(3, 4);
				releaseIndicator = fileContent.substring(6, 7);
			}

			else {
				seperator = String.valueOf(fileContent.charAt(3));
				String data[] = fileContent.split("UNH");

				if (data[0].equals("\n") || data[0].endsWith("\n"))
					delimiter = fileContent.charAt(data[0].length() - 2);
				else
					delimiter = fileContent.charAt(data[0].length() - 1);
				// logger.info("delimiter if not have UNA:" + delimiter);

			}

			logger.info("Delimiter :" + delimiter + "  Seperator :" + seperator
					+ "  Sub-Seperator :" + subSeperator
					+ "  Release-Indicator :" + releaseIndicator);
			
			boolean linefeedFlag=true;
			if(fileContent.endsWith("\n")){
				linefeedFlag=true;
				logger.info("This file use linefeed.");
			}
			else{
				linefeedFlag=false;
				logger.info("This file no use linefeed.");
			}
			
			String[] lines = fileContent.split("\\" + delimiter);
			StringBuffer newContent = new StringBuffer();

			for (int i = 0; i < lines.length; i++) {

				lines[i] = lines[i].replace("\n", "");
				// lines[i] = lines[i].trim();

				if (!(lines[i].equals(""))) {

					if (lines[i].startsWith(modifyDetail[0])) {

						String[] data = lines[i].split("\\" + seperator);

						int valuePos = Integer.parseInt(modifyDetail[1]);

						StringBuffer value = new StringBuffer();

						for (int k = 0; k <= data.length - 1; k++) {

							if (k == valuePos) {
								if (modifyDetail.length >= 3) {

									String[] subData = data[k].split("\\"
											+ subSeperator);
									int subValuePos = Integer
											.parseInt(modifyDetail[2]);
									if (subValuePos > 0) {
										subValuePos = subValuePos - 1;
									}
									for (int m = 0; m <= subData.length - 1; m++) {

										if (m == subValuePos) {
											if (m == subData.length - 1) {

												value.append(newValue);
											}

											else {
												value.append(newValue
														+ subSeperator);
											}
										} else {

											if (m == subData.length - 1) {
												value.append(subData[m]);

											} else {
												value.append(subData[m]
														+ subSeperator);
											}
										}

									}

								} else if (modifyDetail.length == 2) {

									value.append(newValue);
								}
								if (k != data.length - 1) {
									value.append(seperator);
								}
							} else {
								if (k != data.length - 1) {
									value.append(data[k] + seperator);
								} else {
									value.append(data[k]);
								}
							}

						}
						// if (valuePos == data.length - 1)
						// value.append("#");
						// else
						// value.append(data[data.length - 1]);

						lines[i] = value.toString();
						logger.info("Modify " + modifyDetail + " with "
								+ newValue + " : " + lines[i]);

					}

					if(delimiter=='\n'){
						newContent.append(lines[i] + delimiter);
					}else{
						newContent.append(lines[i] + delimiter + "\n");
					}	

				} else if (i != lines.length - 1) {
					newContent.append(lines[i] + "\n");
				}

			}
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(ediPath)));
				bw.write(newContent.toString());

				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void modifyUIF(String uifPath, String modifyDetailStr,
			String newValue) {
		logger.info("Modify UIF...");
		String[] modifyDetail;
		if (!modifyDetailStr.contains("-")) {
			logger.warn("Can't modify uif field by config like "
					+ modifyDetailStr);
			return;
		} else {
			modifyDetail = modifyDetailStr.split("-");
			if (modifyDetail.length < 3) {
				logger.warn("Can't modify uif field by config like "
						+ modifyDetailStr);
				return;
			}
		}
		if (modifyDetail.length >= 3) {
			String indicater = modifyDetail[0];
			int positon = Integer.parseInt(modifyDetail[1]);
			int length = Integer.parseInt(modifyDetail[2]);
			String fileContent = readContent(new File(uifPath));
			boolean linefeedflag=false;
			if(fileContent.endsWith("\n")){
				linefeedflag=true;
			}
			fileContent = fileContent.replaceAll("\r\n", "\n");
			fileContent = fileContent.replaceAll("\r", "\n");
			Pattern p = Pattern.compile("\n", Pattern.DOTALL);
			String[] lines = p.split(fileContent);
		//	String[] lines = fileContent.split("\n");
			logger.info("length="+lines.length);
			StringBuffer newContent = new StringBuffer();
			int row=99999999;

			if(indicater.startsWith("_")){
				row = Integer.parseInt(indicater.substring(1));
			}
			for (int i = 0; i < lines.length; i++) {

				if (lines[i].startsWith(indicater) || (i==row)) {

					String header = lines[i].substring(0, positon - 1);
					String trailer = lines[i].substring(positon + length - 1);
					if (newValue.length() > length) {
						newValue = newValue.substring(0, length);
					} else if (newValue.length() < length) {

						// newValue = String.format("%1$-1000s", newValue);

						newValue = String.format("%-" + length + "s", newValue);
					}

					lines[i] = header + newValue + trailer;
					logger.info("Replace segement: " + indicater
							+ "  Start position: " + positon + "  Length: "
							+ length + "  with new value " + newValue);
				}

				if(i==(lines.length-1)){
					if(linefeedflag)
						newContent.append(lines[i] + "\n");
					else
						newContent.append(lines[i]);
				}else
					newContent.append(lines[i] + "\n");
				
			}

			try {
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(uifPath)));
				bw.write(newContent.toString());

				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void modifyByField(String uifPath, String modifyDetailStr,String newValue) {
		logger.info("Modify By Field...");
		String[] modifyDetail;
		if (!modifyDetailStr.contains("-")) {
			logger.warn("Can't modify by field by config like "
					+ modifyDetailStr);
			return;
		} else {
			modifyDetail = modifyDetailStr.split("-");
			if (modifyDetail.length != 2) {
				logger.warn("Can't modify uif field by config like "
						+ modifyDetailStr);
				return;
			}
		}
		String indicater = modifyDetail[0];
		int length = Integer.parseInt(modifyDetail[1]);
		String fileContent = readContent(new File(uifPath));
		fileContent = fileContent.replaceAll("<"+indicater+">", newValue.substring(0, length));
		try {
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(uifPath)));
			bw.write(fileContent.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean prettyFormatXML(String xmlFilePath) {
		try {
			FileReader fr;
			fr = new FileReader(new File(xmlFilePath));
			SAXReader reader = new SAXReader();

			Document document = reader.read(fr);

			fr.close();

			 OutputFormat format = OutputFormat.createPrettyPrint(); // ����XML�ĵ������ʽ
             format.setEncoding("UTF-8"); // ����XML�ĵ��ı�������             
             format.setIndent(true);      // �����Ƿ�����
             format.setIndent("   ");     // �Կո�ʽʵ������                        
             format.setNewlines(true); 
             format.setNewLineAfterDeclaration(false);//����ȥ���ڶ��еĿ���
             format.setTrimText(false);
         //    format.setExpandEmptyElements(false);           
      //       format.setPadText(true);
       //      format.setSuppressDeclaration(true);
         //   format.setExpandEmptyElements(false);
			XMLWriter output = new XMLWriter(new FileWriter(new File(
					xmlFilePath)), format);

			output.write(document);
			output.close();

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public static void prettyXML(File xmlfile) {		
	    String filepath=xmlfile.getAbsolutePath();  
		String newFilePath=xmlfile.getAbsolutePath()+"-temp";
	    String root=filepath.split(":")[0];
		logger.info("root="+root);	
		String command = "cmd /c "+ root + ":\\SVN\\Regression\\com\\tools\\xml.exe ed \"" + filepath +"\""+ "> \""+newFilePath+"\"";
		String command1= "taskkill /f /t /im xml.exe";
	
		try {
			logger.info(command);		
			Process process = Runtime.getRuntime().exec(command);	
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info(command1);	
			process = Runtime.getRuntime().exec(command1);	
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			process.destroy();
			} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File file = new File(filepath);
		File newFile = new File(newFilePath);		
		System.gc();
	//	logger.info("new file length="+newFile.length());
		if(newFile.length()>0)
		{	
			System.gc();
			xmlfile.delete();		
			newFile.renameTo(file);
		}else
		{
			System.gc();
			newFile.delete();
		}
		
}
	
	public static String translateCodeToXML(String content) {

		content = StringEscapeUtils.escapeXml(content);

		return content;

	}

	public static boolean checkInColumnList(List list, String key) {
		boolean inFlag = false;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).toString().toLowerCase().equals(
					key.toLowerCase().trim())) {
				inFlag = true;
				break;
			}
		}

		return inFlag;

	}

	public static boolean checkInRowList(List list, String column,
			String valueExpected) {
		boolean inFlag = false;

		String ignoreDetail = null;
		String[] ignoreDetailArray = null;
		for (int i = 0; i < list.size(); i++) {

			ignoreDetail = list.get(i).toString();
			if (!ignoreDetail.contains(";")) {
				logger
						.info("Ignored key "
								+ ignoreDetail
								+ " for ignored row is not correct.Please define the configuration for ignored row like DB.ignoreCompare.sheet-name.Row = column-name;value ");
			} else {
				ignoreDetailArray = ignoreDetail.split(";");

				if ((ignoreDetailArray[0].toString().toLowerCase()
						.equals(column.toLowerCase().trim()))
						&& (ignoreDetailArray[1].toString().toLowerCase()
								.equals(valueExpected.toLowerCase().trim()))) {
					inFlag = true;
					break;
				}
			}
		}

		return inFlag;

	}

	public static boolean backupFilewithTimestemp(File file){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
		Calendar calendar = Calendar.getInstance();
		File backFile = new File(file.getParent()+"/Auto-Backup-" +simpleDateFormat.format(calendar.getTime())+file.getName());
		if (file.exists()) {
			try {
				FileUtils.copyFile(file, backFile);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			// actualFile.renameTo(expectedFile);

		} else {
			return false;
		}
	}

	public static boolean backupFile(File file) {
		File backFile = new File(file.getParent()+"/Auto-Backup-" + file.getName());
		if (file.exists()) {
			try {
				FileUtils.copyFile(file, backFile);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			// actualFile.renameTo(expectedFile);

		} else {
			return false;
		}

	}
	
	public static String genBLNum(String type){
		String upperCaseAlphaAndNumber = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				upperCaseAlphaAndNumber, 1);
		String blNum=null;
		if(type.equals("MB"))
			 blNum="MB"+systime+RandomNum;
		else
			 blNum="BL"+systime+RandomNum;
		
		logger.info("blNum="+blNum);
		return blNum;
	}
	public static String genCSRefNum(){
		String upperCaseAlphaAndNumber = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("HHmmssSSS");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				upperCaseAlphaAndNumber, 1);
		String csRefNum="CS"+systime+RandomNum;
		logger.info("CSBookingRefNumber="+csRefNum);
		return csRefNum;
	}
	public static String genTXNNum(){
		String upperCaseAlphaAndNumber = "1234567890";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("HHmmssSSS");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				upperCaseAlphaAndNumber, 1);
		String TXNNum="TXN"+systime+"-00"+RandomNum;
		logger.info("TXNNum="+TXNNum);
		return TXNNum;
	}
	public static String genFRNum(){
		String upperCaseAlphaAndNumber = "1234567890";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("MMddHHmmss");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				upperCaseAlphaAndNumber, 4);
		String TXNNum="TXN"+systime+RandomNum;
		logger.info("TXNNum="+TXNNum);
		return TXNNum;
	}
	public static String genRandomNum(String length ){
		String upperCaseAlphaAndNumber = "1234567890";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("MMddHHmmss");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				upperCaseAlphaAndNumber, Integer.valueOf(length).intValue());
		//String RndNum="TXN"+systime+RandomNum;
		//logger.info("TXNNum="+TXNNum);
		return RandomNum;
	}
	public static String genMsgID(){
		String upperCaseAlphaAndNumber = "1234567890";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("MMddHHmmssSSS");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				upperCaseAlphaAndNumber, 2);
		Calendar calendar = Calendar.getInstance();
		String MsgID="EDIT"+(calendar.get(Calendar.YEAR)-1)+systime+"-"+RandomNum;
		logger.info("MsgID="+MsgID);
		return MsgID;
	}
	public static void modifyCIFFile(String path,String tpID,String msgType){
		logger.info("Start to generate BLRefNum ......");
		
		String alphaAndNumber = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		String UpperC="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("MMddHHmmss");
		String systime = form.format(time);
		String RandomNum = FunctionHelper.getRandomString(
				alphaAndNumber, 2);
		String RandomNum2 = FunctionHelper.getRandomString(
				alphaAndNumber, 2);
		String blNum=systime+RandomNum;
		String blNum2=systime+RandomNum2;
		logger.info("blNum="+blNum);
		logger.info("blNum2="+blNum);
		String scac=FunctionHelper.getRandomString(
				UpperC, 4);
		logger.info("scac="+scac);
		modifyXML(path, "/Manifest/BillOfLading/HouseBillNumber", blNum);
		modifyXML(path, "/Manifest/BillOfLading/MasterBillNumber", scac+blNum);
		modifyXML(path, "/Manifest/BillOfLading/OceanBillNumber", blNum2);
		
	}
	
	public static boolean modifyXMLFile(String filePath, String tpID, String msgType,String msgConfigFile) {
		try {
			Configuration config = new PropertiesConfiguration(msgConfigFile);
			ArrayList modifiedFieldList = (ArrayList) config.getList(tpID + "."
					+ msgType);
			logger.info("There are " + modifiedFieldList.size()
					+ " fields to be modified.Config is got by " + tpID + "."
					+ msgType);
			String[] configuration;
			String replacement = null;
			for (int i = 0; i < modifiedFieldList.size(); i++) {	
				configuration = modifiedFieldList.get(i).toString().split(";");
				String format = configuration[0].trim().toLowerCase();
				
				if(configuration[1].trim()!=null && configuration[1].trim().equals("CSRefNum")){
					logger.info("Start to generen CSBookingRefNumber. " );
					if(replacement==null)
						replacement=genCSRefNum();
					if (format.equals("xml")) {

						FunctionHelper.modifyXML(filePath,
								configuration[2], replacement);

					}
				}else{
					FunctionHelper.modifyXML(filePath,
							configuration[2], configuration[1]);
				}
										
			}
			return true;
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

	}	
	
	
	public static String getCurrentDateTime(int longIndicator){
		Date time = new Date();
		SimpleDateFormat form=null;
			if(longIndicator==0){
				form= new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SSS");}
			else {
				form= new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
			}
//		Calendar cal = Calendar
//				.getInstance(new SimpleTimeZone(0, "GMT"));
//		form.setCalendar(cal);
		return form.format(time);
	}
	
	
	
	public static boolean saveContentToFile(String content,String filePath){
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath)));
			bw.write(content);

			bw.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getTempFileName(String inputFilePath) {
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String systime = form.format(time);
		long randomNum = Math.round(Math.floor((Math.random() * 100)));	
		/*String tempInputFileName = ((new File(inputFilePath)).getName()).replace(" ", "") + "-"
				+ systime + "-" + randomNum;*/
		String tempInputFileName = (new File(inputFilePath)).getName() + "-"
				+ systime + "-" + randomNum;
		return tempInputFileName;
	}
	
	public static String getTempFileNameAddBefore(String inputFilePath) {
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String systime = form.format(time);
		long randomNum = Math.round(Math.floor((Math.random() * 100)));	
		/*String tempInputFileName = ((new File(inputFilePath)).getName()).replace(" ", "") + "-"
				+ systime + "-" + randomNum;*/
		String tempInputFileName = systime + "-" + randomNum + "-" + (new File(inputFilePath)).getName();
		return tempInputFileName;
	}
	
	public static String getTempFileNameEU24SE(String inputFilePath) {
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("MMddHHmmss");
		String systime = form.format(time);
		long randomNum = Math.round(Math.floor((Math.random() * 100)));	
		/*String tempInputFileName = ((new File(inputFilePath)).getName()).replace(" ", "") + "-"
				+ systime + "-" + randomNum;*/
		String tempInputFileName = (new File(inputFilePath)).getName() + "-"
				+ systime + "-" + randomNum;
		return tempInputFileName;
	}
	
	public static String getTempFileNameEU24IL_O(String inputFilePath) {
		Date time = new Date();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String systime = form.format(time);
		long randomNum = Math.round(Math.floor((Math.random() * 100)));	
		/*String tempInputFileName = ((new File(inputFilePath)).getName()).replace(" ", "") + "-"
				+ systime + "-" + randomNum;*/
		String tempInputFileName = systime + "-" + randomNum;
		return tempInputFileName;
	}
	
	
	public static boolean modifyFileJP24(String filePath, String tpID, String msgType,String msgConfigFile) {
		try {
			Configuration config = new PropertiesConfiguration(msgConfigFile);
			ArrayList modifiedFieldList = (ArrayList) config.getList(tpID + "."
					+ msgType);
			logger.info("There are " + modifiedFieldList.size()
					+ " fields to be modified.Config is got by " + tpID + "."
					+ msgType);
			String[] configuration;
			String replacement = null;
			for (int i = 0; i < modifiedFieldList.size(); i++) {

				configuration = modifiedFieldList.get(i).toString().split(";");
				String format = configuration[0].trim().toLowerCase();

				if(configuration[1].trim()!=null && configuration[1].trim().equals("BLNUM")){
					logger.info("Start to generen BL NUMBER. " );
					if(replacement==null)
						replacement=genBLNum("BL");
					if (format.equals("xml")) {

						FunctionHelper.modifyXML(filePath,
								configuration[2], replacement);

					}
				}
										
			}
			return true;
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

	}	
	
	public static boolean copyFiles(String filePath,String tag) {
		//logger.info("filePath="+filePath);
		//File file =new File(filePath);
		String rootFolder=filePath.substring(0,filePath.indexOf("."));
		//logger.info("rootFolder="+rootFolder);
		String sourcePath=null;
		String targetPath=null;
		if(tag.equals("E2E")){
			sourcePath=rootFolder+"/E2EActualFile";
			targetPath=rootFolder+"/E2EExpectedFile";
		}else if(tag.equals("E2EXML")){
			sourcePath=rootFolder+"/E2EActualXML";
			targetPath=rootFolder+"/E2EExpectedXML";
		//	sourcePath=rootFolder+"/E2EExpectedXML";
		//	targetPath=rootFolder+"/E2EExpectedFile";
		}else{
			 sourcePath=rootFolder+"/Actual"+tag;
			 targetPath=rootFolder+"/Expected"+tag;
		}
		
		logger.info("Copy " +tag+ " from " +sourcePath + " to "+"targetPath");
		//logger.info("sourcePath="+sourcePath);
		File source =new File(sourcePath.toString());
		try {
			
			if(source.exists()&&source.isDirectory()){
				//logger.info("source exist");
				File target =new File(targetPath);
				if(!target.exists() || !target.isDirectory()){
					target.mkdirs();
					logger.info(" Create "+target);
				}
				File[] actualFile= source.listFiles();
				for (int i = 0; i < actualFile.length; i++) {
		            if (actualFile[i].isFile()) {
		            //	logger.info("actualFile[i]");
		            	File expectedFile=new File(target+"/"+actualFile[i].getName());
		            	if(tag!=null && tag.toLowerCase().equals("db")){
		            		if(expectedFile.exists())
		            			expectedFile.delete();
		            		FileUtils.copyFile(actualFile[i], expectedFile);
		            	}else{
		            		String xml = readContent(actualFile[i]);
			            	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
			    					new FileOutputStream(expectedFile), "UTF-8"));

			    			bw.write(xml);
			    			bw.close();
			    			
		            	}
		            	actualFile[i].delete();
		            	
		            }    	
				}
			}
		
		} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return true;
	}
	
	private static void getAndModifyElement(Element element,String Xpath,String newValue){    
        List list = element.elements();    
        //�ݹ鷽��     
      //  logger.info("Need to Modify Xpath="+Xpath);
     //   logger.info("Xpath.trim()="+Xpath.trim());
        for(Iterator its =  list.iterator();its.hasNext();){    
        //	 logger.info("Need to Modify Xpath Ignore ="+Xpath);
            Element chileEle = (Element)its.next();    
        //    logger.info("�ڵ㣺"+chileEle.getName()+",���ݣ�"+chileEle.getText());   
        //    logger.info("chileEle.getPath()="+chileEle.getPath());
      
            if(chileEle.getPath()!=null && chileEle.getPath().trim().equals(Xpath.trim()))
            { 
            //	logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            	if(!StringUtils.isEmpty(chileEle.getText())){
            		logger.info("Replace value for Ignore.");
            		logger.info("Replace "+chileEle.getPath()+" Value="+chileEle.getText()+" with new value  "+newValue);
                	chileEle.setText(newValue);
            	}          	
            }
            getAndModifyElement(chileEle,Xpath,newValue);    
        }    
    }     
	private static String getElement(Element element,String Xpath){    
        List list = element.elements();  
        String XMLElement=null;
        //�ݹ鷽��     
      //  logger.info("Need to Modify Xpath="+Xpath);
     //   logger.info("Xpath.trim()="+Xpath.trim());
        for(Iterator its =  list.iterator();its.hasNext();){    
        //	 logger.info("Need to Modify Xpath Ignore ="+Xpath);
            Element chileEle = (Element)its.next();    
        //    logger.info("�ڵ㣺"+chileEle.getName()+",���ݣ�"+chileEle.getText());   
        //    logger.info("chileEle.getPath()="+chileEle.getPath());
      
            if(chileEle.getPath()!=null && chileEle.getPath().trim().equals(Xpath.trim()))
            { 
            //	logger.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            	if(!StringUtils.isEmpty(chileEle.getText())){
            		logger.info("Found "+chileEle.getPath()+" Value="+chileEle.getText()); 
            		XMLElement=chileEle.getText();
            	}          	
            }
            getElement(chileEle,Xpath);    
        }    
        return XMLElement;
    } 
	
	public static String getDirection(String excel){//healthcheck-CT-OUT-CS2XML-Special.xlsx
		logger.info("excel: "+excel);
		String[] str = null;
		str = excel.split("-");
		String msgtype = str[1];
		String dir = str[2];
		logger.info("dir: "+dir);
		
		return dir;
	}
	
	public static String getEnv(){
		String env = "QA3";
//		logger.info("excel: "+excel);
//		String[] str = null;
//		str = excel.split("-");
//		String msgtype = str[1];
//		logger.info("msgType: "+msgtype);
//		try {
//			Configuration config = new PropertiesConfiguration(envConfigFile);
//			String envList = config.getString("configEnv");
//		} catch (ConfigurationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		return env;
	}
	/**
	 * 判断时间格式 格式必须为“YYYY-MM-dd”
	 * 2004-2-30 是无效的
	 * 2003-2-29 是无效的
	 * @return
	 */
	public static boolean isValidDate(String str,String pattern) {
		DateFormat formatter = new SimpleDateFormat(pattern);
		try{
			Date date = formatter.parse(str);
			return   str.equals(formatter.format(date));
		}catch(Exception e){
			return  false;
		}
	}
	public static String returnDateFormat(String inputStr){
		String dateFormat = "";
		if(isValidDate(inputStr,"yyyyMMdd")){
			dateFormat = "%yyyyMMdd%";
		}else if(isValidDate(inputStr,"yyMMdd")){
			dateFormat = "%yyMMdd%";
		}else if(isValidDate(inputStr,"HHmm")){
			dateFormat = "%HHmm%";
		}
		return dateFormat;
	}

}
