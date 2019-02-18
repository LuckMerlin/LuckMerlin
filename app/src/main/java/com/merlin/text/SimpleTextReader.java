package com.merlin.text;

import android.content.Context;
import android.content.res.AssetManager;

import com.merlin.debug.Debug;
import com.merlin.file.Closer;
import com.merlin.file.FileOperator;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class SimpleTextReader {

    public final String readAssert(String assertName, Context context) {
        AssetManager manager=null!=context&&null!=assertName&&!assertName.isEmpty()? context.getAssets():null;
        if (null!=manager){
            BufferedReader bf=null;
            try {
                StringBuilder builder = new StringBuilder();
                bf = new BufferedReader(new InputStreamReader(manager.open(assertName)));
                String line;
                while ((line = bf.readLine()) != null) {
                    builder.append(line);
                }
                return builder.toString();
            }catch (Exception e){
                Debug.E(getClass(),"Exception.Read simple text from assert file.e="+e,e);
                e.printStackTrace();
            }finally {
                new Closer().close(bf);
            }
        }
        Debug.W(getClass(),"Can't read simple text from assert file.manager="+manager+" assertName="+assertName+" context="+context);
        return null;
    }

    public final String readFile(File file){
        boolean exist=null!=file?file.exists():false;
        FileOperator operator=exist?new FileOperator():null;
        return  null!=operator?operator.readText(file,null):null;
    }

}
