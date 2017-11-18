package util;

import java.io.File;
import java.io.FileWriter;

public class SQLGenerator_CS2Schema {


	static String inputFolder = "./cs2_schema_validation/";
	static String schemaInputFileName = "CS2-Schema-Files-Zip.zip";
	static String schemaOutputName = "CS2-Schema-Files-Zip";
	
	static String outputFolder = "../../04_prod/sql/";
	static String SQLTemplatePath = "./sqls/template/template_cs2_schema.sql";
	
	
	public static void main(String[] args) throws Exception{
		try {
			SQLGenerator_CS2Schema cs2schema = new SQLGenerator_CS2Schema();
			cs2schema.impl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("main finished.");
	}
	
	public void impl() throws Exception {
		System.out.println("Generate Groovy SQL Start.");
		// SQL File output   		
	    File FileOut = new File(outputFolder+schemaOutputName+".sql");
	    FileWriter FileWriterSQL = new FileWriter(FileOut,false);

	    // Read the SQL Template File	    
		File SQLTemplate = new File (SQLTemplatePath);
		String SQLTemplateBody = LocalFileUtil.readBigFile(SQLTemplate.getAbsolutePath());
		
		// Read the Groovy File
		File mappingEDIGroovyFile = new File(inputFolder+schemaInputFileName);
		String base64 = LocalFileUtil.readBigFileContentDirectlyToBase64(mappingEDIGroovyFile.getAbsolutePath());

		base64 = base64.replace("\r\n", "");
		base64 = base64.replace("\n", "");
		
		//byte [] bytesEncoded = Base64.encodeBase64(mappingGroovyScriptBody.getBytes());
		//String base64 = new String(bytesEncoded );
		
//		byte [] bytesEncoded = mappingGroovyScriptBody.getBytes("UTF-8");
//		String base64 = DatatypeConverter.printBase64Binary(bytesEncoded);
		int count = 0;
		StringBuffer concatVariables = new StringBuffer();
		
		for(String current : CommonUtil.cutString(base64, 1000)){
			String variableName = "var"+count;
			concatVariables.append(variableName);
			
			if(count % 100 == 1){
				concatVariables.append("\n");
			}
			
			concatVariables.append("||");
			SQLTemplateBody = SQLTemplateBody.replace("@VAR64", variableName+" clob :='"+current+"';\n@VAR64");
			count++;
		}
		
		concatVariables.delete(concatVariables.length()-2, concatVariables.length());
		SQLTemplateBody = SQLTemplateBody.replace("@VAR64", "");
		
		String md5 = CommonUtil.generateMD5_A(base64);
	    	
		String sql = SQLTemplateBody.replaceAll("@NAME", schemaOutputName);
		sql = sql.replaceAll("@REFNAME", "Copy from "+schemaInputFileName);
		sql = sql.replaceAll("@MD5", md5);
		sql = sql.replaceAll("@VARIABLE_64", concatVariables.toString());

		System.out.println("Generate validator sql : "+sql);
		
		FileWriterSQL.write(sql);		
		FileWriterSQL.flush();
		FileWriterSQL.close();
			
		System.out.println("Generate Groovy SQL End.");
	}
}
