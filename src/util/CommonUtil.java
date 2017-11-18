package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
	
	public static boolean isEmpty(String str) {
		return (str==null || str.trim().length()==0);
	}
	
	public static  List<String> cutString(String text, int length){
		if (text==null || text.trim().length()==0)
			return null;
		
		List<String> strings = new ArrayList<String>();
		if (length< 1) {
			strings.add(text);
			return strings;
		}
		
		int index = 0;
		while (index < text.length()) {
			strings.add(text.substring(index, Math.min(index + length,text.length())));
			index += length;
		}
		return strings;
	}
	
	public static String generateMD5_A(String s) throws NoSuchAlgorithmException {
		if (s==null || s.length()==0)
			return null;
		
		byte[] bytesOfMessage = s.getBytes();
		MessageDigest md = MessageDigest.getInstance("MD5");
		return String.format("%032x", new BigInteger(1, md.digest(bytesOfMessage)));	
	}
	
	public static void main(String[] args) {
		try {
			ApiParamDefinition param = new ApiParamDefinition();
			
			param.definitionFileName = "CUS_4010_315_CS.xml";
			
			param.TP_ID = "DSGOODS";
			param.MSG_TYPE_ID = "CT";
			param.DIR_ID = "O";
			
			param.ediControlNumberSender = "CARGOSMART";
			param.ediControlNumberReceiver = "DSGOODS";
			param.ediControlNumberMessageType = "CT";
			param.ediControlNumberFormat = "X.12";
			
			param.transformSetting = "{\"recordDelimiter\":\"~\",\"elementDelimiter\":\"*\",\"subElementDelimiter\":\"\",\"escapeChar\":\"\",\"elementType\":\"delimited\",\"isSuppressEmptyNodes\":\"true\",\"isX12\":\"true\",\"isFieldValueTrimRightSpace\":\"false\",\"x12Envelop\":{\"isa\":{\"I01_01\":\"00\",\"I02_02\":\"          \",\"I03_03\":\"00\",\"I04_04\":\"          \",\"I05_05\":\"ZZ\",\"I06_06\":\"CARGOSMART     \",\"I05_07\":\"ZZ\",\"I07_08\":\"APLUNET        \",\"I08_09\":\"%yyMMdd%\",\"I09_10\":\"%HHmm%\",\"I10_11\":\"U\",\"I11_12\":\"00401\",\"I12_13\":\"%EDI_CTRL_NUM%\",\"I13_14\":\"0\",\"I14_15\":\"P\",\"I15_16\":\">\"},\"gs\":{\"E479_01\":\"QO\",\"E142_02\":\"CARGOSMART\",\"E124_03\":\"APLUNET\",\"E373_04\":\"%yyyyMMdd%\",\"E337_05\":\"%HHmm%\",\"E28_06\":\"%GROUP_CTRL_NUM%\",\"E455_07\":\"X\",\"E480_08\":\"004010\"},\"st\":{\"E143_01\":\"315\",\"E329_02\":\"%TXN_CTRL_NUM_START%\"},\"se\":{\"E96_01\":\"   \",\"E329_02\":\"%TXN_CTRL_NUM_END%\"},\"ge\":{\"E97_01\":\"%TXN_COUNT%\",\"E28_02\":\"%GROUP_CTRL_NUM%\"},\"iea\":{\"I16_01\":\"1\",\"I12_02\":\"%EDI_CTRL_NUM%\"}}}";
			
			Gson gson = new GsonBuilder().create();
			String out = gson.toJson(param);
			System.out.println("str: \r\n"+out);
			
			ApiParamMappingScript map = new ApiParamMappingScript();
			map.fullClassName = "cs.b2b.mapping.scripts.CUS_CS2CTXML_315_DSGOODS";
			map.TP_ID = "DSGOODS";
			map.MSG_TYPE_ID = "CT";
			map.DIR_ID = "O";
			String outmap = gson.toJson(map);
			System.out.println("map json: \r\n"+outmap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
