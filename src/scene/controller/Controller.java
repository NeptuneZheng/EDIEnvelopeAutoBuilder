package scene.controller;

import automation.BuildApiJsonforHeader;
import automation.FileHandler;
import base.BaseFunctionHelper;
import bean.PmtConfig;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.css.CssMetaData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import scene.service.ApiSqlGenerateImpl;
import scene.service.ModalDialog;
import scene.utilFunction.SceneUtil;
import scene.utilFunction.StageSingleCase;
import scene.vo.Common;
import scene.vo.Mapping;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends Exception implements Initializable {
    @FXML
    private Button runButton;
    @FXML
    private Button buildSQL;
    @FXML
    private Button mappingChoose,utilChoose;

    @FXML
    private TextArea textArea;
    @FXML
    private TextField tp_id,ReplacementChar,IGDefination,inputFilePath,mappingScriptPath,commonPath;

    @FXML
    private ChoiceBox<String> formateBox,messageType,dbEnviroment;

    @FXML
    private CheckBox igCheckBox,mappingScriptCheckBox,commonCheckBox;

    @FXML
    private ProgressIndicator buildSQLProgress,buildJsonProgress;

    private String tpid;
    private String msg_format;
    private String msg_type_id;
    private String dir_id;
    private String enviroment;
    private String folderpath;
    private String inputfilename;
    private String definition;
    private String replaceChar;
    private String ediResult;
    private boolean isMCI;


    private Stage primaryStage;

    SceneUtil util = new SceneUtil();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        igCheckBox.setSelected(true);
        mappingScriptCheckBox.setSelected(true);
        commonCheckBox.setSelected(true);

        igCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {});
        mappingScriptCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {});
        commonCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {});

    }
    @FXML
    public void runFunction(){
        new Thread(){
            @Override
            public void run() {
                if(buildHeaderJsonPreCheck()){
                    enviroment = dbEnviroment.getValue();
                    tpid =tp_id.getText();
                    dir_id = "O";
                    msg_type_id =messageType.getValue();
                    msg_format = formateBox.getValue();
                    folderpath = "D:\\kukri_sql\\EDI\\";
                    definition = IGDefination.getText();
                    replaceChar = ReplacementChar.getText();
                    if(!inputFilePath.getText().isEmpty()){
                        folderpath = inputFilePath.getText();
                    }
                    inputfilename = BaseFunctionHelper.getFile(folderpath);
                    Platform.runLater(()->{buildJsonProgress.setProgress(-1);});
                    PmtConfig pmtConfig =  new PmtConfig(enviroment,tpid,dir_id,msg_type_id,msg_format,inputfilename,folderpath);
                    FileHandler fileHandler =  new FileHandler();
                    try{
                        pmtConfig = fileHandler.autoSubmitFile(pmtConfig,enviroment);
                        Platform.runLater(()->{buildJsonProgress.setProgress(0.8);});
                        sleep(300);
                    }catch (Exception e){
                        Platform.runLater(()->{buildJsonProgress.setProgress(0);});
                        Platform.runLater(()->{new ModalDialog(StageSingleCase.getInstance(),"App Breakdown During BuildApiJsonforHeader.buildJson4BelugaOcean ! ");});
                    }
                    if(pmtConfig.getMsg_format().equals("X.12")){
                        try{
                            ediResult=BuildApiJsonforHeader.buildJson4BelugaOcean("CARGOSMART",definition,replaceChar,pmtConfig);
                        }catch(Exception e){
                            Platform.runLater(()->{buildJsonProgress.setProgress(0);});
                            Platform.runLater(()->{new ModalDialog(StageSingleCase.getInstance(),"App Breakdown During BuildApiJsonforHeader.buildJson4BelugaOcean ! ");});
                        }
                    }else if(pmtConfig.getMsg_format().equals("EDIFACT")){
                        try{
                            ediResult=BuildApiJsonforHeader.buildJson4BelugaOceanWithEdifact("CARGOSMART",definition,replaceChar,pmtConfig);
                        }catch (Exception e){
                            Platform.runLater(()->{buildJsonProgress.setProgress(0);});
                            Platform.runLater(()->{new ModalDialog(StageSingleCase.getInstance(),"App Breakdown During BuildApiJsonforHeader.buildJson4BelugaOceanWithEdifact ! ");});
                        }

                    }
                    if(ediResult != null){
                        writeJsonFile(ediResult,"D:\\kukri_sql\\","ig_"+tpid+"_"+msg_type_id+"_"+dir_id+".json");
                        Platform.runLater(()->{buildJsonProgress.setProgress(1);});
                        isMCI = pmtConfig.isMCI();
                        resultShow();
                        ediResult = null;
                    }
                }else{
                    Platform.runLater(()->{buildJsonProgress.setProgress(0);});
                }
            }
        }.start();
    }

    public void initFormateBox(){
        formateBox.setItems(FXCollections.observableArrayList("X.12", "EDIFACT","UIF"));
        messageType.setItems(FXCollections.observableArrayList("CT", "BC","BL","IN","BR","CS2OnlineBR"));
        dbEnviroment.setItems(FXCollections.observableArrayList("QA3", "QA4"));
        formateBox.getSelectionModel().select(0);
        messageType.getSelectionModel().select(0);
        dbEnviroment.getSelectionModel().select(0);
    }

    public void resultShow(){
        String str ="TP_Message: "+tpid+"     "+msg_format+"       "+msg_type_id+"\r\n"+"ReplacementChar:  "+replaceChar+"\r\n"+"IGDefination: "+definition+"\r\n"+"InputFile:   "+folderpath+inputfilename+"\r\nDBEnviroment:    "+enviroment+"\r\nUse MCI:    "+isMCI+"\r\n*******************************************************************\r\n"+ediResult;
        textArea.setText(str);
    }

    public void writeJsonFile(String header,String outputPath,String fileName){
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(outputPath+fileName));
            outputStream.write(header.getBytes());
            outputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readJsonFile(String filePath){
        String lineStr ="";
        StringBuffer stringBuffer = new StringBuffer();
        try {
            File file = new File(filePath);
            BufferedReader bufferedReader = null;
            if(file.isFile() && file.exists()){
                bufferedReader = new BufferedReader(new FileReader(file));
            }
            while ((lineStr = bufferedReader.readLine())!=null){
                stringBuffer.append(lineStr);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public void genSQL() throws Exception {
        buildSQLProgress.setProgress(-1);
        if(buildSQLPreCheck()){
            prepareJsonByCheckBox();
            ApiSqlGenerateImpl impl = new ApiSqlGenerateImpl();
            try {
                impl.generateKukriSqlFile(mappingScriptPath.getText(),commonPath.getText(),igCheckBox.isSelected(),mappingScriptCheckBox.isSelected(),commonCheckBox.isSelected());
                buildSQLProgress.setProgress(1);
            } catch (Exception e) {
                buildSQLProgress.setProgress(0);
                new ModalDialog(StageSingleCase.getInstance(),"App Breakdown During generateKukriSqlFile ! ");
            }
        }else{
            buildSQLProgress.setProgress(0);
        }
    }
    public String getMappingScriptPath(){
        String filePath = getChoosedFilePath();
        mappingScriptPath.setText(filePath);
        return filePath;
    }
    public String getCommonUtiltPath(){
        String filePath = getChoosedFilePath();
        commonPath.setText(filePath);
        return filePath;
    }

    public String getChoosedFilePath(){
        String filePath = "";
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("*.groovy", "*.groovy");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        if(file !=null){
            filePath=file.getPath();
        }
        return filePath;
    }

    public void prepareJsonByCheckBox(){
        enviroment = dbEnviroment.getValue();
        tpid =tp_id.getText();
        dir_id = "O";
        msg_type_id =messageType.getValue();
        msg_format = formateBox.getValue();
        folderpath = "D:\\kukri_sql\\EDI\\";
        definition = IGDefination.getText();
        replaceChar = ReplacementChar.getText();
        if(mappingScriptCheckBox.isSelected() && util.checkNotNull(tp_id.getText())){
            Mapping mapping = new Mapping("cs.b2b.mapping.scripts."+returnfullClassName(mappingScriptPath.getText()),tpid,msg_type_id,dir_id,msg_format);
            writeJsonFile(tojson(mapping),"D:\\kukri_sql\\","mapping_"+tpid+"_"+returnfullClassName(mappingScriptPath.getText())+".json");
        }
        if(commonCheckBox.isSelected()){
            Common common = new Common(returnfullClassName(commonPath.getText()));
            writeJsonFile(tojson(common),"D:\\kukri_sql\\","common_"+returnfullClassName(commonPath.getText())+".json");
        }
    }

    public String returnfullClassName(String inputStr){
        if(inputStr==null){
            return inputStr;
        }else{
            String[] arrStr = inputStr.split("\\\\");
            if(arrStr.length<2){
                return null;
            }else{
                return arrStr[arrStr.length-1].replaceAll(".groovy","");
            }
        }
    }

    public String tojson(Object o) {
        Gson gson = new Gson();
        String result = gson.toJson(o);
        return result;

    }

    public boolean buildHeaderJsonPreCheck(){
        if(!util.checkNotNull(tp_id.getText()) || !util.checkNotNull(IGDefination.getText())){
            String message = "Please Make Sure You Have Filled In TP_ID And IGDefination !";
            Platform.runLater(()->{ModalDialog md = new ModalDialog(StageSingleCase.getInstance(),message);});
            return false;
        }else{
            return true;
        }
    }
    public boolean buildSQLPreCheck(){
        File file = new File("D:\\kukri_sql\\");
        if(igCheckBox.isSelected() && !util.fileNameStartWithExist(file,"ig_")){
            String message = "Please Generate igJson File First or Uncheck IG !";
            ModalDialog md = new ModalDialog(StageSingleCase.getInstance(),message);
            return false;
        }else if(mappingScriptCheckBox.isSelected() && !util.checkNotNull(mappingScriptPath.getText())){
            String message = "Please Provide Mapping Script Path or Uncheck MappingScript !";
            ModalDialog md = new ModalDialog(StageSingleCase.getInstance(),message);
            return false;
        }else if(mappingScriptCheckBox.isSelected() && !util.fileNameStartWithExist(file,"mapping_") && !util.checkNotNull(tp_id.getText())){
            String message = "Please Provide At Least One Mapping Json Or Fill In Corresponding TP_ID First !";
            ModalDialog md = new ModalDialog(StageSingleCase.getInstance(),message);
            return false;
        }else if(commonCheckBox.isSelected() && !util.checkNotNull(commonPath.getText())){
            String message = "Please Provide Common Script Path !";
            ModalDialog md = new ModalDialog(StageSingleCase.getInstance(),message);
            return false;
        }else{
            return true;
        }
    }

}
