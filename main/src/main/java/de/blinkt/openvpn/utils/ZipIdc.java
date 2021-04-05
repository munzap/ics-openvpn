/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipIdc {
	public static boolean unpackZip(String zipFile, String dest)
	{       
	     InputStream is;
	     ZipInputStream zis;
	     try 
	     {
	         is = new FileInputStream(zipFile);
	         zis = new ZipInputStream(new BufferedInputStream(is));          
	         ZipEntry ze;

	         while((ze = zis.getNextEntry()) != null) 
	         {
	             ByteArrayOutputStream baos = new ByteArrayOutputStream();
	             byte[] buffer = new byte[10240];
	             int count;

	             String filename = ze.getName();
	             FileOutputStream fout = new FileOutputStream(dest + filename);

	             // reading and writing
	             while((count = zis.read(buffer)) != -1) 
	             {
	                 baos.write(buffer, 0, count);
	                 byte[] bytes = baos.toByteArray();
	                 fout.write(bytes);             
	                 baos.reset();
	             }

	             fout.close();               
	             zis.closeEntry();
	         }

	         zis.close();
	     } 
	     	    	    
	     catch(IOException e)
	     {
	         e.printStackTrace();
	         return false;
	     }	    	

	    return true;
	}
}
