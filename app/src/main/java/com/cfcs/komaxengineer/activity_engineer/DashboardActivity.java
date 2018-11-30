package com.cfcs.komaxengineer.activity_engineer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.SplashActivity;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {


    Button btn_raise_complain, btn_manage_complain, btn_manage_machines, btn_manage_contact, btn_feedback, btn_logout;

    String currentVersion = null;

    String newVersion = null;

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

        // Make sure we use vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config_Engg.isOnline(DashboardActivity.this);
        if (Config_Engg.internetStatus == true) {

            new ForceUpdateAsync(currentVersion).execute();

        } else {
            Config_Engg.toastShow("No Internet Connection! Please Reconnect Your Internet", DashboardActivity.this);
            finish();
        }

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
//                Intent i = new Intent(DashboardActivity.this, FeedbackActivity.class);
//                startActivity(i);
                Toast.makeText(DashboardActivity.this, "Implementation In Process", Toast.LENGTH_LONG).show();
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
