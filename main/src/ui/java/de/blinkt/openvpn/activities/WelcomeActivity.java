/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.activities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.settings.Settings;
import de.blinkt.openvpn.user.AccountPanelAdapter;
import de.blinkt.openvpn.user.AccountValidationAsync;
import de.blinkt.openvpn.user.AccountValidationInterface;
import de.blinkt.openvpn.user.User;
import de.blinkt.openvpn.user.ValidationAsyncResponse;
import de.blinkt.openvpn.user.ValidationResponse;
import de.blinkt.openvpn.user.ValidationResponseProcessor;
import de.blinkt.openvpn.user.ValidationResponseStatus;
import de.blinkt.openvpn.utils.NetworkStatus;
import de.blinkt.openvpn.web.DownloadConfigsAsync;
import de.blinkt.openvpn.web.DownloadResult;
import de.blinkt.openvpn.web.IConfigsReceiver;

import de.blinkt.openvpn.fragments.AskForPW;

import de.blinkt.openvpn.R;
import hugo.weaving.DebugLog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static de.blinkt.openvpn.settings.Settings.LAST_OVPN_FILES_TIMESTAMP;

// Vyhledavani HB  activity
public class WelcomeActivity extends Activity implements IConfigsReceiver, AccountPanelAdapter, AccountValidationInterface
{
    Dialog loadingProgress;

    Boolean listening = false;
    Boolean hBfound = false;
    Button nextButton;
    Button searchButton;
    Button changeAccountInfo;
    TextView resultTextView;
    AsyncTask<Integer, Void, InetAddress> task;
    InetAddress selectedBrain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        TextView tvPdaName = (TextView) findViewById(R.id.pdaNameTextView);
        tvPdaName.setText("Pda name: " + Build.MODEL);


        PackageInfo pinfo;
        try
        {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;

            TextView tvVersion = (TextView) findViewById(R.id.versionTextView);
            tvVersion.setText("Version: " + versionName + " build:  " + versionNumber);

        } catch (NameNotFoundException e)
        {
            System.out.println(e.toString());
        }

        nextButton = (Button) findViewById(R.id.nextButton);
        searchButton = (Button) findViewById(R.id.refreshButton);
        changeAccountInfo = (Button) findViewById(R.id.main_btnChange);

        searchButton.setEnabled(false);
        nextButton.setEnabled(false);
        changeAccountInfo.setEnabled(false);

        resultTextView = (TextView) findViewById(R.id.statusTextView);
        loadingProgress = new Dialog(this);
        loadingProgress.setContentView(R.layout.serach_dlg);
        loadingProgress.setTitle(R.string.downloading_list);
        loadingProgress.setCancelable(true);

        start();

    }

    public void start()
    {
        listening = false;
        Settings sett = Settings.getInstance();
        Settings.getInstance().LoadData(this);

        if(!NetworkStatus.isNetworkAvailable(this))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.internet_not_found);
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

            searchButton.setEnabled(true);
            nextButton.setEnabled(false);
            resultTextView.setText("Server list: no data");

            return;
        }

        if(!Settings.getInstance().getUser().isUserValid())
        {
            AskForPW credDialog = new AskForPW();
            credDialog.askForPW(this,this,R.string.password,sett.getUser());
        }
        else
        {
            startAccountValidation(Settings.getInstance().getUser());
        }
    }

    // Vyvolat activity nastaveni
    public void onChangeAccountInfoClick(View v)
    {
        AskForPW credDialog = new AskForPW();
        credDialog.askForPW(this,this,R.string.password,Settings.getInstance().getUser());
    }

    // Znovu hledat HB
    public void onSearchButtonClick(View v)
    {
        searchButton.setEnabled(false);
        changeAccountInfo.setEnabled(false);
        nextButton.setEnabled(false);
        resultTextView.setText("Server list: downloading");

        SharedPreferences sharedPref = getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LAST_OVPN_FILES_TIMESTAMP, 0);
        editor.commit();

        start();
    }



    // HB vybran chceme ho ovladat
    public void onNextButtonClick(View v)
    {
    	/* For testing only
    	v = null;
    	v.bringToFront();
    	*/

        Intent startInt = new Intent(this, de.blinkt.openvpn.activities.MainActivity.class);
        startActivityForResult(startInt, 1);
    }


    public void onDownloadingCanceled()
    {
        hBfound = false;
        loadingProgress.cancel();

        resultTextView.setText("Server list: download failed");
        nextButton.setEnabled(false);
        searchButton.setEnabled(true);
    }


    public void onDownloadingDone(String path, DownloadResult res)
    {
        loadingProgress.setTitle(R.string.unpacking_list);

        if (res.res == DownloadResult.DownloadResultEnum.DOWNLOADED || res.res == DownloadResult.DownloadResultEnum.NOT_CHANGED )
        {
            if(res.res == DownloadResult.DownloadResultEnum.DOWNLOADED) {



                SharedPreferences sharedPref = getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong(LAST_OVPN_FILES_TIMESTAMP, res.timestamp);
                editor.commit();
            }
            hBfound = true;
            resultTextView.setText("Server list: ready");
            nextButton.setEnabled(true);
        }
        else
        {
            hBfound = false;
            resultTextView.setText("Server list: download failed");
            nextButton.setEnabled(false);
            loadingProgress.cancel();
        }

        loadingProgress.cancel();
        searchButton.setEnabled(true);
    }

    @Override
    public void onBackPressed()
    {
        closeApplication();
    }

    protected void closeApplication()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.main_exitQuestion);
        builder.setNegativeButton(R.string.question_btn_no, null);
        builder.setPositiveButton(R.string.question_btn_yes,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (task != null)
                            task.cancel(true);

                        WelcomeActivity.this.finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    void startAccountValidation(User user)
    {
        loadingProgress.setTitle(R.string.verifying_user);
        loadingProgress.show();

        String path = this.getDir("configs", MODE_PRIVATE).getPath();
        AccountValidationAsync accVal = new AccountValidationAsync(this,user);
        accVal.execute();
    }


    @Override
    public void accountNotChanged() {
        changeAccountInfo.setEnabled(true);
    }

    @Override
    public void passwordEntered(User user) {
        startAccountValidation(user);
    }

    @Override
    public void accountValidated(ValidationResponse res) {

        changeAccountInfo.setEnabled(true);

        if(res.getStatus() == ValidationResponseStatus.Valid)
        {
            Settings.getInstance().setUser(res.getUser());
            Settings.getInstance().SaveData(this);

            loadingProgress.setTitle(R.string.downloading_list);
            String path = this.getDir("configs", MODE_PRIVATE).getPath();

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            long oldTimestamp = sharedPref.getLong(LAST_OVPN_FILES_TIMESTAMP, 0);

            DownloadConfigsAsync dwn = new DownloadConfigsAsync(this, oldTimestamp);
            dwn.execute(path);
        }
        else
        {
            loadingProgress.cancel();
            nextButton.setEnabled(false);

            ValidationResponseProcessor rp = new ValidationResponseProcessor();

            final WelcomeActivity activity = this;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(rp.onValidationSucced(res));
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            AskForPW credDialog = new AskForPW();
                            credDialog.askForPW(activity,activity,R.string.password,Settings.getInstance().getUser());
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void accountValidationFailed(ValidationAsyncResponse res) {
        loadingProgress.cancel();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.connection_failed);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        searchButton.setEnabled(true);
        nextButton.setEnabled(false);
        resultTextView.setText("Server list: no data");
    }

}
