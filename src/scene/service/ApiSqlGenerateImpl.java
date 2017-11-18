package scene.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import util.*;

import java.io.File;
import java.nio.file.Files;

public class ApiSqlGenerateImpl {

//	static String ideaFolder = "main/groovy/";
//	static String groovyCommonProjectFolderRootPath = "./../../b2b_is/";

	public static void main(String[] args) {
		try {
			ApiSqlGenerateImpl impl = new ApiSqlGenerateImpl();
//			impl.generateKukriSqlFile("", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateKukriSqlFile(String mappingScriptFile,String commonFile,Boolean igCheckStatus,Boolean mappingCheckStatus,Boolean commonCheckStatus) throws Exception {
//		groovyCommonProjectFolderRootPath = b2bisCommonProjectPath;

		File workFile = new File(API_JSON_FOLDER);
		if (!workFile.exists()) {
			init();
			System.out.println("\r\n>> Working folder "+API_JSON_FOLDER+" and samples are created, please check.\r\n");
			return;
		}

		File[] files = workFile.listFiles();
		if (files==null || files.length==0) {
			System.out.println("No working file (*.json) under folder "+API_JSON_FOLDER+".");
			return;
		}

		for(File file : files) {
			if (!file.getName().endsWith(".json")) {
				continue;
			}
			System.out.println(">>> Working on: "+file.getName()+"...");
			//handle files
			generate(file,mappingScriptFile,commonFile,igCheckStatus,mappingCheckStatus,commonCheckStatus);
		}
	}

	private static void init() throws Exception {
		//copy ./api_samples to API_JSON_FOLDER
		File originSampleFolder = new File( "D:\\kukri_sql\\api_samples");
		if (!originSampleFolder.exists()) {
			throw new Exception("GIT is not updated, please pull and you can see 'sampleFolder' ready in project CSB2BEDIGroovyDevelopment.");
		}
		File apiFolder = new File(API_JSON_FOLDER);
		apiFolder.mkdirs();
		
		System.out.println("Init kukri sql working folder...");
		File[] fs = originSampleFolder.listFiles();
		for(int i=0; fs!=null&&i<fs.length; i++) {
			File trgFile = new File(API_JSON_FOLDER + "api_samples/" + fs[i].getName());
			System.out.println("Creating sample file: "+trgFile.getAbsolutePath());
			//jdk 1.7 function
			if (!trgFile.getParentFile().exists()) {
				trgFile.getParentFile().mkdirs();
			}
			Files.copy(fs[i].toPath(), trgFile.toPath());
			System.out.println("Copy sample file: "+trgFile.getAbsolutePath());
		}
		System.out.println("Done.");
	}
	
	private static void generate(File apiJsonFile,String mappingFile,String commonFile,Boolean igCheckStatus,Boolean mappingCheckStatus,Boolean commonCheckStatus) throws Exception {
		if (apiJsonFile==null || ! apiJsonFile.exists()) {
			throw new Exception("Cannot find json file ("+(apiJsonFile==null?"empty":apiJsonFile.getName())+") under folder "+API_JSON_FOLDER+", please check.");
		}
		
		String outputSqlFileName = "";
		
		if (apiJsonFile.getName().startsWith("mapping_") && mappingFile!=null && mappingCheckStatus) {
			callMappingScriptGenerator(apiJsonFile, false,mappingFile);
		} else if (apiJsonFile.getName().startsWith("common_")&& commonFile!=null && commonCheckStatus) {
			callMappingScriptGenerator(apiJsonFile, true,commonFile);
		} else if (apiJsonFile.getName().startsWith("ig_") && igCheckStatus) {
			callDefinitionGenerator(apiJsonFile);
		} else if(!apiJsonFile.getName().startsWith("mapping_") && !apiJsonFile.getName().startsWith("common_") && !apiJsonFile.getName().startsWith("ig_")){
			throw new Exception("Invalid json file name start part, only accept: mapping_ , ig_ , common_ now.");
		}
		
		File outputFile = new File(API_JSON_FOLDER+outputSqlFileName);
		if (! outputFile.getParentFile().exists()) {
			outputFile.mkdirs();
			System.out.println("Created folder: "+outputFile.getParent()+". ");
		}
	}
	
	private static void callMappingScriptGenerator(File apiJsonFile, boolean isCommon,String mappingFile) throws Exception {
		String jsonBody = LocalFileUtil.readBigFile(apiJsonFile.getAbsolutePath());
		Gson gson = new GsonBuilder().create();
		ApiParamMappingScript mapping = gson.fromJson(jsonBody, ApiParamMappingScript.class);
		if (isCommon) {
			mapping.TP_ID = "";
			mapping.MSG_TYPE_ID = "";
			mapping.DIR_ID = "";
			mapping.PROC_TYPE_ID = "";
		} else {
			if (mapping.PROC_TYPE_ID==null || mapping.PROC_TYPE_ID.trim().length()==0) {
				mapping.PROC_TYPE_ID = "TRANSLATE";
			}
			if (CommonUtil.isEmpty(mapping.TP_ID) || CommonUtil.isEmpty(mapping.MSG_TYPE_ID) || CommonUtil.isEmpty(mapping.DIR_ID) || CommonUtil.isEmpty(mapping.MSG_FMT_ID)) {
				throw new Exception("Invalid empty TP_ID, MSG_TYPE_ID, DIR_ID, MSG_FMT_ID found. ");
			} else if (mapping.PROC_TYPE_ID==null || (!mapping.PROC_TYPE_ID.equals("PREPROCESS") && !mapping.PROC_TYPE_ID.equals("TRANSLATE") && !mapping.PROC_TYPE_ID.equals("POSTPROCESS"))) {
				throw new Exception("Invalid PROC_TYPE_ID, possible value is: TRANSLATE, PREPROCESS, POSTPROCESS.");
			}
		}
		File mf = new File(mappingFile);
		if (!mf.exists()) {
			throw new Exception("Cannot find mapping script: "+mf.getAbsolutePath());
		}
		
		SQLGenerator_GroovyScript pmtUtil = new SQLGenerator_GroovyScript();
		pmtUtil.groovyScriptFileFullPath = mappingFile;
		pmtUtil.tpId = mapping.TP_ID;
		pmtUtil.msgTypeId = mapping.MSG_TYPE_ID;
		pmtUtil.dirId = mapping.DIR_ID;
		pmtUtil.msgFmtId = mapping.MSG_FMT_ID;
		pmtUtil.sqlOutputFilePath = apiJsonFile.getParent()+"/";
		pmtUtil.sqlFileStarter = apiJsonFile.getName().replace(".json", "");
		pmtUtil.pmtProcTypeId = mapping.PROC_TYPE_ID;
		pmtUtil.generateSqlForScript(mappingFile);
	}
	
	public static void callDefinitionGenerator(File apiJsonFile) throws Exception {
		String jsonBody = LocalFileUtil.readBigFile(apiJsonFile.getAbsolutePath());
		
		//20170608 - add special handling for special json in transformSetting
		String transformKeyword = "<<transformSetting>>";
		if (jsonBody.indexOf(transformKeyword)>0) {
			String preBody = jsonBody.substring(0, jsonBody.indexOf(transformKeyword)).trim();
			String transStr = jsonBody.substring(jsonBody.indexOf(transformKeyword) + transformKeyword.length()).trim();
			//rebuild the json string
			preBody = preBody.replace("\r\n", "");
			preBody = preBody.replace("\n", "");
			if (preBody.endsWith("}")) {
				preBody = preBody.substring(0, preBody.length()-1);
				if (preBody.endsWith(",")) {
					//no need add ',' if exists
				} else {
					preBody += ",";
				}
				transStr = transStr.replace("\r\n", "");
				transStr = transStr.replace("\n", "");
				transStr = transStr.replace("\"", "\\\"");
				preBody = preBody + "\"transformSetting\":\"" + transStr + "\"}";
				
				jsonBody = preBody;
				
//				System.out.println(preBody);
//				System.out.println("=------=");
			} else {
				throw new Exception("Json body pre-transformSetting part is not valid, not end with '}'. ");
			}
		}
		
		
		Gson gson = new GsonBuilder().create();
		ApiParamDefinition def = gson.fromJson(jsonBody, ApiParamDefinition.class);
		if (def==null || CommonUtil.isEmpty(def.definitionFileName)) {
			throw new Exception("Invalid emtpy definitionFileName found. ");
		}
		if (CommonUtil.isEmpty(def.TP_ID) || CommonUtil.isEmpty(def.MSG_TYPE_ID) || CommonUtil.isEmpty(def.DIR_ID)) {
			throw new Exception("Invalid emtpy TP_ID, MSG_TYPE_ID or DIR_ID found. ");
		}
		if (CommonUtil.isEmpty(def.MSG_FMT_ID)) {
			throw new Exception("Please provide MSG_FMT_ID, it's required from Feb-24, 2017. ");
		}
		if (CommonUtil.isEmpty(def.operation)) {
			throw new Exception("Invalid empty operation setting.");
		}
		if (!def.operation.equals("x2e") && !def.operation.equals("e2x")) {
			throw new Exception("Invalid operation setting: "+def.operation+", only accept e2x or x2e.");
		}
		if (def.operation.equals("x2e")) {
			if (CommonUtil.isEmpty(def.ediControlNumberSender) || CommonUtil.isEmpty(def.ediControlNumberReceiver) || 
					CommonUtil.isEmpty(def.ediControlNumberMessageType) || CommonUtil.isEmpty(def.ediControlNumberFormat)) {
				throw new Exception("Invalid x2e setting, edi control number is required, your ediControlNumberSender: "+def.ediControlNumberSender
						+", ediControlNumberReceiver: "+def.ediControlNumberReceiver+", ediControlNumberMessageType: "+def.ediControlNumberMessageType+
						", ediControlNumberFormat: "+def.ediControlNumberFormat+". ");
			}
			if (CommonUtil.isEmpty(def.transformSetting)) {
				throw new Exception("Invalid transform setting for x2e, EDI envelop setting cannot be empty. ");
			}
		}
		File dfile = new File( "resource/IG_Definition/" + def.definitionFileName);
		if (!dfile.exists()) {
			throw new Exception("Cannot find definition file: "+dfile.getAbsolutePath()+". ");
		}
		SQLGenerator_IGDefinitionNew igUtil = new SQLGenerator_IGDefinitionNew();
		igUtil.scriptFileFullPath = dfile.getAbsolutePath();
		igUtil.tpId = def.TP_ID;
		igUtil.msgTypeId = def.MSG_TYPE_ID;
		igUtil.msgFmtId = def.MSG_FMT_ID;
		igUtil.dirId = def.DIR_ID;
		igUtil.operation = def.operation;
		igUtil.ctrlnum_Sender = def.ediControlNumberSender;
		igUtil.ctrlnum_receiver = def.ediControlNumberReceiver;
		igUtil.ctrlnum_msgtype = def.ediControlNumberMessageType;
		igUtil.ctrlnum_format = def.ediControlNumberFormat;
		igUtil.transformSettingsJsonStr = def.transformSetting;
		igUtil.sqlOutputFilePath = apiJsonFile.getParent()+"/";
		igUtil.sqlOutputFileStarter = apiJsonFile.getName().replace(".json", "");
		igUtil.generateSqlForDefinitionFile();
	}
	
	final static String IG_DEFINITION_FOLDER = "IG_Definition";
	final static String IG_FORESIGHT_FOLDER = "./IG_ForesightSchema/";
	final static String API_JSON_FOLDER = "d:/kukri_sql/";
	
}
