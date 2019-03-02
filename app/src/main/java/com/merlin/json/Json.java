package com.merlin.json;
/**
 * 2011-2019, LuckMerlin
 * Author: LuckMerlin
 * Date: 2019/3/2 13:47
 * Description:
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class Json {

	public JSONArray resolveJsonArray(Object json) {
		Object object=null!=json?resolveJsonType(json):null;
		return  null!=object&&object instanceof JSONArray?(JSONArray)object:null;
	}

	public JSONObject resolveJsonObject(Object json) {
		 Object object=null!=json?resolveJsonType(json):null;
		 return  null!=object&&object instanceof JSONObject?(JSONObject)object:null;
	}

	public   Object resolveJsonType(Object json) {
		Object obj=null!=json&&json instanceof String?parseJsonString((String)json):json;
		return  null!=obj&&(obj instanceof JSONObject||obj instanceof JSONArray)?obj:null;
	}	
	
	public  Object parseJsonString(String json) {
		JSONTokener tokener = null;
		Object object = null;
		try {
			return (null!=json&&null!=(tokener=new JSONTokener(json))&&null!=(object=tokener.nextValue()))?object:null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
}
 