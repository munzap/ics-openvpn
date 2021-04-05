/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

import android.os.Build;

public class UrlAddress
{

	public static String getProtocol()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // SSL 1.2 support
			return "https://";
		else
			return "http://";
	}


	public static final String MAIN_SERVER_NAME = "key2marketing.net";
	public static final String MAIN_SERVER_IP = "78.46.181.74";
	public static String MAIN_SERVER = MAIN_SERVER_NAME;
	public static final String FOLDER = "/idc";
	public static String ACCOUNT_CHECK_URL = getProtocol() + MAIN_SERVER + FOLDER + "/pwd_check.php";

	//public static String ACCOUNT_CHECK_URL = "http://" + "www.oneclickproxy.com/tmp" + "/pwd_check.php.xml";
	
	public static String PROXY_LIST_URL = getProtocol() + MAIN_SERVER + FOLDER + "/proxy.list.php?v2=1&akey=";
	public static String TNBLK_CERTS = getProtocol() + MAIN_SERVER + FOLDER + "/ovpn_files.zip";
	public static String OPENVPN_FILES_TIMESTAMP = getProtocol() + MAIN_SERVER + FOLDER + "/ovpn_timestamp.txt"; // timestamp for OpenVPN package change
	public static boolean VALIDATE_CERT = true;
}
