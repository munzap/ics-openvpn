/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import de.blinkt.openvpn.user.User;


public class Settings {	

	public static String TBLK_CONFIGS_DIR = "/Configurations/";
	public static String TBLK_ZIP_CONFIGS = "/configs.zip";
	public static String LAST_OVPN_FILES_TIMESTAMP = "OVPN_FILES_TIMESTAMP";
	public static String ORIG_PACKAGE_NAME = "de.blinkt.openvpn";

	static final class SettingStrings
	{
		public static final String username = "username";
		public static final String password = "password";
	}
	
	User user;
	
	public static final String PREFERENCES_NAME = "com.tfr.idc_android.android";
	private static Settings instance = null;
			 
	protected Settings() {
	    // Exists only to defeat instantiation.
	}
	
	public static Settings getInstance() {
	   if(instance == null) {
	      instance = new Settings();
	   }
	  return instance;
   }	

	public void LoadData(Context _context)
	{
		Map<String, ?> data = _context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE).getAll();

		user = new User();
		
		if (data.containsKey(SettingStrings.username))
		{
			user.setUsername((String) data.get(SettingStrings.username));
		} else
		{
			user.setUsername((String) data.get(""));
		}
		if (data.containsKey(SettingStrings.password))
		{			
			user.setPassword((String) data.get(SettingStrings.password));
		} else
		{
			user.setPassword("");
		}			
	}

	public void SaveData(Context _context)
	{
		SharedPreferences.Editor prefs = _context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
		
		prefs.putString(SettingStrings.username, user.getUsername());
		prefs.putString(SettingStrings.password, user.getPassword());		

		prefs.commit();
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
}
