package com.example.giftme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftme.R;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    Context context;
    Activity activity;
    TextView notificationCountTV;

    public NotificationsAdapter(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
        notificationCountTV = ((Activity) context).findViewById(R.id.notification_count);
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.notification_card, parent, false);
        return new ViewHolder(notificationView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        //populate
        //set views
        TextView notif = holder.notifTV;
        notif.setText("Notif");
    }

    @Override
    public int getItemCount() {
//        return notifs.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView notifTV;
        public View view;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notifTV = itemView.findViewById(R.id.notification_message);
            linearLayout = itemView.findViewById(R.id.notification_lv);
        }
    }
}
