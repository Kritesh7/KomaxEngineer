package com.cfcs.komaxengineer.activity_engineer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.model.SparePartListDataModel;
import com.cfcs.komaxengineer.utils.SimpleSpanBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;

import java.util.Objects;
import java.util.regex.Pattern;


public class AddDailyReport extends AppCompatActivity {

    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    String SOAP_ACTION2 = "http://cfcs.co.in/AppEngineerDailyReportdetails";
    String METHOD_NAME2 = "AppEngineerDailyReportdetails";

    private static String SOAP_ACTION3 = "http://cfcs.co.in/AppEngineerContactChange";
    private static String METHOD_NAME3 = "AppEngineerContactChange";

    private static String SOAP_ACTION4 = "http://cfcs.co.in/ddlCustomerContactPersonByComplainNo";
    private static String METHOD_NAME4 = "ddlCustomerContactPersonByComplainNo";

    String complainno;
    TextView txt_complaint_no, txt_header, txt_next_follow_up;
    EditText txt_service, txt_travel, txt_work_done_plant, txt_work_detail, txt_suggestion,
            txt_reason_close, txt_country_code, txt_add_service_charge, txt_engg_travel_cost, txt_engg_other_exp, txt_add_customer_remark, txt_engg_exp_detail, txt_sign_by_name, txt_sign_by_mobile, txt_sign_by_email;
    Button btn_spare_search, btn_clear, btn_preview;
    ListView listView_sparePart;
    String searchSparePart;
    LinearLayout llSparePartsLayout;
    private LinearLayout llSpareParts;

    ArrayList<SparePartListDataModel> addedPart = new ArrayList<SparePartListDataModel>();
    ArrayList<String> editTextQty;

    File file;

    InputFilter timeFilter, timeFilterTravel;

    String WorkDonePlant, WorkDetails, Suggestion, NextFollowUp = "", ReasonForNotClose, ServiceTime, TravelTime,
            EngieerOtherExpense, EngieerExpenseDetails, SignByName, SignByMobileNo, SignByE_mailid, CustomerRemark, CountryCode;
    String EngieerTravelCost = "00";
    String ServiceCharge = "00";

    int currentapiVersion = 0;
    EditText edQuantity;
    String[] edtQytArray;
    String sparePartJson = "";
    String date_time1;

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    Calendar c;

    List<EditText> allEds;

    String ReportNo = "0";
    String reportMode = "";

    LinearLayout maincontainer;

    TextView tv_observation, tv_action, tv_service_time, tv_travel_time, tv_sign_by_name;

    Spinner spinner_existing_customer_contacts;

    List<String> contactIDList;
    List<String> contactNameList;

    ArrayAdapter<String> spinneradapterContact;

    int checkConatct = 0;

    String SelectedContactID;

    String SignByContactIDUpdate = "";

    String SignByNameUpdate = "";

    String SignByMobileUpdate = "";

    String SignByMailIDUpdate = "";

    String CustomerRemarkUpdate = "";

    String CountryCodeUpdate = "";

    String ComplainByContactID = "00000000-0000-0000-0000-000000000000";

    String SelectedSignByContactID;

    boolean changing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_report);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        c = Calendar.getInstance();

        editTextQty = new ArrayList<String>();

        file = new File("");


        tv_observation = findViewById(R.id.tv_observation);
        tv_action = findViewById(R.id.tv_action);
        tv_service_time = findViewById(R.id.tv_service_time);
        tv_travel_time = findViewById(R.id.tv_travel_time);
        tv_sign_by_name = findViewById(R.id.tv_sign_by_name);
        spinner_existing_customer_contacts = findViewById(R.id.spinner_existing_customer_contacts);

        SimpleSpanBuilder ssbObservation = new SimpleSpanBuilder();
        ssbObservation.appendWithSpace("Observation");
        ssbObservation.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_observation.setText(ssbObservation.build());

        SimpleSpanBuilder ssbAction = new SimpleSpanBuilder();
        ssbAction.appendWithSpace("Action Taken");
        ssbAction.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_action.setText(ssbAction.build());

        SimpleSpanBuilder ssbServiceTime = new SimpleSpanBuilder();
        ssbServiceTime.appendWithSpace("Service Time");
        ssbServiceTime.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_service_time.setText(ssbServiceTime.build());

        SimpleSpanBuilder ssbTravelTime = new SimpleSpanBuilder();
        ssbTravelTime.appendWithSpace("Travel Time");
        ssbTravelTime.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_travel_time.setText(ssbTravelTime.build());

        SimpleSpanBuilder ssbSignByName = new SimpleSpanBuilder();
        ssbSignByName.appendWithSpace("Sign By Name");
        ssbSignByName.append("*", new ForegroundColorSpan(Color.RED), new RelativeSizeSpan(1));
        tv_sign_by_name.setText(ssbSignByName.build());


        txt_complaint_no = findViewById(R.id.txt_complaint_no);
        txt_next_follow_up = findViewById(R.id.txt_next_follow_up);
        txt_service = findViewById(R.id.txt_service);
        txt_travel = findViewById(R.id.txt_travel);

        btn_spare_search = findViewById(R.id.btn_spare_search);
        llSparePartsLayout = (LinearLayout) findViewById(R.id.llSparePartsLayout);
        llSpareParts = (LinearLayout) findViewById(R.id.llSpareParts);
        txt_work_done_plant = findViewById(R.id.txt_work_done_plant);
        txt_work_detail = findViewById(R.id.txt_work_detail);
        txt_suggestion = findViewById(R.id.txt_suggestion);
        txt_reason_close = findViewById(R.id.txt_reason_close);
        txt_engg_travel_cost = findViewById(R.id.txt_engg_travel_cost);
        txt_engg_other_exp = findViewById(R.id.txt_engg_other_exp);
        txt_engg_exp_detail = findViewById(R.id.txt_engg_exp_detail);
        txt_sign_by_name = findViewById(R.id.txt_sign_by_name);
        txt_sign_by_mobile = findViewById(R.id.txt_sign_by_mobile);
        txt_sign_by_email = findViewById(R.id.txt_sign_by_email);
        txt_header = findViewById(R.id.txt_header);
        txt_add_service_charge = findViewById(R.id.txt_add_service_charge);
        txt_country_code = findViewById(R.id.txt_country_code);


        btn_preview = findViewById(R.id.btn_preview);
        btn_clear = findViewById(R.id.btn_clear);

        maincontainer = findViewById(R.id.maincontainer);

        currentapiVersion = Build.VERSION.SDK_INT;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            complainno = getIntent().getExtras().getString("ComplainNo");
            reportMode = getIntent().getExtras().getString("IsEditCheck");
            ReportNo = getIntent().getExtras().getString("DailyReportNo");
        }

        txt_complaint_no.setText(complainno);

        txt_header.setText("Add Daily Report");

        if (reportMode.compareTo("true") == 0) {
            txt_header.setText("Update Daily Report");
            new DailyReportDetailAsy().execute();
        }

        Config_Engg.isOnline(AddDailyReport.this);
        if (Config_Engg.internetStatus) {

            new AddContactPersonDropDown().execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", AddDailyReport.this);
        }


        txt_country_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (!changing && txt_country_code.getText().toString().startsWith("0")) {
                    changing = true;
                    txt_country_code.setText(txt_country_code.getText().toString().replace("0", ""));
                }
                changing = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });


        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_work_done_plant.setText("");
                txt_work_detail.setText("");
                txt_suggestion.setText("");
                txt_next_follow_up.setText("");
                txt_reason_close.setText("");
                txt_add_service_charge.setText("");
                txt_engg_travel_cost.setText("");
                txt_service.setText("");
                txt_travel.setText("");
                txt_engg_other_exp.setText("");
                txt_engg_exp_detail.setText("");
                txt_sign_by_name.setText("");
                txt_sign_by_mobile.setText("");
                txt_sign_by_email.setText("");
                if (allEds != null) {
                    allEds.clear();
                }
                if (addedPart != null) {
                    addedPart.clear();
                }
                llSparePartsLayout.setVisibility(View.GONE);
                txt_work_done_plant.requestFocus();

            }
        });


        txt_work_done_plant.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (txt_work_done_plant.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        txt_work_detail.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (txt_work_detail.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        txt_suggestion.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (txt_suggestion.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });


        txt_reason_close.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (txt_reason_close.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });


        txt_next_follow_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        btn_spare_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShowpopSpareSearch();
            }
        });


        timeFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {


                if (source.length() == 0) {
                    return null;// deleting, keep original editing
                }
                String result = "";
                result += dest.toString().substring(0, dstart);
                result += source.toString().substring(start, end);
                result += dest.toString().substring(dend, dest.length());

                boolean allowEdit = true;
                char c;
                if (result.length() > 0) {
                    c = result.charAt(0);
                    allowEdit &= (c >= '0' && c <= '2');
                }
                if (result.length() > 1) {
                    c = result.charAt(1);
                    if (result.charAt(0) == '0' || result.charAt(0) == '1') {
                        allowEdit &= (c >= '0' && c <= '9');
                    } else {
                        allowEdit &= (c >= '0' && c <= '3');
                        if (result.charAt(1) != '0' && result.charAt(1) != '1' && result.charAt(1) != '2' && result.charAt(1) != '3') {

                            return allowEdit ? null : "";

                        }
                    }
                }
                if (result.length() == 2) {
                    //  c = result.charAt(2);
                    result += ":";
                    txt_service.setText(result);
                    txt_service.setSelection(result.length());

                }
                if (result.length() > 5) {
                    txt_travel.requestFocus();
                    return "";// do not allow this edit
                }

                if (result.length() > 3) {
                    c = result.charAt(3);
                    allowEdit &= (c >= '0' && c <= '5');

                }
                if (result.length() > 4) {
                    c = result.charAt(4);
                    allowEdit &= (c >= '0' && c <= '9');
                }

                return allowEdit ? null : "";
            }

        };

        timeFilterTravel = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                if (source.length() == 0) {
                    return null;// deleting, keep original editing
                }
                String result = "";
                result += dest.toString().substring(0, dstart);
                result += source.toString().substring(start, end);
                result += dest.toString().substring(dend, dest.length());

                boolean allowEdit = true;
                char c;
                if (result.length() > 0) {
                    c = result.charAt(0);
                    allowEdit &= (c >= '0' && c <= '2');
                }
                if (result.length() > 1) {
                    c = result.charAt(1);
                    if (result.charAt(0) == '0' || result.charAt(0) == '1') {
                        allowEdit &= (c >= '0' && c <= '9');
                    } else {
                        allowEdit &= (c >= '0' && c <= '3');
                        if (result.charAt(1) != '0' && result.charAt(1) != '1' && result.charAt(1) != '2' && result.charAt(1) != '3') {

                            return allowEdit ? null : "";

                        }
                    }
                }
                if (result.length() == 2) {
                    //  c = result.charAt(2);
                    result += ":";
                    txt_travel.setText(result);
                    txt_travel.setSelection(result.length());

                }
                if (result.length() > 5) {
                    //  txt_travel.requestFocus();
                    return "";// do not allow this edit
                }

                if (result.length() > 3) {
                    c = result.charAt(3);
                    allowEdit &= (c >= '0' && c <= '5');

                }
                if (result.length() > 4) {
                    c = result.charAt(4);
                    allowEdit &= (c >= '0' && c <= '9');
                }

                return allowEdit ? null : "";
            }

        };


        txt_service.setFilters(new InputFilter[]{timeFilter});
        txt_travel.setFilters(new InputFilter[]{timeFilterTravel});

        spinner_existing_customer_contacts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                checkConatct += 1;
                Log.i("kritesh", String.valueOf(checkConatct));
                Config_Engg.isOnline(AddDailyReport.this);
                if (Config_Engg.internetStatus) {

                    long SelectedContact = parent.getSelectedItemId();
                    SelectedContactID = contactIDList.get((int) SelectedContact);

                    if (checkConatct == 1 && reportMode.compareTo("true") == 0) {

                        txt_sign_by_name.setText(SignByNameUpdate);

                        txt_sign_by_name.setEnabled(true);

                        txt_sign_by_mobile.setText(SignByMobileUpdate);

                        txt_country_code.setText(CountryCodeUpdate);

                        txt_sign_by_email.setText(SignByMailIDUpdate);


                    }
                    if (checkConatct > 1 && reportMode.compareTo("true") == 0) {

                        if (SelectedContact != 0) {

                            new AddContactChange().execute();

                        } else {

                            txt_sign_by_name.setText("");
                            txt_sign_by_name.setEnabled(true);
                            txt_sign_by_mobile.setText("");
                            txt_sign_by_email.setText("");

                            txt_country_code.setText("");
                        }

                    } else {

                        if (SelectedContact != 0 && reportMode.compareTo("true") != 0) {

                            new AddContactChange().execute();

                        } else if (reportMode.compareTo("true") != 0) {
                            txt_sign_by_name.setText("");
                            txt_sign_by_name.setEnabled(true);
                            txt_sign_by_mobile.setText("");
                            txt_sign_by_email.setText("");
                            txt_country_code.setText("");
                        }

                    }

                } else {
                    Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", AddDailyReport.this);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config_Engg.isOnline(AddDailyReport.this);
                if (Config_Engg.internetStatus) {

                    if (allEds != null) {
                        edtQytArray = new String[allEds.size()];
                    }
                    if (allEds != null) {
                        if (allEds.size() > 0) {

                            for (int i = 0; i < allEds.size(); i++) {
                                edtQytArray[i] = allEds.get(i).getText().toString();
                            }
                        }
                    }

                    if (addedPart.size() > 0) {
                        makeJson();
                    } else {
                        sparePartJson = "";
                    }

                    String workDonePlant = txt_work_done_plant.getText().toString().trim();
                    String workDetal = txt_work_detail.getText().toString().trim();
                    String checkmobile = txt_sign_by_mobile.getText().toString().trim();
                    String code = txt_country_code.getText().toString().trim();
                    if (workDonePlant.compareTo("") == 0) {
                        Config_Engg.alertBox("Please Enter Observation ",
                                AddDailyReport.this);
                        txt_work_done_plant.requestFocus();
                        //focusOnView();
                    } else if (workDetal.compareTo("") == 0) {
                        Config_Engg.alertBox("Please Enter Action Taken ",
                                AddDailyReport.this);
                        txt_work_detail.requestFocus();
                    } else if (txt_service.getText().toString().length() < 5) {
                        Config_Engg.alertBox("Please Enter Service Time hh : mm correct formate",
                                AddDailyReport.this);
                        txt_service.requestFocus();
                    } else if (txt_travel.getText().toString().length() < 5) {
                        Config_Engg.alertBox("Please Enter Travel Time hh : mm correct formate",
                                AddDailyReport.this);
                        txt_travel.requestFocus();
                    } else if (checkmobile.compareTo("") != 0 || code.compareTo("") != 0) {

                        if (checkmobile.compareTo("") != 0 && !isValidMobile(txt_sign_by_mobile.getText().toString().trim())) {
                            Config_Engg.alertBox("Please Enter Vaild Mobile No.",
                                    AddDailyReport.this);
                            txt_sign_by_mobile.requestFocus();
                        } else if (checkmobile.compareTo("") == 0) {
                            Config_Engg.alertBox("Please Enter Mobile No.", AddDailyReport.this);
                            txt_sign_by_mobile.requestFocus();
                        } else if (code.compareTo("") == 0) {
                            Config_Engg.alertBox("Please Enter Country Code", AddDailyReport.this);
                            txt_country_code.requestFocus();
                        } else {


                            long SelectedContactId = spinner_existing_customer_contacts.getSelectedItemId();
                            SelectedSignByContactID = contactIDList.get((int) SelectedContactId);

                            WorkDonePlant = txt_work_done_plant.getText().toString().trim();
                            WorkDetails = txt_work_detail.getText().toString().trim();
                            Suggestion = txt_suggestion.getText().toString().trim();
                            NextFollowUp = txt_next_follow_up.getText().toString().trim();
                            ReasonForNotClose = txt_reason_close.getText().toString().trim();
                            EngieerTravelCost = txt_engg_travel_cost.getText().toString().trim();
                            ServiceTime = txt_service.getText().toString().trim();
                            TravelTime = txt_travel.getText().toString().trim();
                            EngieerOtherExpense = txt_engg_other_exp.getText().toString().trim();
                            EngieerExpenseDetails = txt_engg_exp_detail.getText().toString().trim();
                            SignByName = txt_sign_by_name.getText().toString().trim();
                            SignByMobileNo = txt_sign_by_mobile.getText().toString().trim();
                            SignByE_mailid = txt_sign_by_email.getText().toString().trim();
                            ServiceCharge = txt_add_service_charge.getText().toString().trim();
                            CountryCode = txt_country_code.getText().toString().toString().trim();


                            Intent i = new Intent(AddDailyReport.this, AddDailyReportPreview.class);
                            i.putExtra("ComplainNo", complainno);
                            i.putExtra("WorkDonePlant", WorkDonePlant);
                            i.putExtra("WorkDetails", WorkDetails);
                            i.putExtra("Suggestion", Suggestion);
                            i.putExtra("NextFollowUp", NextFollowUp);
                            i.putExtra("ReasonForNotClose", ReasonForNotClose);
                            i.putExtra("EngieerTravelCost", EngieerTravelCost);
                            i.putExtra("ServiceTime", ServiceTime);
                            i.putExtra("TravelTime", TravelTime);
                            i.putExtra("EngieerOtherExpense", EngieerOtherExpense);
                            i.putExtra("EngieerExpenseDetails", EngieerExpenseDetails);
                            i.putExtra("SignByName", SignByName);
                            i.putExtra("SignByMobileNo", SignByMobileNo);
                            i.putExtra("SignByE_mailid", SignByE_mailid);
                            i.putExtra("ServiceCharge", ServiceCharge);
                            i.putExtra("CountryCode", CountryCode);
                            i.putExtra("sparePartJson", sparePartJson);
                            i.putExtra("SelectedSignByContactID", SelectedSignByContactID);
                            i.putExtra("reportMode", reportMode);
                            i.putExtra("ReportNo", ReportNo);
                            if (reportMode.compareTo("true") == 0) {
                                i.putExtra("CustomerRemarkUpdate", CustomerRemarkUpdate);
                            }
                            startActivity(i);

                        }
                    } else {


                        long SelectedContactId = spinner_existing_customer_contacts.getSelectedItemId();
                        SelectedSignByContactID = contactIDList.get((int) SelectedContactId);

                        WorkDonePlant = txt_work_done_plant.getText().toString().trim();
                        WorkDetails = txt_work_detail.getText().toString().trim();
                        Suggestion = txt_suggestion.getText().toString().trim();
                        NextFollowUp = txt_next_follow_up.getText().toString();
                        ReasonForNotClose = txt_reason_close.getText().toString().trim();
                        EngieerTravelCost = txt_engg_travel_cost.getText().toString().trim();
                        ServiceTime = txt_service.getText().toString().trim();
                        TravelTime = txt_travel.getText().toString().trim();
                        EngieerOtherExpense = txt_engg_other_exp.getText().toString().trim();
                        EngieerExpenseDetails = txt_engg_exp_detail.getText().toString().trim();
                        SignByName = txt_sign_by_name.getText().toString().trim();
                        SignByMobileNo = txt_sign_by_mobile.getText().toString().trim();
                        SignByE_mailid = txt_sign_by_email.getText().toString().trim();
                        ServiceCharge = txt_add_service_charge.getText().toString().trim();
                        CountryCode = txt_country_code.getText().toString().toString().trim();


                        Intent i = new Intent(AddDailyReport.this, AddDailyReportPreview.class);
                        i.putExtra("ComplainNo", complainno);
                        i.putExtra("WorkDonePlant", WorkDonePlant);
                        i.putExtra("WorkDetails", WorkDetails);
                        i.putExtra("Suggestion", Suggestion);
                        i.putExtra("NextFollowUp", NextFollowUp);
                        i.putExtra("ReasonForNotClose", ReasonForNotClose);
                        i.putExtra("EngieerTravelCost", EngieerTravelCost);
                        i.putExtra("ServiceTime", ServiceTime);
                        i.putExtra("TravelTime", TravelTime);
                        i.putExtra("EngieerOtherExpense", EngieerOtherExpense);
                        i.putExtra("EngieerExpenseDetails", EngieerExpenseDetails);
                        i.putExtra("SignByName", SignByName);
                        i.putExtra("SignByMobileNo", SignByMobileNo);
                        i.putExtra("SignByE_mailid", SignByE_mailid);
                        i.putExtra("ServiceCharge", ServiceCharge);
                        i.putExtra("CountryCode", CountryCode);
                        i.putExtra("sparePartJson", sparePartJson);
                        i.putExtra("SelectedSignByContactID", SelectedSignByContactID);
                        i.putExtra("reportMode", reportMode);
                        i.putExtra("ReportNo", ReportNo);
                        if (reportMode.compareTo("true") == 0) {
                            i.putExtra("CustomerRemarkUpdate", CustomerRemarkUpdate);
                        }
                        startActivity(i);
                    }

                } else {
                    Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", AddDailyReport.this);
                }


            }
        });


    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() != 10) {
                // if(phone.length() != 10) {
                check = false;
                //       Config_Engg.toastShow("Not Valid Number",RaiseComplaintActivity.this);
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    private class DailyReportDetailAsy extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String dailyReport_detail_value, DailyReportdetail;
        String SparesConsumed;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AddDailyReport.this, "Loading...", "Please Wait....", true, false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String EngineerID = Config_Engg.getSharedPreferences(AddDailyReport.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(AddDailyReport.this, "pref_Engg", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("DailyReportNo", ReportNo);
            request.addProperty("AuthCode", AuthCode);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION2, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    dailyReport_detail_value = result.getProperty(0).toString();

                    Object json = new JSONTokener(dailyReport_detail_value).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(dailyReport_detail_value);
                        JSONArray dailyReportdetail = object.getJSONArray("DailyReportdetail");
                        DailyReportdetail = dailyReportdetail.toString();
                        JSONArray sparesConsumed = object.getJSONArray("SparesConsumed");
                        SparesConsumed = sparesConsumed.toString();
                        if (dailyReport_detail_value.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(dailyReport_detail_value);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;
                        } else {
                            flag = 2;
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(dailyReport_detail_value);
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
                    JSONArray jsonArray = new JSONArray(dailyReport_detail_value);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(DailyReportdetail);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String Workdone = jsonObject.getString("Workdone").toString();
                    txt_work_done_plant.setText(Workdone);

                    String WorkDetail = jsonObject.getString("WorkDetail").toString();
                    txt_work_detail.setText(WorkDetail);

                    String Suggestion = jsonObject.getString("Suggestion").toString();
                    txt_suggestion.setText(Suggestion);


                    String NextFollowUpTimeText = jsonObject.getString("NextFollowUpTimeText").toString();
                    String NextFollowUpDateText = jsonObject.getString("NextFollowUpDateText").toString();
                    txt_next_follow_up.setText(NextFollowUpDateText + " " + NextFollowUpTimeText);


                    String ReasonForNotClose = jsonObject.getString("ReasonForNotClose").toString();
                    txt_reason_close.setText(ReasonForNotClose);


                    String EngineerTravelExpense = jsonObject.getString("EngineerTravelExpense").toString();
                    txt_engg_travel_cost.setText(EngineerTravelExpense);


                    String EngineerOtherExpense = jsonObject.getString("EngineerOtherExpense").toString();
                    txt_engg_other_exp.setText(EngineerOtherExpense);

                    String Traveltime = firstTwoTravel(jsonObject.getString("Traveltime").toString());
                    txt_travel.setText(Traveltime);

                    String Servicetime = firstTwoService(jsonObject.getString("Servicetime").toString());
                    txt_service.setText(Servicetime);

                    String ExpenseDetail = jsonObject.getString("ExpenseDetail").toString();
                    txt_engg_exp_detail.setText(ExpenseDetail);

                    SignByContactIDUpdate = jsonObject.getString("SignByContactID").toString();

                    SignByNameUpdate = jsonObject.getString("SignByName").toString();
                    //   txt_sign_by_name.setText(SignByNameUpdate);

                    SignByMobileUpdate = jsonObject.getString("SignByMobile").toString();
                    //   txt_sign_by_mobile.setText(SignByMobileUpdate);

                    SignByMailIDUpdate = jsonObject.getString("SignByMailID").toString();
                    // txt_sign_by_email.setText(SignByMailIDUpdate);

                    CustomerRemarkUpdate = jsonObject.getString("CustomerRemark").toString();
                    //   txt_add_customer_remark.setText(CustomerRemark);

                    CountryCodeUpdate = jsonObject.getString("CountryCode").toString();

                    String ServiceCharge = jsonObject.getString("ServiceChargeAmount").toString();
                    txt_add_service_charge.setText(ServiceCharge);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    JSONArray jsonArray = new JSONArray(SparesConsumed);
                    //  JSONObject jsonObject = jsonArray.getJSONObject(0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        SparePartListDataModel sparePartListDataModel = new SparePartListDataModel();
                        sparePartListDataModel.setSpareID(jsonObject2.getString("SpareID"));
                        sparePartListDataModel.setSparePartNo(jsonObject2.getString("PartNo"));
                        sparePartListDataModel.setSpareDesc(jsonObject2.getString("SpareDesc"));
                        sparePartListDataModel.setQuantity(jsonObject2.getString("SpareQuantity"));

                        addedPart.add(sparePartListDataModel);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("addedPart size ", " cfcs " + addedPart.size());
                llSparePartsLayout.removeAllViews();

                allEds = new ArrayList<EditText>();

                if (addedPart.size() > 0) {
                    final String partID = "";
                    String partName = "";
                    llSpareParts.setVisibility(View.VISIBLE);
                    for (int i = 0; i < addedPart.size(); i++) {
                        final String pID = addedPart.get(i).getSpareID();
                        final String pName = addedPart.get(i).getSparePartNo();
                        final String quant = addedPart.get(i).getQuantity();

                        LinearLayout.LayoutParams paramtest = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                0.33f
                        );

                        final LinearLayout linearLayout = new LinearLayout(AddDailyReport.this);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                        final TextView tvSparepart = new TextView(AddDailyReport.this);
                        tvSparepart.setTextColor(Color.BLACK);
                        tvSparepart.setLayoutParams(paramtest);
                        tvSparepart.setGravity(Gravity.CENTER);
                        tvSparepart.setPadding(0, 2, 0, 0);

                        Log.e("pName ", "cfcs " + pName);
                        tvSparepart.setText(" " + pName);

                        edQuantity = new EditText(AddDailyReport.this);
                        edQuantity.setHint("Qty");
                        edQuantity.setTextColor(Color.BLACK);
                        edQuantity.setLayoutParams(paramtest);
                        edQuantity.setGravity(Gravity.CENTER);
                        edQuantity.setTextSize(12);
                        edQuantity.setPadding(0, 0, 0, 0);
                        edQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                        edQuantity.setText(quant);


                        ImageView imageView = new ImageView(AddDailyReport.this);
                        imageView.setImageResource(R.drawable.ic_delete);
                        imageView.setLayoutParams(paramtest);


                        linearLayout.addView(tvSparepart);
                        linearLayout.addView(edQuantity);
                        linearLayout.addView(imageView);
                        llSparePartsLayout.addView(linearLayout);
                        llSparePartsLayout.setVisibility(View.VISIBLE);
                        partName = partName + ", " + pName;

                        allEds.add(edQuantity);

                        txt_work_done_plant.requestFocus();

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder altDialog = new AlertDialog.Builder(AddDailyReport.this);
                                altDialog.setMessage("Do You Want to delete !!");

                                altDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        for (int i = 0; i < addedPart.size(); i++) {
                                            if (pID.compareTo(addedPart.get(i).getSpareID()) == 0) {
                                                addedPart.remove(i);
                                                allEds.remove(i);
                                            }
                                        }

                                        linearLayout.setVisibility(View.GONE);
                                        if (addedPart.size() > 0) {
                                            llSpareParts.setVisibility(View.VISIBLE);
                                        } else {
                                            llSpareParts.setVisibility(View.GONE);
                                        }
                                    }
                                });

                                altDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                altDialog.show();
                            }
                        });
                    }

                }


            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", AddDailyReport.this);

            } else if (flag == 4) {

                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
                Config_Engg.logout(AddDailyReport.this);
                Config_Engg.putSharedPreferences(AddDailyReport.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {

                Snackbar snackbar = Snackbar
                        .make(maincontainer, "connectivity issues", Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }
            progressDialog.dismiss();

        }

        private String firstTwoService(String servicetime) {
            return servicetime.length() < 5 ? servicetime : servicetime.substring(0, 5);
        }

        private String firstTwoTravel(String traveltime) {
            return traveltime.length() < 5 ? traveltime : traveltime.substring(0, 5);
        }
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

                        Calendar calendar2 = Calendar.getInstance();
                        // calendar2.add(Calendar.DATE, -1);
                        Date today = calendar2.getTime();
                        String todayDate = DateFormat.getDateInstance().format(today);
                        Date currentDate = new Date();
                        Date selectedNextDate = new Date();

                        try {
                            currentDate = DateFormat.getDateInstance().parse(todayDate);
                            selectedNextDate = DateFormat.getDateInstance().parse(date_time);
                            if (selectedNextDate.after(currentDate)) {
                                tiemPicker(date_time);
                            } else {
                                txt_next_follow_up.setText("");
                                Config_Engg.alertBox("Please select correct date after today ", AddDailyReport.this);
                            }
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }, this.mYear, mMonth, mDay);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getString(R.string.clear), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // todo on click

                        txt_next_follow_up.setText("");

                    }
                });

        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    private void tiemPicker(String date_time) {
        this.date_time1 = date_time;
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        int hour = hourOfDay;
                        int minutes = minute;


                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12) {
                            timeSet = "PM";
                        } else {
                            timeSet = "AM";
                        }

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes;
                        else
                            min = String.valueOf(minutes);

                        // Append in a StringBuilder
                        String aTime = new StringBuilder().append(hour).append(':')
                                .append(min).append(" ").append(timeSet).toString();

                        txt_next_follow_up.setText(date_time1 + " " + aTime);


                    }
                }, 0, 0, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private class ShowpopSpareSearch {

        {

            final EditText txt_search_sparePart;
            Button btn_search;
            ImageView imv_closed;
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddDailyReport.this);
            LayoutInflater inflater = getLayoutInflater();
            final View convertView = (View) inflater.inflate(R.layout.spare_layout_request, null);
            alertDialog.setView(convertView);

            txt_search_sparePart = (EditText) convertView.findViewById(R.id.txt_search_sparePart);
            btn_search = (Button) convertView.findViewById(R.id.btn_search);
//            imv_closed = convertView.findViewById(R.id.imv_closed);

            listView_sparePart = (ListView) convertView.findViewById(R.id.listView_sparePart);


            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    searchSparePart = txt_search_sparePart.getText().toString();
                    if (searchSparePart.compareTo("") == 0 || searchSparePart.isEmpty()) {
                        Config_Engg.alertBox("Please Enter text to search", AddDailyReport.this);
                    } else {
                        if (searchSparePart.length() > 2) {
                            new SparePartsAsync().execute();

                        } else {
                            Config_Engg.alertBox("Please Enter atleast 3 text to search", AddDailyReport.this);
                        }
                    }
                }
            });


            alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Log.e("addedPart size ", " cfcs " + addedPart.size());
                    llSparePartsLayout.removeAllViews();

                    allEds = new ArrayList<EditText>();

                    if (addedPart.size() > 0) {
                        final String partID = "";
                        String partName = "";
                        llSpareParts.setVisibility(View.VISIBLE);
                        for (int i = 0; i < addedPart.size(); i++) {
                            final String pID = addedPart.get(i).getSpareID();
                            final String pName = addedPart.get(i).getSparePartNo();

                            LinearLayout.LayoutParams paramtest = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    0.33f
                            );


                            final LinearLayout linearLayout = new LinearLayout(AddDailyReport.this);
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                            final TextView tvSparepart = new TextView(AddDailyReport.this);
                            tvSparepart.setTextColor(Color.BLACK);
                            tvSparepart.setLayoutParams(paramtest);
                            tvSparepart.setGravity(Gravity.CENTER);
                            tvSparepart.setPadding(0, 2, 0, 0);

                            Log.e("pName ", "cfcs " + pName);
                            tvSparepart.setText(" " + pName);

                            edQuantity = new EditText(AddDailyReport.this);
                            edQuantity.setHint("Qty");
                            edQuantity.setTextColor(Color.BLACK);
                            edQuantity.setLayoutParams(paramtest);
                            edQuantity.setGravity(Gravity.CENTER);
                            edQuantity.setTextSize(12);
                            edQuantity.setPadding(0, 0, 0, 0);
                            edQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);


                            ImageView imageView = new ImageView(AddDailyReport.this);
                            imageView.setImageResource(R.drawable.ic_delete);
                            imageView.setLayoutParams(paramtest);


                            linearLayout.addView(tvSparepart);
                            linearLayout.addView(edQuantity);
                            linearLayout.addView(imageView);
                            llSparePartsLayout.addView(linearLayout);
                            llSparePartsLayout.setVisibility(View.VISIBLE);
                            partName = partName + ", " + pName;

                            allEds.add(edQuantity);

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    AlertDialog.Builder altDialog = new AlertDialog.Builder(AddDailyReport.this);
                                    altDialog.setMessage("Do You Want to delete !!");

                                    altDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            for (int i = 0; i < addedPart.size(); i++) {
                                                if (pID.compareTo(addedPart.get(i).getSpareID()) == 0) {
                                                    addedPart.remove(i);
                                                    allEds.remove(i);
                                                }
                                            }

                                            linearLayout.setVisibility(View.GONE);
                                            if (addedPart.size() > 0) {
                                                llSpareParts.setVisibility(View.VISIBLE);
                                            } else {
                                                llSpareParts.setVisibility(View.GONE);
                                            }

                                        }
                                    });

                                    altDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    altDialog.show();
                                }
                            });
                        }

                        dialog.dismiss();
                    }
                }
            });

            alertDialog.show();

        }
    }

    public class SparePartsAsync extends AsyncTask<String, String, String> {

        private String SOAP_ACTION1 = "http://cfcs.co.in/AppEngineerPartSearch";
        private String METHOD_NAME1 = "AppEngineerPartSearch";
        private String NAMESPACE = "http://cfcs.co.in/";
        private String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

        ArrayList<SparePartListDataModel> SparePartArrayList = new ArrayList<SparePartListDataModel>();

        String[] SpareID;
        String[] SparePartNo;
        String[] SpareDesc;

        ProgressDialog progressSparePart;
        int flag = 0;
        String msgstatus = "", json_value = "", partDetail = "";
        JSONObject jsonObject;
        JSONArray jsonArray;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SparePartArrayList.clear();
            progressSparePart = ProgressDialog.show(AddDailyReport.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("PartName", searchSparePart);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {

                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    json_value = result.getProperty(0).toString();

                    jsonArray = new JSONArray(json_value);
                    jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("MsgNotification")) {
                        msgstatus = jsonObject.getString("MsgNotification");
                        flag = 1;
                    } else {

                        SpareID = new String[jsonArray.length()];
                        SparePartNo = new String[jsonArray.length()];
                        SpareDesc = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                SparePartListDataModel partListDataModel = new SparePartListDataModel();
                                partListDataModel.setSpareID(jsonObject1.getString("SpareID").toString());
                                partListDataModel.setSparePartNo(jsonObject1.getString("SparePartNo").toString());
                                partListDataModel.setSpareDesc(jsonObject1.getString("SpareDesc").toString());
                                // Add this object into the ArrayList SparePartArrayList
                                SparePartArrayList.add(partListDataModel);
                                flag = 2;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        flag = 2;
                    }
                } else {
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return json_value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
                listView_sparePart.setAdapter(null);

            } else {
                if (flag == 2) {
                    listView_sparePart.setAdapter(new SparePartListAdapter(AddDailyReport.this, SparePartArrayList));
                } else {
                    if (flag == 3) {
                        Config_Engg.toastShow("No Response", AddDailyReport.this);
                    }
                }
            }
            progressSparePart.dismiss();
        }
    }

    public class SparePartListAdapter extends BaseAdapter {

        ArrayList<SparePartListDataModel> myArrayList = new ArrayList<SparePartListDataModel>();
        LayoutInflater inflater;
        Context context;


        public SparePartListAdapter(Context context, ArrayList<SparePartListDataModel> myArrayList) {

            this.myArrayList = myArrayList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return myArrayList.size();
        }

        @Override
        public SparePartListDataModel getItem(int position) {
            return myArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final SparePartListAdapter.MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spare_part_list, parent, false);
                mViewHolder = new SparePartListAdapter.MyViewHolder();
                mViewHolder.txt_spare_part_no = convertView.findViewById(R.id.txt_spare_part_no);
                mViewHolder.txt_spare_desc = convertView.findViewById(R.id.txt_spare_desc);
                mViewHolder.btn_image_add_sparePart = convertView.findViewById(R.id.btn_image_add_sparePart);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (SparePartListAdapter.MyViewHolder) convertView.getTag();
            }
            final SparePartListDataModel currentListData = getItem(position);
            mViewHolder.txt_spare_part_no.setText(currentListData.getSparePartNo());
            mViewHolder.txt_spare_desc.setText(currentListData.getSpareDesc());


            mViewHolder.btn_image_add_sparePart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (addedPart.size() > 0) {
                        int flagn = 0;
                        for (int iq = 0; iq < addedPart.size(); iq++) {
                            String addedPartNo = addedPart.get(iq).getSparePartNo();
                            String PartNo = currentListData.getSpareID();
                            if (addedPartNo.compareTo(PartNo) == 0) {
                                Config_Engg.toastShow("Part Added", context);
                                flagn = 0;
                                break;
                            } else {
                                flagn = 1;
                            }
                        }
                        if (flagn == 1) {
                            SparePartListDataModel partBean = new SparePartListDataModel();
                            partBean.setSpareID("" + currentListData.getSpareID());
                            partBean.setSparePartNo("" + currentListData.getSparePartNo());
                            partBean.setSpareDesc("" + currentListData.getSpareDesc());
                            addedPart.add(partBean);
                            Config_Engg.toastShow("Part Added", context);
                            myArrayList.remove(position);
                            notifyDataSetChanged();
                        }
                    } else {
                        SparePartListDataModel partBean = new SparePartListDataModel();
                        partBean.setSpareID("" + currentListData.getSpareID());
                        partBean.setSparePartNo("" + currentListData.getSparePartNo());
                        partBean.setSpareDesc("" + currentListData.getSpareDesc());
                        addedPart.add(partBean);
                        Config_Engg.toastShow("Part Added", context);
                        myArrayList.remove(position);
                        notifyDataSetChanged();
                    }

                }

            });


            return convertView;
        }

        class MyViewHolder {
            ImageView btn_image_add_sparePart;
            TextView txt_spare_desc, txt_spare_part_no;
        }
    }

    public void makeJson() {

        try {
            Gson gson = new Gson();
            JSONObject jsonObj = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < addedPart.size(); i++) {

                String PartID = addedPart.get(i).getSpareID();
                String PartNo1 = addedPart.get(i).getSparePartNo();
                String SpareDesc = addedPart.get(i).getSpareDesc();
                String Quantity = edtQytArray[i];
                SparePartListDataModel diary = getImageObjectFilled(PartID, PartNo1, SpareDesc, Quantity);
                String case_json = gson.toJson(diary);
                JSONObject objImg = new JSONObject(case_json);
                array.put(objImg);
                jsonObj.put("members", array);
                //Log.e("make json size is ", array+" null");

            }
            Log.e("make json size is ", " cfcs " + jsonObj.toString());
            sparePartJson = jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SparePartListDataModel getImageObjectFilled(String PartID, String PartNo, String SpareDesc, String Quantity) {
        SparePartListDataModel bean = new SparePartListDataModel();
        bean.setSpareID(PartID);
        bean.setSparePartNo(PartNo);
        bean.setSpareDesc(SpareDesc);
        bean.setQuantity(Quantity);
        return bean;
    }

    private void ScanckBar() {

        Snackbar snackbar = Snackbar
                .make(maincontainer, "Connectivity issues", Snackbar.LENGTH_LONG)
                .setDuration(60000)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Config_Engg.isOnline(AddDailyReport.this);
                        if (Config_Engg.internetStatus) {

                            if (reportMode.compareTo("true") == 0) {
                                new DailyReportDetailAsy().execute();
                            }

                            new AddContactPersonDropDown().execute();

                        } else {
                            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", AddDailyReport.this);
                        }

                        btn_preview.setEnabled(true);
                        btn_clear.setEnabled(true);

                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        snackbar.show();

    }

    private class AddContactChange extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String contact_change_detail_value, contactChangeList;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AddDailyReport.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME3);
            String EngineerID = Config_Engg.getSharedPreferences(AddDailyReport.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(AddDailyReport.this, "pref_Engg", "AuthCode", "");
            String ContactPersonID = SelectedContactID;
            request.addProperty("ContactPersonID", ContactPersonID);
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION3, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    contact_change_detail_value = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(contact_change_detail_value);
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
                        flag = 2;
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
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray2 = new JSONArray(contact_change_detail_value);
                    JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
                    String ContactPersonName = jsonObject2.getString("ContactPersonName");
                    String Email = jsonObject2.getString("Email");
                    String Phone = jsonObject2.getString("Phone");
                    String Code = jsonObject2.getString("CountryCode");

                    txt_sign_by_name.setText(ContactPersonName);
                    txt_sign_by_name.setEnabled(false);
                    txt_sign_by_email.setText(Email);
                    txt_country_code.setText(Code);
                    txt_sign_by_mobile.setText(Phone);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }
                progressDialog.dismiss();
            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", AddDailyReport.this);

            } else if (flag == 4) {
                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
                Config_Engg.logout(AddDailyReport.this);
                Config_Engg.putSharedPreferences(AddDailyReport.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {
                ScanckBar();
                btn_preview.setEnabled(false);
                btn_clear.setEnabled(false);
                progressDialog.dismiss();
            }
            progressDialog.dismiss();
        }
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
                intent = new Intent(AddDailyReport.this, ChangePassword.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:

                Config_Engg.logout(AddDailyReport.this);
                finish();
                Config_Engg.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.dashboard:
                intent = new Intent(AddDailyReport.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(AddDailyReport.this, ProfileUpdate.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise:
                intent = new Intent(AddDailyReport.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complain:
                intent = new Intent(AddDailyReport.this, ManageComplaint.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines:
                intent = new Intent(AddDailyReport.this, ManageMachines.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_contact:
                intent = new Intent(AddDailyReport.this, ManageContact.class);
                startActivity(intent);
                finish();
                return (true);


            case R.id.btn_menu_service_hour:
                intent = new Intent(AddDailyReport.this, ServiceHourList.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(AddDailyReport.this, FeedbackActivity.class);
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
        Intent intent = new Intent(AddDailyReport.this, DailyReport.class);
        intent.putExtra("ComplainNo", complainno);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private class AddContactPersonDropDown extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String contact_detail, contact_list;
        String LoginStatus;
        String invalid = "LoginFailed";
        ProgressDialog progressDialog;
        int count = 0;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(AddDailyReport.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME4);
            String EngineerID = Config_Engg.getSharedPreferences(AddDailyReport.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(AddDailyReport.this, "pref_Engg", "AuthCode", "");

            request.addProperty("ComplainNo", complainno);
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION4, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    contact_detail = result.getProperty(0).toString();
                    if(contact_detail.compareTo("[]") != 0){
                        JSONArray jsonArray = new JSONArray(contact_detail);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        contact_list = jsonArray.toString();
                        if (jsonObject.has("status")) {
                            LoginStatus = jsonObject.getString("status");
                            msgstatus = jsonObject.getString("MsgNotification");
                            if (LoginStatus.equals(invalid)) {
                                flag = 4;
                            } else {

                                flag = 1;
                            }
                        } else {
                            flag = 2;
                        }

                    }else {

                        flag = 6;
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
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
            } else if (flag == 2) {
                try {

                    // Add value in Plant List Status Spinner
                    JSONArray jsonArray2 = new JSONArray(contact_list);
                    contactIDList = new ArrayList<String>();
                    contactIDList.add(0, "00000000-0000-0000-0000-000000000000");
                    contactNameList = new ArrayList<String>();
                    contactNameList.add(0, "New");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        count += 1;
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String ContactPersonID = jsonObject2.getString("ContactPersonID");
                        String ContactPersonName = jsonObject2.getString("ContactPersonName");
                        if (reportMode.compareTo("true") != 0) {

                            ComplainByContactID = jsonObject2.getString("ComplainByContactID");
                        }

                        contactIDList.add(i + 1, ContactPersonID);
                        contactNameList.add(i + 1, ContactPersonName);
                    }

                    spinneradapterContact = new ArrayAdapter<String>(AddDailyReport.this,
                            android.R.layout.simple_spinner_item, contactNameList);
                    spinneradapterContact.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_existing_customer_contacts.setAdapter(spinneradapterContact);


                    int index = -1;
                    if (reportMode.compareTo("true") != 0) {
                        for (int i = 0; i < contactIDList.size(); i++) {
                            if (contactIDList.get(i).equals(ComplainByContactID)) {
                                index = i;
                                break;
                            }
                        }
                    } else {
                        for (int i = 0; i < contactIDList.size(); i++) {
                            if (contactIDList.get(i).equals(SignByContactIDUpdate)) {
                                index = i;
                                break;
                            }
                        }

                    }

                    if (index > 0) {

                        String plantString = contactNameList.get((int) index);
                        if (!plantString.equalsIgnoreCase("")) {
                            int spinnerpos = spinneradapterContact.getPosition(plantString);
                            spinner_existing_customer_contacts.setSelection(spinnerpos);
                        }
                    } else if (count == 1) {

                        ///  spinner_existing_customer_contacts.setSelection(1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    flag = 5;
                }
                progressDialog.dismiss();
            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", AddDailyReport.this);

            } else if (flag == 4) {
                Config_Engg.toastShow(msgstatus, AddDailyReport.this);
                Config_Engg.logout(AddDailyReport.this);
                Config_Engg.putSharedPreferences(AddDailyReport.this, "checklogin", "status", "2");
                finish();
            } else if (flag == 5) {

                ScanckBar();
                btn_preview.setEnabled(false);
                btn_clear.setEnabled(false);
                progressDialog.dismiss();
            }else if(flag == 6){
                contactIDList = new ArrayList<String>();
                contactIDList.add(0, "00000000-0000-0000-0000-000000000000");
                contactNameList = new ArrayList<String>();
                contactNameList.add(0, "New");

                spinneradapterContact = new ArrayAdapter<String>(AddDailyReport.this,
                        android.R.layout.simple_spinner_item, contactNameList);
                spinneradapterContact.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_existing_customer_contacts.setAdapter(spinneradapterContact);

            }

            progressDialog.dismiss();
        }
    }
}

