package com.cfcs.komaxengineer.activity_engineer;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.adapter.ServiceHourListAdapter;
import com.cfcs.komaxengineer.model.ServiceHourListDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ServiceHourList extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppEngineerServiceHrsList";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppEngineerServiceHrsList";
    private static String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    private static String SOAP_ACTION2 = "http://cfcs.co.in/AppEngineerServiceHrInitialData";
    private static String METHOD_NAME2 = "AppEngineerServiceHrInitialData";

    RecyclerView recycler_view;
    CoordinatorLayout maincontainer;
    FloatingActionButton fab, fab1, fab2;
    LinearLayout fabLayout1, fabLayout2;
    View fabBGLayout;
    boolean isFABOpen = false;
    RecyclerView.LayoutManager mLayoutManager;

    ArrayList<ServiceHourListDataModel> serviceist = new ArrayList<ServiceHourListDataModel>();

    String dateFrom = "", dateTo = "",todayStatus = "";

    AlertDialog dialog;

    Spinner spinner_today_staus;
    TextView txt_service_hour_date_from,txt_service_hourt_date_to;
    Button btn_search_find,btn_search_clear;

    ArrayList<String> todayStatusMasterIDList;
    ArrayList<String> todayStatusMasterNameList;
    ArrayAdapter<String> spinneradapterTodayStatusMaster;

    Calendar myCalendar1;
    Calendar myCalendar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_hour_list);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }


        Config_Engg.getSharedPreferenceRemove(ServiceHourList.this, "pref_Engg", "Service_DateFrom");

        Config_Engg.getSharedPreferenceRemove(ServiceHourList.this, "pref_Engg", "Service_DateUpto");

        Config_Engg.getSharedPreferenceRemove(ServiceHourList.this, "pref_Engg", "TodayStatus");

        recycler_view = findViewById(R.id.recycler_view);
        maincontainer = findViewById(R.id.maincontainer);

        mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        myCalendar1 = Calendar.getInstance();
        myCalendar2 = Calendar.getInstance();

        dateFrom = "";
        dateTo = "";
        todayStatus = "0";

        Config_Engg.isOnline(ServiceHourList.this);
        if (Config_Engg.internetStatus) {

               new ServiceHoursListAsy(dateFrom,dateTo,todayStatus).execute();

               new AddInitialData().execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHourList.this);
        }

        fab = findViewById(R.id.fab);
        fabLayout1 = (LinearLayout) findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) findViewById(R.id.fabLayout2);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fabBGLayout = findViewById(R.id.fabBGLayout);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchPopByFab();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ServiceHourList.this, ServiceHours.class);
                startActivity(i);
            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);

        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));

    }
    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);

                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    private void showSearchPopByFab() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.add_serach_service_hour, null);
        alertDialog.setView(convertView);

        spinner_today_staus = convertView.findViewById(R.id.spinner_today_staus);

        txt_service_hour_date_from = convertView.findViewById(R.id.txt_service_hour_date_from);
        txt_service_hourt_date_to = convertView.findViewById(R.id.txt_service_hourt_date_to);

        btn_search_find = convertView.findViewById(R.id.btn_search_find);
        btn_search_clear = convertView.findViewById(R.id.btn_search_clear);

        dialog = alertDialog.create();

        String ComparedDateFrom = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "Service_DateFrom", "");

        txt_service_hour_date_from.setText(ComparedDateFrom);

        String ComparedDateTo = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "Service_DateUpto", "");

        txt_service_hourt_date_to.setText(ComparedDateTo);

        String ComparedTodayStatus = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "TodayStatus", "");

        spinner_today_staus.setAdapter(spinneradapterTodayStatusMaster);


        if (!ComparedTodayStatus.equalsIgnoreCase("")) {

            int spinnerPosition = spinneradapterTodayStatusMaster.getPosition(ComparedTodayStatus);
            spinner_today_staus.setSelection(spinnerPosition);

        }


        // DatePicker Listener for Date From
        final DatePickerDialog.OnDateSetListener date_from = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar1.set(Calendar.YEAR, year);
                myCalendar1.set(Calendar.MONTH, monthOfYear);
                myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateFrom();
            }

            private void updateDateFrom() {

                Date date1 = new Date(String.valueOf(myCalendar1.getTime()));
                String stringDate = DateFormat.getDateInstance().format(date1);
                txt_service_hour_date_from.setText(stringDate);

            }

        };


        // DatePicker Listener for Date to
        final DatePickerDialog.OnDateSetListener date_to = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateTo();
            }

            private void updateDateTo() {

                Date date1 = new Date(String.valueOf(myCalendar2.getTime()));
                String stringDate = DateFormat.getDateInstance().format(date1);
                txt_service_hourt_date_to.setText(stringDate);

            }

        };


        // DatePicker Dialog Date From
        txt_service_hour_date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                new DatePickerDialog(ServiceHourList.this, android.R.style.Theme_Holo_Dialog, date_from, myCalendar1
                        .get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // DatePicker Dialog Date To
        txt_service_hourt_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                new DatePickerDialog(ServiceHourList.this, android.R.style.Theme_Holo_Dialog, date_to, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        btn_search_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String todayStatus = "0";

                Config_Engg.isOnline(ServiceHourList.this);
                if (Config_Engg.internetStatus) {

                    long statusID = spinner_today_staus.getSelectedItemId();
                    todayStatus = todayStatusMasterIDList.get((int) statusID);

                    if (todayStatus.compareTo("") == 0){

                        todayStatus = "0";
                    }


                    String DateFrom = txt_service_hour_date_from.getText().toString();
                    String DateUpto = txt_service_hourt_date_to.getText().toString();

                    serviceist.clear();

                      Config_Engg.putSharedPreferences(ServiceHourList.this, "pref_Engg", "TodayStatus", spinner_today_staus.getSelectedItem().toString());

                      Config_Engg.putSharedPreferences(ServiceHourList.this, "pref_Engg", "Service_DateFrom", DateFrom);

                      Config_Engg.putSharedPreferences(ServiceHourList.this, "pref_Engg", "Service_DateUpto", DateUpto);

                    dialog.dismiss();

                    new ServiceHoursListAsy(DateFrom,DateUpto,todayStatus).execute();

                    closeFABMenu();

                } else {
                    Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHourList.this);
                }

            }
        });


        btn_search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config_Engg.getSharedPreferenceRemove(ServiceHourList.this, "pref_Engg", "Service_DateFrom");

                Config_Engg.getSharedPreferenceRemove(ServiceHourList.this, "pref_Engg", "Service_DateUpto");

                Config_Engg.getSharedPreferenceRemove(ServiceHourList.this, "pref_Engg", "TodayStatus");

                Config_Engg.isOnline(ServiceHourList.this);
                if (Config_Engg.internetStatus) {

                    dialog.dismiss();
                    new ServiceHoursListAsy(dateFrom,dateTo,todayStatus).execute();
                    closeFABMenu();
                } else {
                    Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHourList.this);
                }

            }
        });


        dialog.show();
    }
    private class ServiceHoursListAsy extends AsyncTask<String,String,String>{

        ProgressDialog progressDialog;
        String serviceHourList;
        String LoginStatus;
        String invalid = "LoginFailed";
        int flag;
        String msgstatus;
        String DateFrom = "",DateTo = "", TodayStatus = "";

        public ServiceHoursListAsy(String dateFrom, String dateTo, String todayStatus) {
            this.DateFrom = dateFrom;
            this.DateTo = dateTo;
            this.TodayStatus = todayStatus;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ServiceHourList.this, "Loading", "Please Wait...", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String EngineerID = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            request.addProperty("DateFrom", DateFrom);
            request.addProperty("DateUpto", DateTo);
            request.addProperty("TodayStatusID", TodayStatus);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    serviceHourList = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(serviceHourList);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {
                            flag = 4;
                        } else {
                            flag = 1;
                        }
                    } else {

                           serviceist.clear();


                        for (int i = 0; i < jsonArray.length(); i++) {


                            try {
                                JSONObject jsonObject1 = jsonArray
                                        .getJSONObject(i);

                                ServiceHourListDataModel serviceHourListDataModel = new ServiceHourListDataModel(AuthCode, AuthCode, AuthCode,
                                        AuthCode, AuthCode, AuthCode);
                                serviceHourListDataModel.setServiceHrID(jsonObject1.getString("ServiceHrID").toString());
                                serviceHourListDataModel.setEngineerID(jsonObject1.getString("EngineerID").toString());
                                serviceHourListDataModel.setServiceHrDate(jsonObject1.getString("ServiceHrDateText").toString());
                                serviceHourListDataModel.setTodayStatusName(jsonObject1.getString("TodayStatusName").toString());
                                serviceHourListDataModel.setTomorrowPlanName(jsonObject1.getString("TomorrowPlanName").toString());
                                serviceHourListDataModel.setTotalHrs(jsonObject1.getString("TotalHrs").toString());


                                // Add this object into the ArrayList myList

                                serviceist.add(serviceHourListDataModel);
                                flag = 2;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    flag = 3;
                    // finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error is 1 ", e.toString());
                flag = 5;
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (flag == 1) {

                Config_Engg.toastShow(msgstatus, ServiceHourList.this);
                //  progressDialog.dismiss();
                recycler_view.setAdapter(null);

            } else {
                if (flag == 2) {

                    recycler_view.setAdapter(new ServiceHourListAdapter(ServiceHourList.this, serviceist));

                } else {
                    if (flag == 3) {
                        Config_Engg.toastShow("No Response", ServiceHourList.this);
                        //    progressDialog.dismiss();
                    } else {
                        if (flag == 4) {

                            Config_Engg.toastShow(msgstatus, ServiceHourList.this);
                            Config_Engg.logout(ServiceHourList.this);
                            Config_Engg.putSharedPreferences(ServiceHourList.this, "checklogin", "status", "2");
                            finish();
                        } else if (flag == 5) {

                            ScanckBar();
                            recycler_view.setAdapter(null);
                            progressDialog.dismiss();

                        }


                    }
                }
            }
            progressDialog.dismiss();
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
            progressDialog = ProgressDialog.show(ServiceHourList.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            String EngineerID = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(ServiceHourList.this, "pref_Engg", "AuthCode", "");
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    initialData = result.getProperty(0).toString();
                    Object json = new JSONTokener(initialData).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(initialData);
                        JSONArray probleTitleArray = object.getJSONArray("TodayStatusMaster");
                        todayStatusMaster = probleTitleArray.toString();

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
                Config_Engg.toastShow(msgstatus, ServiceHourList.this);
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

                    spinneradapterTodayStatusMaster = new ArrayAdapter<String>(ServiceHourList.this, android.R.layout.simple_spinner_item, todayStatusMasterNameList);
                    spinneradapterTodayStatusMaster.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }else if (flag == 3) {
                Config_Engg.toastShow("No Response", ServiceHourList.this);
                finish();
            } else {
                if (flag == 4) {

                    Config_Engg.toastShow(msgstatus, ServiceHourList.this);
                    Config_Engg.logout(ServiceHourList.this);
                    Config_Engg.putSharedPreferences(ServiceHourList.this, "checklogin", "status", "2");
                    finish();
                } else if (flag == 5) {

                    ScanckBar();

                }


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
                        Config_Engg.isOnline(ServiceHourList.this);
                        if (Config_Engg.internetStatus) {

                      //      new MachineListAsy(zoneID, customerID, siteID, modelID, transactionType, approveStatusID, SearchStr).execute();
                     //    new SetdataInCustomerSpinner().execute();

                        } else {
                            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHourList.this);
                        }


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
                intent = new Intent(ServiceHourList.this, ChangePassword.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:
                Config_Engg.logout(ServiceHourList.this);
                finish();
                Config_Engg.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.dashboard:
                intent = new Intent(ServiceHourList.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ServiceHourList.this, ProfileUpdate.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise:
                intent = new Intent(ServiceHourList.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complain:
                intent = new Intent(ServiceHourList.this, ManageComplaint.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines:
                intent = new Intent(ServiceHourList.this, ManageMachines.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_contact:
                intent = new Intent(ServiceHourList.this, ManageContact.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_service_hour:
                intent = new Intent(ServiceHourList.this, ServiceHourList.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(ServiceHourList.this, FeedbackActivity.class);
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
        Intent intent = new Intent(ServiceHourList.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
