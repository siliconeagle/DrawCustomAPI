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

import co.uk.sentinelweb.drawcustom.Globals;

import android.content.Context;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

/**
 * @author robm
 * Disk utilities for DrawCustom API
 */
public class DiskUtil {
	//static StatFs sf = new StatFs(android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
	static File extDir = android.os.Environment.getExternalStorageDirectory();
	
	public static File homeDir = null;
	public static File imgDir;
	public static File fontDir;
	public static File typesDir;
	/**
	 * Check the CD scard is mounted
	 * @return
	 */
	public static boolean sdMounted() {
		String sdcardState = android.os.Environment.getExternalStorageState(); 
		if (sdcardState.contentEquals(android.os.Environment.MEDIA_MOUNTED)){ 
		    return true;
        } 
        return false; 
	} 
	/**
	 * Checks external stroage is available
	 * requires permission: 	&lt;uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/&gt;
	 * @return
	 */
	public static boolean checkDisk() {
		if (extDir==null) {
			 extDir = android.os.Environment.getExternalStorageDirectory();
		}
		
		return sdMounted();
	}
	
	/**
	 * Check all required directories are created
	 * @return
	 */
	public static boolean checkDirectories() {
		if (DiskUtil.checkDisk()) {
			if (homeDir==null) {
				homeDir = new File(DiskUtil.extDir,DrawCustomGlobals.APP_DIRECTORY);
				if (!homeDir.exists()) {
					homeDir.mkdirs();
				}
			}
			if (imgDir==null) {
				imgDir = new File (homeDir,DrawCustomGlobals.IMG_DIRECTORY);
				if (!imgDir.exists()) {
					imgDir.mkdirs();
				}
			}
			if (fontDir==null) {
				fontDir = new File (homeDir,DrawCustomGlobals.FONT_DIRECTORY);
				if (!fontDir.exists()) {
					fontDir.mkdirs();
				}
			}
			if (typesDir==null) {
				typesDir = new File (homeDir,DrawCustomGlobals.TYPES_DIRECTORY);
				if (!typesDir.exists()) {
					typesDir.mkdirs();
				}
			}
			return true;
		}
		return false;
	}
	
}
