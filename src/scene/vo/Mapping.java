package scene.vo;

/**
 * Created by ZHENGNE on 11/1/2017.
 */
public class Mapping {
    private String fullClassName;
    private String TP_ID;
    private String MSG_TYPE_ID;
    private String DIR_ID;
    private String MSG_FMT_ID;

    public Mapping(String fullClassName, String TP_ID, String MSG_TYPE_ID, String DIR_ID, String MSG_FMT_ID) {
        this.fullClassName = fullClassName;
        this.TP_ID = TP_ID;
        this.MSG_TYPE_ID = MSG_TYPE_ID;
        this.DIR_ID = DIR_ID;
        this.MSG_FMT_ID = MSG_FMT_ID;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getTP_ID() {
        return TP_ID;
    }

    public void setTP_ID(String TP_ID) {
        this.TP_ID = TP_ID;
    }

    public String getMSG_TYPE_ID() {
        return MSG_TYPE_ID;
    }

    public void setMSG_TYPE_ID(String MSG_TYPE_ID) {
        this.MSG_TYPE_ID = MSG_TYPE_ID;
    }

    public String getDIR_ID() {
        return DIR_ID;
    }

    public void setDIR_ID(String DIR_ID) {
        this.DIR_ID = DIR_ID;
    }

    public String getMSG_FMT_ID() {
        return MSG_FMT_ID;
    }

    public void setMSG_FMT_ID(String MSG_FMT_ID) {
        this.MSG_FMT_ID = MSG_FMT_ID;
    }
}
