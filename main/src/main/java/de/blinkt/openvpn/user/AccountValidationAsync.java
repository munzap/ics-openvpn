/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

import android.os.AsyncTask;

import de.blinkt.openvpn.exceptions.AccessKeyVerificationExeption;


public class AccountValidationAsync extends AsyncTask<Void, Void, ValidationResponse> {
	
	User user;
	AccountValidationInterface res;
	private Exception exception = null;
	
		
	public AccountValidationAsync(AccountValidationInterface res, User user)
	{
		this.user = user;
		this.res = res;
	}
	
		
	 @Override
	protected void onPostExecute(ValidationResponse valRes) {
		 ValidationAsyncResponse valAsyncRes;
		 
		 if(exception == null)
		 {
		    valAsyncRes = new ValidationAsyncResponse(user,valRes);
			res.accountValidated(valRes);
		 }
		 else	
		 {
			valAsyncRes = new ValidationAsyncResponse(user,exception);
			res.accountValidationFailed(valAsyncRes);
		 }			 
	}
	 
	@Override
	protected ValidationResponse doInBackground(Void... params) {
		
		try
		{
			return AccountValidation.checkUserOnline(user);
		}
		catch(AccessKeyVerificationExeption ex)
		{
			exception = ex;
		}
		
		return null;
	} 
}
