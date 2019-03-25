package com.cfcs.komaxengineer.activity_engineer;

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;

import java.util.Objects;

public class ServiceHourDetail extends AppCompatActivity {


    TextView txt_total_availability,txt_tomorrow_plan,txt_total_hours,txt_remark,txt_service_date;

    LinearLayout maincontainer,ll_add;

    String serviceHrID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_hour_detail);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        txt_total_availability = findViewById(R.id.txt_total_availability);
        txt_tomorrow_plan = findViewById(R.id.txt_tomorrow_plan);
        txt_total_hours = findViewById(R.id.txt_total_hours);
        txt_service_date = findViewById(R.id.txt_service_date);
        txt_remark = findViewById(R.id.txt_remark);

        maincontainer = findViewById(R.id.maincontainer);
        ll_add = findViewById(R.id.ll_add);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            serviceHrID = getIntent().getExtras().getString("ServiceHrID");

        }

        Config_Engg.isOnline(ServiceHourDetail.this);
        if (Config_Engg.internetStatus) {

            new ServiceDetailAsy().execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHourDetail.this);
        }

    }

    private class ServiceDetailAsy extends AsyncTask<String, String, String> {

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
            progressDialog = ProgressDialog.show(ServiceHourDetail.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            String AuthCode = Config_Engg.getSharedPreferences(ServiceHourDetail.this, "pref_Engg", "AuthCode", "");
            String EngineerID = Config_Engg.getSharedPreferences(ServiceHourDetail.this, "pref_Engg", "EngineerID", "");
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("ServiceHrID", serviceHrID);
            request.addProperty("AuthCode", AuthCode);
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
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, ServiceHourDetail.this);
            } else if (flag == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(serviceHrsMaster);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String TodayStatusName = jsonObject.getString("TodayStatusName").toString();
                    txt_total_availability.setText(TodayStatusName);

                    String TomorrowPlanName = jsonObject.getString("TomorrowPlanName").toString();
                    txt_tomorrow_plan.setText(TomorrowPlanName);


                    String ServiceHrDateText = jsonObject.getString("ServiceHrDateText").toString();
                    txt_service_date.setText(ServiceHrDateText);

                    String UserRemark = jsonObject.getString("UserRemark").toString();
                    txt_remark.setText(UserRemark);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {

                    JSONArray jsonArray2 = new JSONArray(serviceHrsDetail);

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        final String serviceHrTitleName = jsonObject2.getString("ServiceHrTitleName");
                        String ServiceHrs = jsonObject2.getString("ServiceHrs");

                        LinearLayout.LayoutParams Linearparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0f);
                        Linearparams.gravity = Gravity.CENTER;
                        Linearparams.setMargins(0, 20, 0, 0);

                        LinearLayout.LayoutParams Viewparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 5, 1f);
                        Viewparams.gravity = Gravity.CENTER;
                        Viewparams.setMargins(0, 20, 0, 0);

                        LinearLayout.LayoutParams Textparams = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 6f);

                        LinearLayout.LayoutParams Dotparams = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);


                        LinearLayout.LayoutParams Valueparams = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f);


                        View view = new View(ServiceHourDetail.this);
                        view.setLayoutParams(Viewparams);
                        view.setBackgroundColor(Color.parseColor("#e0e0e0"));

                        final LinearLayout linearLayout = new LinearLayout(ServiceHourDetail.this);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setLayoutParams(Linearparams);

                        TextView textView = new TextView(ServiceHourDetail.this);
                        textView.setLayoutParams(Textparams);
                        textView.setText(serviceHrTitleName);
                       // textView.setTextSize(15);
                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

                        TextView textView1 = new TextView(ServiceHourDetail.this);
                        textView1.setLayoutParams(Dotparams);
                        textView1.setText(":");
                        // textView.setTextSize(15);
                        textView1.setTypeface(textView1.getTypeface(), Typeface.BOLD);


                        TextView textView2 = new TextView(ServiceHourDetail.this);
                        textView2.setLayoutParams(Valueparams);
                        textView2.setText(ServiceHrs);

                        ll_add.addView(view);
                        ll_add.addView(linearLayout);
                        linearLayout.addView(textView);
                        linearLayout.addView(textView1);
                        linearLayout.addView(textView2);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", ServiceHourDetail.this);

            } else if (flag == 4) {

                Config_Engg.toastShow(msgstatus, ServiceHourDetail.this);
                Config_Engg.logout(ServiceHourDetail.this);
                Config_Engg.putSharedPreferences(ServiceHourDetail.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {

                ScanckBar();
                progressDialog.dismiss();
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
                        if (Config_Engg.internetStatus) {

                            new ServiceDetailAsy().execute();

                        } else {
                            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", ServiceHourDetail.this);
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
                intent = new Intent(ServiceHourDetail.this, ChangePassword.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:
                Config_Engg.logout(ServiceHourDetail.this);
                finish();
                Config_Engg.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.dashboard:
                intent = new Intent(ServiceHourDetail.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(ServiceHourDetail.this, ProfileUpdate.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_raise:
                intent = new Intent(ServiceHourDetail.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complain:
                intent = new Intent(ServiceHourDetail.this, ManageComplaint.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines:
                intent = new Intent(ServiceHourDetail.this, ManageMachines.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_contact:
                intent = new Intent(ServiceHourDetail.this, ManageContact.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_service_hour:
                intent = new Intent(ServiceHourDetail.this, ServiceHourList.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(ServiceHourDetail.this, FeedbackActivity.class);
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
        Intent intent = new Intent(ServiceHourDetail.this, ServiceHourList.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
