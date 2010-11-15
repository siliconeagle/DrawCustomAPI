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
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.util.Log;


public class DrawCustomSetItem {
	
	int width = -1;
	int height = -1;
	Integer bgColor = null;// transparent black
	String description = "";
	
	public DrawCustomSetItem( ) {
	}
	
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public String fromJSON(JSONObject o) {
		try {width=o.getInt("width");} catch (JSONException e) {}
		try {height=o.getInt("height");} catch (JSONException e) {}
		try {String colorString = o.getString("bg");
			bgColor = Color.parseColor(colorString);
		} 	catch (Exception e) {	}
		try {description=o.getString("description");} catch (JSONException e) {}
		try {
			String id=o.getString("id");
			return id;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject toJSON(String id) {
		JSONObject o = new JSONObject() ;
		try {
			o.put("id", id);
			if (width>-1) {o.put("width",width);}
			if (height>-1) {o.put("height",height);}
			if (bgColor!=null) {o.put("bg",DrawCustomSetTemplate.toColorString(bgColor));}
			if (description!=null && !"".equals(description)) {o.put("description", description);}
			return o;
		} catch (JSONException e) {
			Log.d(DrawCustomGlobals.TAG,"Error serialising template",e);
		}
		return null;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getBgColor() {
		return bgColor;
	}
	public void setBgColor(Integer bgColor) {
		this.bgColor = bgColor;
	}
}
