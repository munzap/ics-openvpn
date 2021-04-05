/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

public class DMProvider
{
		private static IDownloader instance = null;
		private static final Object padlock = new Object();

		/*
		 * Return dowloader class which implements  IDownloader interface 
		 */
		public static IDownloader getDMInstance()
		{
			synchronized (padlock) // singleton
			{
				if (instance == null)
				{
					instance = new OkHttpDownloader();
				}									
				
				return instance;
			}
		}
}