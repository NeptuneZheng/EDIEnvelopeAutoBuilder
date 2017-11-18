package automation;

import base.BaseFunctionHelper;
import bean.PmtConfig;

/**
 * Created by XIAOTR on 10/12/2017.
 */
public class AutoGenHeader {



    public static void main(String[] args) {


        String env = "qa3";
        String tp_id ="VFCGTN";
        String dir_id = "O";
        String msg_type_id ="CT";
        String msg_format = "X.12";

        String folderpath = "D:\\kukri_sql\\EDI\\";
        String inputfilename = BaseFunctionHelper.getFile(folderpath);
        PmtConfig pmtConfig =  new PmtConfig(env,tp_id,dir_id,msg_type_id,msg_format,inputfilename,folderpath);
        FileHandler fileHandler =  new FileHandler();
        pmtConfig = fileHandler.autoSubmitFile(pmtConfig,env);
        System.out.println(BaseFunctionHelper.encode(pmtConfig.getDelimiter()));


        if(pmtConfig.getMsg_format().equals("X.12")){
            BuildApiJsonforHeader.buildJson4BelugaOcean("CARGOSMART","CUS_4010_310_CS.xml","|",pmtConfig);

        }else if(pmtConfig.getMsg_format().equals("EDIFACT")){
            BuildApiJsonforHeader.buildJson4BelugaOceanWithEdifact("CARGOSMART","CUS_D99B_IFTSTA_CS.xml","?",pmtConfig);
        }










    }


}
