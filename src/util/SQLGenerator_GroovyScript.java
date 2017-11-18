package util;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class SQLGenerator_GroovyScript {

	//* For SQL generating Scripts
	// "./src/cs/b2b/core/mapping/util/MappingUtil.groovy";
	// "./src/cs/b2b/core/mapping/util/MappingUtil_CT_O_Common.groovy";
	// "./src/cs/b2b/core/mapping/util/MappingUtil_SI_I_Common.groovy";
	// "./src/cs/b2b/core/mapping/bean/ct/ContainerMovement.groovy";  
	// "./src/cs/b2b/core/mapping/bean/MessageBeanCS2XmlCommon.groovy";
	// "./src/cs/b2b/core/mapping/bean/edi/edifact/d99b/IFTMIN/EDI_IFTMIN.groovy";
	// "./src/cs/b2b/core/mapping/script/CUS_VGMXML_CS2ManifestXML_INTTRA.groovy";
	//"./src/cs/b2b/mapping/scripts/CUS_VGMXML_CS2ManifestXML_YUSENLOGISTICS_VGM.groovy";
	
	//PMT Mapping Script
	// "./src/cs/b2b/mapping/scripts/CUS_CS2CTXML_315_DSGOODS.groovy";
	// APL_IFTMCS_to_CS2XML.groovy
	// CUS_CS2SSXML_323_GTNEXUSPORT.groovy
	// "./src/cs/b2b/mapping/scripts/CUS_IFTMIN_CS2SIXML_HAVEN.groovy";
	public String groovyScriptFileFullPath = "";
	
	//pmt use
	public String tpId = "";//"YUSENLOGISTICS_VGM";//"LALEMENTSTREAM_VGM";//"HAVEN";//"GTNEXUSPORT";//"APLUPROD";//"DSGOODS";
	public String msgTypeId = "";//"VGM";//"VGM";//"SI";//"SS_PT2PT";//"BL";//"CT";
	public String dirId = "";//"I";//"I";//"I";//"O";
	public String msgFmtId = "";
	
	//* For output sql path
	public String sqlOutputFilePath = "";//"../../04_prod/sql/";
	public String sqlFileStarter = "";

	public String pmtProcTypeId = "TRANSLATE";
	String pmtRefKey = "";
	
	String groovyClassName = "";
	String groovyClassType = "";
	String commonScriptRemarks = "";
	
	String groovyScriptFileName = "";

	public String ideaFolder = "";

	public void generateSqlForScript(String mappingFile) throws Exception {
		groovyScriptFileFullPath = mappingFile;
		// Read the Groovy File
 		File mappingEDIGroovyFile = new File(groovyScriptFileFullPath);
 		if (! mappingEDIGroovyFile.exists()) {
 			System.out.println("File not found for : " + mappingEDIGroovyFile.getAbsolutePath() + ". ");
 			System.out.println("Please check file name and package name! ");
 			throw new Exception("File not found: " + mappingEDIGroovyFile.getAbsolutePath());
 		}
 		groovyScriptFileName = mappingEDIGroovyFile.getName();
		 		
		//1, Detect what your type want to action
		String packageName = mappingEDIGroovyFile.getParent().replace("\\", "/");
		String pkgPreffix = "/src/"+ideaFolder;

		if (packageName.contains(pkgPreffix)) {
			packageName = packageName.substring(packageName.indexOf(pkgPreffix)+pkgPreffix.length());
		} else {
			throw new Exception("Only allow to generate resource under this project, with correctly package name, please check. Valid example is './src/cs/b2b/core/mapping/bean/ct/ContainerMovement.groovy'. ");
		}
		packageName = packageName.replace("/", ".");
		
		// System parameters **
		
		String scriptTemplatePath = "";
		if (sqlOutputFilePath==null || sqlOutputFilePath.trim().length()==0) {
			sqlOutputFilePath = "c:/";
		}
		
		//start to generate sql, check script type first
		if (packageName.startsWith("main.groovy.cs.b2b.core.mapping.bean")) {
			//this is bean script
			scriptTemplatePath =  "resource/sqls/template/template_groovy_script_common.sql";
			tpId = " ";
			msgTypeId = " ";
			dirId = " ";
			msgFmtId = " ";
			groovyClassName = groovyScriptFileName.replace(".groovy", "");
			if (packageName.startsWith("main.groovy.cs.b2b.core.mapping.bean.edi.")) {
				groovyClassType = "BEAN-EDI";
				groovyClassName = packageName.replace("main.groovy.","") + "." + groovyClassName;
			} else {
				groovyClassType = "BEAN-CS2XML";
				groovyClassName = packageName.replace("main.groovy.","") + "." + groovyClassName;
			}
		} else if (packageName.startsWith("main.groovy.cs.b2b.core.mapping.util")) {
			//this is mapping util script
			scriptTemplatePath =  "resource/sqls/template/template_groovy_script_common.sql";
			//check msg type id and dir id in file name
			if (groovyScriptFileName.equals("MappingUtil.groovy")) {
				msgTypeId = "ALL";
				dirId = " ";
			} else {
				if (! groovyScriptFileName.contains("_") || !groovyScriptFileName.startsWith("MappingUtil_") || !groovyScriptFileName.endsWith("_Common.groovy")) {
					throw new Exception("Invalid MsgType_DirID common script file name, valid example is: 'MappingUtil_CT_O_Common.groovy', please check your file name: '"+groovyScriptFileName+"'. ");
				}
				String tmp = groovyScriptFileName.replace("MappingUtil_", "");
				tmp = tmp.replace("_Common.groovy", "");
				//now it's MsgType_DirID
				if (tmp.indexOf("_")<=0) {
					throw new Exception("Invalid MsgType_DirID common script file name, my valid example is: 'MappingUtil_CT_O_Common.groovy', please check your file name: '"+groovyScriptFileName+"'. ");
				}
				msgTypeId = tmp.substring(0, tmp.indexOf("_"));
				dirId = tmp.substring(tmp.indexOf("_")+1);
			}
			tpId = " ";
			msgFmtId = " ";
			groovyClassName = packageName.replace("main.groovy.","") + "." + groovyScriptFileName.replace(".groovy", "");
			groovyClassType = "UTIL";
		} else if (packageName.startsWith("main.groovy.cs.b2b.mapping.scripts")) {
			//PMT mapping script
			scriptTemplatePath = "resource/sqls/template/template_groovy_script_pmt.sql";
			if (tpId==null || tpId.trim().length()==0 || msgTypeId==null || msgTypeId.trim().length()==0 || dirId==null || dirId.trim().length()==0) {
				throw new Exception("Invalid PMT info for pmt groovy script generatino. ");
			}
			if (msgFmtId==null || msgFmtId.trim().length()==0) {
				throw new Exception("Please add MSG_FMT_ID for pmt groovy script generation (requires from Feb 24, 2017).");
			}
			
			pmtRefKey = CommonUtil.generateMD5_A(tpId.trim() + msgTypeId.trim() + dirId.trim() + msgFmtId.trim() + pmtProcTypeId.trim());//FingerprintUtils.generateRefKey
			groovyClassName = packageName.replace("main.groovy.","") + "." + groovyScriptFileName.replace(".groovy", "");
		} else {
			throw new Exception("Cannot identify the script type, valid package name is cs.b2b.core.mapping.bean, cs.b2b.core.mapping.util and cs.b2b.mapping.scripts.");
		}
		
		if (groovyClassName.length()>150) {
			throw new Exception("Groovy Class Name is over table design length (150), your length is: "+groovyClassName.length()+", name: "+groovyClassName+", please help to review. ");
		}
		
		if (tpId==null || tpId.length()==0 || msgTypeId==null || msgTypeId.length()==0 || dirId==null || dirId.length()==0 || msgFmtId==null || msgFmtId.length()==0) {
			throw new Exception("Invalid PMT ["+tpId+" / "+msgTypeId+" / "+dirId+" / "+msgFmtId+"] info for script generatino. ");
		}
		
		System.out.println("Generate Groovy SQL Start for PMT ["+tpId+" / "+msgTypeId+" / "+dirId+" / "+msgFmtId+"] ...");
		
		// SQL File output
		if (CommonUtil.isEmpty(sqlFileStarter)) {
			sqlFileStarter = (tpId==null || tpId.trim().length()==0?"b2b_owner":tpId.trim());
		} else {
			sqlFileStarter = sqlFileStarter.trim();
		}
	    File FileOut = new File(sqlOutputFilePath + groovyScriptFileName + ".sql");
	    
	   
 		String mappingGroovyScriptBody = LocalFileUtil.readBigFile(mappingEDIGroovyFile.getAbsolutePath());
 		
 		byte [] bytesEncoded = mappingGroovyScriptBody.getBytes("UTF-8");
		String base64 = DatatypeConverter.printBase64Binary(bytesEncoded);
		int count = 0;
		StringBuffer concatVariables = new StringBuffer();
		
		// Read the SQL Template File	    
		 File SQLTemplate = new File (scriptTemplatePath);
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
	    
		String sql = SQLTemplateBody.replace("@REFNAME", "Copy from " + groovyScriptFileName+", generated at "+(new Date()));
		sql = sql.replace("@MD5", md5);
		sql = sql.replace("@TP_ID", tpId);
		sql = sql.replace("@MSG_TYPE_ID", msgTypeId);
		sql = sql.replace("@DIR_ID", dirId);
		sql = sql.replace("@MSG_FMT_ID", msgFmtId);
		sql = sql.replace("@VARIABLE_64", concatVariables.toString());
		//common script use only
		sql = sql.replace("@SCRIPT_CLASS_NAME", groovyClassName);
		sql = sql.replace("@SCRIPT_TYPE", groovyClassType);
		sql = sql.replace("@SCRIPT_COMMON_REMARKS", commonScriptRemarks);
		//pmt script use only
		sql = sql.replace("@PMT_REF_KEY", pmtRefKey);
		sql = sql.replace("@PROC_TYPE_ID", pmtProcTypeId);
		

		FileWriter fileWriterSQL = new FileWriter(FileOut, false);
		fileWriterSQL.write(sql);		
		fileWriterSQL.flush();
		fileWriterSQL.close();
		
		System.out.println("Generate validator sql : \r\n\r\n"+sql);
		
		System.out.println("Generate Groovy SQL End.\r\n");
		
		System.out.println("------------------------------------------------");
		System.out.println("Output Folder: "+FileOut.getParentFile().getAbsolutePath());
		System.out.println("Output SQL File: "+FileOut.getName());
		System.out.println("------------------------------------------------");
		
	}
	
	/**
	 * by David Liang : welcome to use, feel free to contact me if any problem.
	 */
	
	public static void main(String[] args) throws Exception {
		
		SQLGenerator_GroovyScript util = new SQLGenerator_GroovyScript();
		
		try {
//			util.generateSqlForScript();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
