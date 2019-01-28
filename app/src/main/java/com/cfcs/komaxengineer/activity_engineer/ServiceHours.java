package com.cfcs.komaxengineer.activity_engineer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.model.EditTextModelServiceHours;
import com.cfcs.komaxengineer.utils.GPSTracker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ServiceHours extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppEngineerServiceHrInitialData";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppEngineerServiceHrInitialData";
    private static String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    private static String SOAP_ACTION2 = "http://cfcs.co.in/AppEngineerServiceHrsInsUpdt";
    private static String METHOD_NAME2 = "AppEngineerServiceHrsInsUpdt";

    LinearLayout ll_edit_text_bind,  maincontainer;

    EditText txt_any_remark;

    TextView txt_date,txt_header;

    ArrayList<String> todayStatusMasterIDList;

    ArrayList<String> todayStatusMasterNameList;

    ArrayAdapter<String> spinneradapterTodayStatusMaster;

    Spinner spinner_today_status_master,spinner_tomorrow_plan_master;

    ArrayList<String> tomorrowPlanMasterIDList;

    ArrayList<String> tomorrowPlanMasterNameList;

    ArrayAdapter<String> spinneradaptertomorrowPlanMaster;

    ArrayList<EditText> arrayEditText;
    ArrayList<String> arrayID;
    ArrayList<String> arrayisRequired;
    ArrayList<String> arrayServiceHrTitleName;
    String[] edtArray;

    String serviceHrsDetailJson = "";
    String masterJson = "";

    Button btn_submit,btn_clear;

    Calendar c;
    int mYear;
    int mMonth;
    int mDay;

    public GPSTracker gps;

    String lacti;
    String longi;

    String serviceHrID = "00000000-0000-0000-0000-000000000000";
    String edit = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_hours);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        c = Calendar.getInstance();
        ll_edit_text_bind = findViewById(R.id.ll_edit_text_bind);
        maincontainer = findViewById(R.id.maincontainer);
        spinner_today_status_master = findViewById(R.id.spinner_today_status_master);
        spinner_tomorrow_plan_master = findViewById(R.id.spinner_tomorrow_plan_master);
        txt_any_remark = findViewById(R.id.txt_any_remark);
        txt_date = findViewById(R.id.txt_date);
        btn_submit = findViewById(R.id.btn_submit);
        btn_clear = findViewById(R.id.btn_clear);
        txt_header = findViewById(R.id.txt_header);

        getEngineerLocation();


        Config_Engg.isOnline(ServiceHours.this);
        if (Config_Engg.internetStatus) {

            new AddInitialData().execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHours.this);
        }


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            serviceHrID = getIntent().getExtras().getString("ServiceHrID");
            edit = getIntent().getExtras().getString("Edit");
        }

        if (edit.compareTo("Edit") == 0) {

            txt_header.setText("Update Service Report");
            btn_submit.setText("Update");
            new FillServiceHourData().execute();

        }

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePicker();
            }
        });

//        Onload Date Set in txt_date
        Date date1 = new Date();
        String stringDate = DateFormat.getDateInstance().format(date1);
        txt_date.setText(stringDate );

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int todaySpinnerPos = spinner_today_status_master.getSelectedItemPosition();
                int tomorrowSpinnerPos = spinner_tomorrow_plan_master.getSelectedItemPosition();


                if (todaySpinnerPos == 0){
                    Toast.makeText(ServiceHours.this,"Please select today status",Toast.LENGTH_LONG).show();
                    return;
                }

                if(tomorrowSpinnerPos == 0){
                    Toast.makeText(ServiceHours.this,"Please select tomorrow plan",Toast.LENGTH_LONG).show();
                    return;
                }

                if (arrayEditText != null) {
                    edtArray = new String[arrayEditText.size()];
                   // edtID = new String[arrayEditText.size()];
                }
                if (arrayEditText != null) {
                    if (arrayEditText.size() > 0) {

                        for (int i = 0; i < arrayEditText.size(); i++) {
                            String name = arrayEditText.get(i).getText().toString();
                            String checkMandantory = arrayisRequired.get(i);
                            String ServiceHrTitleName = arrayServiceHrTitleName.get(i);
                            if (name.compareTo("") ==0 && checkMandantory.compareTo("true") == 0){
                                Toast.makeText(ServiceHours.this,"Please Fill" + " " + ServiceHrTitleName,Toast.LENGTH_LONG).show();
                                return;
                            }else {

                                edtArray[i] = arrayEditText.get(i).getText().toString();
                            }

                        }
                    }
                }

                if (arrayEditText.size() > 0) {
                    makeJson();
                } else {
                 //   sparePartJson = "";
                }


                new submitServiceHour().execute();

            }
        });


        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceHours.this, ServiceHourList.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public void datePicker() {
        // Get Current Date
        this.mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        Date date1 = new Date(String.valueOf(c.getTime()));
                        String date_time = DateFormat.getDateInstance().format(date1);

                        txt_date.setText(date_time);


                    }
                }, this.mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select Date");
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void makeJsonMasterJson() {

        try {
            Gson gson = new Gson();
            JSONObject jsonObj = new JSONObject();
            JSONArray array = new JSONArray();

                String latt = lacti;
                String lang = longi;
                String ServiceHrDate = txt_date.getText().toString();
                String Remark = txt_any_remark.getText().toString();
                long SelectedTodayStatusID = spinner_today_status_master.getSelectedItemId();
                String TodayStatusID = todayStatusMasterIDList.get((int) SelectedTodayStatusID);
                long SelectedTomorrowPlanID = spinner_tomorrow_plan_master.getSelectedItemId();
                String TomorrowPlanID = tomorrowPlanMasterIDList.get((int) SelectedTomorrowPlanID);
                EditTextModelServiceHours diary = getMasterObjectFilled(serviceHrID, ServiceHrDate,Remark,TodayStatusID,TomorrowPlanID,latt,lang);
                String case_json = gson.toJson(diary);
                JSONObject objImg = new JSONObject(case_json);
                array.put(objImg);


            Log.e("make json size is ", " cfcs " + jsonObj.toString());
            masterJson = objImg.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private EditTextModelServiceHours getMasterObjectFilled(String ServiceHrID, String ServiceHrDate,String Remark,String
            TodayStatusID,String TomorrowPlanID,String latt, String lang) {
        EditTextModelServiceHours bean = new EditTextModelServiceHours();
        bean.setServiceHrID(ServiceHrID);
        bean.setServiceHrDate(ServiceHrDate);
        bean.setRemark(Remark);
        bean.setTodayStatusID(TodayStatusID);
        bean.setTomorrowPlanID(TomorrowPlanID);
        bean.setLatt(latt);
        bean.setLang(lang);
        return bean;
    }

    private EditTextModelServiceHours getObjectFilled(String ServiceHrTitleID, String ServiceHrs) {
        EditTextModelServiceHours bean = new EditTextModelServiceHours();
        bean.setServiceHrTitleID(ServiceHrTitleID);
        bean.setServiceHrs(ServiceHrs);
        return bean;
    }

    public void makeJson() {

        try {
            Gson gson = new Gson();
            JSONObject jsonObj = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < arrayEditText.size(); i++) {

                String ServiceHrTitleID = arrayID.get(i);
                String ServiceHrs = edtArray[i];
                EditTextModelServiceHours diary = getObjectFilled(ServiceHrTitleID, ServiceHrs);
                String case_json = gson.toJson(diary);
                JSONObject objImg = new JSONObject(case_json);
                array.put(objImg);

            }
            Log.e("make json size is ", " cfcs " + jsonObj.toString());
            serviceHrsDetailJson = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEngineerLocation() {

        gps = new GPSTracker(ServiceHours.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            lacti = String.valueOf(latitude);
            longi = String.valueOf(longitude);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private class AddInitialData extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String initialData, todayStatusMaster, tomorrowPlanMaster, serviceHrTitleMaster;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ServiceHours.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            String EngineerID = Config_Engg.getSharedPreferences(ServiceHours.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(ServiceHours.this, "pref_Engg", "AuthCode", "");
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    initialData = result.getProperty(0).toString();
                    Object json = new JSONTokener(initialData).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(initialData);
                        JSONArray probleTitleArray = object.getJSONArray("TodayStatusMaster");
                        todayStatusMaster = probleTitleArray.toString();
                        JSONArray plantjsonArray = object.getJSONArray("TomorrowPlanMaster");
                        tomorrowPlanMaster = plantjsonArray.toString();
                        JSONArray trancsonArray = object.getJSONArray("ServiceHrTitleMaster");
                        serviceHrTitleMaster = trancsonArray.toString();
                        if (initialData.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(initialData);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;
                        } else {
                            flag = 2;
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(initialData);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (jsonObject.has("status")) {

                            LoginStatus = jsonObject.getString("status");
                            msgstatus = jsonObject.getString("MsgNotification");
                            if (LoginStatus.equals(invalid)) {

                                flag = 4;
                            } else {

                                flag = 1;
                            }
                        }

                    }

                } else {
                    JSONArray jsonArray = new JSONArray(initialData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    msgstatus = jsonObject.getString("MsgNotification");
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return null;
        }


        @Override
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, ServiceHours.this);
            } else if (flag == 2) {
                try {

                    JSONArray jsonArray2 = new JSONArray(todayStatusMaster);
                    todayStatusMasterIDList = new ArrayList<String>();
                    todayStatusMasterIDList.add(0, "");
                    todayStatusMasterNameList = new ArrayList<String>();
                    todayStatusMasterNameList.add(0, "Today Status");

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String TodayStatusID = jsonObject2.getString("TodayStatusID");
                        String TodayStatusName = jsonObject2.getString("TodayStatusName");

                        todayStatusMasterIDList.add(i + 1, TodayStatusID);
                        todayStatusMasterNameList.add(i + 1, TodayStatusName);
                    }

                    spinneradapterTodayStatusMaster = new ArrayAdapter<String>(ServiceHours.this, android.R.layout.simple_spinner_item, todayStatusMasterNameList);
                    spinneradapterTodayStatusMaster.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_today_status_master.setAdapter(spinneradapterTodayStatusMaster);

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                try {
                    JSONArray jsonArray2 = new JSONArray(tomorrowPlanMaster);
                    tomorrowPlanMasterIDList = new ArrayList<String>();
                    tomorrowPlanMasterIDList.add(0, "");
                    tomorrowPlanMasterNameList = new ArrayList<String>();
                    tomorrowPlanMasterNameList.add(0, "Tomorrow Plan");

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String TomorrowPlanID = jsonObject2.getString("TomorrowPlanID");
                        String TomorrowPlanName = jsonObject2.getString("TomorrowPlanName");

                        tomorrowPlanMasterIDList.add(i + 1, TomorrowPlanID);
                        tomorrowPlanMasterNameList.add(i + 1, TomorrowPlanName);
                    }

                    spinneradaptertomorrowPlanMaster = new ArrayAdapter<String>(ServiceHours.this, android.R.layout.simple_spinner_item, tomorrowPlanMasterNameList);
                    spinneradaptertomorrowPlanMaster.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_tomorrow_plan_master.setAdapter(spinneradaptertomorrowPlanMaster);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                if (edit.compareTo("Edit") != 0){

                    try {

                        JSONArray jsonArray2 = new JSONArray(serviceHrTitleMaster);
                        arrayEditText = new ArrayList<EditText>();
                        arrayID = new ArrayList<String>();
                        arrayisRequired = new ArrayList<String>();
                        arrayServiceHrTitleName = new ArrayList<String>();
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                            final String serviceHrTitleName = jsonObject2.getString("ServiceHrTitleName");
                            String isRequired = jsonObject2.getString("isRequired");
                            final String serviceHrTitleID = jsonObject2.getString("ServiceHrTitleID");


                            LinearLayout.LayoutParams Textparams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                            Textparams.gravity = Gravity.CENTER;
                            Textparams.setMargins(0, 20, 0, 0);


                            EditText EditName = new EditText(ServiceHours.this);
                            EditName.setLayoutParams(Textparams);
                            EditName.setHint(serviceHrTitleName);
                            EditName.setPadding(20,20,0,20);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                EditName.setBackground(getDrawable(R.drawable.edit_text_box_enable_disable));
                            }
                            EditName.setTypeface(null, Typeface.NORMAL);
                            EditName.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            arrayEditText.add(EditName);
                            arrayID.add(serviceHrTitleID);
                            arrayisRequired.add(isRequired);
                            arrayServiceHrTitleName.add(serviceHrTitleName);
                            ll_edit_text_bind.addView(EditName);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Log.e("Error is here", e.toString());
                    }

                }

            }else if (flag == 3) {
                Config_Engg.toastShow("No Response", ServiceHours.this);
                finish();
            } else {
                if (flag == 4) {

                    Config_Engg.toastShow(msgstatus, ServiceHours.this);
                    Config_Engg.logout(ServiceHours.this);
                    Config_Engg.putSharedPreferences(ServiceHours.this, "checklogin", "status", "2");
                    finish();
                } else if (flag == 5) {

                    ScanckBar();
                    btn_submit.setEnabled(false);
                    btn_clear.setEnabled(false);
                }


            }
            progressDialog.dismiss();
        }
    }

    private class  submitServiceHour extends  AsyncTask<String,String,String>{

        int flag;
        String jsonValue, msg;
        String LoginStatus;
        String invalid = "LoginFailed";
        String valid = "success";
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ServiceHours.this, "Loading...", "Please Wait....", true, false);
            makeJsonMasterJson();
        }

        @Override
        protected String doInBackground(String... strings) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            String AuthCode = Config_Engg.getSharedPreferences(ServiceHours.this, "pref_Engg", "AuthCode", "");
            String EngineerID = Config_Engg.getSharedPreferences(ServiceHours.this, "pref_Engg", "EngineerID", "");
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("MasterJson", masterJson);
            request.addProperty("ServiceHrsDetailJson", serviceHrsDetailJson);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    jsonValue = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msg = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {

                            flag = 4;

                        } else if (LoginStatus.equals(valid)) {

                            flag = 2;
                        }else {
                            flag = 1;
                        }
                    } else {
                        flag = 1;
                    }
                } else {
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag == 1) {
                Config_Engg.toastShow(msg, ServiceHours.this);
            } else {
                if (flag == 2) {
                    Config_Engg.toastShow(msg, ServiceHours.this);
                    Intent intent = new Intent(ServiceHours.this, ServiceHourList.class);
                    intent.putExtra("DateFrom1","");
                    intent.putExtra("DateTo1","");
                    intent.putExtra("PriorityID","0");
                    intent.putExtra("HeaderName","Open");
                    startActivity(intent);
                    finish();
                } else if (flag == 3) {
                    Config_Engg.toastShow("No Response", ServiceHours.this);
                } else if (flag == 4) {
                    Config_Engg.toastShow(msg, ServiceHours.this);
                    Config_Engg.logout(ServiceHours.this);
                    Config_Engg.putSharedPreferences(ServiceHours.this, "checklogin", "status", "2");
                    finish();
                } else if (flag == 5) {
                    ScanckBar();
                    btn_submit.setEnabled(false);
                    btn_clear.setEnabled(false);
                    progressDialog.dismiss();
                }
            }
            progressDialog.dismiss();
            btn_submit.setClickable(true);
        }
    }

    private class FillServiceHourData extends AsyncTask<String, String, String> {

        private String SOAP_ACTION = "http://cfcs.co.in/AppEngineerServiceHrsDetail";
        private String NAMESPACE = "http://cfcs.co.in/";
        private String METHOD_NAME = "AppEngineerServiceHrsDetail";
        private String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

        int flag;
        String msgstatus;
        String service_hour_detail_value;
        ProgressDialog progressDialog;
        String serviceHrsMaster;
        String serviceHrsDetail;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ServiceHours.this, "Loading...", "Please wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            String AuthCode = Config_Engg.getSharedPreferences(ServiceHours.this, "pref_Engg", "AuthCode", "");
            String EngineerID = Config_Engg.getSharedPreferences(ServiceHours.this, "pref_Engg", "EngineerID", "");
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("ServiceHrID", serviceHrID);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    service_hour_detail_value = result.getProperty(0).toString();

                    Object json = new JSONTokener(service_hour_detail_value).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(service_hour_detail_value);
                        JSONArray ServiceHrsMaster = object.getJSONArray("ServiceHrsMaster");
                        serviceHrsMaster = ServiceHrsMaster.toString();
                        JSONArray ServiceHrsDetail = object.getJSONArray("ServiceHrsDetail");
                        serviceHrsDetail = ServiceHrsDetail.toString();
                        if (service_hour_detail_value.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(service_hour_detail_value);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;
                        } else {
                            flag = 2;
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(service_hour_detail_value);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (jsonObject.has("status")) {

                            LoginStatus = jsonObject.getString("status");
                            msgstatus = jsonObject.getString("MsgNotification");
                            if (LoginStatus.equals(invalid)) {

                                flag = 4;
                            } else {

                                flag = 1;
                            }
                        }

                    }

                } else {
                    JSONArray jsonArray = new JSONArray(service_hour_detail_value);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    msgstatus = jsonObject.getString("MsgNotification");
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }
            return service_hour_detail_value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, ServiceHours.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(serviceHrsMaster);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String TodayStatusIDUpdate = jsonObject.getString("TodayStatusID").toString();

                    int index = -1;
                    for (int i = 0; i < todayStatusMasterIDList.size(); i++) {
                        if (todayStatusMasterIDList.get(i).equals(TodayStatusIDUpdate)) {
                            index = i;
                            break;
                        }
                    }

                    if (index > 0) {

                        String customerString = todayStatusMasterNameList.get((int) index);

                        if (!customerString.equalsIgnoreCase("")) {
                            int spinnerpos = spinneradapterTodayStatusMaster.getPosition(customerString);
                            spinner_today_status_master.setSelection(spinnerpos);
                        }
                    }

                    String TomorrowPlanIDUpdate = jsonObject.getString("TomorrowPlanID").toString();

                    int indexTomorrowPlan = -1;
                    for (int i = 0; i < tomorrowPlanMasterIDList.size(); i++) {
                        if (tomorrowPlanMasterIDList.get(i).equals(TomorrowPlanIDUpdate)) {
                            indexTomorrowPlan = i;
                            break;
                        }
                    }

                    if (indexTomorrowPlan > 0) {

                        String PrinicipalString = tomorrowPlanMasterNameList.get((int) indexTomorrowPlan);

                        if (!PrinicipalString.equalsIgnoreCase("")) {
                            int spinnerpos = spinneradaptertomorrowPlanMaster.getPosition(PrinicipalString);
                            spinner_tomorrow_plan_master.setSelection(spinnerpos);
                        }
                    }


                    String ServiceHrDateText = jsonObject.getString("ServiceHrDateText").toString();
                    txt_date.setText(ServiceHrDateText);

                    String UserRemark = jsonObject.getString("UserRemark").toString();
                    txt_any_remark.setText(UserRemark);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {

                    JSONArray jsonArray2 = new JSONArray(serviceHrsDetail);
                    arrayEditText = new ArrayList<EditText>();
                    arrayID = new ArrayList<String>();
                    arrayisRequired = new ArrayList<String>();
                    arrayServiceHrTitleName = new ArrayList<String>();
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        final String serviceHrTitleName = jsonObject2.getString("ServiceHrTitleName");
                        String isRequired = jsonObject2.getString("isRequired");
                        final String serviceHrTitleID = jsonObject2.getString("ServiceHrTitleID");
                        String ServiceHrs = jsonObject2.getString("ServiceHrs");


                        LinearLayout.LayoutParams Textparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                        Textparams.gravity = Gravity.CENTER;
                        Textparams.setMargins(0, 20, 0, 0);


                        EditText EditName = new EditText(ServiceHours.this);
                        EditName.setLayoutParams(Textparams);
                        EditName.setHint(serviceHrTitleName);
                        EditName.setText(ServiceHrs);
                        EditName.setPadding(20,20,0,20);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            EditName.setBackground(getDrawable(R.drawable.edit_text_box_enable_disable));
                        }
                        EditName.setTypeface(null, Typeface.NORMAL);
                        EditName.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        arrayEditText.add(EditName);
                        arrayID.add(serviceHrTitleID);
                        arrayisRequired.add(isRequired);
                        arrayServiceHrTitleName.add(serviceHrTitleName);
                        ll_edit_text_bind.addView(EditName);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }


            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", ServiceHours.this);

            } else if (flag == 4) {
                Config_Engg.toastShow(msgstatus, ServiceHours.this);
                Config_Engg.logout(ServiceHours.this);
                Config_Engg.putSharedPreferences(ServiceHours.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {
                ScanckBar();
                btn_submit.setEnabled(false);
                btn_clear.setEnabled(false);
            }
            progressDialog.dismiss();
        }
    }

    private void ScanckBar() {

        Snackbar snackbar = Snackbar
                .make(maincontainer, "Connectivity issues", Snackbar.LENGTH_LONG)
                .setDuration(60000)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ll_edit_text_bind.removeAllViews();

                            new AddInitialData().execute();
                            btn_submit.setEnabled(true);
                            btn_clear.setEnabled(true);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                Intent intent;
                intent = new Intent(ServiceHours.this, ChangePassword.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:
                Config_Engg.logout(ServiceHours.this);
                finish();
                Config_Engg.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.dashboard:
                intent = new Intent(ServiceHours.this, DashboardActivity.class);
                startActivity(intent);
                finish();

                return (true);
            case R.id.profile:
                intent = new Intent(ServiceHours.this, ProfileUpdate.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise:
                intent = new Intent(ServiceHours.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complain:
                intent = new Intent(ServiceHours.this, ManageComplaint.class);
                startActivity(intent);
                finish();
                return (true);
            case R.id.btn_machines:
                intent = new Intent(ServiceHours.this, ManageMachines.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_contact:
                intent = new Intent(ServiceHours.this, ManageContact.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_service_hour:
                intent = new Intent(ServiceHours.this, ServiceHourList.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(ServiceHours.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.download_file:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.komaxindia.co.in/Engineer/Engineer-User-Manual.pdf"));
                startActivity(browserIntent);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ServiceHours.this, ServiceHourList.class);
        intent.putExtra("DateFrom1","");
        intent.putExtra("DateTo1","");
        intent.putExtra("PriorityID","0");
        intent.putExtra("HeaderName","Open");
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
