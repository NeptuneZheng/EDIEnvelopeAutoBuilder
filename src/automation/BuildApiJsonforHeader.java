package automation;

import base.BaseFunctionHelper;
import bean.ApiParamDefinition;
import bean.BelugaDefinitionJsonSettings;
import bean.PmtConfig;

import bean.edi.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utility.process.FunctionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * add edifact samples for json building
 * @author Tracy
 *
 */
public class BuildApiJsonforHeader {


	
	public static String buildJson4BelugaOcean(String contrlSender,String definition,String x12ReplacementChar, PmtConfig pmtConfig) {
		ApiParamDefinition def = new ApiParamDefinition();
		def.definitionFileName = definition;
//		def.definitionFileName = "CUS_4010_301_CS.xml";
		def.TP_ID =  pmtConfig.getTp_id();
		def.MSG_TYPE_ID = pmtConfig.getMsg_type_id();
//		def.MSG_TYPE_ID = "BC";
		def.DIR_ID = pmtConfig.getDir_id();
		def.MSG_FMT_ID = pmtConfig.getMsg_format();
		
		//x2e or e2x
		def.operation = "x2e";
		
		//Outgoing EDI settings
		//1, edi control number setting
		def.ediControlNumberSender = contrlSender;
		def.ediControlNumberReceiver = pmtConfig.getTp_id();
		def.ediControlNumberMessageType = pmtConfig.getMsg_type_id();
//		def.ediControlNumberMessageType = "BC";
		def.ediControlNumberFormat = pmtConfig.getMsg_format();
		
		//2, edi envelop settings, X.12 or Edifact
		//def.transformSetting = x12Envelop(pmtConfig,x12ReplacementChar);
//		def.transformSetting = x12Envelop();
		//def.transformSetting = edifactEnvelopV2();
		String edifactCfg = x12Envelop(pmtConfig,x12ReplacementChar);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(def)+"\r\n"+"<<transformSetting>>"+"\r\n"+edifactCfg;
		System.out.println(str);
		return str;
	}
	
	public static String buildJson4BelugaOceanWithEdifact(String contrlSender,String definition,String escapeChar, PmtConfig pmtConfig) {
		ApiParamDefinition def = new ApiParamDefinition();
//		def.definitionFileName = "CUS_D99B_IFTSTA_CS.xml";
		def.definitionFileName = definition;
		def.TP_ID = pmtConfig.getTp_id();
//		def.MSG_TYPE_ID = "BC";
		def.MSG_TYPE_ID = pmtConfig.getMsg_type_id();
		def.DIR_ID = pmtConfig.getDir_id();
		def.MSG_FMT_ID = pmtConfig.getMsg_format();
		
		//x2e or e2x
		def.operation = "x2e";
		
		//Outgoing EDI settings
		//1, edi control number setting
		def.ediControlNumberSender = contrlSender;
		def.ediControlNumberReceiver = pmtConfig.getTp_id();
//		def.ediControlNumberMessageType = "BC";
		def.ediControlNumberMessageType = pmtConfig.getMsg_type_id();
		def.ediControlNumberFormat = pmtConfig.getMsg_format();
		
		//2, edi envelop settings, X.12 or Edifact
		//def.transformSetting = x12Envelop();
		//def.transformSetting = edifactEnvelopV2(pmtConfig,escapeChar);

		String edifactCfg = edifactEnvelopV2(pmtConfig,escapeChar);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(def)+"\r\n\r\n"+"<<transformSetting>>"+"\r\n\r\n"+edifactCfg;
		System.out.println(str);
		return str;
	}
	
	/**
	 * Delimiter encoding in BelugaOcean Definition: 
	 * "0x27 0x0d 0x0a" = '\r\n
	 * "0x7e 0x0d 0x0a" = ~\r\n
	 * "&quot;" = "
	 * "0x09" = \t
	 * "0x27" = '
	 * "0x27 0x0a" = '\n
	 * "0x0d 0x0a" = \r\n
	 * "0x0a" = \n
	 * "0x7e" = ~
	 * 
	 */

	private static String x12Envelop(PmtConfig pmtConfig,String x12ReplacementChar) {

		BelugaDefinitionJsonSettings cfg = new BelugaDefinitionJsonSettings();
		cfg.recordDelimiter = BaseFunctionHelper.encode(pmtConfig.getDelimiter());//0x7e 0x0d 0x0a or ~
		cfg.elementDelimiter = pmtConfig.getSeperator();
		cfg.subElementDelimiter = pmtConfig.getSubElementSeperator();

		String[] isaArrays = pmtConfig.getIsa().split(String.format("\\%s", pmtConfig.getSeperator()));
		String[] gsArrays = pmtConfig.getGs().split(String.format("\\%s", pmtConfig.getSeperator()));

		//20170505 add
		cfg.x12ReplacementChar = x12ReplacementChar;
		cfg.escapeChar = "";
		cfg.elementType = "delimited";
		cfg.isSuppressEmptyNodes = "true";
		cfg.isX12 = "true";
		cfg.isFieldValueTrimRightSpace = "false";
		cfg.isFieldValueTrimLeadingSpace = "false";

		cfg.x12EnvelopV2 = new X12EnvelopV2();
		cfg.x12EnvelopV2.isa = new ISA();
		cfg.x12EnvelopV2.gs = new GS();
		cfg.x12EnvelopV2.st = new ST();
		cfg.x12EnvelopV2.se = new SE();
		cfg.x12EnvelopV2.ge = new GE();
		cfg.x12EnvelopV2.iea = new IEA();

		cfg.x12EnvelopV2.isa.e01 = isaArrays[1];
		cfg.x12EnvelopV2.isa.e02 = isaArrays[2];
		cfg.x12EnvelopV2.isa.e03 = isaArrays[3];
		cfg.x12EnvelopV2.isa.e04 = isaArrays[4];
		cfg.x12EnvelopV2.isa.e05 = isaArrays[5];
		cfg.x12EnvelopV2.isa.e06 = isaArrays[6];
		cfg.x12EnvelopV2.isa.e07 = isaArrays[7];
		cfg.x12EnvelopV2.isa.e08 = isaArrays[8];
//		cfg.x12Envelop.isa.I08_09 = "%yyMMdd%";
		cfg.x12EnvelopV2.isa.e09 = FunctionHelper.returnDateFormat(isaArrays[9]);
//		cfg.x12Envelop.isa.I09_10 = "%HHmm%";
		cfg.x12EnvelopV2.isa.e10 = FunctionHelper.returnDateFormat(isaArrays[10]);
		cfg.x12EnvelopV2.isa.e11 = isaArrays[11];
		cfg.x12EnvelopV2.isa.e12 = isaArrays[12];
		cfg.x12EnvelopV2.isa.e13 = "%EDI_CTRL_NUM%";
		cfg.x12EnvelopV2.isa.e14 = isaArrays[14];
		cfg.x12EnvelopV2.isa.e15 = isaArrays[15];
		cfg.x12EnvelopV2.isa.e16 = isaArrays[16];

		cfg.x12EnvelopV2.gs.e01 = gsArrays[1];
		cfg.x12EnvelopV2.gs.e02 = gsArrays[2];
		cfg.x12EnvelopV2.gs.e03 = gsArrays[3];
//		cfg.x12Envelop.gs.E373_04 = "%yyyyMMdd%";
		cfg.x12EnvelopV2.gs.e04 = FunctionHelper.returnDateFormat(gsArrays[4]);
//		cfg.x12Envelop.gs.E337_05 = "%HHmm%";
		cfg.x12EnvelopV2.gs.e05 = FunctionHelper.returnDateFormat(gsArrays[5]);
		cfg.x12EnvelopV2.gs.e06 = "%GROUP_CTRL_NUM%";
		cfg.x12EnvelopV2.gs.e07 = gsArrays[7];
		cfg.x12EnvelopV2.gs.e08 = gsArrays[8];

		cfg.x12EnvelopV2.st.e01 = pmtConfig.getTxnIndentifier();
//		cfg.x12Envelop.st.E143_01 = "301";
		cfg.x12EnvelopV2.st.e02 = "%TXN_CTRL_NUM_START%";

		cfg.x12EnvelopV2.se.e01 = "-";
		cfg.x12EnvelopV2.se.e02 = "%TXN_CTRL_NUM_END%";

		cfg.x12EnvelopV2.ge.e01 = "%TXN_COUNT%";
		cfg.x12EnvelopV2.ge.e02 = "%GROUP_CTRL_NUM%";

		cfg.x12EnvelopV2.iea.e01 = "1";
		cfg.x12EnvelopV2.iea.e02 = "%EDI_CTRL_NUM%";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(cfg);
		return str;
	}
	
	private static String edifactEnvelopV2(PmtConfig pmtConfig,String escapeChar) {
		BelugaDefinitionJsonSettings cfg = new BelugaDefinitionJsonSettings();
		cfg.recordDelimiter = BaseFunctionHelper.encode(pmtConfig.getDelimiter());
		cfg.elementDelimiter = pmtConfig.getSeperator();
		cfg.subElementDelimiter = pmtConfig.getSubSeperator();
		if(!escapeChar.equals("")){
			cfg.escapeChar = escapeChar;
		}else{
			cfg.escapeChar = "?";
		}

		cfg.elementType = "delimited";
		cfg.isSuppressEmptyNodes = "true";
		cfg.isX12 = "false";
		cfg.isEdifact = "true";
		cfg.isFieldValueTrimRightSpace = "false";
		cfg.isFieldValueTrimLeadingSpace = "false";
		//20170505
		// set UNB_05 edi control number max length, will only keep the right part if over than maxLength
		cfg.formatStringEdiControlNumberMaxLength = "14";
		// set UNH_01 edi control number max length in transaction ctrl number if you use %EDI_CTRL_NUM_IN_TXN_FORMAT% in UNH_01
		cfg.formatStringEdiControlNumberInTransactionMaxLength = "9";
		// edi transaction control number - sequence formatting, %05d means length = 5 and supplement 0 in left side
		cfg.formatStringTxnCount = "%05d";
		// edi transaction control number - sequence, set the max output length, only keep right part if over than maxLength
		cfg.formatStringTxnCountMaxLength = "5";
		
		cfg.edifactEnvelopV2 = new EdifactEnvelopV2();

		cfg.edifactEnvelopV2.una = pmtConfig.getUna();

		String[] unbArrays = pmtConfig.getUnb().split(String.format("\\%s", pmtConfig.getSeperator()));
		String[] unhArrays = pmtConfig.getUnh().split(String.format("\\%s", pmtConfig.getSeperator()));
        String[] ungArrays = new String[7];
		if(!pmtConfig.getUng().equals("")){
           ungArrays = pmtConfig.getUng().split(String.format("\\%s", pmtConfig.getSeperator()));
        }

        List<String> unb01 =  new ArrayList<String>();
		//String[]  unb02=  new String[3];
		//String[]  unb03=  new String[3];
		List<String> unb02 =  new ArrayList<String>();
		List<String> unb03 =  new ArrayList<String>();
		List<String> unb06 =  new ArrayList<String>();
        //List<Element> list = Arrays.asList(array);
        String[] unb01tmp =  unbArrays[1].split(String.format("\\%s", pmtConfig.getSubSeperator()));
        String[] unb02tmp =  unbArrays[2].split(String.format("\\%s", pmtConfig.getSubSeperator()));
        String[] unb03tmp =  unbArrays[3].split(String.format("\\%s", pmtConfig.getSubSeperator()));
        String[] unb04tmp =  unbArrays[4].split(String.format("\\%s", pmtConfig.getSubSeperator()));
		String[] unb06tmp = new String[2];
		if(unbArrays.length>6){
			unb06tmp =  unbArrays[6].split(String.format("\\%s", pmtConfig.getSubSeperator()));
		}

        for(int i=0;i<unb01tmp.length;i++){
            unb01.add(unb01tmp[i]);
        }

        for(int i=0;i<unb02tmp.length;i++){
            unb02.add(unb02tmp[i]);
        }

        for(int i=0;i<unb03tmp.length;i++){
            unb03.add(unb03tmp[i]);
        }

        if(unb02.size()<3){
            for(int i=0;i<=3-unb02.size();i++){
                unb02.add("");
            }
        }
        if(unb03.size()<3){
            for(int i=0;i<=3-unb03.size();i++){
                unb03.add("");
            }
        }
		for(int i=0;i<unb06tmp.length;i++){
			unb06.add(unb06tmp[i]);
		}

        cfg.edifactEnvelopV2.unb = new UNB();
		cfg.edifactEnvelopV2.unb.s01 = new UNBS001();
        cfg.edifactEnvelopV2.unb.s01.e01 =  unb01.get(0);
        cfg.edifactEnvelopV2.unb.s01.e02 =  unb01.get(1);


		cfg.edifactEnvelopV2.unb.s02 = new UNBS002();
        cfg.edifactEnvelopV2.unb.s02.e01 = unb02.get(0);
        cfg.edifactEnvelopV2.unb.s02.e02 = unb02.get(1);
        cfg.edifactEnvelopV2.unb.s02.e03 = unb02.get(2);

		
		cfg.edifactEnvelopV2.unb.s03 = new UNBS003();
        cfg.edifactEnvelopV2.unb.s03.e01 = unb03.get(0);
        cfg.edifactEnvelopV2.unb.s03.e02 = unb03.get(1);
        cfg.edifactEnvelopV2.unb.s03.e03 = unb03.get(2);

		
		cfg.edifactEnvelopV2.unb.s04 = new UNBS004();
//		cfg.edifactEnvelopV2.unb.s004_04.E0017_01 = "%yyMMdd%";
		if(unb04tmp.length>0){
			cfg.edifactEnvelopV2.unb.s04.e01 = FunctionHelper.returnDateFormat(unb04tmp[0]);
		}
		if(unb04tmp.length>1){
			cfg.edifactEnvelopV2.unb.s04.e02 = FunctionHelper.returnDateFormat(unb04tmp[1]);
		}

		//%EDI_CTRL_NUM_FORMAT% - edi outgoing control number, can format with:
		//    cfg.formatStringEdiControlNumberMaxLength = "14" -- set the max output length
		//    cfg.formatStringEdiControlNumber = "%09d"  -- means add left 0 if length less than 9 
		cfg.edifactEnvelopV2.unb.e05 = "%EDI_CTRL_NUM_FORMAT%";
		if(unbArrays.length>6 && !unb06.get(0).equals("")) {
			cfg.edifactEnvelopV2.unb.s06 = new UNBS006();
			cfg.edifactEnvelopV2.unb.s06.e01 = unb06.get(0);
			cfg.edifactEnvelopV2.unb.s06.e02 = unb06.get(1);
		}

		String unb07 = "";
		String unb08 = "";
		String unb09 = "";
		String unb10 = "";
		String unb11 = "";
		if(unbArrays.length>7){
			unb07 = unbArrays[7].split(String.format("\\%s", pmtConfig.getSubSeperator()))[0];
		}
		if(unbArrays.length>8){
			unb08 = unbArrays[8].split(String.format("\\%s", pmtConfig.getSubSeperator()))[0];
		}
		if(unbArrays.length>9){
			unb09 = unbArrays[9].split(String.format("\\%s", pmtConfig.getSubSeperator()))[0];
		}
		if(unbArrays.length>10){
			unb10 = unbArrays[10].split(String.format("\\%s", pmtConfig.getSubSeperator()))[0];
		}
		if(unbArrays.length>11){
			unb11= unbArrays[11].split(String.format("\\%s", pmtConfig.getSubSeperator()))[0];
		}

		if(!unb07.equals("")){
			cfg.edifactEnvelopV2.unb.e07 = unb07;
		}
		if(!unb08.equals("")){
			cfg.edifactEnvelopV2.unb.e08 = unb08;
		}
		if(!unb09.equals("")){
			cfg.edifactEnvelopV2.unb.e09 = unb09;
		}
		if(!unb10.equals("")){
			cfg.edifactEnvelopV2.unb.e10 = unb10;
		}
		if(!unb11.equals("")){
			cfg.edifactEnvelopV2.unb.e11 = unb11;
		}
        //ung

        if(!pmtConfig.getUng().equals("")){
            cfg.edifactEnvelopV2.ung = new UNG();
            cfg.edifactEnvelopV2.ung.e01 = ungArrays[1];
            if(ungArrays.length>1){
                String[] ung02Elemtent = ungArrays[2].split(String.format("\\%s", pmtConfig.getSubSeperator()));
                cfg.edifactEnvelopV2.ung.s02 = new UNGS002();
                cfg.edifactEnvelopV2.ung.s02.e01 = ung02Elemtent[0];
                if(ung02Elemtent.length>1){
                    cfg.edifactEnvelopV2.ung.s02.e02 = ung02Elemtent[1];
                }

            }
            if(ungArrays.length>2){
                String[] ung03Elemtent = ungArrays[3].split(String.format("\\%s", pmtConfig.getSubSeperator()));
                cfg.edifactEnvelopV2.ung.s03 = new UNGS003();
                cfg.edifactEnvelopV2.ung.s03.e01 = ung03Elemtent[0];
                if(ung03Elemtent.length>1){
                    cfg.edifactEnvelopV2.ung.s03.e02 = ung03Elemtent[1];
                }

            }
            if(ungArrays.length>3){
                String[] ung04Elemtent = ungArrays[4].split(String.format("\\%s", pmtConfig.getSubSeperator()));
                cfg.edifactEnvelopV2.ung.s04 = new UNGS004();
                cfg.edifactEnvelopV2.ung.s04.e01 = FunctionHelper.returnDateFormat(ung04Elemtent[0]);
                cfg.edifactEnvelopV2.ung.s04.e02 = FunctionHelper.returnDateFormat(ung04Elemtent[1]);
            }
            cfg.edifactEnvelopV2.ung.e05 = "%EDI_CTRL_NUM_FORMAT%";
            if(ungArrays.length>5){
                cfg.edifactEnvelopV2.ung.e06 = ungArrays[6];
            }
            if(ungArrays.length>6){
                String[] ung07Elemtent = ungArrays[7].split(String.format("\\%s", pmtConfig.getSubSeperator()));
                cfg.edifactEnvelopV2.ung.s07 = new UNGS007();
                cfg.edifactEnvelopV2.ung.s07.e01= ung07Elemtent[0];
                if(ung07Elemtent.length>1){
                    cfg.edifactEnvelopV2.ung.s07.e02= ung07Elemtent[1];
                }
                if(ung07Elemtent.length>2){
                    cfg.edifactEnvelopV2.ung.s07.e03= ung07Elemtent[2];
                }

            }
        }


		//unh
		String[] unh02Elemtent = unhArrays[2].split(String.format("\\%s", pmtConfig.getSubSeperator()));
		cfg.edifactEnvelopV2.unh = new UNH();
		//%EDI_CTRL_NUM_IN_TXN_FORMAT% - can formatting with cfg.formatStringEdiControlNumberInTransactionMaxLength = "4" and cfg.formatStringEdiControlNumberInTransaction = "%04d" if need
		//%TXN_COUNT_FORMAT% - can formatting with cfg.formatStringTxnCount = "%05d" and cfg.formatStringTxnCountMaxLength = "5"
		cfg.edifactEnvelopV2.unh.e01 = "%EDI_CTRL_NUM_IN_TXN_FORMAT%%TXN_COUNT_FORMAT%";
		cfg.edifactEnvelopV2.unh.s02 = new UNHS002();
//		cfg.edifactEnvelopV2.unh.S009_02.E0065_01 = "IFTMBC";
		cfg.edifactEnvelopV2.unh.s02.e01 = unh02Elemtent[0];
		cfg.edifactEnvelopV2.unh.s02.e02 = unh02Elemtent[1];
		cfg.edifactEnvelopV2.unh.s02.e03 = unh02Elemtent[2];
		cfg.edifactEnvelopV2.unh.s02.e04 = unh02Elemtent[3];
		
		cfg.edifactEnvelopV2.unt = new UNT();
		cfg.edifactEnvelopV2.unt.e01 = "";
		//reference with cfg.edifactEnvelopV2.unh.E0062_01, should be same
		cfg.edifactEnvelopV2.unt.e02 = "%EDI_CTRL_NUM_IN_TXN_FORMAT%%TXN_COUNT_FORMAT%";
		if(!pmtConfig.getUne().equals("")){
			cfg.edifactEnvelopV2.une = new UNE();
			cfg.edifactEnvelopV2.une.e01 = "%TXN_COUNT%" ;
			cfg.edifactEnvelopV2.une.e02 = "%EDI_CTRL_NUM_FORMAT%";

		}
		//if une exists then unz will hard code 1
		cfg.edifactEnvelopV2.unz = new UNZ();
		// the transaction sequence, no prefix 0
		if(!pmtConfig.getUne().equals("")){
			cfg.edifactEnvelopV2.unz.e01 = "1";
		}else{
			cfg.edifactEnvelopV2.unz.e01 = "%TXN_COUNT%";
		}

		// reference with cfg.edifactEnvelopV2.unb.e0020_05, should be same
		cfg.edifactEnvelopV2.unz.e02 = "%EDI_CTRL_NUM_FORMAT%";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(cfg);
		return str;
	}
}
