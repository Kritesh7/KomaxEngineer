package com.cfcs.komaxengineer.activity_engineer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.SplashActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    private static String SOAP_ACTION1 = "http://cfcs.co.in/ComplaintEngineerSummary";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "ComplaintEngineerSummary";
    private String url = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    Button btn_raise_complain, btn_manage_complain, btn_manage_machines, btn_manage_contact, btn_feedback, btn_logout;

    String currentVersion = null;

    String newVersion = null;

    BarChart barchart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<String> BarEntryLable;
    ArrayList<BarEntry> entriesbar;

    TextView txt_open_all, txt_less_than_5, txt_less_than_10, txt_old_days, txt_open_all_text, txt_less_than_5_text, txt_less_than_10_text, txt_old_days_text,
            txt_date_to1, txt_date_from1, txt_date_to2, txt_date_from2, txt_date_to3, txt_date_from3, txt_date_to4, txt_date_from4;

    LinearLayout ll_priority_summary, ll_all_open, ll_less_than_5_text, ll_less_than_10_text, ll_old_days_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }


        //initiate widgets
        btn_raise_complain = findViewById(R.id.btn_raise_complain);
        btn_manage_complain = findViewById(R.id.btn_manage_complain);
        btn_manage_machines = findViewById(R.id.btn_manage_machines);
        btn_manage_contact = findViewById(R.id.btn_manage_contact);
        btn_feedback = findViewById(R.id.btn_feedback);
        btn_logout = findViewById(R.id.btn_logout);

        txt_open_all =  findViewById(R.id.txt_open_all);
        txt_less_than_5 =  findViewById(R.id.txt_less_than_5);
        txt_less_than_10 =  findViewById(R.id.txt_less_than_10);
        txt_old_days =  findViewById(R.id.txt_old_days);
        txt_open_all_text =  findViewById(R.id.txt_open_all_text);
        txt_less_than_5_text =  findViewById(R.id.txt_less_than_5_text);
        txt_less_than_10_text =  findViewById(R.id.txt_less_than_10_text);
        txt_old_days_text =  findViewById(R.id.txt_old_days_text);

        txt_date_to1 =  findViewById(R.id.txt_date_to1);
        txt_date_from1 =  findViewById(R.id.txt_date_from1);
        txt_date_to2 =  findViewById(R.id.txt_date_to2);
        txt_date_from2 =  findViewById(R.id.txt_date_from2);
        txt_date_to3 =  findViewById(R.id.txt_date_to3);
        txt_date_from3 =  findViewById(R.id.txt_date_from3);
        txt_date_to4 =  findViewById(R.id.txt_date_to4);
        txt_date_from4 =  findViewById(R.id.txt_date_from4);

        ll_all_open =  findViewById(R.id.ll_all_open);
        ll_less_than_5_text =  findViewById(R.id.ll_less_than_5_text);
        ll_less_than_10_text =  findViewById(R.id.ll_less_than_10_text);
        ll_old_days_text =  findViewById(R.id.ll_old_days_text);

        ll_priority_summary =  findViewById(R.id.ll_priority_summary);


        barchart =  findViewById(R.id.chart2);

        // Make sure we use vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Config_Engg.isOnline(DashboardActivity.this);
        if (Config_Engg.internetStatus) {

            new ComplaintEngineerSummary().execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", DashboardActivity.this);
        }


        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config_Engg.isOnline(DashboardActivity.this);
        if (Config_Engg.internetStatus) {

            new ForceUpdateAsync(currentVersion).execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", DashboardActivity.this);
            finish();
        }

        barchart.animateY(3000);
        barchart.setHighlightPerTapEnabled(true);
        barchart.setDescription(null);

        ll_all_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ManageComplaint.class);
                intent.putExtra("DateFrom1", txt_date_from4.getText().toString());
                intent.putExtra("DateTo1", txt_date_to4.getText().toString());
                intent.putExtra("PriorityID", "0");
                intent.putExtra("HeaderName",txt_open_all_text.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        ll_less_than_5_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ManageComplaint.class);
                intent.putExtra("DateFrom1", txt_date_from3.getText().toString());
                intent.putExtra("DateTo1", txt_date_to3.getText().toString());
                intent.putExtra("PriorityID", "0");
                intent.putExtra("HeaderName",txt_less_than_5_text.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        ll_less_than_10_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ManageComplaint.class);
                intent.putExtra("DateFrom1", txt_date_from2.getText().toString());
                intent.putExtra("DateTo1", txt_date_to2.getText().toString());
                intent.putExtra("PriorityID", "0");
                intent.putExtra("HeaderName",txt_less_than_10_text.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        ll_old_days_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ManageComplaint.class);
                intent.putExtra("DateFrom1", txt_date_from1.getText().toString());
                intent.putExtra("DateTo1", txt_date_to1.getText().toString());
                intent.putExtra("PriorityID", "0");
                intent.putExtra("HeaderName",txt_old_days_text.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        //Click Listener
        btn_raise_complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, RaiseComplaintActivity.class);
                startActivity(i);
            }
        });

        btn_manage_complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, ManageComplaint.class);
                startActivity(i);
            }
        });

        btn_manage_machines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, ManageMachines.class);
                startActivity(i);
            }
        });
        btn_manage_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, ManageContact.class);
                startActivity(i);
            }
        });

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, ServiceHourList.class);
                startActivity(i);
//                Toast.makeText(DashboardActivity.this, "Implementation In Process", Toast.LENGTH_LONG).show();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config_Engg.logout(DashboardActivity.this);
                Config_Engg.putSharedPreferences(DashboardActivity.this, "checklogin", "status", "2");
                finish();
            }
        });
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {

        private String latestVersion;
        private String currentVersion;
        public ForceUpdateAsync(String currentVersion){
            this.currentVersion = currentVersion;

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+getBaseContext().getPackageName()+"&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null && !latestVersion.isEmpty()){
                if (Float.valueOf(currentVersion) < Float.valueOf(latestVersion)) {
                    //show dialog
                    showForceUpdateDialog();
                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(DashboardActivity.this,
                    R.style.LibAppTheme));

            alertDialogBuilder.setTitle(DashboardActivity.this.getString(R.string.youAreNotUpdatedTitle));
            alertDialogBuilder.setMessage(DashboardActivity.this.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + DashboardActivity.this.getString(R.string.youAreNotUpdatedMessage1));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DashboardActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + DashboardActivity.this.getPackageName())));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }


    private class ComplaintEngineerSummary extends AsyncTask<String, String, String> {

        int flag;
        String msgstatus;
        String complaint_engineer_summary, OpensummaryList, PrioritySummaryList, TransactionSummaryList, MonthlyClosedList;
        ProgressDialog progressDialog;
        String LoginStatus;
        String invalid = "LoginFailed";

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DashboardActivity.this, "Loading...", "Please Wait....", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
            String EngineerID = Config_Engg.getSharedPreferences(DashboardActivity.this, "pref_Engg", "EngineerID", "");
            String AuthCode = Config_Engg.getSharedPreferences(DashboardActivity.this, "pref_Engg", "AuthCode", "");
            request.addProperty("EngineerID", EngineerID);
            request.addProperty("AuthCode", AuthCode);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;
            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
                androidHttpTransport.call(SOAP_ACTION1, envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result != null) {
                    complaint_engineer_summary = result.getProperty(0).toString();

                    Object json = new JSONTokener(complaint_engineer_summary).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(complaint_engineer_summary);
                        JSONArray OpenSummaryjsonArray = object.getJSONArray("OpenSummary");
                        OpensummaryList = OpenSummaryjsonArray.toString();
                        JSONArray PrioritySummaryjsonArray = object.getJSONArray("PrioritySummary");
                        PrioritySummaryList = PrioritySummaryjsonArray.toString();
                        JSONArray TransactionSummaryjsonArray = object.getJSONArray("TransactionSummary");
                        TransactionSummaryList = TransactionSummaryjsonArray.toString();
                        JSONArray MonthlyClosedjsonArray = object.getJSONArray("MonthlyClosed");
                        MonthlyClosedList = MonthlyClosedjsonArray.toString();
                        if (complaint_engineer_summary.compareTo("true") == 0) {
                            JSONArray jsonArray = new JSONArray(complaint_engineer_summary);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            msgstatus = jsonObject.getString("MsgNotification");
                            flag = 1;

                        } else {
                            flag = 2;
                        }

                    } else if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(complaint_engineer_summary);
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
                    flag = 3;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = 5;
            }


            return null;
        }


        @TargetApi(Build.VERSION_CODES.O)
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String complain_detail_value) {
            super.onPostExecute(complain_detail_value);
            if (flag == 1) {
                Config_Engg.toastShow(msgstatus, DashboardActivity.this);
            } else if (flag == 2) {
                try {

                    JSONArray IndexText = null;

                    JSONArray jsonArray2 = new JSONArray(OpensummaryList);

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        IndexText = jsonObject2.names();
                        String KeyName = jsonObject2.getString("KeyName");
                        String KeyValue = jsonObject2.getString("KeyValue");
                        String DateFrom = jsonObject2.getString("DateFrom");
                        String DateUpto = jsonObject2.getString("DateUpto");


                        if (i == 0) {
                            txt_less_than_5.setText(KeyValue);
                            txt_less_than_5_text.setText(KeyName);
                            txt_date_to3.setText(DateUpto);
                            txt_date_from3.setText(DateFrom);


                        } else if (i == 1) {
                            txt_old_days.setText(KeyValue);
                            txt_old_days_text.setText(KeyName);
                            txt_date_to1.setText(DateUpto);
                            txt_date_from1.setText(DateFrom);


                        } else if (i == 2) {
                            txt_less_than_10.setText(KeyValue);
                            txt_less_than_10_text.setText(KeyName);
                            txt_date_to2.setText(DateUpto);
                            txt_date_from2.setText(DateFrom);

                        } else {
                            txt_open_all.setText(KeyValue);
                            txt_open_all_text.setText(KeyName);
                            txt_date_to4.setText(DateUpto);
                            txt_date_from4.setText(DateFrom);

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                try {

                    JSONArray jsonArray2 = new JSONArray(MonthlyClosedList);

                    entriesbar = new ArrayList<>();

                    BarEntryLable = new ArrayList<String>();

                    for (int i = 0; i < jsonArray2.length(); i++) {

                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        String DateMonthYear = jsonObject2.getString("DateMonthYear");
                        String ClosedComplaintCount = jsonObject2.getString("ClosedComplaintCount");

                        BarEntryLable.add(DateMonthYear);
                        entriesbar.add(new BarEntry(Float.parseFloat(ClosedComplaintCount), i));

                    }

                    barDataSet = new BarDataSet(entriesbar, "");
                    barData = new BarData(BarEntryLable, barDataSet);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barchart.setData(barData);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                try {

                    JSONArray jsonArray2 = new JSONArray(PrioritySummaryList);

                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                        final String PriorityName = jsonObject2.getString("PriorityName");
                        String ComplainCount = jsonObject2.getString("ComplainCount");
                        String Colour = jsonObject2.getString("Colour");
                        final String priorityID = jsonObject2.getString("PriorityID");

                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                        lparams.setMargins(2, 8, 2, 8);

                        CardView cardView = new CardView(DashboardActivity.this);
                        cardView.setLayoutParams(lparams);
                        // Set CardView corner radius
                        cardView.setRadius(10);
                        // Set cardView content padding
                        // cardView.setContentPadding(15, 8, 15, 8);
                        // Set a background color for CardView
                        //  cardView.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));
                        // Set the CardView maximum elevation
                        // cardView.setMaxCardElevation(20);
                        cardView.setClickable(true);
                        // Set CardView elevation
                        cardView.setCardElevation(20);


                        LinearLayout parentInCardView = new LinearLayout(DashboardActivity.this);
                        parentInCardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        parentInCardView.setOrientation(LinearLayout.VERTICAL);
                        parentInCardView.setGravity(Gravity.CENTER);
                        parentInCardView.setBackgroundColor(Color.parseColor(Colour));
                        parentInCardView.setPadding(20, 20, 20, 20);
                        cardView.addView(parentInCardView);

                        LinearLayout childProDetail = new LinearLayout(DashboardActivity.this);
                        childProDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        childProDetail.setOrientation(LinearLayout.VERTICAL);
                        childProDetail.setGravity(Gravity.CENTER_VERTICAL);
                        childProDetail.setGravity(Gravity.CENTER_HORIZONTAL);
                        parentInCardView.addView(childProDetail);

                        LinearLayout.LayoutParams Textparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                        Textparams.gravity = Gravity.CENTER;


                        TextView TvPriorityName = new TextView(DashboardActivity.this);
                        TvPriorityName.setLayoutParams(Textparams);
                        TvPriorityName.setText(PriorityName);
                        TvPriorityName.setTypeface(null, Typeface.BOLD);
                        TvPriorityName.setTextSize(13);
                        TvPriorityName.setTextColor(Color.parseColor("#ffffff"));

                        LinearLayout.LayoutParams Textparams2 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                        Textparams2.gravity = Gravity.CENTER;

                        TextView TvComplainCount = new TextView(DashboardActivity.this);
                        TvComplainCount.setLayoutParams(Textparams2);
                        TvComplainCount.setText(ComplainCount);
                        TvComplainCount.setTextSize(13);
                        TvComplainCount.setTextColor(Color.parseColor("#ffffff"));

                        childProDetail.addView(TvPriorityName);
                        childProDetail.addView(TvComplainCount);
                        cardView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String upToNCharacters = firstFive(PriorityName);
                                Intent intent = new Intent(DashboardActivity.this, ManageComplaint.class);
                                intent.putExtra("DateFrom1", "");
                                intent.putExtra("DateTo1", "");
                                intent.putExtra("PriorityID", priorityID);
                                intent.putExtra("HeaderName",upToNCharacters);
                                startActivity(intent);
                                finish();
                            }
                        });

                        ll_priority_summary.addView(cardView);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e("Error is here", e.toString());
                }

                progressDialog.dismiss();
            } else if (flag == 3) {
                Config_Engg.toastShow("No Response", DashboardActivity.this);
            } else if (flag == 4) {

                Config_Engg.toastShow(msgstatus, DashboardActivity.this);
                Config_Engg.logout(DashboardActivity.this);
                Config_Engg.putSharedPreferences(DashboardActivity.this, "checklogin", "status", "2");
                finish();

            } else if (flag == 5) {
//				ScanckBar();
////				btn_submit.setEnabled(false);
////				btn_update.setEnabled(false);
////				btn_clear.setEnabled(false);
                progressDialog.dismiss();
            }
            progressDialog.dismiss();
        }
    }

    public String firstFive(String str) {
        return str.length() < 5 ? str : str.substring(0, 5);
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
                intent = new Intent(DashboardActivity.this, ChangePassword.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.logout:
                Config_Engg.logout(DashboardActivity.this);
                finish();
                Config_Engg.putSharedPreferences(this, "checklogin", "status", "2");
                return (true);

            case R.id.dashboard:
                intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.profile:
                intent = new Intent(DashboardActivity.this, ProfileUpdate.class);
                startActivity(intent);
                finish();
                return (true);
            case R.id.btn_raise:
                intent = new Intent(DashboardActivity.this, RaiseComplaintActivity.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_complain:
                intent = new Intent(DashboardActivity.this, ManageComplaint.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_machines:
                intent = new Intent(DashboardActivity.this, ManageMachines.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_contact:
                intent = new Intent(DashboardActivity.this, ManageContact.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_service_hour:
                intent = new Intent(DashboardActivity.this, ServiceHourList.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(DashboardActivity.this, FeedbackActivity.class);
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
        moveTaskToBack(true);
    }

}
