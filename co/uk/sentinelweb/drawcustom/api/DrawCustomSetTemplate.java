package co.uk.sentinelweb.drawcustom.api;
/*
Drawable Customiser API
Copyright (C) <year>  <name of author>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class DrawCustomSetTemplate {
	int version = 1;
	int width = -1;
	int height = -1;
	int bgColor = Color.BLACK;
	String description = "";
	String typeName = null;
	String baseName = null;
	private HashMap<String,DrawCustomSetItem> items = new HashMap<String,DrawCustomSetItem>();
	private Vector<String> itemsOrder = new Vector<String>();
	
	public DrawCustomSetTemplate(String fullName) {
		this.baseName = fullName.substring(0,fullName.lastIndexOf("."));
		this.typeName = getTypeFromFull(fullName);
	}
	
	public DrawCustomSetTemplate(Context c,String typeName) throws Exception{
		super();
		String fullName = getFullName(c,typeName);
		this.typeName=getTypeFromFull(fullName);
		setBaseName(c);
	}
	
	public static String getFullName(Context c,String typeName) throws Exception {
		String fullName = null;
		if (typeName.indexOf(".")>-1) {
			// check base is the same
			String base = DrawCustomSetTemplate.getBase(c);
			String nameBase = typeName.substring(0,c.getPackageName().lastIndexOf("."));
			Log.d(DrawCustomGlobals.TAG, "checkTemplate:"+base+":"+nameBase);
			if (!base.equals(nameBase)) {
				throw new Exception("The base names do not match");
			}
			fullName=typeName;
		} else {
			fullName = getBase(c)+"."+typeName;
		}
		return fullName;
	}
	
	public static String getBase(Context c) {
		return c.getPackageName().substring(0,c.getPackageName().lastIndexOf("."));
	}
	private static String getTypeFromFull(String fullName) {
		return fullName.substring(fullName.lastIndexOf(".")+1);
	}
	public static boolean checkTypeName(Context c,String s) throws Exception {
		if (s==null) {
			throw new Exception("typeName is null");	
		}
		String fullName = getFullName(c,s);
		String typeName=getTypeFromFull(fullName);
		for (int i=typeName.length()-1;i>0;i--) {
			if (!Character.isLetterOrDigit(typeName.charAt(i)) && typeName.charAt(i)!='_') {
				throw new Exception("Illegal type name, letters, digits or underscores only");	
			}
		}
		return true;
	}
	
	public static String getReadableTypeName(String typeName) {
		if (typeName.indexOf(".")>-1) {
			typeName=getTypeFromFull(typeName);
		}
		return typeName.replaceAll("_", " ");
	}
	
	public String getReadableTypeName() {
		return typeName.replaceAll("_", " ");
	}
	
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	public void setBaseName(Context c) {
		baseName = getBase(c);
	}
	
	public DrawCustomSetItem getItem(String key) {
		return items.get(key);
	}
	public DrawCustomSetItem getItem(int i) {
		return items.get(itemsOrder.get(i));
	}
	public int size() {
		return items.size();
	}
	public int indexOf(String string) {
		return itemsOrder.indexOf(string);
	}
	public String getKey(int i) {
		return itemsOrder.get(i);
	}
	public String getFullTypeName() {
		return baseName+"."+typeName;
	}
	
	public void addItem(String itemName) {
		boolean exists = items.containsKey(itemName);
		items.put(itemName,new DrawCustomSetItem());
		if (!exists) { itemsOrder.add(itemName); }
	}
	
	

	public void addItem(String itemName,int width,int height,int bgColor) {
		boolean exists = items.containsKey(itemName);
		DrawCustomSetItem drawCustomSetItem = new DrawCustomSetItem();
		drawCustomSetItem.setDimensions(width, height);
		drawCustomSetItem.setBgColor(bgColor);
		items.put(itemName,drawCustomSetItem);
		if (!exists) { itemsOrder.add(itemName); }
	}
	
	public boolean fromJSON(File f){
		if (f.exists()) {
			try {
				StringWriter sw = new StringWriter();
				BufferedReader reader = new BufferedReader(	new FileReader(f));
				String readline = "";
				while ((readline = reader.readLine()) != null) {
					sw.append(readline);
				}
				return fromJSON(sw.toString());
			} catch (FileNotFoundException e) {
				Log.d(DrawCustomGlobals.TAG,"Error reading template:"+f.getAbsolutePath(),e);
				return false;
			} catch (IOException e) {
				Log.d(DrawCustomGlobals.TAG,"Error reading template:"+f.getAbsolutePath(),e);
				return false;
			}
		}
		return false;
	}
	public boolean fromJSON(String s){
		try {
			JSONObject o = new JSONObject(new JSONTokener(s));  
			try {this.version = o.getInt("version");} catch (JSONException e) {}
			try {this.width = o.getInt("width");} catch (JSONException e) {}
			try {this.height = o.getInt("height");} catch (JSONException e) {}
			try {String colorString = o.getString("bg");
				bgColor = Color.parseColor(colorString);
			} 	catch (Exception e) {	}
			try {description=o.getString("description");} catch (JSONException e) {}
			JSONArray itemsJSON = o.getJSONArray("items");
			items.clear();
			itemsOrder.clear();
			//JSONArray names = itemsJSON.names();
			for (int i=0;i<itemsJSON.length();i++) {
				JSONObject itemJSON = itemsJSON.getJSONObject(i);
				DrawCustomSetItem dcsi = new DrawCustomSetItem();
				String key = dcsi.fromJSON(itemJSON);
				items.put(key,dcsi);
				itemsOrder.add(key);
			}
			return true;
		} catch (JSONException e) {
			Log.d(DrawCustomGlobals.TAG,"Error parsing JSON template:",e);
			return false;
		}
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		try {
			o.put("version", version);
			o.put("width", width);
			o.put("height", height);
			o.put("bg", toColorString(bgColor));
			o.put("description", description);
			
			JSONArray itemsJSON = new JSONArray();
			for (String key : itemsOrder) {
				JSONObject itemJSON = items.get(key).toJSON(key);
				itemsJSON.put(itemJSON);
			}
			o.put("items", itemsJSON);
			
			return o;
		} catch (JSONException e) {
			Log.d(DrawCustomGlobals.TAG,"Error serialising template",e);
			return null;
		}
	}
	public static String hex2(int val) {
		String a = Integer.toHexString(val);
		if (a.length()<2) {a="0"+a;}
		return a;
	}
	public static String toColorString(int bgColor) {
		return "#"+hex2(Color.alpha(bgColor))
				+hex2(Color.red(bgColor))
				+hex2(Color.green(bgColor))
				+hex2(Color.blue(bgColor));
	}
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}
	
}
