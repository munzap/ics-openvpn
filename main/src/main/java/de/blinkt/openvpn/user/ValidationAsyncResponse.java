/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

public class ValidationAsyncResponse extends ValidationResponse {

	boolean sucess = false;
	Exception exception = null;
	
	public ValidationAsyncResponse(User u, Exception e) {
		
		super(u);
		exception = e;
	}
	
	public ValidationAsyncResponse(User u, ValidationResponse res) {
		super(u,res.getStatus());
	
		sucess = true;
	}
	
	public boolean getValidationSucceed()
	{
		return sucess;
	}
	
	public Exception getException()
	{
		return exception;
	}
}
