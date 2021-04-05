/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

public class User
{		
		public User()
		{
			this("", "");			
		}
		
		public User(String user, String pass)
		{
			setUsername(user);
			setPassword(pass);
		}						
		
		private String username;
		public final String getUsername()
		{
			return username;
		}

		public final void setUsername(String value)
		{		
			username = value;
		}

		private String password;
		public final String getPassword()
		{
			return password;
		}
		public final void setPassword(String value)
		{
			password = value;
		}
					
		public boolean isUserValid()
		{
			if (username == null || username.isEmpty() || password == null || password.isEmpty() || username.equals("demo"))
				return false;
			else
				return true;
		}

		@Override
		public boolean equals(Object other){
		    if (other == null) return false;
		    if (other == this) return true;
		    if (!(other instanceof User))return false;
		
		    User u = (User) other;
		    
		    if(u.getUsername().equals(getUsername()) && u.getPassword().equals(getPassword()))
				return true;
		    
		    return false;
		}
		
		@Override
		public String toString()
		{
			return "Username: " + getUsername() + " Password: " + getPassword() + " ";
		}
		
}
