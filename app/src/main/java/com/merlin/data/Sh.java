package com.merlin.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.merlin.debug.Debug;

import java.util.Map;
import java.util.Set;

public class Sh {
	private final SharedPreferences S;

	public Sh(Context c) {
		this(c, null==c?null:c.getPackageName());
	}
	
	public Sh(Context c, String name) {
		S = null!=c?c.getSharedPreferences(null==name?c.getPackageName():name, Context.MODE_PRIVATE):null;
	}

	public Map<String, ?> getAll(){
		if (null==S){
			Debug.W(getClass(),"Can't get all value from SH.S="+S);
		}
		return null!=S?S.getAll():null;
	}
	
	public boolean cleanAll(){
		Editor editor=null!=S?S.edit():null;
		if (null==editor){
			Debug.W(getClass(),"Can't clean all value from SH.editor="+editor+" S="+S);
		}
		return null!=editor?editor.clear().commit():false;
	}

	public boolean putString(String key,String value){
		Editor editor=null!=key&&null!=S?S.edit():null;
		if (null!=editor){
			return editor.putString(key,value).commit();
		}
		Debug.W(getClass(),"Can't put string.editor="+editor+" key="+key+" S="+S+" value="+value);
		return false;
	}

	public String getString(String key, String defValue){
		if (null!=key&&null!=S) {
			try {
				return S.getString(key, defValue);
			} catch (Exception e) {
				Debug.E(getClass(), "Can't get string from SH. key="+key+" defValue="+defValue);
				e.printStackTrace();
			}
		}
		Debug.W(getClass(),"Can't get string value from SH.key="+key+" S="+S+" defValue="+defValue);
		return defValue;
	}

	public boolean putInt(String key,int value){
		Editor editor=null!=key&&null!=S?S.edit():null;
		if (null!=editor){
			return editor.putInt(key,value).commit();
		}
		Debug.W(getClass(),"Can't put int.editor="+editor+" key="+key+" S="+S+" value="+value);
		return false;
	}

	public  int  getInt(String key, int defValue){
		if (null!=key&&null!=S) {
			try{
				return S.getInt(key, defValue);
			} catch (Exception e) {
				Debug.E(getClass(), "Can't get int from SH. key="+key+" defValue="+defValue);
				e.printStackTrace();
			}
		}
		Debug.W(getClass(),"Can't get int value from SH.key="+key+" S="+S+" defValue="+defValue);
		return defValue;
	}

	public boolean putBoolean(String key,boolean value){
		Editor editor=null!=key&&null!=S?S.edit():null;
		if (null!=editor){
			return editor.putBoolean(key,value).commit();
		}
		Debug.W(getClass(),"Can't put boolean.editor="+editor+" key="+key+" S="+S+" value="+value);
		return false;
	}

	public  boolean  getBoolean(String key, boolean defValue){
		if (null!=key&&null!=S) {
			try{
				return S.getBoolean(key, defValue);
			} catch (Exception e) {
				Debug.E(getClass(), "Exception!getString from SH. key="+key+" defValue="+defValue);
				e.printStackTrace();
			}
		}
		Debug.W(getClass(),"Can't get boolean value from SH.key="+key+" S="+S+" defValue="+defValue);
		return defValue;
	}

	public boolean putFloat(String key,float value){
		Editor editor=null!=key&&null!=S?S.edit():null;
		if (null!=editor){
			return editor.putFloat(key,value).commit();
		}
		Debug.W(getClass(),"Can't put float.editor="+editor+" key="+key+" S="+S+" value="+value);
		return false;
	}

	public  float  getFloat(String key, float defValue){
		if (null!=key&&null!=S) {
			try{
			   return S.getFloat(key, defValue);
			} catch (Exception e) {
				Debug.E(getClass(), "Exception!getString from SH. key="+key+" defValue="+defValue);
				e.printStackTrace();
			}
		}
		Debug.W(getClass(),"Can't get float value from SH.key="+key+" S="+S+" defValue="+defValue);
		return defValue;
	}

	public boolean putLong(String key,long value){
		Editor editor=null!=key&&null!=S?S.edit():null;
		if (null!=editor){
          return editor.putLong(key,value).commit();
		}
		Debug.W(getClass(),"Can't put long.editor="+editor+" key="+key+" S="+S+" value="+value);
		return false;
	}

	public  long  getLong(String key, long defValue){
		if (null!=key&&null!=S) {
			try {
			  return S.getLong(key, defValue);
			} catch (Exception e) {
				Debug.E(getClass(), "Exception!getString from SH. key="+key+" defValue="+defValue);
				e.printStackTrace();
			}
		}
		Debug.W(getClass(),"Can't get long value from SH.key="+key+" S="+S+" defValue="+defValue);
		return defValue;
	}
	
	
	public Set<String> getStringSet(String key, Set<String> defValues){
		if (null!=key&&null!=S) {
			try{
			    return S.getStringSet(key, defValues);
				} catch (Exception e) {
					Debug.E(getClass(), "Exception!getString from SH. key="+key+" defValues="+defValues);
					e.printStackTrace();
				}
		}
		Debug.W(getClass(),"Can't get string set value from SH.key="+key+" S="+S+" defValues="+defValues);
		return defValues;
	}
	
	public   boolean remove(String key){
		  if (null!=S&&null!=key) {
			Editor editor=S.edit().remove(key);
			if (null!=editor) {
				editor.commit();
				return true;
			}
		}
		return false;
	}
}
