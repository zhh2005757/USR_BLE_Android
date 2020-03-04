package com.usr.usrsimplebleassistent.item;

/**
 * Created by shizhiyuan on 2017/5/24.
 */

public class SppUpdateListViewItem {

    public String Spp_Version_code;
    public String Spp_Version_name;
    public String Spp_Version_url;

    public SppUpdateListViewItem() {
    }

    public SppUpdateListViewItem(String spp_Version_code, String spp_Version_name, String spp_Version_url) {
        Spp_Version_code = spp_Version_code;
        Spp_Version_name = spp_Version_name;
        Spp_Version_url = spp_Version_url;
    }

    public String getSpp_Version_code() {
        return Spp_Version_code;
    }

    public void setSpp_Version_code(String spp_Version_code) {
        Spp_Version_code = spp_Version_code;
    }

    public String getSpp_Version_name() {
        return Spp_Version_name;
    }

    public void setSpp_Version_name(String spp_Version_name) {
        Spp_Version_name = spp_Version_name;
    }

    public String getSpp_Version_url() {
        return Spp_Version_url;
    }

    public void setSpp_Version_url(String spp_Version_url) {
        Spp_Version_url = spp_Version_url;
    }
}
