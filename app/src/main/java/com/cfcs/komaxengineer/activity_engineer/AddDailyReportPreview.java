package com.cfcs.komaxengineer.activity_engineer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.co.cfcs.kriteshsignaturepad.views.SignaturePad;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class AddDailyReportPreview extends AppCompatActivity {


    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppEngineerDailyReportIns";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppEngineerDailyReportIns";
    private static String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    String WorkDonePlant, WorkDetails, Suggestion, Causeoffailure, NextFollowUp = "", ReasonForNotClose, ServiceTime, TravelTime,
            EngieerOtherExpense, EngieerExpenseDetails, SignByName, SignByMobileNo, SignByE_mailid, CustomerRemark, CountryCode,
            sparePartJson,SelectedSignByContactID,ReportNo,reportMode,ReportFileJson,CustomerRemarkUpdate;
    String EngieerTravelCost = "00";
    String ServiceCharge = "00";
    String NextFollowUpDate, NextFollowUpTime,complainno,partValue;

    EditText txt_add_customer_remark;

    TextView txt_next_follow_up, txt_service, txt_travel, txt_work_done_plant, txt_work_detail, txt_suggestion,
            txt_reason_close, txt_country_code, txt_add_service_charge, txt_engg_travel_cost, txt_engg_other_exp,
            txt_engg_exp_detail, txt_sign_by_name, txt_sign_by_mobile, txt_sign_by_email,
             txt_complaint_no;

    Button btn_add_sign,btn_add_daily_report,btn_back;

    LinearLayout llSparePartsLayout,llSpareParts,maincontainer;

    int currentapiVersion = 0;

    private SignaturePad mSignaturePad;
    private PopupWindow pwindo;
    String ImgExtension = "", ImgString = "", pathtodeletesign = "";

    File file;

    String spareList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily_report_preview);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_komax);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        txt_work_done_plant = findViewById(R.id.txt_work_done_plant);
        txt_complaint_no = findViewById(R.id.txt_complaint_no);
        txt_next_follow_up = findViewById(R.id.txt_next_follow_up);
        txt_service = findViewById(R.id.txt_service);
        txt_travel = findViewById(R.id.txt_travel);
        txt_work_done_plant = findViewById(R.id.txt_work_done_plant);
        txt_work_detail = findViewById(R.id.txt_work_detail);
        txt_suggestion = findViewById(R.id.txt_suggestion);
        txt_reason_close = findViewById(R.id.txt_reason_close);
        txt_add_customer_remark = findViewById(R.id.txt_add_customer_remark);
        llSparePartsLayout = findViewById(R.id.llSparePartsLayout);
        llSpareParts = findViewById(R.id.llSpareParts);
        maincontainer = findViewById(R.id.maincontainer);

        btn_add_sign = findViewById(R.id.btn_add_sign);
        btn_add_daily_report = findViewById(R.id.btn_add_daily_report);
        btn_back = findViewById(R.id.btn_back);

        currentapiVersion = android.os.Build.VERSION.SDK_INT;

        file = new File("");

        sparePartJson = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            complainno = getIntent().getExtras().getString("ComplainNo");
            WorkDonePlant = getIntent().getExtras().getString("WorkDonePlant");
            WorkDetails = getIntent().getExtras().getString("WorkDetails");
            Suggestion = getIntent().getExtras().getString("Suggestion");
            NextFollowUp = getIntent().getExtras().getString("NextFollowUp");
            ReasonForNotClose = getIntent().getExtras().getString("ReasonForNotClose");
            EngieerTravelCost = getIntent().getExtras().getString("EngieerTravelCost");
            ServiceTime = getIntent().getExtras().getString("ServiceTime");
            TravelTime = getIntent().getExtras().getString("TravelTime");
            EngieerOtherExpense = getIntent().getExtras().getString("EngieerOtherExpense");
            EngieerExpenseDetails = getIntent().getExtras().getString("EngieerExpenseDetails");
            SignByName = getIntent().getExtras().getString("SignByName");
            SignByMobileNo = getIntent().getExtras().getString("SignByMobileNo");
            SignByE_mailid = getIntent().getExtras().getString("SignByE_mailid");
            ServiceCharge = getIntent().getExtras().getString("ServiceCharge");
            CountryCode = getIntent().getExtras().getString("CountryCode");
            sparePartJson = getIntent().getExtras().getString("sparePartJson");
            SelectedSignByContactID = getIntent().getExtras().getString("SelectedSignByContactID");
            reportMode = getIntent().getExtras().getString("reportMode");
            ReportNo = getIntent().getExtras().getString("ReportNo");
            ReportFileJson = getIntent().getExtras().getString("ReportFileJson");
            if (reportMode.compareTo("true") == 0) {
                CustomerRemarkUpdate = getIntent().getExtras().getString("CustomerRemarkUpdate");
            }
        }

        txt_work_done_plant.setText(WorkDonePlant);
        txt_complaint_no.setText(complainno);
        txt_next_follow_up.setText(NextFollowUp);
        txt_service.setText(ServiceTime);
        txt_travel.setText(TravelTime);
        txt_work_detail.setText(WorkDetails);
        txt_suggestion.setText(Suggestion);
        txt_reason_close.setText(ReasonForNotClose);
        txt_add_customer_remark.setText(CustomerRemarkUpdate);

        if(sparePartJson !=null){
            try {
                JSONObject jsonObject = new JSONObject(sparePartJson);
                JSONArray sparejsonArray = jsonObject.getJSONArray("members");
                spareList = sparejsonArray.toString();

                JSONArray jsonArray2 = new JSONArray(spareList);
                llSpareParts.setVisibility(View.VISIBLE);

                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                    String SparePartNo = jsonObject2.getString("SparePartNo");
                    String SpareQuantity = jsonObject2.getString("SpareQuantity");

                    LinearLayout.LayoutParams paramtest = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            0.33f
                    );


                    final LinearLayout linearLayout = new LinearLayout(AddDailyReportPreview.this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    final TextView tvSparepart = new TextView(AddDailyReportPreview.this);
                    tvSparepart.setTextColor(Color.BLACK);
                    tvSparepart.setLayoutParams(paramtest);
                    tvSparepart.setGravity(Gravity.CENTER);
                    tvSparepart.setPadding(0, 2, 0, 0);

                    Log.e("pName ", "cfcs " + SparePartNo);
                    tvSparepart.setText(" " + SparePartNo);

                    final TextView tvSpareQuntaity = new TextView(AddDailyReportPreview.this);
                    tvSpareQuntaity.setTextColor(Color.BLACK);
                    tvSpareQuntaity.setLayoutParams(paramtest);
                    tvSpareQuntaity.setGravity(Gravity.CENTER);
                    tvSpareQuntaity.setPadding(0, 2, 0, 0);

                    Log.e("pName ", "cfcs " + SparePartNo);
                    tvSparepart.setText(" " + SparePartNo);
                    tvSpareQuntaity.setText(SpareQuantity);


                    linearLayout.addView(tvSparepart);
                    linearLayout.addView(tvSpareQuntaity);
                    llSparePartsLayout.addView(linearLayout);
                    llSparePartsLayout.setVisibility(View.VISIBLE);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




        btn_add_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentapiVersion <= 22) {

                    new ShowpopSignaturePad();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        } else {

                            new ShowpopSignaturePad();
                        }
                    }
                }
            }
        });

        btn_add_daily_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config_Engg.isOnline(AddDailyReportPreview.this);
                if (Config_Engg.internetStatus == true) {
                    if (file.length() > 0) {
                        String signImgPath = String.valueOf(file);
                        int dotposition = signImgPath.lastIndexOf(".");
                        ImgExtension = signImgPath.substring(dotposition + 1, signImgPath.length());
                        ImgString = decodeImage(signImgPath);
                        pathtodeletesign = signImgPath; // getting path to delete sign from gallery after submit
                    }


                    CustomerRemark = txt_add_customer_remark.getText().toString();

                    new AddDailyReportAsy().execute();

                } else {
                    Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", AddDailyReportPreview.this);
                }

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private class ShowpopSignaturePad {

        {

            final Button mClearButton;
            final Button mSaveButton;
            final Button close_button;


            LayoutInflater inflater = (LayoutInflater) AddDailyReportPreview.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.signature_pad_layout,
                    (ViewGroup) findViewById(R.id.popupLayout));
            pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);


            mSignaturePad = (SignaturePad) layout.findViewById(R.id.signature_pad);
            mClearButton = (Button) layout.findViewById(R.id.clear_button);
            mSaveButton = (Button) layout.findViewById(R.id.save_button);
            close_button = layout.findViewById(R.id.close_button);


            mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                @Override
                public void onStartSigning() {
                    // Toast.makeText(AddDailyReport.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSigned() {
                    mSaveButton.setEnabled(true);
                    mClearButton.setEnabled(true);

                }

                @Override
                public void onClear() {
                    mSaveButton.setEnabled(false);
                    mClearButton.setEnabled(false);

                }
            });


            mClearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSignaturePad.clear();

                }
            });

            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                    String pattern = "mm ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                    String time = formatter.format(new Date());
                    String path = ("/sign" + time + ".jpg");
                    file = new File(Environment.getExternalStorageDirectory() + path);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        try {
                            //DrawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                                //Toast.makeText(this, "File Saved ::" + path, Toast.LENGTH_SHORT).show();

                                // tvAddedSign.setVisibility(View.VISIBLE);
                                Config_Engg.toastShow("Sign saved", AddDailyReportPreview.this);
                                btn_add_sign.setText("Signature Added");
                                btn_add_sign.setTextColor(Color.parseColor("#008000"));
                                pwindo.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Config_Engg.toastShow("Error occured during saving your sign", AddDailyReportPreview.this);
                        }
                    }


                }
            });

            close_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pwindo.dismiss();
                }
            });


        }

        String imgString;
    }

    public String decodeImage(String imgPath) {
        Bitmap decodedBitmap = BitmapFactory.decodeFile(imgPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] ba = baos.toByteArray();
        String imgString = Base64.encodeToString(ba, Base64.DEFAULT);
        return imgString;
    }

    private class AddDailyReportAsy extends AsyncTask<String, String, String> {

        String jsonValue;
        int flag;
        String msgstatus;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddDailyReportPreview.this, "Loading", "Please Wait", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {


            String EngineerID = Config_Engg.getSharedPreferences(AddDailyReportPreview.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(AddDailyReportPreview.this, "pref_Engg", "AuthCode", "");

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            request.addProperty("DailyReportNo", ReportNo);
            request.addProperty("ComplainNo", complainno);
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("WordDone", WorkDonePlant);
            request.addProperty("WorkDetail", WorkDetails);
            request.addProperty("Suggestion", Suggestion);
            request.addProperty("CauseOfFailure", Causeoffailure);
            request.addProperty("ReasonForNotClose", ReasonForNotClose);
            request.addProperty("NextFollowUp", NextFollowUp);
            request.addProperty("EngineerTravelExpense", EngieerTravelCost);
            request.addProperty("EngineerOtherExpense", EngieerOtherExpense);
            request.addProperty("ServiceTime", ServiceTime);
            request.addProperty("TravelTime", TravelTime);
            request.addProperty("ServiceChargeAmount", ServiceCharge);
            request.addProperty("ExpenseDetail", EngieerExpenseDetails);
            request.addProperty("SignByContactID", SelectedSignByContactID);
            request.addProperty("SignByName", SignByName);
            request.addProperty("CountryCode", CountryCode);
            request.addProperty("SignByMobile", SignByMobileNo);
            request.addProperty("SignByMailID", SignByE_mailid);
            request.addProperty("CustomerRemark", CustomerRemark);
            request.addProperty("ImgJson", ImgString);
            request.addProperty("ImageExtension", ImgExtension);
            request.addProperty("ReportFileJson", ReportFileJson);
            request.addProperty("AuthCode", AuthCode);

            if (sparePartJson != null) {
                request.addProperty("SparePartJson", sparePartJson);
            } else {
                request.addProperty("SparePartJson", "");
            }

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    jsonValue = result.getProperty(0).toString();
                    JSONArray jsonArray = new JSONArray(jsonValue);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {
                            flag = 4;
                        } else {
                            File file = new File(pathtodeletesign);
                            boolean deleted = file.delete();
                            Log.e("msgstatus", "cfcs " + msgstatus);

                            flag = 1;
                        }
                    } else {
                        msgstatus = jsonObject.getString("MsgNotification");
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (flag == 1) {

                if (LoginStatus.equalsIgnoreCase("success")) {
                    Config_Engg.toastShow("Daily Report Saved Successfully", AddDailyReportPreview.this);
                    Intent intent = new Intent(AddDailyReportPreview.this, DailyReport.class);
                    intent.putExtra("ComplainNo", complainno);
                    startActivity(intent);
                    finish();
                    //delete file content internal
                    if (file.isDirectory()) {
                        String[] children = file.list();
                        for (int i = 0; i < children.length; i++) {
                            new File(file, children[i]).delete();
                        }
                    }
                } else {
                    Config_Engg.toastShow(msgstatus, AddDailyReportPreview.this);
                }

            } else if (flag == 2) {
                Config_Engg.toastShow(msgstatus, AddDailyReportPreview.this);
            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", AddDailyReportPreview.this);
            } else if (flag == 4) {

                Config_Engg.toastShow(msgstatus, AddDailyReportPreview.this);
                Config_Engg.logout(AddDailyReportPreview.this);
                Config_Engg.putSharedPreferences(AddDailyReportPreview.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {

                ScanckBar();
                btn_add_daily_report.setEnabled(false);
            }
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    private void ScanckBar() {

        Snackbar snackbar = Snackbar
                .make(maincontainer, "Connectivity issues", Snackbar.LENGTH_LONG)
                .setDuration(60000)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        btn_add_daily_report.setEnabled(true);
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
                intent = new Intent(AddDailyReportPreview.this, ChangePassword.class);
                startActivity(intent);
                finish();

                return (true);
            case R.id.logout:

                Config_Engg.logout(AddDailyReportPreview.this);
                finish();
                Config_Engg.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.dashboard:
                intent = new Intent(AddDailyReportPreview.this, DashboardActivity.class);
                startActivity(intent);
                finish();

                return (true);
            case R.id.profile:
                intent = new Intent(AddDailyReportPreview.this, ProfileUpdate.class);
                startActivity(intent);
                finish();
                return (true);
            case R.id.btn_raise:
                intent = new Intent(AddDailyReportPreview.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);
            case R.id.btn_complain:
                intent = new Intent(AddDailyReportPreview.this, ManageComplaint.class);
                startActivity(intent);
                finish();
                return (true);
            case R.id.btn_machines:
                intent = new Intent(AddDailyReportPreview.this, ManageMachines.class);
                startActivity(intent);
                finish();
                return (true);
            case R.id.btn_contact:
                intent = new Intent(AddDailyReportPreview.this, ManageContact.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(AddDailyReportPreview.this, FeedbackActivity.class);
                startActivity(intent);
                finish();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}
