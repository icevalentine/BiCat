package com.banledcamung.bicat;

public interface ITransfer {
    //void enableButton(String btnName);
    //void disableButton(String btnName);
    //void setTextViewText(String textName, String content);
    //String getTextViewText(String textName);
    //void setSwitch(boolean auto);

    void setPopupProgress(int currentProgress);
    void setCheckConnectBtn(boolean b);

    void showToast(String text);

    void setConneted(boolean b);
    ////void marksentline();
}