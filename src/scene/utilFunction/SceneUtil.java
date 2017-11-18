package scene.utilFunction;

import java.io.File;

/**
 * Created by ZHENGNE on 11/4/2017.
 */
public class SceneUtil {

    public Boolean checkNotNull(String input){
        if(input.equals("") || input == null || input.trim().equals("")){
            return false;
        }else{
            return true;
        }
    }

    public boolean fileNameStartWithExist(File file,String fileNamePrimer){
        boolean flag = false;
        for(File f : file.listFiles()){
            if(f.isFile() && f.getName().startsWith(fileNamePrimer)){
                flag = true;
                break;
            }
        }
        return flag;
    }


}
