/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

public class ValidationResponse {
	
	User user;
	ValidationResponseStatus status = ValidationResponseStatus.Invalid;
	
	public ValidationResponse(User user)
	{
		this.user = user;
	}
	
	public ValidationResponse(User user, ValidationResponseStatus status)
	{	
		this(user);
		
		this.status = status;
	}
	
	public ValidationResponseStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(String passwordValidity, String status)
	{
		if(passwordValidity.equals("valid"))
			this.status = ValidationResponseStatus.Valid;
		
		else if(passwordValidity.equals("invalid"))
		{
			if(status.equals("active"))
				this.status = ValidationResponseStatus.Invalid;

			else if(status.equals("inactive"))
				this.status = ValidationResponseStatus.ExpiredInvalid; // bad password and expired subs
			
			else if(status.equals("locked"))
				this.status = ValidationResponseStatus.Locked; // account abuse
			
			else if(status.equals("invalidUsername"))
				this.status = ValidationResponseStatus.NonExisting;
			else
				this.status = ValidationResponseStatus.Invalid;
		}
		
		else if(passwordValidity.equals("expiredValid"))
		{
			this.status = ValidationResponseStatus.ExpiredValid; // valid account password but expired subs
		}
		else
			this.status = ValidationResponseStatus.Invalid;
	}
	
	public User getUser()
	{
		return user;
	}
}
