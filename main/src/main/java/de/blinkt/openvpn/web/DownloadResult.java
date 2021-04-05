/*
 * Copyright (c) 2012-2021 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.web;

public class DownloadResult {

    public DownloadResultEnum res;

    public enum DownloadResultEnum {
        NOT_CHANGED, DOWNLOADED, FAILED
    }

    DownloadResult(DownloadResultEnum res)
    {
        this.res = res;
    }

    public long timestamp;
}