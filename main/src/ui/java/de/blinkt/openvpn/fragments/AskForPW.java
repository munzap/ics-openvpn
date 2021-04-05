/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.fragments;

import de.blinkt.openvpn.user.AccountPanelAdapter;
import de.blinkt.openvpn.user.AccountValidation;
import de.blinkt.openvpn.user.User;

import de.blinkt.openvpn.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class AskForPW {
	
	public void askForPW(final AccountPanelAdapter adapter, Context context,final int type,final User credentials) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		final EditText entry = new EditText(context);
        final View userpwlayout = inflater.inflate(R.layout.userpass, null);

		entry.setSingleLine();
		entry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		entry.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("Need " + context.getString(type));
		dialog.setMessage("Enter the password for profile ");

        
        ((EditText)userpwlayout.findViewById(R.id.username)).setText(credentials.getUsername());
        ((EditText)userpwlayout.findViewById(R.id.password)).setText(credentials.getPassword());            
        ((CheckBox)userpwlayout.findViewById(R.id.show_password)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        
        	@Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                   ((EditText)userpwlayout.findViewById(R.id.password)).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                else
                   ((EditText)userpwlayout.findViewById(R.id.password)).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
             }
            });

            dialog.setView(userpwlayout);            
        
        AlertDialog.Builder builder = dialog.setPositiveButton(android.R.string.ok,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {      
                    	                         
                         String	username = ((EditText) userpwlayout.findViewById(R.id.username)).getText().toString().toLowerCase();
                         String	password = ((EditText) userpwlayout.findViewById(R.id.password)).getText().toString();
                        	
                         User newUser = new User (username,password);
                         
                         if(credentials.equals(newUser))
                        	 adapter.accountNotChanged();
                         else
                        	 adapter.passwordEntered(newUser);                         
                    }
                });
        
        dialog.setNegativeButton(android.R.string.cancel,
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				/*
				VpnStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
                        ConnectionStatus.LEVEL_NOTCONNECTED);
				*/
				adapter.accountNotChanged();			
			}
		});

		dialog.create().show();				
	}
}
