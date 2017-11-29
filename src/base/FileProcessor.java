package base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileProcessor {
	static Logger logger = Logger.getLogger(FileProcessor.class.getName());

	private Configuration config;

//	public FileProcessor(String propertiesFile) {
//
//		try {
//			this.config = new PropertiesConfiguration(propertiesFile);
//		} catch (ConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public String getContentFromTem(String sourceFilePath,String format,String dirid) {
		String content = null;

		content = BaseFunctionHelper.readContent(new File(sourceFilePath));
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");
		
		content = content.replaceAll("&amp;amp;lt;", "&lt;");
		content = content.replaceAll("&amp;amp;gt;", "&gt;");
		content = content.replaceAll("&amp;amp;", "&amp;");
		content = content.replaceAll("&amp;#xD;", "");
		// add by lion on 20100913		
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&amp;lt;", "&lt;");
		content = content.replaceAll("&amp;gt;", "&gt;");
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");
		if(dirid.toLowerCase().startsWith("o") && !(format.toLowerCase().endsWith("xml"))){

		//	content = content.replaceAll("&lt;", "<");
			//content = content.replaceAll("&gt;", ">");
			
			content = content.replaceAll("&amp;amp;", "&amp;");
			content = content.replaceAll("&amp;", "&");
		}else if(dirid.toLowerCase().startsWith("o")){
			content = content.replaceAll("&amp;amp;", "&amp;");
		}
			
		content = content.replaceAll("&apos;", "\'");
		content = content.replaceAll("&quot;", "\"");
		
		
		// if (content.contains("\n")) {
		// content = content.replaceAll("&#xD;", ""); // &#xD; sometime
		// // is\n,sometime is
		// // space
		// } else {
		// content = content.replaceAll("&#xD;", "\n");
		// }
		// content = StringEscapeUtils.unescapeXml(content);

		return content;

	}

	public String getActFileFromTem(String sourceFilePath, String format,
			String key,String dirid) {
		String xml = getContentFromTem(sourceFilePath,format,dirid);
		logger.info("xml aaa="+xml);
		String msgFormat = format;
		String actualFilePath = null;
		try {
			logger.info("Get actual file from temp, get start text by "
					+ key
					+ ".getFileFromDBStartLabel and get end text by "
					+ key + ".getFileFromDBEndLabel");
//			String start = config.getString(key
//					+ ".getFileFromDBStartLabel");
//			String end = config.getString(key
//					+ ".getFileFromDBEndLabel");
            String start = "<ns0:Body>";
            String end = "</ns0:Body>";
			logger.info("Start label : " + start);
			logger.info("End label : " + end);
			//logger.info("XML Content : " + xml);
			if (xml.contains(start) && xml.contains(end)) {
				xml = xml.substring(xml.indexOf(start) + start.length(),
						xml.lastIndexOf(end));
				if (xml.contains("&#xD;")) {
					xml = xml.replaceAll("&#xD;", "\r");
//					if (xml.contains("\n")) {
//						xml = xml.replaceAll("&#xD;", ""); // &#xD; sometime
//															// is\n,sometime is
//															// space
//					} else {
//						xml = xml.replaceAll("&#xD;", "\n");
//					}
				}

			} else {
				logger.warn("The temp file doesn't contain start label: "
						+ start + " and end label: " + end
						+ "\n Please check your config.");
			}
			// actualFilePath = sourceFilePath.substring(0, sourceFilePath
			// .lastIndexOf("."))
			// + "." + format.toLowerCase();
			
			if(!xml.startsWith("<")){
				xml = xml.replaceAll("&amp;", "&");
			}else{
				if(xml.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")){
					logger.info("Start to remove mutil start tag.");
					String formatTag="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
					String startTag=xml.substring(0,formatTag.length());
					xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-8\"\\?>", "");	
					xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
					xml=startTag+xml;				
				}
			}
			//logger.info("xml bbbb="+xml);
			//System.out.println(sourceFilePath);
			actualFilePath = sourceFilePath;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(actualFilePath), "UTF-8"));

			bw.write(xml);
			bw.close();
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
		return actualFilePath;
	}
	
	public String getBCActFileFromTem(String sourceFilePath, String format,String dirid,String delimiter) {
		String xml = getContentFromTem(sourceFilePath,format,dirid);
		//logger.info("xml aaa="+xml);
		String msgFormat = format;
		String actualFilePath = null;
		try {
			String start = "<ns0:Body>";
			String end = "</ns0:Body>";
			logger.info("Start label : " + start);
			logger.info("End label : " + end);
			if (xml.contains(start) && xml.contains(end)) {
				xml = xml.substring(xml.indexOf(start) + start.length(),
						xml.lastIndexOf(end));
				if (xml.contains("&#xD;")) {
					if (xml.contains("\n")) {
						xml = xml.replaceAll("&#xD;", ""); // &#xD; sometime
															// is\n,sometime is
															// space
					} else {
						xml = xml.replaceAll("&#xD;", "\n");
					}
				}

			} else {
				logger.warn("The temp file doesn't contain start label: "
						+ start + " and end label: " + end
						+ "\n Please check your config.");
			}
			// actualFilePath = sourceFilePath.substring(0, sourceFilePath
			// .lastIndexOf("."))
			// + "." + format.toLowerCase();
			
			if(!xml.startsWith("<")){
				xml = xml.replaceAll("&amp;", "&");
			}else{
				if(xml.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")){
					logger.info("Start to remove mutil start tag.");
					String formatTag="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
					String startTag=xml.substring(0,formatTag.length());
					xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"utf-8\"\\?>", "");	
					xml = xml.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
					xml=startTag+xml;				
				}
			}
			if(!StringUtils.isEmpty(delimiter)){
				xml=xml.replaceAll(delimiter,delimiter+"\r\n");
			}
			actualFilePath = sourceFilePath;
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(actualFilePath), "UTF-8"));

			bw.write(xml);
			bw.close();
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
		return actualFilePath;
	}


	public String getComparableContent(File file, String tpID,
			String msgTypeID, String msgFormat) throws IOException {
		String fileContent = BaseFunctionHelper.readContent(file);
		// if ((msgTypeID.startsWith("IE") || msgTypeID.toLowerCase().trim()
		// .contains("ack"))
		// && (fileContent.contains("><"))) {
		// fileContent = fileContent.replaceAll("><", ">\n<");
		// logger.info("Add linefeed for >< .");
		// }
	//	logger.info("fileContent000="+fileContent);
		fileContent = fileContent.replaceAll("\r\n", "\n");
		fileContent = fileContent.replaceAll("\n\r", "\n");
		fileContent = fileContent.replaceAll("\r", "\n");
		ArrayList<String> ignoredList = null;
		if (msgFormat == null) {
			if (fileContent.trim().startsWith("ISA")) {
				msgFormat = "X.12";
			} else if (fileContent.trim().startsWith("UNA")
					|| fileContent.trim().startsWith("UNB")) {
				msgFormat = "EDIFACT";
			} else if (fileContent.trim().startsWith("<")) {
				msgFormat = "XML";
			} else {
				msgFormat = "UIF";
			}
		}
		// logger.info("Msg type ID : "+msgTypeID);
		 logger.info("File format : "+msgFormat);

		String ignoreType = config.getString(msgTypeID + "-" + msgFormat + "-" + tpID
				+ ".ignoredType");
		
		logger.info(msgTypeID + "-" + msgFormat + "-" + tpID
				+ ".ignoredType");

		if (!StringUtils.isEmpty(ignoreType)) {
			logger.info("ignoreType by TP_ID="+ignoreType);
			ignoredList = (ArrayList) config.getList(msgTypeID + "-"
					+ msgFormat + "-" + tpID + ".ingoreCompareField");
		} else {
			ignoreType = config.getString(msgTypeID + "-" + msgFormat
					+ ".ignoredType");
			logger.info("ignoreType by msg_type ="+ignoreType);
			if (StringUtils.isEmpty(ignoreType)) {
				logger.info("Please indicate the ingore details for "
						+ msgTypeID + "-" + msgFormat + " or " + msgTypeID
						+ "-" + msgFormat + "-" + tpID);
				return fileContent;
			} else {
				ignoredList = (ArrayList) config.getList(msgTypeID + "-"
						+ msgFormat + ".ingoreCompareField");

			}

		}

		StringBuffer ignoreSB = new StringBuffer();
		for (int i = 0; i < ignoredList.size(); i++) {
			ignoreSB.append(ignoredList.get(i).toString() + "    ");
		}
		logger.info("Ignore type : " + ignoreType);
		logger.info("Ignore list : " + ignoreSB.toString());
		
		logger.info("fileContent111="+fileContent);

		return getContentAfterIgnore(fileContent, ignoreType, ignoredList);

	}

	public Map<String,String> checkingSeparator(String fileContent){
	    Map<String,String> separators = new HashMap<String, String>();

        String delimiter = "";
        String sep = "+";
        String subSeperator = ":";
        logger.info(fileContent);
        if (fileContent.startsWith("ISA")) {

			delimiter =  fileContent.replaceAll("[\\s\\S]*[0-9]","");
//        	if(fileContent.endsWith("\r\n") && !String.valueOf(fileContent.charAt(105)).equals("\r")){
//				delimiter =  String.valueOf(fileContent.charAt(105))+"\r\n";
//			}else if(fileContent.endsWith("\n") && !String.valueOf(fileContent.charAt(105)).equals("\r") && !String.valueOf(fileContent.charAt(105)).equals("\n")){
//				delimiter =  String.valueOf(fileContent.charAt(105))+"\n";
//			}else if(fileContent.endsWith("\n") && String.valueOf(fileContent.charAt(105)).equals("\n")){
//				delimiter =  "\n";
//			}else if(fileContent.endsWith("\r\n") &&  String.valueOf(fileContent.charAt(105)).equals("\r")){
//				delimiter = "\r\n";
//			} else {
//				delimiter = String.valueOf(fileContent.charAt(105));
//			}
            //	delimiter = fileContent.charAt(fileContent.indexOf("ISA") + 105);
            sep = String.valueOf(fileContent.charAt(3));
            //sep = String.valueOf(fileContent.charAt(fileContent.indexOf("ISA") + 3));
            subSeperator = fileContent.substring(104, 105);
            //	subSeperator = fileContent.substring(fileContent.indexOf("ISA")+104, fileContent.indexOf("ISA")+105);
        } else if(fileContent.contains("\nISA*")){


            delimiter = fileContent.replaceAll("[\\s\\S]*[0-9]","");

            sep = String.valueOf(fileContent.charAt(fileContent.indexOf("ISA") + 3));

            subSeperator = fileContent.substring(fileContent.indexOf("ISA")+104, fileContent.indexOf("ISA")+105);

        }else if (fileContent.startsWith("UNA")) {
            delimiter = String.valueOf(fileContent.charAt(fileContent.indexOf("UNA") + 8));
            //sep = String.valueOf(fileContent.charAt(4));
            sep = String.valueOf(fileContent.charAt(fileContent.indexOf("UNA") + 4));
            subSeperator = fileContent.substring(3, 4);
        }
        else {
            //sep = String.valueOf(fileContent.charAt(3));
            sep = String.valueOf(fileContent.charAt(fileContent.indexOf("UNH") + 3));
            String data[] = fileContent.split("UNH");

            if (data[0].equals("\n") || data[0].endsWith("\n")){

                delimiter = String.valueOf(fileContent.charAt(data[0].length() - 2));
                logger.info(delimiter);
                if(!delimiter.equals("'"))
                    delimiter = "\n";
            }
            else
                delimiter = String.valueOf(fileContent.charAt(data[0].length() - 1));
            // logger.info("delimiter if not have UNA:" + delimiter);
            subSeperator = String.valueOf(data[0].charAt(data[0].indexOf("UNB") + 8));



        }
        separators.put("Delimiter" , delimiter);
        separators.put("Seperator" , sep);
        separators.put("subSeperator" , subSeperator);

        logger.info("Delimiter :" + delimiter);
        logger.info("Seperator :" + sep);
        logger.info("subSeperator :" + subSeperator);
        //	String[] lines = fileContent.split("\\" + delimiter);
       // System.out.println(separators);
        return separators;

    }
	


	public String getContentAfterIgnore(String fileContent, String ignoreType,
			ArrayList<String> ignoredList){
		if (ignoreType.toLowerCase().equals("noignore")) {
			return fileContent;
		} else if (ignoreType.toLowerCase().equals("ignorebyoneindicator")) {	
			StringBuffer newContent = new StringBuffer();
			Date time = new Date();
			SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String systime = form.format(time);
			String xmlFilePath = "d:/Temp-XML-"+systime+".xml";
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(xmlFilePath), "UTF-8"));
				bw.write(fileContent);
				bw.close();
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
			for(int m=0;m<ignoredList.size(); m++){
				
				if(ignoredList.get(m).toString().startsWith("/")){
					logger.info("Ignore XML : "+ignoredList.get(m).toString());
				//	BaseFunctionHelper.modifyXMLForIgnore(xmlFilePath,ignoredList.get(m).toString().trim(),"XXXX");
					BaseFunctionHelper.modifyXML(xmlFilePath,ignoredList.get(m).toString().trim(),"XXXX");
				}
			}
			
			File xmlFile= new File(xmlFilePath);
			fileContent = BaseFunctionHelper.readContent(xmlFile);
			

		if(xmlFile.delete())
				logger.info("Remove Temp File Success.");
			else
				logger.info("Remove Temp File Error.");
			
			
			String[] lines = fileContent.split("\n");
			for (int i = 0; i < lines.length; i++) {
				// For EU24
				lines[i] = lines[i].trim();

				for (int j = 0; j < ignoredList.size(); j++) {
					if (lines[i].contains(ignoredList.get(j).toString())) {
						int start = lines[i].indexOf(">") + 1;
						int end = lines[i].indexOf("</");
						String replaceChar = "#";
						String pre = lines[i].substring(0, start);
						if (end > 0) {
							String post = lines[i].substring(end);
							lines[i] = pre + replaceChar + post;
						} else
							lines[i] = ignoredList.get(j).toString()
									+ replaceChar;

					//	logger.info("Ignore compare :" + lines[i]);
					}
				}
				newContent.append(lines[i] + "\n");
			}		
		//	logger.info("newContent222="+newContent.toString());
				return newContent.toString();
		} else if (ignoreType.toLowerCase().equals("ignorebytwoindicator")) {
			//char delimiter = 0;
			String delimiter = "";
			String sep = "+";
			String subSeperator = ":";
			if (fileContent.startsWith("ISA")) {
				delimiter = String.valueOf(fileContent.charAt(105));
			//	delimiter = fileContent.charAt(fileContent.indexOf("ISA") + 105);
				sep = String.valueOf(fileContent.charAt(3));
				//sep = String.valueOf(fileContent.charAt(fileContent.indexOf("ISA") + 3));
				subSeperator = fileContent.substring(104, 105);
			//	subSeperator = fileContent.substring(fileContent.indexOf("ISA")+104, fileContent.indexOf("ISA")+105);

			} else if(fileContent.contains("\nISA*")){
				
					delimiter = String.valueOf(fileContent.charAt(fileContent.indexOf("ISA") + 105));
				
					sep = String.valueOf(fileContent.charAt(fileContent.indexOf("ISA") + 3));
				
					subSeperator = fileContent.substring(fileContent.indexOf("ISA")+104, fileContent.indexOf("ISA")+105);
				
			}else if (fileContent.startsWith("UNA")) {
				delimiter = String.valueOf(fileContent.charAt(fileContent.indexOf("UNA") + 8));
				//sep = String.valueOf(fileContent.charAt(4));
				sep = String.valueOf(fileContent.charAt(fileContent.indexOf("UNA") + 4));
				subSeperator = fileContent.substring(3, 4);
			}
			else {      
				//sep = String.valueOf(fileContent.charAt(3));
				sep = String.valueOf(fileContent.charAt(fileContent.indexOf("UNH") + 3));
				String data[] = fileContent.split("UNH");

				if (data[0].equals("\n") || data[0].endsWith("\n")){

					delimiter = String.valueOf(fileContent.charAt(data[0].length() - 2));
					logger.info(delimiter);
					if(!delimiter.equals("'"))
						delimiter = "\n";
				}
				else
					delimiter = String.valueOf(fileContent.charAt(data[0].length() - 1));
				// logger.info("delimiter if not have UNA:" + delimiter);
				subSeperator = String.valueOf(data[0].charAt(data[0].indexOf("UNB") + 8));

				

			}

			logger.info("Delimiter :" + delimiter);
			logger.info("Seperator :" + sep);
			logger.info("subSeperator :" + subSeperator);
		//	String[] lines = fileContent.split("\\" + delimiter);
			String[] lines = fileContent.split(delimiter);

			StringBuffer newContent = new StringBuffer();

			
			for (int i = 0; i < lines.length; i++) {
				String temp=lines[i].toString();
			//	temp.replace("\n", "");

					lines[i] = lines[i].replace("\n", "");
						
			//	lines[i] = lines[i].trim();
				//logger.info("temp="+temp);
				int listSize = ignoredList.size();
				if (!(lines[i].equals(""))) {
					for (int j = 0; j < listSize; j++) {
						String[] ignoreDetail = ignoredList.get(j).toString()
								.split(";");												
						if(ignoreDetail.length>1){
							if(ignoreDetail[0].equals("UNB")){
								//logger.info(lines[i]);
							//	logger.info("aaaa="+temp.indexOf("\n"+ignoreDetail[0] + sep));
								if(temp.indexOf("\n"+ignoreDetail[0] + sep)>=0){
									ignoreDetail[0]=lines[i].substring(0, lines[i].indexOf(sep));
									//logger.info("ignoreDetail[0]="+ignoreDetail[0]);
									//ignoreDetail[0].replace("\r\n", "");
									//ignoreDetail[0].replace("\n", "");
								//	logger.info("ignoreDetail[0]="+ignoreDetail[0]);
								}
								
							}
							
							if (lines[i].startsWith(ignoreDetail[0] + sep) || lines[i].startsWith(ignoreDetail[0] + subSeperator)) {
							//	logger.info(lines[i]);
							//	logger.info(ignoreDetail[0] + sep);
								String[] data = lines[i].split(String.format("\\%s", sep));
									
								int	 valuePos = 0;

									StringBuffer value = new StringBuffer();

									for (int kk = 0; kk < data.length; kk++) {
										if(ignoreDetail[1].contains("-")){
											String[] subignoreDetail = ignoreDetail[1].split("-");
											valuePos = Integer.parseInt(subignoreDetail[1]);
											if (kk == valuePos) {
												int ignorelength=0;
												String leaveString = null;
											//	logger.info("data[kk].length()="+data[kk].length());
												if(subignoreDetail[0].toLowerCase().startsWith("l")){
													ignorelength=Integer.parseInt(subignoreDetail[0].substring(1));
												//	logger.info("ignorelength="+ignorelength);
													leaveString = data[kk].substring(data[kk].length()-ignorelength+1);
												//	logger.info("leaveString="+leaveString);
													value.append("#" +leaveString);
												}else if(subignoreDetail[0].toLowerCase().startsWith("r")){
													ignorelength=Integer.parseInt(subignoreDetail[0].substring(1));
											//		logger.info("ignorelength="+ignorelength);
													leaveString = data[kk].substring(0,data[kk].length()-ignorelength);
											//		logger.info("leaveString="+leaveString);
													value.append(leaveString+"#");
												}else{
													logger.info("You config "+ignoreDetail[1]+" ignore parameter 2 must start with L or R!!");
												}
											}else{
												value.append(data[kk] + sep);
											}
											
										}else{
											
										    valuePos = Integer.parseInt(ignoreDetail[1]);
											if (kk == valuePos) {
												value.append("#" + sep);

											} else
												value.append(data[kk] + sep);
											
										//	logger.info("value"+value);
										}

										
									}
								/*	if (valuePos == data.length - 1)
										value.append("#");
									else
										value.append(data[data.length - 1]);*/

									lines[i] = value.toString();
								//	logger.info("Ignore compare :" + lines[i]);
																					
							}
							
						}else{
							if (lines[i].startsWith(ignoreDetail[0])){
								StringBuffer value1 = new StringBuffer();
								value1.append(ignoreDetail[0]+sep+"#");
							//	int end_edifact = lines[i].indexOf(delimiter);
								lines[i] = value1.toString();
							//	logger.info("Ignore compare :" + lines[i]);
							}														
						}
						
					}
				
						newContent.append(lines[i] + delimiter + "\n");
					
					
				} else if (i != lines.length - 1) {		
					
						newContent.append(lines[i] + "\n");
					
				}

			}
			return newContent.toString();
		}

		else if (ignoreType.toLowerCase().equals("ignorebythreeindicator")) {

			Pattern p = Pattern.compile("\n", Pattern.DOTALL);

			// System.out.println("P:"+p);
			String[] lines = p.split(fileContent);
			// String[] lines = content.split("\n");
			StringBuffer newContent = new StringBuffer();

			for (int i = 0; i < lines.length; i++) {
				int listSize = ignoredList.size();
				for (int j = 0; j < listSize; j++) {
					String[] ignoreDetail = ignoredList.get(j).toString()
							.split(";");
					if(ignoreDetail[0].startsWith("_")){
					//	logger.info("Ignore by row # .");
					//	logger.info(ignoreDetail[0]);
					//	logger.info(ignoreDetail[0].substring(1, ignoreDetail[0].length()));
						int row = Integer.parseInt(ignoreDetail[0].substring(1, ignoreDetail[0].length()));						
					//	logger.info("ignore row="+row);
						if(i==row-1){
						//	logger.info("ignore row="+row);
							int valueStartPos = Integer.parseInt(ignoreDetail[1]) - 1;
							int valueLength = Integer.parseInt(ignoreDetail[2]);

							String pre = lines[i].substring(0, valueStartPos);

							StringBuffer value = new StringBuffer();
							for (int s = 0; s < valueLength; s++)
								value.append("#");

							String post = lines[i].substring(valueStartPos
									+ valueLength);
							lines[i] = pre + value.toString() + post;
							logger.info("Ignore compare :" + lines[i]);
						}
					}else{
						if (lines[i].trim().startsWith(ignoreDetail[0])) {
							int valueStartPos = Integer.parseInt(ignoreDetail[1]) - 1;
							int valueLength = Integer.parseInt(ignoreDetail[2]);

							String pre = lines[i].substring(0, valueStartPos);

							StringBuffer value = new StringBuffer();
							for (int s = 0; s < valueLength; s++)
								value.append("#");

							String post = lines[i].substring(valueStartPos
									+ valueLength);
							lines[i] = pre + value.toString() + post;
						//	logger.info("Ignore compare :" + lines[i]);
						}

					}
					
				}
				newContent.append(lines[i] + "\n");

			}
	return newContent.toString();
		}

		else if (ignoreType.toLowerCase().equals("ignoreineachline")) {
			Pattern p = Pattern.compile("\n", Pattern.DOTALL);

			// System.out.println("P:"+p);
			String[] lines = p.split(fileContent);
			// String[] lines = content.split("\n");
			StringBuffer newContent = new StringBuffer();

			for (int i = 0; i < lines.length; i++) {

				lines[i] = lines[i].replace("\n", "");
				lines[i] = lines[i].trim();
				int listSize = ignoredList.size();

				for (int j = 0; j < listSize; j++) {
					String[] ignoreDetail = ignoredList.get(j).toString()
							.split(";");

					// String sep = String.valueOf(lines[i].charAt(key
					// .length()));

					String[] data = lines[i].split("\t");

					int valuePos = Integer.parseInt(ignoreDetail[1]);

					StringBuffer value = new StringBuffer();

					for (int kk = 0; kk < data.length - 1; kk++) {

						if (kk == valuePos) {
							value.append("#");

						} else
							value.append(data[kk]);
					}
					if (valuePos == data.length - 1)
						value.append("#");
					else
						value.append(data[data.length - 1]);

					lines[i] = value.toString();
					logger.info("Ignore compare :" + lines[i]);

				}

				newContent.append(lines[i] + "\n");

			}
			return newContent.toString();
		}

		return null;

	}

	public boolean combine(List fileNameList, String targetFilePath)
			throws IOException {
		logger.info("Begin to combine " + fileNameList.size()
				+ " file(s) ......");
		boolean combine = false;
		StringBuffer sb = new StringBuffer();
		String combineFileName = targetFilePath;

		for (int i = 0; i < fileNameList.size(); i++) {
			FileReader fr = new FileReader(fileNameList.get(i).toString());
			BufferedReader br = new BufferedReader(fr);

			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
				sb.append("\n");
			}

		}
		sb.deleteCharAt(sb.length() - 1);
		FileWriter fw = new FileWriter(combineFileName);
		fw.write(sb.toString());
		fw.close();
		File file = new File(combineFileName);
		if (file.exists()) {
			combine = true;
			logger.info("Combine file successfully : " + file.getName());
		}
		return combine;
	}
	
	public static byte[] getByteContent(String sourceFilePath) throws IOException {
		File file = new File(sourceFilePath);  		  
        long fileSize = file.length();  
        if (fileSize > Integer.MAX_VALUE) {  
            System.out.println("file too big...");  
            return null;  
        }  
  
        FileInputStream fi = new FileInputStream(file);  
  
        byte[] buffer = new byte[(int) fileSize];  
  
        int offset = 0;  
  
        int numRead = 0;  
  
        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {  
  
            offset += numRead;  
  
        }  
  
        if (offset != buffer.length) {  
  
            throw new IOException("Could not completely read file "  
                    + file.getName());   
        }  
  
        fi.close();    
        return buffer;  
	}

}
