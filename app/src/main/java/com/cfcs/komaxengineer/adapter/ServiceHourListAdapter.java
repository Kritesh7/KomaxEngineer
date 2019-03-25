package com.cfcs.komaxengineer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.cfcs.komaxengineer.Config_engineer.Config_Engg;
import com.cfcs.komaxengineer.R;
import com.cfcs.komaxengineer.activity_engineer.ManageContact;
import com.cfcs.komaxengineer.activity_engineer.NewMachine;
import com.cfcs.komaxengineer.activity_engineer.ServiceHourDetail;
import com.cfcs.komaxengineer.activity_engineer.ServiceHourList;
import com.cfcs.komaxengineer.activity_engineer.ServiceHours;
import com.cfcs.komaxengineer.background.DeleteContact;
import com.cfcs.komaxengineer.background.DeleteServiceDetails;
import com.cfcs.komaxengineer.model.ServiceHourListDataModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ServiceHourListAdapter extends RecyclerView.Adapter<ServiceHourListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ServiceHourListDataModel> serviceist = new ArrayList<ServiceHourListDataModel>();

    public ServiceHourListAdapter(ServiceHourList serviceHourList, ArrayList<ServiceHourListDataModel> serviceist) {
        this.serviceist = serviceist;
        this.context = serviceHourList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.service_hour_list_layout, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);


        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ServiceHourListDataModel currentListData = getItem(position);

        holder.txt_ServiceHrDate.setText(currentListData.getServiceHrDate());
        holder.txt_today_availability.setText(currentListData.getTodayStatusName());
        holder.txt_tomorrow_plan.setText(currentListData.getTomorrowPlanName());
        holder.txt_total_hours.setText(currentListData.getTotalHrs());

        holder.action_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.action_dots, position, currentListData.getServiceHrID());
            }
        });

        holder.card_view_service_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ServiceHourDetail.class);
                intent.putExtra("ServiceHrID", currentListData.getServiceHrID());
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });


    }



    private void showPopupMenu(ImageView action_dots, int position, String serviceHrID) {

        PopupMenu popup = new PopupMenu(context, action_dots);

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_action_service_hour, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, serviceHrID));
        popup.show();

        Menu popupMenu = popup.getMenu();
        popupMenu.findItem(R.id.action_edit_record).setVisible(true);
        popupMenu.findItem(R.id.action_delete).setVisible(true);
       // String EditDelete = IsEditDelete;
//        if (EditDelete.compareTo("false") == 0) {
//            popupMenu.findItem(R.id.action_edit_record).setVisible(false);
//        } else {
//            popupMenu.findItem(R.id.action_edit_record).setVisible(true);
//        }


    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;
        String serviceHrID;


        public MyMenuItemClickListener(int position, String serviceHrID) {
            this.position = position;
            this.serviceHrID = serviceHrID;

        }

        public MyMenuItemClickListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            Intent intent = new Intent();
            switch (item.getItemId()) {
                case R.id.action_edit_record:
                    intent.setClass(context, ServiceHours.class);
                    intent.putExtra("ServiceHrID", serviceHrID);
                    intent.putExtra("Edit", "Edit");
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    return true;
                case R.id.action_delete:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setMessage("Do you want to delete this service hour?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            String EngineerID = Config_Engg.getSharedPreferences(context, "pref_Engg", "EngineerID", "");
                            String ServiceHrID = serviceHrID;
                            String AuthCode = Config_Engg.getSharedPreferences(context, "pref_Engg", "AuthCode", "");

                            String deleteServiceDetails[] = {ServiceHrID, EngineerID, AuthCode};

                            new DeleteServiceDetails(context).execute(deleteServiceDetails);
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    alertDialog.show();
                    return true;
                default:
            }

            return false;
        }
    }

    @Override
    public int getItemCount() {
        return serviceist.size();
    }

    public ServiceHourListDataModel getItem(int position) {

        return serviceist.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_ServiceHrDate,txt_today_availability,txt_tomorrow_plan,txt_total_hours;
        ImageView action_dots;
        LinearLayout card_view_service_hour;


        public MyViewHolder(View itemView) {
            super(itemView);

            txt_ServiceHrDate = itemView.findViewById(R.id.txt_ServiceHrDate);
            txt_today_availability = itemView.findViewById(R.id.txt_today_availability);
            txt_tomorrow_plan = itemView.findViewById(R.id.txt_tomorrow_plan);
            txt_total_hours = itemView.findViewById(R.id.txt_total_hours);
            action_dots = itemView.findViewById(R.id.action_dots);
            card_view_service_hour = itemView.findViewById(R.id.card_view_service_hour);

        }
    }
}
