package com.uais.uais.Utils;

import java.util.ArrayList;

/**
 * Created by HP on 11/7/2016.
 */

public class FilterData {

    public static int countNew(ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> count = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                count.add(sender.get(i));
            }
        }
        return count.size();
    }
    public static ArrayList<CharSequence> replyId(ArrayList<String> reply, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> reply_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                reply_.add(reply.get(i));
            }
        }
        return reply_;
    }
    public static ArrayList<CharSequence> senderPN(ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> sender_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                sender_.add(sender.get(i));
            }
        }
        return sender_;
    }
    public static ArrayList<CharSequence> subjectPN(ArrayList<String> subject, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> subject_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                subject_.add(subject.get(i));
            }
        }
        return subject_;
    }
    public static ArrayList<CharSequence> fromPN(ArrayList<String> from, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> from_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                from_.add(from.get(i));
            }
        }
        return from_;
    }
    public static ArrayList<CharSequence> datePN(ArrayList<String> date, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> date_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                date_.add(date.get(i));
            }
        }
        return date_;
    }
    public static ArrayList<CharSequence> timePN(ArrayList<String> time, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> time_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                time_.add(time.get(i));
            }
        }
        return time_;
    }
    public static ArrayList<CharSequence> smsPN(ArrayList<String> sms, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> sms_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                sms_.add(sms.get(i));
            }
        }
        return sms_;
    }
    public static ArrayList<CharSequence> smsIdPN(ArrayList<String> sms_id, ArrayList<String> sender, String deDupSender){
        ArrayList<CharSequence> sms_id_ = new ArrayList<>();
        for(int i=0; i< sender.size(); i++){
            if(sender.get(i).equals(deDupSender)){
                sms_id_.add(sms_id.get(i));
            }
        }
        return sms_id_;
    }
}
