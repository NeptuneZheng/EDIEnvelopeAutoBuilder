package automation;

import bean.ApiParamDefinition;
import bean.ApiParamMappingScript;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cs.b2b.beluga.common.edi.BelugaDefinitionJsonSettings;
import cs.b2b.beluga.common.edi.EdifactEnvelop;
import cs.b2b.beluga.common.edi.GE;
import cs.b2b.beluga.common.edi.GS;
import cs.b2b.beluga.common.edi.IEA;
import cs.b2b.beluga.common.edi.ISA;
import cs.b2b.beluga.common.edi.SE;
import cs.b2b.beluga.common.edi.ST;
import cs.b2b.beluga.common.edi.UNB;
import cs.b2b.beluga.common.edi.UNBS001;
import cs.b2b.beluga.common.edi.UNBS002;
import cs.b2b.beluga.common.edi.UNBS003;
import cs.b2b.beluga.common.edi.UNBS004;
import cs.b2b.beluga.common.edi.UNH;
import cs.b2b.beluga.common.edi.UNHS009;
import cs.b2b.beluga.common.edi.UNT;
import cs.b2b.beluga.common.edi.UNZ;
import cs.b2b.beluga.common.edi.X12Envelop;
/**
 * add edifact samples for json building
 * @author LIANGDA
 *
 */
public class BuildApiJson {

	public static void main(String[] args) {
		try {
			
			//buildJson4CommonGroovy();
			
			//buildJson4GroovyPMT();
			
			//build X.12 Transform Settings
			buildJson4BelugaOcean();
			
			//build Edifact Transform Settings
			//buildJson4BelugaOceanWithEdifact();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void buildJson4CommonGroovy() {
		ApiParamMappingScript common = new ApiParamMappingScript();
		common.fullClassName = "cs.b2b.core.mapping.bean.edi.edifact.d99b.IFTMBF.EDI_IFTMBF";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(common);
		System.out.println(str);
	}
	
	public static void buildJson4GroovyPMT() {
		ApiParamMappingScript pmt = new ApiParamMappingScript();
		pmt.fullClassName = "cs.b2b.mapping.scripts.CUS_CS2CTXML_315_DSGOODS";
		pmt.TP_ID = "DSGOODS";
		pmt.MSG_TYPE_ID = "CT";
		pmt.DIR_ID = "O";
		pmt.MSG_FMT_ID = "X.12";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(pmt);
		System.out.println(str);
	}
	
	public static void buildJson4BelugaOcean() {
		ApiParamDefinition def = new ApiParamDefinition();
		def.definitionFileName = "CUS_4010_315_CS.xml";
//		def.definitionFileName = "CUS_4010_301_CS.xml";
		def.TP_ID = "X013308849";
		def.MSG_TYPE_ID = "CT";
//		def.MSG_TYPE_ID = "BC";
		def.DIR_ID = "O";
		def.MSG_FMT_ID = "X.12";
		
		//x2e or e2x
		def.operation = "x2e";
		
		//Outgoing EDI settings
		//1, edi control number setting
		def.ediControlNumberSender = "CARGOSMART";
		def.ediControlNumberReceiver = "X013308849";
		def.ediControlNumberMessageType = "CT";
//		def.ediControlNumberMessageType = "BC";
		def.ediControlNumberFormat = "X.12";
		
		//2, edi envelop settings, X.12 or Edifact
		def.transformSetting = x12Envelop("ISA*00*          *00*          *ZZ*CARGOSMART     *12*2013308849     *170921*1003*U*00401*000064301*0*P*>", "GS*QO*CARGOSMART*ASO*20170921*1003*64301*X*004010");
//		def.transformSetting = x12Envelop();
		//def.transformSetting = edifactEnvelop();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(def);
		System.out.println(str);
	}
	
	public static void buildJson4BelugaOceanWithEdifact() {
		ApiParamDefinition def = new ApiParamDefinition();
//		def.definitionFileName = "CUS_D99B_IFTMBC_CS.xml";
		def.definitionFileName = "CUS_D99B_IFTSTA_CS.xml";
		def.TP_ID = "BMW";
//		def.MSG_TYPE_ID = "BC";
		def.MSG_TYPE_ID = "CT";
		def.DIR_ID = "O";
		def.MSG_FMT_ID = "EDIFACT";
		
		//x2e or e2x
		def.operation = "x2e";
		
		//Outgoing EDI settings
		//1, edi control number setting
		def.ediControlNumberSender = "CARGOSMART";
		def.ediControlNumberReceiver = "BMW";
//		def.ediControlNumberMessageType = "BC";
		def.ediControlNumberMessageType = "CT";
		def.ediControlNumberFormat = "EDIFACT";
		
		//2, edi envelop settings, X.12 or Edifact
		//def.transformSetting = x12Envelop();
		def.transformSetting = edifactEnvelop();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(def);
		System.out.println(str);
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
	
	private static String x12Envelop() {
		BelugaDefinitionJsonSettings cfg = new BelugaDefinitionJsonSettings();
		cfg.recordDelimiter = "0x7e";//0x7e 0x0d 0x0a or ~
		cfg.elementDelimiter = "*";
		cfg.subElementDelimiter = "";
		//20170505 add
		cfg.x12ReplacementChar = "|";
		cfg.escapeChar = "";
		cfg.elementType = "delimited";
		cfg.isSuppressEmptyNodes = "true";
		cfg.isX12 = "true";
		cfg.isFieldValueTrimRightSpace = "false";
		cfg.isFieldValueTrimLeadingSpace = "false";

		cfg.x12Envelop = new X12Envelop();
		cfg.x12Envelop.isa = new ISA();
		cfg.x12Envelop.gs = new GS();
		cfg.x12Envelop.st = new ST();
		cfg.x12Envelop.se = new SE();
		cfg.x12Envelop.ge = new GE();
		cfg.x12Envelop.iea = new IEA();

		cfg.x12Envelop.isa.I01_01 = "00";
		cfg.x12Envelop.isa.I02_02 = "          ";
		cfg.x12Envelop.isa.I03_03 = "00";
		cfg.x12Envelop.isa.I04_04 = "          ";
		cfg.x12Envelop.isa.I05_05 = "01";
		cfg.x12Envelop.isa.I06_06 = "CARGOSMART     ";
		cfg.x12Envelop.isa.I05_07 = "08";
		cfg.x12Envelop.isa.I07_08 = "6123410000     ";
		cfg.x12Envelop.isa.I08_09 = "%yyMMdd%";
		cfg.x12Envelop.isa.I09_10 = "%HHmm%";
		cfg.x12Envelop.isa.I10_11 = "U";
		cfg.x12Envelop.isa.I11_12 = "00401";
		cfg.x12Envelop.isa.I12_13 = "%EDI_CTRL_NUM%";
		cfg.x12Envelop.isa.I13_14 = "0";
		cfg.x12Envelop.isa.I14_15 = "P";
		cfg.x12Envelop.isa.I15_16 = ":";

		cfg.x12Envelop.gs.E479_01 = "IO";
		cfg.x12Envelop.gs.E142_02 = "CARGOSMART";
		cfg.x12Envelop.gs.E124_03 = "ODEP001";
		cfg.x12Envelop.gs.E373_04 = "%yyyyMMdd%";
		cfg.x12Envelop.gs.E337_05 = "%HHmm%";
		cfg.x12Envelop.gs.E28_06 = "%GROUP_CTRL_NUM%";
		cfg.x12Envelop.gs.E455_07 = "X";
		cfg.x12Envelop.gs.E480_08 = "004010";

		cfg.x12Envelop.st.E143_01 = "310";
//		cfg.x12Envelop.st.E143_01 = "301";
		cfg.x12Envelop.st.E329_02 = "%TXN_CTRL_NUM_START%";

		cfg.x12Envelop.se.E96_01 = "-";
		cfg.x12Envelop.se.E329_02 = "%TXN_CTRL_NUM_END%";

		cfg.x12Envelop.ge.E97_01 = "%TXN_COUNT%";
		cfg.x12Envelop.ge.E28_02 = "%GROUP_CTRL_NUM%";

		cfg.x12Envelop.iea.I16_01 = "1";
		cfg.x12Envelop.iea.I12_02 = "%EDI_CTRL_NUM%";

		Gson gson = new GsonBuilder().create();
		String str = gson.toJson(cfg);
		return str;
	}

	private static String x12Envelop(String isa, String gs) {

		BelugaDefinitionJsonSettings cfg = new BelugaDefinitionJsonSettings();
		cfg.recordDelimiter = "0x7e";//0x7e 0x0d 0x0a or ~
		cfg.elementDelimiter = "*";
		cfg.subElementDelimiter = "";

		String[] isaArrays = isa.split("\\*");
		String[] gsArrays = gs.split("\\*");

		//20170505 add
		cfg.x12ReplacementChar = "|";
		cfg.escapeChar = "";
		cfg.elementType = "delimited";
		cfg.isSuppressEmptyNodes = "true";
		cfg.isX12 = "true";
		cfg.isFieldValueTrimRightSpace = "false";
		cfg.isFieldValueTrimLeadingSpace = "false";

		cfg.x12Envelop = new X12Envelop();
		cfg.x12Envelop.isa = new ISA();
		cfg.x12Envelop.gs = new GS();
		cfg.x12Envelop.st = new ST();
		cfg.x12Envelop.se = new SE();
		cfg.x12Envelop.ge = new GE();
		cfg.x12Envelop.iea = new IEA();

		cfg.x12Envelop.isa.I01_01 = isaArrays[1];
		cfg.x12Envelop.isa.I02_02 = isaArrays[2];
		cfg.x12Envelop.isa.I03_03 = isaArrays[3];
		cfg.x12Envelop.isa.I04_04 = isaArrays[4];
		cfg.x12Envelop.isa.I05_05 = isaArrays[5];
		cfg.x12Envelop.isa.I06_06 = isaArrays[6];
		cfg.x12Envelop.isa.I05_07 = isaArrays[7];
		cfg.x12Envelop.isa.I07_08 = isaArrays[8];
		cfg.x12Envelop.isa.I08_09 = "%yyMMdd%";
		cfg.x12Envelop.isa.I09_10 = "%HHmm%";
		cfg.x12Envelop.isa.I10_11 = isaArrays[11];
		cfg.x12Envelop.isa.I11_12 = isaArrays[12];
		cfg.x12Envelop.isa.I12_13 = "%EDI_CTRL_NUM%";
		cfg.x12Envelop.isa.I13_14 = isaArrays[14];
		cfg.x12Envelop.isa.I14_15 = isaArrays[15];
		cfg.x12Envelop.isa.I15_16 = isaArrays[16];

		cfg.x12Envelop.gs.E479_01 = gsArrays[1];
		cfg.x12Envelop.gs.E142_02 = gsArrays[2];
		cfg.x12Envelop.gs.E124_03 = gsArrays[3];
		cfg.x12Envelop.gs.E373_04 = "%yyyyMMdd%";
		cfg.x12Envelop.gs.E337_05 = "%HHmm%";
		cfg.x12Envelop.gs.E28_06 = "%GROUP_CTRL_NUM%";
		cfg.x12Envelop.gs.E455_07 = gsArrays[7];
		cfg.x12Envelop.gs.E480_08 = gsArrays[8];

		cfg.x12Envelop.st.E143_01 = "315";
//		cfg.x12Envelop.st.E143_01 = "301";
		cfg.x12Envelop.st.E329_02 = "%TXN_CTRL_NUM_START%";

		cfg.x12Envelop.se.E96_01 = "-";
		cfg.x12Envelop.se.E329_02 = "%TXN_CTRL_NUM_END%";

		cfg.x12Envelop.ge.E97_01 = "%TXN_COUNT%";
		cfg.x12Envelop.ge.E28_02 = "%GROUP_CTRL_NUM%";

		cfg.x12Envelop.iea.I16_01 = "1";
		cfg.x12Envelop.iea.I12_02 = "%EDI_CTRL_NUM%";

		Gson gson = new GsonBuilder().create();
		String str = gson.toJson(cfg);
		return str;
	}
	
	private static String edifactEnvelop() {
		BelugaDefinitionJsonSettings cfg = new BelugaDefinitionJsonSettings();
		cfg.recordDelimiter = "0x27";
		cfg.elementDelimiter = "+";
		cfg.subElementDelimiter = ":";
		cfg.escapeChar = "?";
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
		
		cfg.edifactEnvelop = new EdifactEnvelop();
		
		cfg.edifactEnvelop.UNA = "UNA:+.? ";
		cfg.edifactEnvelop.unb = new UNB();
		
		cfg.edifactEnvelop.unb.s001_01 = new UNBS001();
		cfg.edifactEnvelop.unb.s001_01.E0001_01 = "UNOC";
		cfg.edifactEnvelop.unb.s001_01.E0002_02 = "3";
		
		cfg.edifactEnvelop.unb.s002_02 = new UNBS002();
		cfg.edifactEnvelop.unb.s002_02.E0004_01 = "CARGOSMART";
		cfg.edifactEnvelop.unb.s002_02.E0007_02 = "";
		cfg.edifactEnvelop.unb.s002_02.E0008_03 = "";
		
		cfg.edifactEnvelop.unb.s003_03 = new UNBS003();
		cfg.edifactEnvelop.unb.s003_03.E0010_01 = "O0013000045BMW-MWD";
		cfg.edifactEnvelop.unb.s003_03.E0007_02 = "";
		cfg.edifactEnvelop.unb.s003_03.E0014_03 = "";
		
		cfg.edifactEnvelop.unb.s004_04 = new UNBS004();
		cfg.edifactEnvelop.unb.s004_04.E0017_01 = "%yyMMdd%";
		cfg.edifactEnvelop.unb.s004_04.E0019_02 = "%HHmm%";
		
		//%EDI_CTRL_NUM_FORMAT% - edi outgoing control number, can format with:
		//    cfg.formatStringEdiControlNumberMaxLength = "14" -- set the max output length
		//    cfg.formatStringEdiControlNumber = "%09d"  -- means add left 0 if length less than 9 
		cfg.edifactEnvelop.unb.e0020_05 = "%EDI_CTRL_NUM_FORMAT%";
		
//		//unb_06 - unb11 settings if you have
//		cfg.edifactEnvelop.unb.s005_06 = new UNBS005();
//		cfg.edifactEnvelop.unb.s005_06.E0022_01 = "A1";
//		cfg.edifactEnvelop.unb.s005_06.E0025_02 = "B2";
//		
//		cfg.edifactEnvelop.unb.e0026_07 = "e07";
//		cfg.edifactEnvelop.unb.e0029_08 = "e008";
//		cfg.edifactEnvelop.unb.e0031_09 = "e0009";
//		cfg.edifactEnvelop.unb.e0032_10 = "e0010";
//		cfg.edifactEnvelop.unb.e0035_11 = "e0011";
//		//end of unb_06 - unb11 settings
		
		//unh
		cfg.edifactEnvelop.unh = new UNH();
		//%EDI_CTRL_NUM_IN_TXN_FORMAT% - can formatting with cfg.formatStringEdiControlNumberInTransactionMaxLength = "4" and cfg.formatStringEdiControlNumberInTransaction = "%04d" if need
		//%TXN_COUNT_FORMAT% - can formatting with cfg.formatStringTxnCount = "%05d" and cfg.formatStringTxnCountMaxLength = "5"
		cfg.edifactEnvelop.unh.E0062_01 = "%EDI_CTRL_NUM_IN_TXN_FORMAT%%TXN_COUNT_FORMAT%";
		cfg.edifactEnvelop.unh.S009_02 = new UNHS009();
//		cfg.edifactEnvelop.unh.S009_02.E0065_01 = "IFTMBC";
		cfg.edifactEnvelop.unh.S009_02.E0065_01 = "IFTSTA";
		cfg.edifactEnvelop.unh.S009_02.E0052_02 = "D";
		cfg.edifactEnvelop.unh.S009_02.E0054_03 = "99B";
		cfg.edifactEnvelop.unh.S009_02.E0051_04 = "UN";
		
		cfg.edifactEnvelop.unt = new UNT();
		cfg.edifactEnvelop.unt.E0074_01 = "";
		//reference with cfg.edifactEnvelop.unh.E0062_01, should be same
		cfg.edifactEnvelop.unt.E0062_02 = "%EDI_CTRL_NUM_IN_TXN_FORMAT%%TXN_COUNT_FORMAT%";
		
		cfg.edifactEnvelop.unz = new UNZ();
		// the transaction sequence, no prefix 0
		cfg.edifactEnvelop.unz.E0036_01 = "%TXN_COUNT%";
		// reference with cfg.edifactEnvelop.unb.e0020_05, should be same
		cfg.edifactEnvelop.unz.E0020_02 = "%EDI_CTRL_NUM_FORMAT%";
		
		Gson gson = new GsonBuilder().create();
		String str = gson.toJson(cfg);
		return str;
	}
}
