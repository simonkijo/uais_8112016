package com.uais.uais.message;

public class DataConstructor {

    private int img,count_new;
    private String person_name, person_subject, person_time,url_,mSms;
    private CharSequence mSms_,mSubject,mTime,sms_id, mReply,mFrom,mDate,name;
    private boolean isSelected;
    private boolean isVisible;

    public DataConstructor(){}
    public DataConstructor(String url, String name){
        this.url_ = url;
        this.person_name = name;
    }
    public DataConstructor(String url, String name, int countNew){
        this.url_ = url;
        this.person_name = name;
        this.count_new = countNew;
    }
    public DataConstructor(CharSequence message, CharSequence subject, CharSequence time){
        this.mSms_ = message;
        this.mSubject = subject;
        this.mTime = time;
    }
    public DataConstructor(String url, String name, String subject, String time){
        this.url_ = url;
        this.person_name = name;
        this.person_subject = subject;
        this.person_time = time;
    }
    public DataConstructor(CharSequence reply,CharSequence sender,CharSequence from,CharSequence date,CharSequence smsId,CharSequence message, CharSequence subject, CharSequence time,boolean isSelected,boolean isVisible){
        this.mReply = reply;
        this.name = sender;
        this.mFrom = from;
        this.mDate = date;
        this.sms_id = smsId;
        this.mSms_ = message;
        this.mSubject = subject;
        this.mTime = time;
        this.isSelected = isSelected;
        this.isVisible = isVisible;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public boolean isVisibled() {
        return isVisible;
    }
    public void setReplyId(String reply){this.mReply = reply;}
    public void setFromPerson(String from){this.mFrom = from;}
    public void setDate(String date){this.mDate = date;}
    public void setSms(String sms){this.mSms = sms;}
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public void setVisibled(boolean isVisible) {
        this.isVisible = isVisible;
    }
    public void setImage(int img_){this.img = img_;}
    public void setPersonName(String name_){this.person_name = name_;}
    public void setSubject(String sub){this.person_subject = sub;}
    public void setTime(String tm){this.person_time = tm;}
    public void setUrl(String uri){this.url_ = uri;}
    public void setSmsId(String smsid){this.sms_id = smsid;}
    public void setCountNew(int count){this.count_new = count;}

    public int getImage(){return img;}
    public String getPersonName(){return person_name;}
    public String getPersonSubject(){return person_subject;}
    public String getPersonTime(){return person_time;}
    public String getUrl(){return url_;}
    public CharSequence getSmsId(){return sms_id;}
    public CharSequence getReplyId(){return mReply;}
    public CharSequence getFromPerson(){return mFrom;}
    public CharSequence getDate(){return mDate;}
    public String getSms(){return mSms;}
    public int getCountNew(){return count_new;}

    public CharSequence getmSms_(){return mSms_;}
    public CharSequence getmSubject(){return mSubject;}
    public CharSequence getmTime(){return mTime;}
    public CharSequence getPName(){return name;}
}
