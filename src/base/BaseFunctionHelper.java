package base;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XIAOTR on 10/6/2017.
 */
public class BaseFunctionHelper {

    static Logger logger = Logger.getLogger(BaseFunctionHelper.class.getName());

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
    public static void modifyXML(String xmlFilePath, String xpath,
                                 String newValue) {
        try {
            boolean standalone=false;
            String preComment=null;
            File xml=new File(xmlFilePath);
            String fileContent = BaseFunctionHelper.readContent(xml);
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
                    fileContent = BaseFunctionHelper.readContent(newXml);
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
    private static void getAndModifyElement(Element element,String Xpath,String newValue){
        List list = element.elements();
        //�ݹ鷽��
        //  logger.info("Need to Modify Xpath="+Xpath);
        //   logger.info("Xpath.trim()="+Xpath.trim());
        for(Iterator its = list.iterator(); its.hasNext();){
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


    public static String encode(String str)
    {
       //根据默认编码获取字节数组
        String hexString="0123456789abcdef";
        byte[] bytes=str.getBytes();
        StringBuilder sb=new StringBuilder(bytes.length*2);
        //将字节数组中每个字节拆解成2位16进制整数
        for(int i=0;i<bytes.length;i++)
        {
            sb.append("0x");
            sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
            sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
            sb.append(" ");

        }
        return sb.toString().trim();
    }

    public static String getFile(String filepath){
        String filename = "";
        File file = new File(filepath);
        if (!file.isDirectory()) {
            logger.warn("please provide the folder path!");
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            logger.info("found total"+filelist.length+"files!");
            logger.info("get the first file to run");

            for(int i=0;i<filelist.length;i++){
                File readfile = new File(filepath + "\\" + filelist[i]);
                if (!readfile.isDirectory()) {
                    if(! readfile.getName().contains("_Output")){
                        filename = readfile.getName();
                        continue;
                    }

                } else if (readfile.isDirectory()) {
                    logger.warn("Ignored the directory!");
                }
            }

        }

        return filename;
    }

}
