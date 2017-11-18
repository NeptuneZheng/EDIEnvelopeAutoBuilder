package util;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class SQLGenerator_IGDefinitionNew {

	//CUS_4010_315_CS.xml
	//IFTMCS_D99B_CAR_IFTMCS_20140813.XSD-IG-Definition.xml
	//CUS_4010_323_GTNEXUS.xml
	//BelugaOcean-Demo-Incoming-CT.315.4010-IG-definition.xml
	//CAR_D99B_IFTMCS.xml
	public String scriptFileFullPath = "./demo/IG_Definition/CUS_D99B_IFTMIN_CS.xml";
	
	//pmt use
	public String tpId = "HAVEN";//"BELUGA_OCEAN_E2X";//"GTNEXUSPORT";//"APLUPROD";//"DSGOODS";        //@TP_ID
	public String msgTypeId = "SI";//"SI", "SS_PT2PT";//"BL";//"CT";        //@MSG_TYPE_ID
	public String dirId = "I";//"I";//"O";             //@DIR_ID
	public String msgFmtId = "";
	public String operation = "e2x";//"e2x";//"x2e";         //@OPERATION , x2e or e2x
	public String ctrlnum_Sender = "";//"CARGOSMART";             //"CARGOSMART";     //@CTRL_NUM_SENDER
	public String ctrlnum_receiver = "";//"GTNEXUS";              //"DSGOODS";   //@CTRL_NUM_RECEIVER
	public String ctrlnum_msgtype = "";//"SS_PT2PT";              //"CT";    //@CTRL_NUM_MSGTYPE
	public String ctrlnum_format = "";//"X.12";                   //"X.12";     //@CTRL_NUM_FORMAT
	public String transformSettingsJsonStr = "";//"{\"recordDelimiter\":\"0x7e\",\"elementDelimiter\":\"|\",\"subElementDelimiter\":\"\",\"escapeChar\":\"\",\"elementType\":\"delimited\",\"isSuppressEmptyNodes\":\"true\",\"isX12\":\"true\",\"isFieldValueTrimRightSpace\":\"false\",\"x12Envelop\":{\"isa\":{\"I01_01\":\"00\",\"I02_02\":\"          \",\"I03_03\":\"00\",\"I04_04\":\"          \",\"I05_05\":\"01\",\"I06_06\":\"CARGOSMART     \",\"I05_07\":\"ZZ\",\"I07_08\":\"GTNEXUS        \",\"I08_09\":\"%yyMMdd%\",\"I09_10\":\"%HHmm%\",\"I10_11\":\"U\",\"I11_12\":\"00401\",\"I12_13\":\"%EDI_CTRL_NUM%\",\"I13_14\":\"0\",\"I14_15\":\"P\",\"I15_16\":\"\u003e\"},\"gs\":{\"E479_01\":\"SO\",\"E142_02\":\"CARGOSMART\",\"E124_03\":\"GTNEXUS\",\"E373_04\":\"%yyyyMMdd%\",\"E337_05\":\"%HHmm%\",\"E28_06\":\"%GROUP_CTRL_NUM%\",\"E455_07\":\"X\",\"E480_08\":\"004010\"},\"st\":{\"E143_01\":\"323\",\"E329_02\":\"%TXN_CTRL_NUM_START%\"},\"se\":{\"E96_01\":\" \",\"E329_02\":\"%TXN_CTRL_NUM_END%\"},\"ge\":{\"E97_01\":\"%TXN_COUNT%\",\"E28_02\":\"%GROUP_CTRL_NUM%\"},\"iea\":{\"I16_01\":\"1\",\"I12_02\":\"%EDI_CTRL_NUM%\"}}}";             //"{\"recordDelimiter\":\"~\",\"elementDelimiter\":\"*\",\"subElementDelimiter\":\"\",\"escapeChar\":\"\",\"elementType\":\"delimited\",\"isSuppressEmptyNodes\":\"true\",\"isX12\":\"true\",\"isFieldValueTrimRightSpace\":\"false\",\"x12Envelop\":{\"isa\":{\"I01_01\":\"00\",\"I02_02\":\"          \",\"I03_03\":\"00\",\"I04_04\":\"          \",\"I05_05\":\"ZZ\",\"I06_06\":\"CARGOSMART     \",\"I05_07\":\"ZZ\",\"I07_08\":\"APLUNET        \",\"I08_09\":\"%yyMMdd%\",\"I09_10\":\"%HHmm%\",\"I10_11\":\"U\",\"I11_12\":\"00401\",\"I12_13\":\"%EDI_CTRL_NUM%\",\"I13_14\":\"0\",\"I14_15\":\"P\",\"I15_16\":\"\u003e\"},\"gs\":{\"E479_01\":\"QO\",\"E142_02\":\"CARGOSMART\",\"E124_03\":\"APLUNET\",\"E373_04\":\"%yyyyMMdd%\",\"E337_05\":\"%HHmm%\",\"E28_06\":\"%GROUP_CTRL_NUM%\",\"E455_07\":\"X\",\"E480_08\":\"004010\"},\"st\":{\"E143_01\":\"315\",\"E329_02\":\"%TXN_CTRL_NUM_START%\"},\"se\":{\"E96_01\":\"   \",\"E329_02\":\"%TXN_CTRL_NUM_END%\"},\"ge\":{\"E97_01\":\"%TXN_COUNT%\",\"E28_02\":\"%GROUP_CTRL_NUM%\"},\"iea\":{\"I16_01\":\"1\",\"I12_02\":\"%EDI_CTRL_NUM%\"}}}"; //@TRANSFORM_SETTINGS_JSON_STRING

	//* For output sql path
	public String sqlOutputFilePath = "";//"../../04_prod/sql/";
	public String sqlOutputFileStarter = "";
	
	String definitionFileName = ""; //@SCRIPT_NAME
	String md5 = "";                //@MD5
	public String groovyCommonProjectFolderRootPath = "";

	public void generateSqlForDefinitionFile() throws Exception {
		if (CommonUtil.isEmpty(msgFmtId)) {
			throw new Exception("msgFmtId is mandatory.");
		}
		if (CommonUtil.isEmpty(tpId)) {
			throw new Exception("tpId is mandatory.");
		}
		if (CommonUtil.isEmpty(msgTypeId)) {
			throw new Exception("msgTypeId is mandatory.");
		}
		if (CommonUtil.isEmpty(dirId)) {
			throw new Exception("dirId is mandatory.");
		}
		
		// Read the Groovy File
 		File scriptFile = new File(scriptFileFullPath);
 		if (! scriptFile.exists()) {
 			System.out.println("File not found for : " + scriptFile.getAbsolutePath() + ". ");
 			throw new Exception("File not found: " + scriptFile.getAbsolutePath());
 		}
 		definitionFileName = scriptFile.getName();
		 		
		// System parameters **
		
		String scriptTemplatePath = "template_beluga_cfg.sql";
		
		if (sqlOutputFilePath==null || sqlOutputFilePath.trim().length()==0) {
			sqlOutputFilePath = "c:/";
		}
		// SQL File output
		if (CommonUtil.isEmpty(sqlOutputFileStarter)) {
			sqlOutputFileStarter = "b2b_owner";
		} else {
			sqlOutputFileStarter = sqlOutputFileStarter.trim();
		}
	    File FileOut = new File(sqlOutputFilePath +sqlOutputFileStarter+"_"+ definitionFileName + ".sql");
	    
	   
 		String scriptBody = LocalFileUtil.readBigFile(scriptFile.getAbsolutePath());
 		
 		byte [] bytesEncoded = scriptBody.getBytes("UTF-8");
		String base64 = DatatypeConverter.printBase64Binary(bytesEncoded);
		int count = 0;
		StringBuffer concatVariables = new StringBuffer();
		
		// Read the SQL Template File	    
		 File SQLTemplate = new File ("resource/sqls/template/" + scriptTemplatePath);
		 String SQLTemplateBody = LocalFileUtil.readBigFile(SQLTemplate.getAbsolutePath());
		 		
		for(String current : CommonUtil.cutString(base64, 1000)){
			String variableName = "var"+count;
			concatVariables.append(variableName);
			concatVariables.append("||");
			SQLTemplateBody = SQLTemplateBody.replace("@VAR64", variableName+" clob :='"+current+"';\n@VAR64");
			count++;
		}
		
		concatVariables.delete(concatVariables.length()-2, concatVariables.length());
		SQLTemplateBody = SQLTemplateBody.replace("@VAR64", "");
		
		String md5 = CommonUtil.generateMD5_A(base64);
	    
		String sql = SQLTemplateBody.replace("@REFNAME", "Copy from " + definitionFileName+", generated at "+(new Date()));
		sql = sql.replace("@MD5", md5);
		sql = sql.replace("@TP_ID", tpId);
		sql = sql.replace("@MSG_TYPE_ID", msgTypeId);
		sql = sql.replace("@DIR_ID", dirId);
		sql = sql.replace("@MSG_FMT_ID", msgFmtId);
		sql = sql.replace("@VARIABLE_64", concatVariables.toString());
		
		sql = sql.replace("@SCRIPT_NAME", definitionFileName);
		sql = sql.replace("@OPERATION", operation);
		//ctrlnum setting
		ctrlnum_Sender = ctrlnum_Sender==null?"":ctrlnum_Sender.trim();
		sql = sql.replace("@CTRL_NUM_SENDER", ctrlnum_Sender);
		ctrlnum_receiver = ctrlnum_receiver==null?"":ctrlnum_receiver.trim();
		sql = sql.replace("@CTRL_NUM_RECEIVER", ctrlnum_receiver);
		ctrlnum_msgtype = ctrlnum_msgtype==null?"":ctrlnum_msgtype.trim();
		sql = sql.replace("@CTRL_NUM_MSGTYPE", ctrlnum_msgtype);
		ctrlnum_format = ctrlnum_format==null?"":ctrlnum_format.trim();
		sql = sql.replace("@CTRL_NUM_FORMAT", ctrlnum_format);
		transformSettingsJsonStr = transformSettingsJsonStr==null?"":transformSettingsJsonStr.trim();
		sql = sql.replace("@TRANSFORM_SETTINGS_JSON_STRING", transformSettingsJsonStr);
		

		FileWriter fileWriterSQL = new FileWriter(FileOut, false);
		fileWriterSQL.write(sql);		
		fileWriterSQL.flush();
		fileWriterSQL.close();
		
		System.out.println("Generate validator sql : \r\n\r\n"+sql);
		
		System.out.println("Generate definition SQL End.\r\n");
		
		System.out.println("------------------------------------------------");
		System.out.println("Output Folder: "+FileOut.getParentFile().getAbsolutePath());
		System.out.println("Output SQL File: "+FileOut.getName());
		System.out.println("------------------------------------------------");
		
	}
	
	/**
	 * by David Liang : welcome to use, feel free to contact me if any problem.
	 */
	
	public static void main(String[] args) throws Exception {
		
		SQLGenerator_IGDefinitionNew util = new SQLGenerator_IGDefinitionNew();
		
		try {
			util.generateSqlForDefinitionFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
