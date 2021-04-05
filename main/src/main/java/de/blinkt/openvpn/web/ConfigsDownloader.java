/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import de.blinkt.openvpn.settings.Settings;
import de.blinkt.openvpn.utils.ZipIdc;

import static de.blinkt.openvpn.web.UrlAddress.OPENVPN_FILES_TIMESTAMP;


public class ConfigsDownloader {

	IDownloader downloader;

	public ConfigsDownloader(IDownloader downloader)
	{
		this.downloader = downloader;
	}

	// timestamp on OpenVPN package update
	public  long GeTimestamp()
	{
		String timestampStr = "0";
		try {
			timestampStr = downloader.downloadString(OPENVPN_FILES_TIMESTAMP);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 0;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 0;
		}
		catch (Exception ex)
		{
			return 0;
		}

		if(!timestampStr.isEmpty())
			return Long.parseLong(timestampStr);

		return 0;
	}

	public  Boolean downloadConfigs(String path)
	{
		String configsDirPath = path + Settings.TBLK_CONFIGS_DIR;
		String configsZipPath = path + Settings.TBLK_ZIP_CONFIGS;

		try {
			downloader.downloadFile(UrlAddress.TNBLK_CERTS, configsZipPath);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		File configDir = new File(configsDirPath);
		if(configDir.exists())
			deleteFolder(configDir);

		configDir.mkdirs();

		File keysDir = new File(configsDirPath + "/keys" );
		keysDir.mkdirs();

		ZipIdc.unpackZip(configsZipPath, configsDirPath);

	    return  true;
	}
	
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
