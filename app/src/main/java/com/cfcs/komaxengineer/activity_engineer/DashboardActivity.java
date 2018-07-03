package com.cfcs.komaxengineer.activity_engineer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {


    Button btn_raise_complain, btn_manage_complain, btn_manage_machines, btn_manage_contact, btn_feedback, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Set Company logo in action bar with AppCompatActivity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_komax);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        //initiate widgets
        btn_raise_complain = findViewById(R.id.btn_raise_complain);
        btn_manage_complain = findViewById(R.id.btn_manage_complain);
        btn_manage_machines = findViewById(R.id.btn_manage_machines);
        btn_manage_contact = findViewById(R.id.btn_manage_contact);
        btn_feedback = findViewById(R.id.btn_feedback);
        btn_logout = findViewById(R.id.btn_logout);


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
                Toast.makeText(DashboardActivity.this,"Implementation In Process",Toast.LENGTH_LONG).show();
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
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
