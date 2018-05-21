package ru.yugsys.vvvresearch.lconfig.views;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.Toast;
import org.json.JSONException;
import ru.yugsys.vvvresearch.lconfig.App;
import ru.yugsys.vvvresearch.lconfig.Logger;
import ru.yugsys.vvvresearch.lconfig.R;
import ru.yugsys.vvvresearch.lconfig.Services.*;
import ru.yugsys.vvvresearch.lconfig.model.DataEntity.DataDevice;
import ru.yugsys.vvvresearch.lconfig.model.DataEntity.DeviceEntry;
import ru.yugsys.vvvresearch.lconfig.model.Interfaces.Model;
import ru.yugsys.vvvresearch.lconfig.model.Interfaces.ModelListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainViewable,
        View.OnClickListener,
        ModelListener.OnNFCConnected,
        AsyncTaskCallBack.ReadCallBack,
        ModelListener.OnDataRecived,
        DetectInternetConnection.ConnectivityReceiverListener {


    public static final String ADD_NEW_DEVICE_MODE = "AddNewDeviceMode";
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private DeviceEntry currentDevice;
    private DataDevice currentDataDevice;
    private byte[] systemInfo;
    private IntentFilter intentFilter;
    private DetectInternetConnection detectInternetConnection;
    Logger log = Logger.getInstance();
    private MainContentAdapter adapter;
    private RecyclerView recyclerView;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int ERROR = 0;
    private static final int MESSAGE = 1;

    private ProgressBar progressBar;
    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        byte[] arr;
        Tag tagFromIntent;
        currentDataDevice = new DataDevice();
        if ("android.nfc.action.TECH_DISCOVERED".equals(intent.getAction())) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tagFromIntent = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            currentDataDevice.setCurrentTag(tagFromIntent);
            systemInfo = NFCCommand.SendGetSystemInfoCommandCustom(tagFromIntent, currentDataDevice);
            if (!DataDevice.DecodeSystemInfoResponse(systemInfo, currentDataDevice)) {
                return;
            }
            ReadTask readTask = new ReadTask(null,null);
            readTask.subscribe(this);
            readTask.execute(currentDataDevice);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        recyclerView = findViewById(R.id.lc5_recycler_view);
        adapter = new MainContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.MainProgressBar);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        //Connect presenter to main view
        //mainPresenter = new MainPresenter(((App) getApplication()).getModel());
        //mainPresenter.bind(this);
        // mainPresenter.fireUpdateDataForView();

        // WithoutPresenter
        App.getInstance().getModel().getEventManager().subscribeOnDataRecive(this);
        App.getInstance().getModel().loadAllDeviceDataByProperties(Model.Properties.DateOfChange, Model.Direction.Reverse);
        //getPremissionGPS();
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter != null && mAdapter.isEnabled()) {
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            mFilters = new IntentFilter[]{ndef,};
            mTechLists = new String[][]{new String[]{android.nfc.tech.NfcV.class.getName()}};
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getPremissionGPS();
        /*Apps targeting Android 7.0 (API level 24) and higher
         do not receive CONNECTIVITY_ACTION broadcasts if they declare the broadcast receiver in their manifest.
         Apps will still receive CONNECTIVITY_ACTION broadcasts if they register their BroadcastReceiver
          with Context.registerReceiver() and that context is still valid
          */
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        detectInternetConnection = new DetectInternetConnection();
        registerReceiver(detectInternetConnection, intentFilter);
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        App.getInstance().BindConnectivityListener(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
        recyclerView.scrollToPosition(0);
        ((App) getApplication()).getModel().loadAllDeviceDataByProperties(Model.Properties.DateOfChange, Model.Direction.Reverse);
       recyclerView.getRootView().startAnimation(AnimationUtils.loadAnimation(recyclerView.getContext(),R.anim.push_elem));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            GPSTracker gpsTracker = GPSTracker.instance();
            gpsTracker.setContext(this);
            gpsTracker.OnStartGPS();
        }
        try {
            test();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getPremissionGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    PERMISSION_REQUEST_CODE);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.NFC},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_clearBD) {
            ((App) getApplication()).getModel().clearDataBase();
        }
        else if(id == R.id.action_CopyDB){
            //new DataBaseMigrate(((App)getApplication()).getDaoSession()).migrate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentForView(List<DeviceEntry> devices) {
        adapter.setDevices(devices);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            Intent addEditIntent = new Intent(this, AddEditActivity.class);
            addEditIntent.putExtra(ADD_NEW_DEVICE_MODE, true);
            startActivity(addEditIntent);
//            showSnack(DetectInternetConnection.isConnected(this));
        }
    }


    @Override
    public void OnNFCConnected(DeviceEntry dev) {
        log.d("NFC", dev.getType());
    }


    @Override
    public void OnDeviceEntry(DeviceEntry deviceEntry) {
        if(deviceEntry!=null) {
            ((App) getApplication()).getModel().setCurrentDevice(deviceEntry);
            Intent addActivity = new Intent(this, AddEditActivity.class);
            addActivity.putExtra(ADD_NEW_DEVICE_MODE, Boolean.FALSE);
            currentDataDevice = null;
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            startActivity(addActivity);
        }
        else{
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            showDiffrentSnackBar(getString(R.string.Incorrect), ERROR);
//            Toast.makeText(getApplicationContext(), getString(R.string.Incorrect), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnDataRecived(List<DeviceEntry> devList) {
        this.setContentForView(devList);
    }

    @Override
    public void OnNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = getString(R.string.DetectInternetConnection);
            color = Color.WHITE;
        } else {
            message = getString(R.string.ConnectivityChange);
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    private void showDiffrentSnackBar(String msg, int me) {

        int color;
        if (me == 1) {

            color = Color.WHITE;
        } else {

            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), msg, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    public void test() throws IOException {
        try {
            new RestManager().new GetRequest("token=1c68a488ec0d4dde80439e9627d23154&count=60&offset=0&order=desc").execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class RestRequest extends AsyncTask<Void, Void, String> {

        String resultString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String myURL = "https://bs.net868.ru:20010/externalapi/";
                String parammetrs = "appdata?token=1c68a488ec0d4dde80439e9627d23154&count=10&offset=0&startDate=2018-05-17T10:50:33Z&endDate=2018-05-18T10:50:33Z&order=desc";

                byte[] data = null;
                InputStream is = null;

                try {
                    URL url = new URL(myURL + parammetrs);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Length", "" + Integer.toString(parammetrs.getBytes().length));
//                    OutputStream os = conn.getOutputStream();
//                    data = parammetrs.getBytes("UTF-8");
//                    os.write(data);
//                    data = null;
                    conn.connect();
                    int responseCode = conn.getResponseCode();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    if (responseCode == 200) {
                        is = conn.getInputStream();

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        data = baos.toByteArray();
                        resultString = new String(data, "UTF-8");
                    } else {
                    }


                } catch (MalformedURLException e) {

                    //resultString = "MalformedURLException:" + e.getMessage();
                } catch (IOException e) {

                    //resultString = "IOException:" + e.getMessage();
                } catch (Exception e) {

                    //resultString = "Exception:" + e.getMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (resultString != null) {
                Toast toast = Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }






}
