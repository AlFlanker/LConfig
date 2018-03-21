package ru.yugsys.vvvresearch.lconfig.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import ru.yugsys.vvvresearch.lconfig.R;
import ru.yugsys.vvvresearch.lconfig.model.DataEntity.Device;

import java.util.List;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.ViewHolder> {
    public static final String LOGITUDE = "LOGITUDE";
    public static final String LATITUDE = "LATITUDE";
    List<Device> devices;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    private Context context;

    public MainContentAdapter(Context context) {
        this.context = context;
    }

    public void setDevices(List<Device> devices) {
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
        holder.typeOfLC5.setText(devices.get(finalPosition).type);
        holder.isOTAA.setText(devices.get(finalPosition).getIsOTTA() ? context.getText(R.string.yesotta) : context.getText(R.string.nootaa));
        holder.devEUI.setText(String.format("%s: %s", context.getText(R.string.prompt_devEUI), devices.get(finalPosition).getEui()));
        holder.appEUI.setText(String.format("%s: %s", context.getText(R.string.prompt_appEUI), devices.get(finalPosition).appeui));
        holder.appKey.setText(String.format("%s: %s", context.getText(R.string.prompt_appKey), devices.get(finalPosition).appkey));
        holder.nwkID.setText(String.format("%s: %s", context.getText(R.string.prompt_nwkID), devices.get(finalPosition).nwkid));
        holder.devAdr.setText(String.format("%s: %s", context.getText(R.string.prompt_devAdr), devices.get(finalPosition).devadr));
        holder.nwkSKey.setText(String.format("%s: %s", context.getText(R.string.prompt_nwkSKey), devices.get(finalPosition).nwkskey));
        holder.appSKey.setText(String.format("%s: %s", context.getText(R.string.prompt_appSKey), devices.get(finalPosition).appskey));
        holder.gps.setText(Double.toString(devices.get(finalPosition).Longitude) + "\u00B0, " +
                Double.toString(devices.get(finalPosition).Latitude) + "\u00B0");
        holder.outType.setText(String.format("%s %s", context.getText(R.string.out_type_device_is), devices.get(finalPosition).outType));
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
                intent.putExtra(LOGITUDE,devices.get(finalPosition).getLongitude());
                intent.putExtra(LATITUDE,devices.get(finalPosition).getLatitude());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        public TextView outType;
        public ExpandableLinearLayout expandableLayout;
        public ImageButton gpsLocation;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.lc5_item_list, parent, false));
            typeOfLC5 = itemView.findViewById(R.id.lc5_type);
            isOTAA = itemView.findViewById(R.id.lc5_isOTAA);
            devEUI = itemView.findViewById(R.id.lc5_deveui);
            appEUI = itemView.findViewById(R.id.lc5_appEUI);
            appKey = itemView.findViewById(R.id.lc5_appKey);
            nwkID = itemView.findViewById(R.id.lc5_nwkID);
            devAdr = itemView.findViewById(R.id.lc5_devAdr);
            nwkSKey = itemView.findViewById(R.id.lc5_nwkSKey);
            appSKey = itemView.findViewById(R.id.lc5_appSKey);
            gps = itemView.findViewById(R.id.lc5_gps);
            outType = itemView.findViewById(R.id.lc5_out_type);
            buttonLayout = (LinearLayout) itemView.findViewById(R.id.button);
            triangleView = itemView.findViewById(R.id.button_triangle);
            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            gpsLocation = itemView.findViewById(R.id.gps_device_location);
        }
    }
}
