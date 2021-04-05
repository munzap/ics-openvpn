/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.blinkt.openvpn.exceptions.AccessKeyVerificationExeption;
import de.blinkt.openvpn.web.DMProvider;
import de.blinkt.openvpn.web.IDownloader;
import de.blinkt.openvpn.web.UrlAddress;

public class AccountValidation {
	User user;
		
	public AccountValidation(User user)
	{
		this.user = user;
	}	

    private static String encryptPassword(String pass)
    {
        String Key = "aRe49VnL2op0tqmL296yz7wN./t#[rdF";
        byte[] KeyData = new byte[0];
        byte [] passwordBytes;
        byte [] passwordBytesPadded;
        byte [] iv = new byte[8];
        byte [] output;

        new Random().nextBytes(iv);

        IvParameterSpec IV = new IvParameterSpec(iv);

        try {

            KeyData = Key.getBytes("UTF8");
            SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");

            Cipher cipher =  Cipher.getInstance("Blowfish/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE,KS, IV);

            passwordBytes = pass.getBytes("UTF8");

            int padding;

            if(passwordBytes.length % 8 != 0)
                padding = 8 - (passwordBytes.length % 8);
            else
                padding = 0;

            passwordBytesPadded = new byte[passwordBytes.length + padding];

            Arrays.fill(passwordBytesPadded,(byte)0);
            System.arraycopy(passwordBytes, 0, passwordBytesPadded, 0, passwordBytes.length);
            byte []enc =  cipher.doFinal(passwordBytesPadded);

            output = new byte[iv.length + enc.length];

            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(enc, 0, output, iv.length, enc.length);

            StringBuilder sb = new StringBuilder();

            for (byte b : output) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        }


        catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        catch (BadPaddingException e) {
            e.printStackTrace();
        }

        catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return "";
    }


	private static String makeRequestUrl(User user) throws AccessKeyVerificationExeption
	{
		try {
			return UrlAddress.ACCOUNT_CHECK_URL + "?u=" + URLEncoder.encode(user.getUsername(),"ISO-8859-1") + "&pe=" + encryptPassword(user.getPassword());
		} catch (UnsupportedEncodingException e) {
			
			throw new AccessKeyVerificationExeption("Problem with creating request url user: " + user.toString(),e);
		}
	}
	
	private static String getServerResponse(String url) throws AccessKeyVerificationExeption
	{
		String xmlString = null;
		IDownloader dm = DMProvider.getDMInstance();
		
		try {
			xmlString = dm.downloadString(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new AccessKeyVerificationExeption("Problem with getting server response url: " + url,e);
		}
		
		if (xmlString == null || xmlString.isEmpty())
			throw new AccessKeyVerificationExeption("Problem with getting server response it is empty url: " + url);
		
		return xmlString;
	}
	
	private static ValidationResponse decodeServerResponse(String xmlString, User user) throws AccessKeyVerificationExeption
	{
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ValidationResponse response = new ValidationResponse(user);
									        
		try {
			InputStream is = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
		} 
		
		catch (Exception e) {
			throw new AccessKeyVerificationExeption("Problem with decoding server response response: " + xmlString,e);
		}
			
		
		response.setStatus(getElementContent(document,"PasswordValidity"),getElementContent(document,"UserStatus"));
		
		return response;
	}
	
	static String getElementContent(Document document, String element) throws AccessKeyVerificationExeption
	{
		NodeList elements = document.getElementsByTagName(element);
		
		if(elements.getLength() > 0)
		{
			Node node = elements.item(0);
			String res = node.getTextContent();
			return res;
		}
		else
			throw new AccessKeyVerificationExeption("PProblem with decoding server response response, it is not valid: " + document.getTextContent() );
	}
	
	public static ValidationResponse checkUserOnline(User user) throws AccessKeyVerificationExeption
	{	   
	   	if (!user.isUserValid())
			return new ValidationResponse(user);
	
	   	String url = makeRequestUrl(user);
	    String xmlString = getServerResponse(url);
	    return  decodeServerResponse(xmlString,user);
		
	}
}
