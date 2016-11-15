package com.uais.uais.Utils;

/**
 * Created by HP on 11/2/2016.
 */

public class UrlProvider {
    //for image data and content data downloading
    public static final String BASE_IMAGE_URL = "http://liziio0aq-site.1tempurl.com/uploads/";
    //used in ExpandableAssignmentFragment
    public static final String DOWNLOAD_FILE_ASSIGNMENT = BASE_IMAGE_URL+"assignment/";
    //used in ExpandableExampleFragment
    public static final String DOWNLOAD_FILE_NOTES = BASE_IMAGE_URL+"academic_notes/";

    //for content data
    public static final String BASE_URL = "http://www.uais.co.nf/mobile";
    //used in AcademicFragment
    public static final String STUDENT_MODULES = BASE_URL+"/studentModules";
    public static final String STUDENT_UPLOAD_ASSIGNMENT = BASE_URL+"/studentUploadAssignment";
    public static final String SEND_FILENAME_AND_MODULE_TO_DB = BASE_URL+"/stSendModuleAndFilenameAssignment";
    //used in AdFragment
    public static final String STUDENT_ADNOTES = BASE_URL+"/studentAdNotes";
    //used in AssignmentFragment
    public static final String STUDENT_ASSIGNMENT = BASE_URL+"/studentAssignment";
    //used in ChildFragmentCompose
    public static final String SEND_MESSAGE = BASE_URL+"/sendMessage";
    //used in ChildFragmentReadSms
    public static final String SET_READ = BASE_URL+"/setSmsRead";
    //used in MasterFragmentInbox
    public static final String LOAD_ROLE = BASE_URL+"/loadRole";
    public static final String LOAD_INBOX = BASE_URL+"/loadInbox";
    //used in MasterFragmentOutBox
    public static final String SEND_TO_TRASH = BASE_URL+"/sendToTrash";
    public static final String LOAD_OUTBOX = BASE_URL+"/loadOutbox";
    //used in MasterFragmentRead
    public static final String LOAD_READ = BASE_URL+"/loadRead";
    //used in MasterFragmentTrash
    public static final String SET_UNTRASH = BASE_URL+"/setUnTrash";
    public static final String LOAD_TRASH = BASE_URL+"/loadTrash";
    //used in ProfileActivity
    public static final String STUDENT_PROFILE_DATA = BASE_URL+"/studentProfileData";
    public static final String STUDENT_UPLOAD_PROF_CHANGES = BASE_URL+"/studentUploadProfChanges";
    //used in LoginActivity
    public static final String LOGIN = BASE_URL+"/login";
    //used in PasswordRecovery
    public static final String RECOVER = BASE_URL+"/recover";
    //used in Registration
    public static final String REGISTER = BASE_URL+"/register";

    //used in AcademicActivity/Accounts/MasterFragmentInbox
    public static String getImageUrl(String image){
        return BASE_IMAGE_URL+image+".jpg";
    }

    //for localhost
    /*public static final String BASE_IMAGE_URL = "http://192.168.43.43/uais/dist/img/";  //for localhost
    public static final String BASE_DL_URL = "http://192.168.43.43/uais/uploads/";  //for localhost
    //used in ExpandableAssignmentFragment
    public static final String DOWNLOAD_FILE_ASSIGNMENT = BASE_DL_URL+"assignment/";
    //used in ExpandableExampleFragment
    public static final String DOWNLOAD_FILE_NOTES = BASE_DL_URL+"academic_notes/";

    public static final String BASE_URL = "http://192.168.43.43/uais/mobile";  //for localhost
    //used in AcademicFragment
    public static final String STUDENT_MODULES = BASE_URL+"/studentModules.php";
    public static final String STUDENT_UPLOAD_ASSIGNMENT = BASE_URL+"/studentUploadAssignment.php";
    public static final String SEND_FILENAME_AND_MODULE_TO_DB = BASE_URL+"/stSendModuleAndFilenameAssignment.php";
    //used in AdFragment
    public static final String STUDENT_ADNOTES = BASE_URL+"/studentAdNotes.php";
    //used in AssignmentFragment
    public static final String STUDENT_ASSIGNMENT = BASE_URL+"/studentAssignment.php";
    //used in ChildFragmentCompose
    public static final String SEND_MESSAGE = BASE_URL+"/sendMessage.php";
    //used in ChildFragmentReadSms
    public static final String SET_READ = BASE_URL+"/setSmsRead.php";
    //used in MasterFragmentInbox
    public static final String LOAD_ROLE = BASE_URL+"/loadRole.php";
    public static final String LOAD_INBOX = BASE_URL+"/loadInbox.php";
    //used in MasterFragmentOutBox
    public static final String SEND_TO_TRASH = BASE_URL+"/sendToTrash.php";
    public static final String LOAD_OUTBOX = BASE_URL+"/loadOutbox.php";
    //used in MasterFragmentRead
    public static final String LOAD_READ = BASE_URL+"/loadRead.php";
    //used in MasterFragmentTrash
    public static final String SET_UNTRASH = BASE_URL+"/setUnTrash.php";
    public static final String LOAD_TRASH = BASE_URL+"/loadTrash.php";
    //used in ProfileActivity
    public static final String STUDENT_PROFILE_DATA = BASE_URL+"/studentProfileData.php";
    public static final String STUDENT_UPLOAD_PROF_CHANGES = BASE_URL+"/studentUploadProfChanges.php";
    //used in LoginActivity
    public static final String LOGIN = BASE_URL+"/login.php";
    //used in PasswordRecovery
    public static final String RECOVER = BASE_URL+"/recover.php";
    //used in Registration
    public static final String REGISTER = BASE_URL+"/register.php";

    //used in AcademicActivity/Accounts/MasterFragmentInbox
    public static String getImageUrl(String image){
        return BASE_IMAGE_URL+image+".jpg";
    }*/
}
