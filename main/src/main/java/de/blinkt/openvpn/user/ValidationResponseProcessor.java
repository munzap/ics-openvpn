/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

import de.blinkt.openvpn.R;

public class ValidationResponseProcessor {
	 

	public ValidationResponseProcessor()
	{
		
	}
	
	public int onValidationSucced(ValidationResponse res) {
		
		if(res.getStatus() == ValidationResponseStatus.Valid)
			return onValid();
		
		else if(res.getStatus() == ValidationResponseStatus.ExpiredValid)
			return onExpiredValid();
		
		else if(res.getStatus() == ValidationResponseStatus.Locked)
			return onAccountLocked();
		
		else if(res.getStatus() == ValidationResponseStatus.NonExisting)
			return onNonExisting();
		
		else if(res.getStatus() == ValidationResponseStatus.ExpiredInvalid)
			return onExpiredInvalid();
		
		else
			return onInvalid();	
	}

	public boolean isValid(ValidationResponse res)
	{
		return res.getStatus() == ValidationResponseStatus.Valid;
	}
	
		
	private int onValid(/*User user*/)
	{
		return R.string.credentials_valid;
	}
	
	private int onInvalid()
	{		
		return R.string.credentials_invalid;
	}
	
	
	private int onExpiredInvalid()
	{
		return R.string.credentials_invalid;	
	}
	
	private int onNonExisting()
	{
		return R.string.credentials_non_existing;			
	}
	
	private int onExpiredValid()
	{
		return R.string.credentials_expired_valid;			
	}
	
	
	private int onAccountLocked()
	{
		return R.string.credentials_locked;		
	}
	
	private int onValidationFailed()
	{
		return R.string.credentials_failed;			
	}
	
}
