package ru.yugsys.vvvresearch.lconfig.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import ru.yugsys.vvvresearch.lconfig.R;
import ru.yugsys.vvvresearch.lconfig.model.DataEntity.DeviceEntry;
import java.util.List;
import java.util.Locale;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.ViewHolder> {
    List<DeviceEntry> devices;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private Context context;
    private long oldsize =-1;
    public MainContentAdapter(Context context) {
        this.context = context;
    }

    public void setDevices(List<DeviceEntry> devices) {
        this.devices = devices;
        for (int i = 0; i < devices.size(); i++) {
            expandState.append(i, false);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int finalPosition = holder.getAdapterPosition();
        if(!devices.get(finalPosition).getIsOTTA()) {
            holder.typeOfLC5.setText(devices.get(finalPosition).getType());
            holder.isOTAA.setText(devices.get(finalPosition).getIsOTTA() ? context.getText(R.string.yesotta) : context.getText(R.string.nootaa));
            holder.devEUI.setText(devices.get(finalPosition).getEui());
            holder.appEUI.setText(devices.get(finalPosition).getAppeui());
            holder.appKey.setText(devices.get(finalPosition).getAppkey());
            holder.nwkID.setText(devices.get(finalPosition).getNwkid());
            holder.devAdrExp.setText(devices.get(finalPosition).getDevadrMSBtoLSB().toUpperCase());
            holder.devAdr.setText(!((devices.get(finalPosition).getComment().isEmpty())) ? devices.get(finalPosition).getComment() : devices.get(finalPosition).getDevadrMSBtoLSB().toUpperCase());
            holder.nwkSKey.setText(devices.get(finalPosition).getNwkskey());
            holder.appSKey.setText(devices.get(finalPosition).getAppskey());
            holder.gps.setText(String.format(Locale.ENGLISH, "%.6f°, %.6f°",
                    devices.get(finalPosition).getLongitude(),
                    devices.get(finalPosition).getLatitude()));
            holder.isSync.setText(context.getText(R.string.Synchronized) + " " + devices.get(finalPosition).getIsSyncServer().toString());
            if (devices.get(finalPosition).getIsSyncServer()) {
                holder.isSync.setTextColor(Color.BLUE);
            } else {
                holder.isSync.setTextColor(Color.RED);
            }
            holder.outputType.setText(devices.get(finalPosition).getOutType());
            holder.comment.setText((devices.get(finalPosition).getComment().isEmpty()) ? "" : devices.get(finalPosition).getComment());
            holder.date.setText(devices.get(finalPosition).getDateOfLastChange().toString());
        }
        else{

            holder.typeOfLC5.setText(devices.get(finalPosition).getType());
            holder.isOTAA.setText(devices.get(finalPosition).getIsOTTA() ? context.getText(R.string.yesotta) : context.getText(R.string.nootaa));
            holder.devEUI.setText(devices.get(finalPosition).getEui());
            holder.appEUI.setText(devices.get(finalPosition).getAppeui());
            holder.appKey.setText(devices.get(finalPosition).getAppkey());
            holder.nwkID.setText(devices.get(finalPosition).getNwkid());
            holder.devAdrExp.setText(devices.get(finalPosition).getDevadrMSBtoLSB().toUpperCase());
            holder.devAdr.setText(!((devices.get(finalPosition).getComment().isEmpty())) ? devices.get(finalPosition).getComment() : devices.get(finalPosition).getDevadrMSBtoLSB().toUpperCase());
            holder.appSKeyLabel.setVisibility(View.GONE);
            holder.appSKey.setVisibility(View.GONE);
            holder.nwkSKeyLabel.setVisibility(View.GONE);
            holder.nwkSKey.setVisibility(View.GONE);
            holder.outputType.setText(devices.get(finalPosition).getOutType());

            holder.gps.setText(String.format(Locale.ENGLISH, "%.6f°, %.6f°",
                    devices.get(finalPosition).getLongitude(),
                    devices.get(finalPosition).getLatitude()));
            holder.isSync.setText(context.getText(R.string.Synchronized) + " " + devices.get(finalPosition).getIsSyncServer().toString());
            if (devices.get(finalPosition).getIsSyncServer()) {
                holder.isSync.setTextColor(Color.BLUE);
            } else {
                holder.isSync.setTextColor(Color.RED);
            }
            holder.comment.setText((devices.get(finalPosition).getComment().isEmpty()) ? "" : devices.get(finalPosition).getComment());
            holder.date.setText(devices.get(finalPosition).getDateOfLastChange().toString());
        }
        holder.expandableLayout.setInRecyclerView(true);
        //holder.expandableLayout.setBackgroundColor(context.(R.color.colorPrimary));
        holder.expandableLayout.setInterpolator(Utils.createInterpolator(Utils.DECELERATE_INTERPOLATOR));
        if (holder.expandableLayout.isExpanded())
            holder.expandableLayout.setExpanded(expandState.get(finalPosition));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                UtilAnimators.createRotateAnimator(holder.triangleView, 0f, 180f).start();
                expandState.put(finalPosition, true);
            }

            @Override
            public void onPreClose() {
                UtilAnimators.createRotateAnimator(holder.triangleView, 180f, 0f).start();
                expandState.put(finalPosition, false);
            }
        });

        holder.buttonLayout.setRotation(expandState.get(finalPosition) ? 180f : 0f);
        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLayout);
            }
        });
        holder.gpsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra(MapsActivity.SHOW_MODE_ALL, false);
                intent.putExtra(MapsActivity.LOGITUDE,devices.get(finalPosition).getLongitude());
                intent.putExtra(MapsActivity.LATITUDE,devices.get(finalPosition).getLatitude());
                intent.putExtra(MapsActivity.COMMENT, devices.get(finalPosition).getComment());
                context.startActivity(intent);

            }
        });

//        setAnimation(holder.itemView,finalPosition);

    }


    private void setAnimation(View viewAnimate, int pos){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_elem);
            viewAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView devAdrExp;
        public LinearLayout buttonLayout;
        public View triangleView;
        public TextView typeOfLC5;
        public TextView isOTAA;
        public TextView devEUI;
        public TextView appEUI;
        public TextView appKey;
        public TextView nwkID;
        public TextView devAdr;
        public TextView nwkSKey;
        public TextView appSKey;
        public TextView gps;
        public TextView isSync;
        public TextView outputType;
        public TextView date;
        public TextView comment;
        public TextView nwkSKeyLabel;
        public TextView appSKeyLabel;
        public TextView additionalInfo;
        public TextView commentLabel;
        public TextView dateLabel;
        public ExpandableLinearLayout expandableLayout;
        public ImageButton gpsLocation;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.lc5_item_list, parent, false));
            additionalInfo = itemView.findViewById(R.id.lc5_AdditionalInfo);
            commentLabel = itemView.findViewById(R.id.lc5_commentLabel);
            dateLabel = itemView.findViewById(R.id.lc5_dateLabel);
            typeOfLC5 = itemView.findViewById(R.id.lc5_type);
            isOTAA = itemView.findViewById(R.id.lc5_isOTAA);
            devEUI = itemView.findViewById(R.id.lc5_deveui);
            appEUI = itemView.findViewById(R.id.lc5_appEUI);
            appKey = itemView.findViewById(R.id.lc5_appKey);
            nwkID = itemView.findViewById(R.id.lc5_nwkID);
            devAdr = itemView.findViewById(R.id.lc5_devAdr);
            devAdrExp = itemView.findViewById(R.id.lc5_devAdrExp);
            nwkSKey = itemView.findViewById(R.id.lc5_nwkSKey);
            appSKey = itemView.findViewById(R.id.lc5_appSKey);
            gps = itemView.findViewById(R.id.lc5_gps);
            isSync = itemView.findViewById(R.id.lc5_out_type);
            outputType = itemView.findViewById(R.id.lc5_outputType_ch);
            buttonLayout = (LinearLayout) itemView.findViewById(R.id.button);
            triangleView = itemView.findViewById(R.id.button_triangle);
            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            gpsLocation = itemView.findViewById(R.id.gps_device_location);
            date = itemView.findViewById(R.id.lc5_date);
            comment = itemView.findViewById(R.id.lc5_comment);
            nwkSKeyLabel =itemView.findViewById(R.id.lc5_nwkSKeyLabel);
            appSKeyLabel =itemView.findViewById(R.id.lc5_appSKeyLabel);


        }
    }
}
