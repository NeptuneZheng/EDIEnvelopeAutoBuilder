package base;


import org.apache.log4j.Logger;
import utility.process.TibcoJMSHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Created by XIAOTR on 10/8/2017.
 */
public class FileUpDownLoadHandler {
    static Logger logger = Logger.getLogger(FileUpDownLoadHandler.class);
    private  String jmsServerUrl;
    private  String jmsUserName;
    private  String jmsPassword;
    private  String environment;
    private  String tpID;
    private  String dirID;
    private  String msgTypeID;
    private  String destinationQueue;
    private  String externalParty;
    private  String replyToQueue;

    private String msgFormat = null;
    private String msgGdlVer = null;
    private String msgCfgVer = null;

    private String prevMsgRequestId = null;
    private String nextMsgRequestId = null;

    private String involveMsgReqID = null;
    private String country = null;
    private String fileName = null;

    private DBHandler dbHandler;

    public FileUpDownLoadHandler(String environment, String jmsTpID, String jmsMsgTypeID,
                                 String jmsDirID, String destinationQueue, String replyToQueue,
                                 String jmsExternalParty,DBHandler dbHandler) {
        this.environment = environment;
        this.dirID = jmsDirID;
        this.msgTypeID = jmsMsgTypeID;
        this.externalParty = jmsExternalParty;
        this.tpID = jmsTpID;
        this.destinationQueue = destinationQueue;
        this.replyToQueue = replyToQueue;
        this.dbHandler = dbHandler;

    }

    public String uploadFile(String inputFilePath){
        boolean uploadSuccess = false;
        String tempInputFileName = BaseFunctionHelper
                .getTempFileName(inputFilePath);
        String jmsServerUrl ="";
        if(this.environment.contains("QA4")){
            jmsServerUrl =  "hksudv38:7264";

        }else {
            jmsServerUrl = "csb2bediqa1ems01:9222";
        }

        String jmsUserName ="admin";
        String jmsPassword = "csadmin";
        TibcoJMSHandler tibcoJMSHandler = new TibcoJMSHandler(
                jmsServerUrl, jmsUserName, jmsPassword);


        Date time = new Date();
        SimpleDateFormat form = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar
                .getInstance(new SimpleTimeZone(0, "GMT"));
        form.setCalendar(cal);
        String gmtDateTime = form.format(time);
        logger.info("Destination Queue Name: -----" + destinationQueue);
        uploadSuccess = tibcoJMSHandler.publish(destinationQueue,
                replyToQueue, tpID, msgTypeID, dirID,
                externalParty, msgFormat, msgGdlVer, msgCfgVer,
                prevMsgRequestId, nextMsgRequestId,
                involveMsgReqID, country, fileName, inputFilePath,
                tempInputFileName);

        if(uploadSuccess){
            return tempInputFileName;
        }else{
            return null;
        }

    }


}
