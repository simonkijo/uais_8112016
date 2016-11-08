package com.uais.uais.academicMaterials;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uais.uais.R;
import com.uais.uais.LocalService;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.detectinternetconnection.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class AcademicFragment extends Fragment implements FilePickerCallback {
    private final static String TAG = AcademicFragment.class.getSimpleName();
    public final static String STUDENT_ID = "AcademicFragment$StId";
    Spinner spinner;
    RecyclerView recyclerView;
    Button choose,upload;
    RelativeLayout container_spinner,progress_spinner;
    // flag for Network connection status
    Boolean isNetworkPresent = false;
    ArrayList<String> spinnerData;
    ArrayList<ChosenFile> file_;
    String module_title;

    private FilePicker filePicker;
    private LocalService s;

    public static AcademicFragment createInstance(String id) {
        Bundle bu = new Bundle();
        bu.putString(STUDENT_ID,id);
        AcademicFragment af = new AcademicFragment();
        af.setArguments(bu);
        return af;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(getContext());
        return inflater.inflate(R.layout.fragment_academic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container_spinner = (RelativeLayout)view.findViewById(R.id.container_spinner);
        spinner = (Spinner)container_spinner.findViewById(R.id.spinner);
        progress_spinner = (RelativeLayout)container_spinner.findViewById(R.id.loading_progress_spinner);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        choose = (Button)view.findViewById(R.id.choose);
        upload = (Button)view.findViewById(R.id.upload);

        showProgress(true);
        //get spinner data
        Data();

        chooseButton(choose);
        uploadFile(upload);

    }

    public void chooseButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose file from content provider
                pickFilesMultiple();
            }
        });
    }
    public void uploadFile(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String error_sms = null;
                try {
                    file_.isEmpty();
                }catch(NullPointerException e){
                    e.printStackTrace();
                    error_sms = e.getMessage();
                }
                if (module_title == null || error_sms != null) {
                    showAlertDialog(getContext(),getResources().getString(R.string.no_fileMod),getResources().getString(R.string.no_fileMod_sms),false);
                }else{
                    if(file_.isEmpty()){
                        showAlertDialog(getContext(),getResources().getString(R.string.no_fileMod),getResources().getString(R.string.no_fileMod_sms),false);
                    }else{
                        Log.d(TAG," : "+file_.isEmpty());

                        s.uploadFilesAsync(file_);
                        s.sendFilenameAndModuleToDb(file_,module_title);

                        setDataToAdapter(null);  //set empty data to adapter after sending data
                        file_.clear(); //clear files chosen
                        module_title = null; //clear selected modules

                    }

                }
            }
        });
    }
    public void setupSpinner(Spinner spinner,final ArrayList<String> arrayList){
        ArrayAdapter<String> aa = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, arrayList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(new NothingSelectedSpinnerAdapter(aa,R.layout.spinner_row_nothing_selected,getContext()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == -1){
                    //do nothing
                }else{
                    module_title = arrayList.get((int)id);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void pickFilesMultiple() {
        filePicker = getFilePicker();
        filePicker.allowMultiple();
        filePicker.pickFile();
    }

    private FilePicker getFilePicker() {
        filePicker = new FilePicker(this);
        filePicker.setFilePickerCallback(this);
        return filePicker;
    }

    @Override
    public void onFilesChosen(List<ChosenFile> files) {
        file_ = new ArrayList<>();

        for (ChosenFile file : files) {
            Log.d(TAG, "onFilesChosen: " + file);
            file_.add(file);
        }
        setDataToAdapter(files);
    }

    public void setDataToAdapter(List<ChosenFile> files){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(files);
        recyclerView.setAdapter(recyclerAdapter);
    }
    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Picker.PICK_FILE && resultCode == Activity.RESULT_OK) {
            filePicker.submit(data);
        }
    }
    //Shows the progress UI and hides the other layout.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            progress_spinner.setVisibility(show ? View.VISIBLE : View.GONE);
            progress_spinner.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progress_spinner.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progress_spinner.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    //get data run background
    private void Data(){
        if(isNetworkPresent) {
            Bundle bundle = getArguments();
            final String studentId = bundle.getString(STUDENT_ID);

            final RequestParams params = new RequestParams();
            params.put("stId", studentId);

            final AsyncHttpClient client = new AsyncHttpClient();
            client.post(UrlProvider.STUDENT_MODULES, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(getClass().getName()," statusCode: "+statusCode+" modules loaded at send assignment");

                    if (isAdded() && getActivity() != null) {  //this isAdded() removes IllegalStateException if user interrupt background process
                        try {
                            showProgress(false);
                            spinnerData = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsondata = response.getJSONObject(i);
                                    spinnerData.add(jsondata.getString("module"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            setupSpinner(spinner, spinnerData);
                        } catch (IllegalStateException ise) {
                            ise.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(getClass().getName()," statusCode: "+statusCode);
                    showProgress(false);
                }
            });
        }else{
            showAlertDialog(getContext(),getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();
        Intent intent= new Intent(context, LocalService.class);
        context.bindService(intent, mConnection,Context.BIND_AUTO_CREATE);

    }
    @Override
    public void onPause() {
        super.onPause();
        //getContext().unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,IBinder binder) {
            LocalService.MyBinder b = (LocalService.MyBinder) binder;
            s = b.getService();

            Log.d("onResume : ","connected to service");
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };

    //alert dialog
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.mipmap.ic_check_circle_black_18dp : R.mipmap.ic_error_black_18dp);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
}
