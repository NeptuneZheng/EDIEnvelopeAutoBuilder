package automation;

import base.*;
import bean.PmtConfig;
import org.apache.log4j.Logger;

import java.io.File;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by XIAOTR on 10/6/2017.
 */
public class FileHandler {

    static Logger logger = Logger.getLogger(FileHandler.class.getName());



    public PmtConfig autoSubmitFile(PmtConfig pmtConfig,String env){
        FileHandler fileHandler = new FileHandler();
        // DB connect
        DBHandler dbHandler = new DBHandler(env);
        dbHandler.connectB2BDB();

        // when input exists, start to upload
        if(new File(pmtConfig.getFolderpath()+pmtConfig.getInputfilename()).exists()){
            FileUpDownLoadHandler fileUpDownLoadHandler = new FileUpDownLoadHandler(pmtConfig.getEnviroment(),pmtConfig.getTp_id(),pmtConfig.getMsg_type_id(),
                    pmtConfig.getDir_id(),pmtConfig.getDestinationQueue(),pmtConfig.getReplyToQueue(),
                    pmtConfig.getJsmExternalParty(),dbHandler);
            String msg_asso_id = fileUpDownLoadHandler.uploadFile(pmtConfig.getFolderpath()+pmtConfig.getInputfilename());
            System.out.println(msg_asso_id);
            //get msg id
            String msg_req_id = dbHandler.getMessageRequestIdByAssociatedID(msg_asso_id,pmtConfig.getDir_id(),10,1);
            System.out.println(msg_req_id);
            // get file from db
            boolean hasMCI = dbHandler.hasMCIDestination(pmtConfig.getTp_id(),pmtConfig.getMsg_type_id(),pmtConfig.getDir_id());
            String proc_id = "";
            boolean isMCI = false;
            if(hasMCI){
                proc_id = "MCI";
                isMCI = true;
            }else {
                proc_id = "MD2SD";
            }

            String getFileFromDBwithSuccess = dbHandler.getFileFromDB(msg_req_id,proc_id,pmtConfig.getFolderpath(),0);

            if(!getFileFromDBwithSuccess.equals("")){
                FileProcessor fileProcessor = new FileProcessor();
                pmtConfig.setMCI(isMCI);
                pmtConfig.setFolderpath(getFileFromDBwithSuccess);
                String filePath = fileProcessor.getActFileFromTem(pmtConfig.getFolderpath(), pmtConfig.getMsg_format(),pmtConfig.getMsg_type_id()+"_"+ pmtConfig.getMsg_format(),pmtConfig.getDir_id());
                System.out.println(filePath);
                String content = BaseFunctionHelper.readContent(new File(filePath));
                System.out.println(content);
                Map<String,String> separator = fileProcessor.checkingSeparator(content);
                pmtConfig.setDelimiter(separator.get("Delimiter"));
                pmtConfig.setSeperator(separator.get("Seperator"));
                pmtConfig.setSubSeperator(separator.get("subSeperator"));

                fileHandler.getHeaderInfo(content,separator,pmtConfig);

            }

        }else {
            logger.error("Failed to find the input file : "+pmtConfig.getFolderpath()+pmtConfig.getInputfilename());
        }
        dbHandler.close();
        return  pmtConfig;
    }

    public static String findValueInArrays(String[] arr, String targetValue) {
        boolean hasValue = false;
        String str = "";
        int i = 0;
        for (String s : arr) {
            if (s.startsWith(targetValue)){
                hasValue =  true;
                if(hasValue){
                    logger.info(targetValue + "'s index = "+ i);
                    str  = arr[i];
                }
                break;
            }
            i++;
        }
        return str;
    }

    public PmtConfig  getHeaderInfo(String content, Map<String,String> separator,PmtConfig pmtConfig){

        String[] elements = content.split(separator.get("Delimiter"));
        if(content.startsWith("UNA")){
            logger.info("This TP has been setup UNA.");
//            pmtConfig.setUna(elements[0]);
//            logger.info("UNA : "+pmtConfig.getUna());
//            pmtConfig.setUnb(elements[1]);
//            logger.info("UNB : " + pmtConfig.getUnb());
//            pmtConfig.setUnh(elements[2]);
//            logger.info("UNH : " + pmtConfig.getUnh());
            pmtConfig.setUna(findValueInArrays(elements,"UNA"));
            logger.info("UNA : "+pmtConfig.getUna());
            pmtConfig.setUnb(findValueInArrays(elements,"UNB"));
            logger.info("UNB : " + pmtConfig.getUnb());
            pmtConfig.setUng(findValueInArrays(elements,"UNG"));
            logger.info("UNG : " + pmtConfig.getUng());
            pmtConfig.setUnh(findValueInArrays(elements,"UNH"));
            logger.info("UNH : " + pmtConfig.getUnh());
            pmtConfig.setUne(findValueInArrays(elements,"UNE"));
            logger.info("UNH : " + pmtConfig.getUne());
        }else {
            if(content.startsWith("ISA")){
                pmtConfig.setIsa(findValueInArrays(elements,"ISA"));
                logger.info("ISA : " + pmtConfig.getIsa());
                pmtConfig.setGs(findValueInArrays(elements,"GS"));
                logger.info("GS : " + pmtConfig.getGs());
//                if(elements[2].startsWith("ST")){
//                    pmtConfig.setTxnIndentifier(elements[2].split(String.format("\\%s", pmtConfig.getSeperator()))[1]);
//                }
                String st = findValueInArrays(elements,"ST");
                pmtConfig.setTxnIndentifier(st.split(String.format("\\%s", pmtConfig.getSeperator()))[1]);
                logger.info("ST02 : " + pmtConfig.getTxnIndentifier());
            }else if(content.startsWith("UNB")){
                pmtConfig.setUnb(findValueInArrays(elements,"UNB"));
                logger.info("UNB : " + pmtConfig.getUnb());
                pmtConfig.setUng(findValueInArrays(elements,"UNG"));
                logger.info("UNG : " + pmtConfig.getUng());
                pmtConfig.setUnh(findValueInArrays(elements,"UNH"));
                logger.info("UNH : " + pmtConfig.getUnh());
                pmtConfig.setUne(findValueInArrays(elements,"UNE"));
                logger.info("UNH : " + pmtConfig.getUne());
            }

        }
        return  pmtConfig;
    }



    public void test(){
        DBHandler dbHandler = new DBHandler();
        dbHandler.connectB2BDB();

        FileUpDownLoadHandler fileUpDownLoadHandler = new FileUpDownLoadHandler("b2bqa3","KNN","CT",
                "O","CS2.B2B.JOB_REQ.CONTAINERMOVEMENT.OUT.QUE","B2B.CS2.INTEGRATION.MESSAGE_ACKNOWLEDGEMENT_OUT.QUE",
                "",dbHandler);
        String msg_asso_id = fileUpDownLoadHandler.uploadFile("D:\\Auto_Code\\TestFile\\STD-Baseline.xml");
        System.out.println(msg_asso_id);
        //get msg id
        String msg_req_id = dbHandler.getMessageRequestIdByAssociatedID(msg_asso_id,"O",1,1);
        System.out.println(msg_req_id);
        dbHandler.getFileFromDB(msg_req_id,"MCI","D:\\Auto_Code\\TestFile\\1.testOutput.txt",0);
        FileProcessor fileProcessor = new FileProcessor();
        String filePath = fileProcessor.getActFileFromTem("D:\\Auto_Code\\TestFile\\1.testOutput.txt","EDIFACT","CT-EDIFACT","O");
        dbHandler.close();
        System.out.println(filePath);
        String content = BaseFunctionHelper.readContent(new File(filePath));
        System.out.println(content);
        Map<String,String> separator = fileProcessor.checkingSeparator(content);
        String[] elements = content.split(separator.get("Delimiter"));
        String isa =  elements[0];
        System.out.println("ISA : " + isa);
        String gs = elements[1];
        System.out.println("GS : " + gs);

    }



}
