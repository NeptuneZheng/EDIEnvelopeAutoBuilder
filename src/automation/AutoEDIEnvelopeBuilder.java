package automation;

import bean.ApiParamDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cs.b2b.beluga.common.edi.BelugaDefinitionJsonSettings;

import cs.b2b.beluga.common.edi.GE;
import cs.b2b.beluga.common.edi.GS;
import cs.b2b.beluga.common.edi.IEA;
import cs.b2b.beluga.common.edi.ISA;
import cs.b2b.beluga.common.edi.SE;
import cs.b2b.beluga.common.edi.ST;

import cs.b2b.beluga.common.edi.X12Envelop;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZHENGNE on 9/28/2017.
 */
public class AutoEDIEnvelopeBuilder {

        public static void main(String[] args) {
            try {
                String tp_id = "AAA";
                String spliter = "\\*";
                String elementDelimiter = "*";
                String recordDelimiter = "~";
                String x12ReplacementChar = "";

                buildISAGSJson(tp_id,spliter,elementDelimiter,recordDelimiter,x12ReplacementChar);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void buildJson4BelugaOceanA(String tp_id, String isa, String gs, String spliter,String elementDelimiter, String recordDelimiter, String x12ReplacementChar) {
        ApiParamDefinition def = new ApiParamDefinition();
        def.definitionFileName = "CUS_4010_315_CS.xml";
//		def.definitionFileName = "CUS_4010_301_CS.xml";
        def.TP_ID = tp_id;
        def.MSG_TYPE_ID = "CT";
//		def.MSG_TYPE_ID = "BC";
        def.DIR_ID = "O";
        def.MSG_FMT_ID = "X.12";

        //x2e or e2x
        def.operation = "x2e";

        //Outgoing EDI settings
        //1, edi control number setting
        def.ediControlNumberSender = "CARGOSMART";
        def.ediControlNumberReceiver = tp_id;
        def.ediControlNumberMessageType = "CT";
//		def.ediControlNumberMessageType = "BC";
        def.ediControlNumberFormat = "X.12";

        //2, edi envelop settings, X.12 or Edifact
        //def.transformSetting = x12Envelop(isa,gs,spliter,elementDelimiter,recordDelimiter,x12ReplacementChar);
//		def.transformSetting = x12Envelop();
        //def.transformSetting = edifactEnvelop();

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
        private static String x12Envelop(String isa, String gs, String spliter,String elementDelimiter, String recordDelimiter, String x12ReplacementChar) {

            BelugaDefinitionJsonSettings cfg = new BelugaDefinitionJsonSettings();
            cfg.recordDelimiter = recordDelimiter;//0x7e 0x0d 0x0a or ~
            cfg.elementDelimiter = elementDelimiter;
            cfg.subElementDelimiter = "";

            String[] isaArrays = isa.split(spliter);
            String[] gsArrays = gs.split(spliter);

            //20170505 add
            if(!x12ReplacementChar.equals("")){
                cfg.x12ReplacementChar = x12ReplacementChar;
            }
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

        public static void buildISAGSJson(String tp_id, String spliter,String elementDelimiter, String recordDelimiter, String x12ReplacementChar){
            Map<String,String> map = new HashMap<String,String>();
            File filePath = new File("D:\\kukri_sql\\");
            BufferedReader io = null;
            String ediString = "";
            String isa = "";
            String gs = "";
            try {
                if(filePath.exists() ){
                    for(File file : filePath.listFiles()){
                        if(file.isFile()){
                            io = new BufferedReader(new FileReader(file));
                            try {
                                int readCount = 1;
                                while ((ediString = io.readLine())!=null && readCount<3){
                                    if(readCount==1){
                                        isa = ediString;
                                    }else{
                                        gs = ediString;
                                    }
                                    readCount ++;
                                }
                                //build X.12 Transform Settings
                                buildJson4BelugaOceanA(tp_id,isa,gs,spliter,elementDelimiter,recordDelimiter,x12ReplacementChar);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                if (io != null) {
                    try {
                        io.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
}
