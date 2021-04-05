/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import de.blinkt.openvpn.activities.ConfigConverter;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.settings.Settings;
import de.blinkt.openvpn.utils.ShortcutUpdater;
import hugo.weaving.DebugLog;

import static de.blinkt.openvpn.settings.Settings.LAST_OVPN_FILES_TIMESTAMP;

public class DownloadConfigsAsync extends AsyncTask<String, Void, DownloadResult> {
	
	IConfigsReceiver receiver;
	
	private Exception exception;
	private  Context context;
    private String path;
    private  long oldTimestamp;


    public DownloadConfigsAsync(Context receiver, long timestamp)
    {
    	this.receiver = (IConfigsReceiver) receiver;
    	context = receiver;
    }


    @DebugLog
    public void addProfiles()
    {
        ProfileManager vpl = ProfileManager.getInstance(context);

        String path = context.getDir("configs", 0).getPath();
        String configsDirPath = path + Settings.TBLK_CONFIGS_DIR;
        File configDir = new File(configsDirPath);

        FilenameFilter filter = new FilenameFilter() // only .ovpn
        {
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".ovpn");
            }
        };

        vpl.removeProfiles(context);

        File [] files = configDir.listFiles(filter);
        ConfigConverter conv = new ConfigConverter();

        for (File f : files)
        {
            FileInputStream is = null;

            try {
                is = new FileInputStream (f);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            conv.doImport(is);
            conv.saveProfile(f.getName().replaceFirst("[.][^.]+$", ""), context);
            String x = f.getName();
            System.out.println(x);
        }


        vpl.saveProfileList(context);
    }

    protected DownloadResult doInBackground(String... params) {
        try {
        	path = params[0];

            ConfigsDownloader cDwn = new ConfigsDownloader( DMProvider.getDMInstance());
            long timestamp = cDwn.GeTimestamp();

            if(timestamp != oldTimestamp || oldTimestamp == 0) {

                if(cDwn.downloadConfigs(path))
                {

                    addProfiles();

                    // shortcut update is crashing now
                 //   ShortcutUpdater shortcutUpdater = new ShortcutUpdater(context);
               //     shortcutUpdater.updateDynamicShortcuts();
                    return new DownloadResult ( DownloadResult.DownloadResultEnum.DOWNLOADED);
                }
            }
            else
                return new DownloadResult ( DownloadResult.DownloadResultEnum.NOT_CHANGED);

            
        } catch (Exception e) {
            this.exception = e;
            return new DownloadResult ( DownloadResult.DownloadResultEnum.FAILED);

        }

        return new DownloadResult ( DownloadResult.DownloadResultEnum.FAILED);
    }

    @Override
    protected void onPostExecute(DownloadResult res) {
    	 // callback hotovo
    	receiver.onDownloadingDone(path,res);
    }
    
    @Override
    protected void onCancelled()
    {
    	receiver.onDownloadingCanceled();
    }

}
