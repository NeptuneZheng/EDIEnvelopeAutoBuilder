package bean;

/**
 * Created by XIAOTR on 10/11/2017.
 */
public class PmtConfig {

    private String inputfilename;
    private String outputfilename;
    private String tp_id;
    private String dir_id;
    private String msg_type_id;
    private String enviroment;
    private String msg_format;
    private String destinationQueue;
    private String replyToQueue;
    private String jsmExternalParty;
    private String delimiter;
    private String seperator;
    private String subSeperator;
    private String subElementSeperator;
    private String isa;
    private String gs;
    private String una;
    private String unb;
    private String unh;
    private String ung;
    private String une;
    private String txnIndentifier;

    public String getUng() {
        return ung;
    }

    public void setUng(String ung) {
        this.ung = ung;
    }

    public String getUne() {
        return une;
    }

    public void setUne(String une) {
        this.une = une;
    }

    private boolean isMCI;

    public boolean isMCI() {
        return isMCI;
    }

    public void setMCI(boolean MCI) {
        isMCI = MCI;
    }

    public String getTxnIndentifier() {
        return txnIndentifier;
    }

    public void setTxnIndentifier(String txnIndentifier) {
        this.txnIndentifier = txnIndentifier;
    }

    public String getSubElementSeperator() {
        if(subElementSeperator==null){
            subElementSeperator = "";
        }
        return subElementSeperator;
    }

    public void setSubElementSeperator(String subElementSeperator) {
        this.subElementSeperator = subElementSeperator;
    }


    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getSeperator() {
        return seperator;
    }

    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }

    public String getSubSeperator() {
        return subSeperator;
    }

    public void setSubSeperator(String subSeperator) {
        this.subSeperator = subSeperator;
    }

    public String getIsa() {
        return isa;
    }

    public void setIsa(String isa) {
        this.isa = isa;
    }

    public String getGs() {
        return gs;
    }

    public void setGs(String gs) {
        this.gs = gs;
    }

    public String getUna() {
        if(una==null){
            una ="";
        }
        return una;
    }

    public void setUna(String una) {
        this.una = una;
    }

    public String getUnb() {
        return unb;
    }

    public void setUnb(String unb) {
        this.unb = unb;
    }

    public String getUnh() {
        return unh;
    }

    public void setUnh(String unh) {
        this.unh = unh;
    }




    public String getInputfilename() {
        return inputfilename;
    }

    public void setInputfilename(String inputfilename) {
        this.inputfilename = inputfilename;
    }

    public String getTp_id() {
        return tp_id;
    }

    public void setTp_id(String tp_id) {
        this.tp_id = tp_id;
    }

    public String getDir_id() {
        return dir_id;
    }

    public void setDir_id(String dir_id) {
        this.dir_id = dir_id;
    }

    public String getMsg_type_id() {
        return msg_type_id;
    }

    public void setMsg_type_id(String msg_type_id) {
        this.msg_type_id = msg_type_id;
    }

    public String getEnviroment() {
        return enviroment;
    }

    public void setEnviroment(String enviroment) {
        this.enviroment = enviroment;
    }

    public String getMsg_format() {
        return msg_format;
    }

    public void setMsg_format(String msg_format) {
        this.msg_format = msg_format;
    }

    public String getDestinationQueue() {
        if(this.msg_type_id.equals( "CT")){
            this.destinationQueue = "CS2.B2B.JOB_REQ.CONTAINERMOVEMENT.OUT.QUE";
        }else if(this.msg_type_id.equals( "SS")){
            this.destinationQueue =  "CS2.B2B.JOB_REQ.SAILINGSCHEDULE.OUT.QUE";
        }else if(this.msg_type_id.equals( "BC")){
            this.destinationQueue =  "CS2.B2B.JOB_REQ.BOOKINGCONFIRMATION.OUT.QUE";
        }else if(this.msg_type_id.equals( "BL")){
            this.destinationQueue =  "CS2.B2B.JOB_REQ.BILLOFLADING.OUT.QUE";
        }
        return destinationQueue;
    }

    public void setDestinationQueue(String destinationQueue) {
        this.destinationQueue = destinationQueue;
    }

    public String getReplyToQueue() {
        if(this.msg_type_id.equals( "CT")){
            this.replyToQueue = "B2B.CS2.INTEGRATION.MESSAGE_ACKNOWLEDGEMENT_OUT.QUE";
        }else if(this.msg_type_id.equals( "SS")){
            this.replyToQueue =  "CS2.B2B.JOB_RPY.SAILINGSCHEDULE.OUT.QUE";
        }else if(this.msg_type_id.equals( "BC")){
            this.replyToQueue =  "CS2.B2B.JOB_RPY.BOOKINGCONFIRMATION.OUT.QUE";
        }else if(this.msg_type_id.equals( "BL")){
            this.replyToQueue =  "B2B.CS2.INTEGRATION.MESSAGE_ACKNOWLEDGEMENT_OUT.QUE";
        }

        return replyToQueue;
    }

    public void setReplyToQueue(String replyToQueue) {

        this.replyToQueue = replyToQueue;
    }

    public String getFolderpath() {
        return folderpath;
    }

    public void setFolderpath(String folderpath) {
        this.folderpath = folderpath;
    }

    private String folderpath;



    public PmtConfig(String enviroment,String tp_id,String dir_id,String msg_type_id,String msg_format,String inputfilename,String folderpath){
        this.enviroment = enviroment;
        this.tp_id = tp_id;
        this.msg_type_id = msg_type_id;
        this.dir_id = dir_id;
        this.inputfilename = inputfilename;
        this.folderpath = folderpath;
        this.msg_format = msg_format;

    }


    public String getOutputfilename() {
        String prefix = "";
        if(this.inputfilename!=null || !this.inputfilename.equals("")){
            if(this.inputfilename.contains(".")){
                prefix = this.inputfilename.substring(this.inputfilename.lastIndexOf(".")+1);

                outputfilename =  this.inputfilename.substring(0,this.inputfilename.lastIndexOf("."))+"_Output."+prefix;
            }else {
                outputfilename =  this.inputfilename+"_Output";
            }
        }else {
            outputfilename = "";
        }
        return outputfilename;
    }

    public void setOutputfilename(String outputfilename) {
        this.outputfilename = outputfilename;
    }

    public String getProcID() {

        return procID;
    }

    public void setProcID(String procID) {
        this.procID = procID;
    }

    private String procID;


    public String getJsmExternalParty() {
        if(jsmExternalParty==null){
            this.jsmExternalParty = "";
        }

        return jsmExternalParty;
    }

    public void setJsmExternalParty(String jsmExternalParty) {
        this.jsmExternalParty = jsmExternalParty;
    }
}
