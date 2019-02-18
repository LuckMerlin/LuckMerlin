package com.merlin.file;

import com.merlin.debug.Debug;

import java.io.File;

public class FilePostfix {
    private final static String POSTFIX_INDEX=".";

    public final int indexNamePostFix(File file){
        String name=null!=file?file.getName():null;
        int index=null!=name?name.lastIndexOf(POSTFIX_INDEX):-1;
        return index>=0&&index<name.length()?index:-1;
    }

    public final String getNamePostFix(File file){
        String name=null!=file?file.getName():null;
        int index=null!=name?name.lastIndexOf(POSTFIX_INDEX):-1;
        return index>=0&&index<name.length()?name.substring(index):null;
    }

    public final String getNameWithoutPostFix(File file){
        String name=null!=file?file.getName():null;
        int index=null!=name?name.lastIndexOf(POSTFIX_INDEX):-1;
        return index>=0&&index<name.length()?name.substring(0,index):null;
    }

    public final String getPathWithoutPostFix(File file){
        String name=null!=file?file.getAbsolutePath():null;
        int index=null!=name?name.lastIndexOf(POSTFIX_INDEX):-1;
        return index>=0&&index<name.length()?name.substring(0,index):null;
    }

    public final String replacePathPostfix(File file,String newPostfix){
        String nameNoPostfix=null!=newPostfix&&null!=file?getPathWithoutPostFix(file):null;
        return newPostfix.startsWith(POSTFIX_INDEX)?nameNoPostfix+newPostfix:nameNoPostfix+POSTFIX_INDEX+newPostfix;
    }


    public final String getFilePostfix(String data,String def){
        final int length=null!=data?data.length():0;
        int index=null!=data&&!data.isEmpty()?data.lastIndexOf("."):-1;
        return index>=0&&index<length?data.substring(index):def;
    }

    public final String getFileName(String data,String def,boolean withSeparator){
        final int length=null!=data?data.length():0;
        final String separator=File.separator;
        if (null!=separator&&!separator.isEmpty()){
            int index=null!=data&&!data.isEmpty()?data.lastIndexOf(separator):-1;
            index=withSeparator?index:index+1;
            return index>=0&&index<length?data.substring(index):def;
        }
        Debug.W(getClass(),"Can't get file name.separator="+separator+" data="+data);
        return def;
    }

    public final String getFileNameWithoutPostfix(String data,String def){
        final String name=null!=data?getFileName(data,null,false):null;
        final int length=null!=name?name.length():0;
        final String separator=".";
        if (null!=name&&!name.isEmpty()&&null!=separator&&!separator.isEmpty()){
            int index=null!=name&&!name.isEmpty()?name.lastIndexOf(separator):-1;
            return index>=0&&index<length?name.substring(index):def;
        }
        Debug.W(getClass(),"Can't get file name without postfix.separator="+separator+" name="+name+" data="+data);
        return def;
    }
}
