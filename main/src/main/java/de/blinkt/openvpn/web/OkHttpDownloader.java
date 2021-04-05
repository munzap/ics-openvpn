/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;
import android.net.TrafficStats;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * HTTP Downloader http://hc.apache.org/httpcomponents-client-ga/index.html 
 */
public class OkHttpDownloader implements IDownloader {

	OkHttpClient client;

	public OkHttpDownloader()
	{
		int THREAD_ID = 10000;
		TrafficStats.setThreadStatsTag(THREAD_ID); // crash otherwise

		client = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build();
	}
	 // Downlonad data and decrypts it
   public byte[] downloadData(String strUrl)
   {

	   Request request = new Request.Builder()
			   .url(strUrl)
			   .build();

	   try {
	   	Response response = client.newCall(request).execute();
		   if (!response.isSuccessful())
		   	throw new IOException("Unexpected code " + response);
/*
		   Headers responseHeaders = response.headers();
		   for (int i = 0; i < responseHeaders.size(); i++) {
			   System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
		   }
*/
		   return  response.body().bytes();
	   }

	   catch (IOException e)
	   {
			e.printStackTrace();
	   }

		return null;
   }
	
	@Override
	public void setDownloadProxy(DownloadProxy value) {
		// TODO Auto-generated method stub
		
	}

	private final static int CACHE_SIZE_BYTES = 1024 * 1024 ;

	@Override
	public void downloadFile(String strUrl, String strFile) throws MalformedURLException, IOException {

		Request request = new Request.Builder().url(strUrl).build();
		Response response = client.newCall(request).execute();

		InputStream is = response.body().byteStream();

		BufferedInputStream input = new BufferedInputStream(is);
		OutputStream output = new FileOutputStream(strFile);

		byte[] data = new byte[1024];

		long total = 0;
		int count = 0;

		while ((count = input.read(data)) != -1) {
			total += count;
			output.write(data, 0, count);
		}

		output.flush();
		output.close();
		input.close();
	}

	@Override
	public String downloadString(String strUrl)  {
		
		byte[] data = downloadData(strUrl);

		if(data == null)
			return "";

	    return new String(data);
	}
}
