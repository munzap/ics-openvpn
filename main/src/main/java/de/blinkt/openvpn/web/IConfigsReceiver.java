/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

public interface IConfigsReceiver {
	 public void onDownloadingDone(String path, DownloadResult res);
	 public void onDownloadingCanceled();
}
