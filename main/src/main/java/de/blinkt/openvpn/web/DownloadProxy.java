/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

/*
 * Proxy for web download or ssh tunel 
 */
public class DownloadProxy {
	
	private boolean privateUseProxy;
	private String privateProxy;
	private int privatePort;
	private String privateProxyLogin;
	private String privateProxyPass;
	private boolean requiresAuth;
	
	public final boolean getUseProxy()
	{
		return privateUseProxy;
	}
	public final void setUseProxy(boolean value)
	{
		privateUseProxy = value;
	}

	
	/*
	 * Proxy host getter
	 */
	public final String getProxy()
	{
		return privateProxy;
	}
	
	public final void setProxy(String value) 
	{
		privateProxy = value;
	}
	
	
	public final int getPort()
	{
		return privatePort;
	}

	public final void setPort(int value)
	{
		privatePort = value;
	}
	
	public final boolean getRequireAuth()
	{
		return requiresAuth;
	}

	public final void setRequireAuth(boolean value)
	{
		requiresAuth = value;
	}
	
	public final String getProxyLogin()
	{
		return privateProxyLogin;
	}
	
	/*
	 * Proxy username setter
	 */	
	public final void setProxyLogin(String value)
	{
		privateProxyLogin = value;
	}
		
	/*
	 * Proxy password getter
	 */	
	public final String getProxyPass()
	{
		return privateProxyPass;
	}
	public final void setProxyPass(String value)
	{
		privateProxyPass = value;
	}
}
