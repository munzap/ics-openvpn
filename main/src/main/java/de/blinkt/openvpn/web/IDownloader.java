/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

import java.io.IOException;
import java.net.MalformedURLException;

/*
 * Iterface for a web downloads 
 */

public interface IDownloader {
	
	void setDownloadProxy(DownloadProxy value); // set a http proxy for downloading
	void downloadFile(String strUrl, String strFile) throws MalformedURLException, IOException;  // download a file from web to folder
	byte[] downloadData(String url) throws MalformedURLException, IOException; // download from web to a byte array
	String downloadString(String strUrl) throws IOException; // download from web to a string
	
}
