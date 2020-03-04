package com.usr.usrsimplebleassistent.item;

/**
 * Created by shizhiyuan on 2017/5/24.
 */

public class SppDeviceListViewItem {

    public int I_ListV_itemImageId;
    public String I_ListV_itemContent;
    public String I_ListV_itemAdress;
    public String I_ListV_itemRssi;

    public SppDeviceListViewItem() {
    }

    public SppDeviceListViewItem(int i_ListV_itemImageId, String i_ListV_itemContent, String i_ListV_itemAdress, int i_ListV_itemRssi) {
        I_ListV_itemImageId = i_ListV_itemImageId;
        I_ListV_itemContent = i_ListV_itemContent;
        I_ListV_itemAdress = i_ListV_itemAdress;
        I_ListV_itemRssi = String.valueOf(i_ListV_itemRssi);
    }

    public int getI_ListV_itemImageId() {
        return I_ListV_itemImageId;
    }

    public void setI_ListV_itemImageId(int i_ListV_itemImageId) {
        I_ListV_itemImageId = i_ListV_itemImageId;
    }

    public String getI_ListV_itemContent() {
        return I_ListV_itemContent;
    }

    public void setI_ListV_itemContent(String i_ListV_itemContent) {
        I_ListV_itemContent = i_ListV_itemContent;
    }

    public String getI_ListV_itemAdress() {
        return I_ListV_itemAdress;
    }

    public void setI_ListV_itemAdress(String i_ListV_itemAdress) {
        I_ListV_itemAdress = i_ListV_itemAdress;
    }

    public String getI_ListV_itemRssi() {
        return I_ListV_itemRssi;
    }

    public void setI_ListV_itemRssi(String i_ListV_itemRssi) {
        I_ListV_itemRssi = i_ListV_itemRssi;
    }
}
