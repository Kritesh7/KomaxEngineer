package com.cfcs.komaxengineer.background;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.LoginActivity;
import com.cfcs.komaxengineer.activity_engineer.ServiceHourList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DeleteServiceDetails extends AsyncTask<String, String, String> {

    private static String SOAP_ACTION = "http://cfcs.co.in/AppEngineerServiceHrDelete";
    private static String NAMESPACE = "http://cfcs.co.in/";
    private static String METHOD_NAME = "AppEngineerServiceHrDelete";
    private static String URL = Config_Engg.BASE_URL + "Engineer/WebApi/EngineerWebService.asmx?";

    String ContactList = "", msgstatus = "", status = "";
    int flag;
    Context context;
    ProgressDialog progressDialog;
    String LoginStatus;
    String invalid = "LoginFailed";

    public DeleteServiceDetails(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "", "Please wait...", true, false, null);
    }

    @Override
    protected String doInBackground(String... params) {

        String ServiceHrID = "", EngineerID = "", AuthCode = "";
        ServiceHrID = params[0];
        EngineerID = params[1];
        AuthCode = params[2];

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("ServiceHrID", ServiceHrID);
        request.addProperty("EngineerID", EngineerID);
        request.addProperty("AuthCode", AuthCode);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject result = (SoapObject) envelope.bodyIn;
            if (result != null) {
                ContactList = result.getProperty(0).toString();
                JSONArray jsonArray = new JSONArray(ContactList);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if (jsonObject.has("status")) {
                    LoginStatus = jsonObject.getString("status");
                    msgstatus = jsonObject.getString("MsgNotification");
                    if (LoginStatus.equals(invalid)) {

                        flag = 4;
                    } else {

                        flag = 1;
                    }
                }

            } else {
                flag = 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (flag == 1) {
            Config_Engg.toastShow(msgstatus, context);

            Intent intent = new Intent(context, ServiceHourList.class);
            intent.putExtra("status", status);
            intent.putExtra("DateFrom1","");
            intent.putExtra("DateTo1","");
            intent.putExtra("PriorityID","0");
            intent.putExtra("HeaderName","Open");
            context.startActivity(intent);

        } else if (flag == 3) {
            Config_Engg.toastShow("No Response", context);

        } else if (flag == 4) {
            Config_Engg.toastShow(msgstatus, context);
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
        }
        progressDialog.dismiss();

    }
}
