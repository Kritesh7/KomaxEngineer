package com.cfcs.komaxengineer.activity_engineer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.model.DecodeFileBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import in.co.cfcs.kriteshfilepicker.FilePickerBuilder;
import in.co.cfcs.kriteshfilepicker.FilePickerConst;
import in.co.cfcs.kriteshfilepicker.models.sort.SortingTypes;
import in.co.cfcs.kriteshfilepicker.utils.Orientation;
import in.co.cfcs.kriteshsignaturepad.views.SignaturePad;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class AddDailyReportPreview extends AppCompatActivity {


    private static String SOAP_ACTION1 = "http://cfcs.co.in/AppEngineerDailyReportIns";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME1 = "AppEngineerDailyReportIns";
    private static String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    String WorkDonePlant, WorkDetails, Suggestion, Causeoffailure, NextFollowUp = "", ReasonForNotClose, ServiceTime, TravelTime,
            EngieerOtherExpense, EngieerExpenseDetails, SignByName, SignByMobileNo, SignByE_mailid, CustomerRemark, CountryCode,
            sparePartJson, SelectedSignByContactID, ReportNo, reportMode, ReportFileJson, CustomerRemarkUpdate;
    String EngieerTravelCost = "00";
    String ServiceCharge = "00";
    String NextFollowUpDate, NextFollowUpTime, complainno, partValue;

    EditText txt_add_customer_remark;

    TextView txt_next_follow_up, txt_service, txt_travel, txt_work_done_plant, txt_work_detail, txt_suggestion,
            txt_reason_close, txt_country_code, txt_add_service_charge, txt_engg_travel_cost, txt_engg_other_exp,
            txt_image_show, txt_sign_by_name, txt_sign_by_mobile, txt_sign_by_email,
            txt_complaint_no;

    Button btn_add_sign, btn_add_daily_report, btn_back, btn_attachment;

    LinearLayout llSparePartsLayout, llSpareParts, maincontainer;

    int currentapiVersion = 0;

    private SignaturePad mSignaturePad;
    private PopupWindow pwindo;
    String ImgExtension = "", ImgString = "", pathtodeletesign = "";

    File file;

    String spareList;

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;

    //Pdf request code
    private int PICK_PDF_REQUEST = 2;
    private final int PICK_IMAGE_MULTIPLE = 1;
    Uri mCapturedImageURI;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 0;

    ArrayList<String> imagesPathList;
    ArrayList<String> totalImage;
    ArrayList<String> totalFile;
    String imageJson = "";

    String[] imagesPath;
    String[] filePath;

    File myFile;

    public static final int RC_PHOTO_PICKER_PERM = 123;
    public static final int RC_FILE_PICKER_PERM = 321;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();


    private Bitmap imgbitmap;

    ImageView btn_attach, btn_gallery, btn_camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_daily_report_preview_main_container);

        //Set Company logo in action bar with AppCompatActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.logo_komax);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

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
        btn_attachment = findViewById(R.id.btn_attachment);

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setPeekHeight(0);

        btn_add_sign = findViewById(R.id.btn_add_sign);
        btn_add_daily_report = findViewById(R.id.btn_add_daily_report);
        btn_back = findViewById(R.id.btn_back);

        btn_attach = findViewById(R.id.btn_attach);
        btn_gallery = findViewById(R.id.btn_gallery);

        txt_image_show = findViewById(R.id.txt_image_show);
        txt_image_show.setVisibility(View.GONE);

        currentapiVersion = android.os.Build.VERSION.SDK_INT;

        totalImage = new ArrayList<String>();

        totalFile = new ArrayList<String>();

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

        if (sparePartJson != null) {
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

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btn_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }


            }
        });


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
                if (Config_Engg.internetStatus) {
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

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalFile.size() > 0) {

                    Config_Engg.alertBox("File already Selected", AddDailyReportPreview.this);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else {

                    if (currentapiVersion <= 22) {
                        int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size();

                        FilePickerBuilder.getInstance()
                                .setMaxCount(maxCount)
                                .setSelectedFiles(photoPaths)
                                .setActivityTheme(R.style.FilePickerTheme)
                                .setActivityTitle("Please select media")
                                .enableVideoPicker(false)
                                .enableCameraSupport(true)
                                .showGifs(false)
                                .showFolderView(false)
                                .enableSelectAll(true)
                                .enableImagePicker(true)
                                .setCameraPlaceholder(R.drawable.custom_camera)
                                .withOrientation(Orientation.UNSPECIFIED)
                                .pickPhoto(AddDailyReportPreview.this, CUSTOM_REQUEST_CODE);

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                            } else if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size();
                                FilePickerBuilder.getInstance()
                                        .setMaxCount(maxCount)
                                        .setSelectedFiles(photoPaths)
                                        .setActivityTheme(R.style.FilePickerTheme)
                                        .setActivityTitle("Please select media")
                                        .enableVideoPicker(false)
                                        .enableCameraSupport(true)
                                        .showGifs(false)
                                        .showFolderView(false)
                                        .enableSelectAll(true)
                                        .enableImagePicker(true)
                                        .setCameraPlaceholder(R.drawable.custom_camera)
                                        .withOrientation(Orientation.UNSPECIFIED)
                                        .pickPhoto(AddDailyReportPreview.this, CUSTOM_REQUEST_CODE);
                            }
                        }
                    }
                }
            }
        });

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (totalImage.size() > 0) {

                    Config_Engg.alertBox("File already Selected", AddDailyReportPreview.this);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else {

                    if (currentapiVersion <= 22) {
                        String[] zips = {".zip", ".rar"};
                        String[] pdfs = {".pdf"};
                        int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();

                        FilePickerBuilder.getInstance()
                                .setMaxCount(MAX_ATTACHMENT_COUNT)
                                .setSelectedFiles(docPaths)
                                .setActivityTheme(R.style.FilePickerTheme)
                                .setActivityTitle("Please select doc")
//                                    .addFileSupport("ZIP", zips)
                                .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)
                                .enableDocSupport(false)
                                .enableSelectAll(true)
                                .sortDocumentsBy(SortingTypes.name)
                                .withOrientation(Orientation.UNSPECIFIED)
                                .pickFile(AddDailyReportPreview.this);

                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                String[] zips = {".zip", ".rar"};
                                String[] pdfs = {".pdf"};
                                int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();

                                FilePickerBuilder.getInstance()
                                        .setMaxCount(MAX_ATTACHMENT_COUNT)
                                        .setSelectedFiles(docPaths)
                                        .setActivityTheme(R.style.FilePickerTheme)
                                        .setActivityTitle("Please select doc")
//                                            .addFileSupport("ZIP", zips)
                                        .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)
                                        .enableDocSupport(false)
                                        .enableSelectAll(true)
                                        .sortDocumentsBy(SortingTypes.name)
                                        .withOrientation(Orientation.UNSPECIFIED)
                                        .pickFile(AddDailyReportPreview.this);

                            }
                        }
                    }
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


            if (totalImage.size() > 0) {
                makeJsonImage();
            } else if (totalFile.size() > 0) {

                makeJsonFile();
            }


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
            request.addProperty("ReportFileJson", imageJson);
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

            case R.id.btn_menu_service_hour:
                intent = new Intent(AddDailyReportPreview.this, ServiceHourList.class);
                startActivity(intent);
                finish();
                return (true);

            case R.id.btn_menu_feedback:
                intent = new Intent(AddDailyReportPreview.this, FeedbackActivity.class);
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

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    totalImage.clear();
                    imagesPath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).toArray(new String[0]);

                    for (int i = 0; i < imagesPath.length; i++) {
                        photoPaths.add(imagesPath[i]);
                    }
                    totalImage.addAll(photoPaths);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    txt_image_show.setVisibility(View.VISIBLE);
                    txt_image_show.setText(totalImage.size() + " Image");
                    txt_image_show.setEnabled(false);

                    //    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));

                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    totalFile.clear();
                    // docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));


                    filePath = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).toArray(new String[0]);


                    for (int i = 0; i < filePath.length; i++) {
                        docPaths.add(filePath[i]);
                        myFile = new File(filePath[i]);

                        // Get length of file in bytes
                        long fileSizeInBytes = myFile.length();
                        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                        long fileSizeInMB = fileSizeInKB / 1024;

                        if (fileSizeInMB > 2) {
                            Config_Engg.alertBox("File Size more than 2 MB. Please Upload file less than 2 MB", AddDailyReportPreview.this);
                            myFile = null;
                            docPaths.clear();
                        } else {
                            totalFile.addAll(docPaths);
                        }
                    }

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    txt_image_show.setVisibility(View.VISIBLE);
                    txt_image_show.setText(totalFile.size() + " File");
                    txt_image_show.setEnabled(false);

                }
                break;
        }

    }

    private void makeJsonImage() {

        try {
            Gson gson = new Gson();
            JSONObject jsonObj = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < totalImage.size(); i++) {
                String imgPath = totalImage.get(i);
                final DecodeFileBean diary = getImageObjectFilledImage(imgPath);
                String case_json = gson.toJson(diary);
                JSONObject objImg = new JSONObject(case_json);
                array.put(objImg);
            }
            Log.e("ImagesJson", " cfcs " + jsonObj.toString());
            imageJson = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private DecodeFileBean getImageObjectFilledImage(String imgPath) {
        DecodeFileBean bean = new DecodeFileBean();
        int dotposition = imgPath.lastIndexOf(".");
        String FileExt = imgPath.substring(dotposition + 1, imgPath.length());
        String FileContent = decodeImage(imgPath);
        bean.setFileExt(FileExt);
        bean.setFileContent(FileContent);
        return bean;
    }

    private void makeJsonFile() {

        try {
            Gson gson = new Gson();
            JSONObject jsonObj = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < totalFile.size(); i++) {
                String FilePath = totalFile.get(i);
                final DecodeFileBean diary = getImageObjectFilledFile(FilePath);
                String case_json = gson.toJson(diary);
                JSONObject objImg = new JSONObject(case_json);
                array.put(objImg);
            }
            Log.e("ImagesJson", " cfcs " + jsonObj.toString());
            imageJson = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private DecodeFileBean getImageObjectFilledFile(String filePath) {

        DecodeFileBean bean = new DecodeFileBean();
        int dotposition = filePath.lastIndexOf(".");
        String FileExt = filePath.substring(dotposition + 1, filePath.length());
        String FileContent = decodeFile(filePath);
        bean.setFileExt(FileExt);
        bean.setFileContent(FileContent);
        return bean;

    }

    private String decodeFile(String filePath) {

        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(filePath);

            byte[] buffer = new byte[10240];//specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            encodedFile = output.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
    }
}
