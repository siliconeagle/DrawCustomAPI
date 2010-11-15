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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.ObjectInputStream.GetField;
import java.util.Vector;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * This is the main API object for DrawCustomAPI
 * @author robm
 *
 */
public class DrawCustomGlobals {
	public static final String TAG="DrawCustomAPI";
	
	private static Boolean paid = null;
	
	public static final String CONTENT_PROVIDER ="content://co.uk.sentinelweb.drawcustomprovider";
	public static final String CONTENT_PROVIDER_PAID ="content://co.uk.sentinelweb.drawcustompaidprovider";
	
	public static final String INTENT_LAUNCH = "co.uk.sentinelweb.drawcustom.ACTION_EDIT";
	/**
	 * Broadcat reciever intent: co.uk.sentinelweb.drawcustom.CHANGED denotes the set has changes <br/>
		set an intent filter to capture this intent to update bitmap after they are changed.<br/>
		Params sent: INTENT_PARAM_SET: the set<br/>
		INTENT_PARAM_TYPE: the the type
	 */
	public static final String INTENT_CHANGED = "co.uk.sentinelweb.drawcustom.CHANGED";
	public static final String INTENT_PARAM_SET = "set";
	public static final String INTENT_PARAM_TYPE = "type";
	
	public static final String PACKAGE_FREE = "co.uk.sentinelweb.drawcustom";
	public static final String PACKAGE_PAID = "co.uk.sentinelweb.drawcustompaid";
	
	public static final String MARKET_URL = "market://search?q=pname:";
	public static final String MARKET_FREE = "market://search?q=pname:"+PACKAGE_FREE;
	public static final String MARKET_PAID = "market://search?q=pname:"+PACKAGE_PAID;
	
	public static final String APP_DIRECTORY = "co.uk.sentinelweb.DrawCustom";
	public static final String IMG_DIRECTORY = ".img";
	public static final String FONT_DIRECTORY = ".fonts";
	public static final String TYPES_DIRECTORY = ".types";
	
	// content provider query types
	public static final String CONTENT_QUERY_SETS = "sets";
	public static final String CONTENT_QUERY_SETS_PUBLISHED = "setsPub";
	public static final int CONTENT_QSETS_TYPE_COL = 0;
	public static final int CONTENT_QSETS_SETNAME_COL = 1;
	public static final int CONTENT_QSETS_PUB_COL = 2;
	public static final int CONTENT_QSETS_TYPEREAD_COL = 3;
	
	public static final String CONTENT_QUERY_FONTS = "fonts";
	public static final String CONTENT_QUERY_FONTS_PUBLISHED = "fontsPub";
	public static final int CONTENT_QFONTS_NAME_COL = 0;
	public static final int CONTENT_QFONTS_PUB_COL = 1;
	
	public static final String CONTENT_QUERY_TYPES = "types";
	public static final int CONTENT_QTYPES_TYPE_COL = 0;
	public static final int CONTENT_QTYPES_TYPEREAD_COL = 1;
	
	public static Boolean isPaid() {return paid;}
	
	public static String getContentURL() {
		return paid?CONTENT_PROVIDER_PAID:CONTENT_PROVIDER;
	}
	
	public static Uri getContentURL(String fullTypeName,String setName, String mapName,String cache) {
		Log.d(DrawCustomGlobals.TAG, "getContentURL:"+ fullTypeName+" : "+setName+" : "+mapName+" : "+cache);
		return Uri.parse((paid?CONTENT_PROVIDER_PAID:CONTENT_PROVIDER)+"/"+fullTypeName+"/"+setName+"/"+mapName+"/"+cache);
	}
	
	public static Uri getContentURL(Context c,String typeName,String setName, String mapName,String cache) throws Exception {
		Log.d(DrawCustomGlobals.TAG, "getContentURL:"+ typeName+" : "+setName+" : "+mapName+" : "+cache);
		String fullTypeName = DrawCustomSetTemplate.getFullName(c, typeName);
		return Uri.parse((paid?CONTENT_PROVIDER_PAID:CONTENT_PROVIDER)+"/"+fullTypeName+"/"+setName+"/"+mapName+"/"+cache);
	}
	
	
	
	public static void goToMarket(Context c) {
		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(MARKET_FREE));
			c.startActivity(myIntent);
		} catch (Exception e) {
			Toast.makeText(c, "Unable to load market app - have you uninstalled it?", 1000).show();
		} 
	}
	
	public static void launchCustomiser(Context c,String typeName,String setName) throws Exception {//,boolean typeIsFull
		String fullType = typeName;
		if (typeName.indexOf(".")==-1) {//!typeIsFull
			fullType = DrawCustomSetTemplate.getFullName(c, typeName);
		}
		try {
			if (checkTemplate(fullType)) {
				Intent myIntent = new Intent(INTENT_LAUNCH);
				myIntent.putExtra(INTENT_PARAM_TYPE, fullType);
				if (setName!=null) {myIntent.putExtra(INTENT_PARAM_SET, setName);}
				c.startActivity(myIntent);
			} else {
				Log.d(TAG,fullType+" not valid - customiser not launched");
			}
		} catch (Exception e) {
			Log.d(TAG,"Could not launch editor",e);
			Toast.makeText(c, "Could not launch editor", 500).show();
		}
	}
	
	public static boolean checkAppInstalled(Context c) {
		try {
			PackageInfo p = c.getPackageManager().getPackageInfo(PACKAGE_PAID, 0);
			if (p!=null) {	paid=true;return true;}
			p = c.getPackageManager().getPackageInfo(PACKAGE_FREE, 0);
			if (p!=null) {	return true;}
		} catch (NameNotFoundException e1) {
			
		}
		return false;
	}
	
	
	////////////////////////////////// content provider access stuff //////////////////////////////////////////////
	public static Cursor getSets(Context c, String[] types,boolean publishedOnly) {
		ContentResolver cr = c.getContentResolver();
		Cursor cursor = cr.query(Uri.parse(getContentURL()), null, publishedOnly?CONTENT_QUERY_SETS_PUBLISHED:CONTENT_QUERY_SETS, types, null);
		return cursor;
	}
	public static Cursor getTypes(Context c, String[] selector) {
		ContentResolver cr = c.getContentResolver();
		Cursor cursor = cr.query(Uri.parse(getContentURL()), null, CONTENT_QUERY_TYPES, selector, null);
		return cursor;
	}
	public static Cursor getFonts(Context c, boolean publishedOnly) {
		ContentResolver cr = c.getContentResolver();
		Cursor cursor = cr.query(Uri.parse(getContentURL()), null, publishedOnly?CONTENT_QUERY_FONTS_PUBLISHED:CONTENT_QUERY_FONTS, null, null);
		return cursor;
	}
	////////////////////////////////// template stuff //////////////////////////////////////////////
	public static boolean checkTemplate(Context c,String typeName) throws Exception {
		String fullName = DrawCustomSetTemplate.getFullName(c, typeName);
		Log.d(TAG, "checkTemplate:typeName:"+typeName);
		File typeRecord = new File(DiskUtil.typesDir,fullName);
		return typeRecord.exists();
	}
	public static boolean checkTemplate(String fullName) throws Exception {
		File typeRecord = new File(DiskUtil.typesDir,fullName);
		return typeRecord.exists();
	}
	
	public static DrawCustomSetTemplate getTemplate(Context c,String typeName) throws Exception {
		if (DiskUtil.checkDirectories()) {
			DrawCustomSetTemplate.checkTypeName(c,typeName);
			String fullName = DrawCustomSetTemplate.getFullName(c, typeName);
			File typeRecord = new File(DiskUtil.typesDir,fullName);
			DrawCustomSetTemplate dct = new DrawCustomSetTemplate(c,typeName);
			dct.fromJSON(typeRecord);
			return dct;
		}
		return null;
	}
	
	public static DrawCustomSetTemplate getTemplate(String fullName) throws Exception {
		if (DiskUtil.checkDirectories()) {
			File typeRecord = new File(DiskUtil.typesDir,fullName);
			DrawCustomSetTemplate dcst = new DrawCustomSetTemplate(fullName);
			dcst.fromJSON(typeRecord);
			return dcst;
		}
		return null;
	}
	
	public static DrawCustomSetTemplate getTemplateFromAsset(Context c,String typeName,String assetPath) throws Exception {
		try {
			InputStream sr = c.getAssets().open(assetPath);
			StringWriter sw = new StringWriter();
			byte[] ch = new byte[1000];
			int pos = -1;
			while ((pos = sr.read(ch, 0, 1000)) > -1) {	sw.write(new String(ch,0,pos)); }
			DrawCustomSetTemplate dcst = new DrawCustomSetTemplate(c,typeName);
			dcst.fromJSON(sw.toString());
			return dcst;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean registerType(Context c, DrawCustomSetTemplate template) throws Exception{
		if (DiskUtil.checkDirectories()) {
			DrawCustomSetTemplate.checkTypeName(c,template.typeName);
			String fullName = DrawCustomSetTemplate.getFullName(c, template.typeName);
			Log.d(TAG,fullName+":"+template.getFullTypeName());
			if (!fullName.equals(template.getFullTypeName())) {
				throw new Exception("Full typeName inconsistent");
			}
			File typeRecord = new File(DiskUtil.typesDir,fullName);
			FileWriter fw = new FileWriter(typeRecord);
			fw.write(template.toJSON().toString(3));
			fw.close();
			return true;
		}
		return false;
	}
	public static DrawCustomSetTemplate loadOrRegisterIfNessecary(Context c,String type,String assetTemplate) throws Exception {
		if (!checkTemplate(c,type)) {
			DrawCustomSetTemplate set = getTemplateFromAsset(c, type, assetTemplate);
			// TODO check version
			if (set!=null) {
				registerType(c, set);
				Log.d(TAG, "registered : "+set.getFullTypeName());
			} else {
				Log.d(TAG, "set null!!");
			}
		}else {
			Log.d(TAG, "set already exists ...");
			return getTemplate(c,type);
		}
		return null;
	}
}
