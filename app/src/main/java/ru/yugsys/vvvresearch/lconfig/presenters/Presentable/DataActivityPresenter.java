package ru.yugsys.vvvresearch.lconfig.presenters.Presentable;

import android.util.Log;
import org.greenrobot.greendao.query.Query;
import ru.yugsys.vvvresearch.lconfig.model.DaoSession;
import ru.yugsys.vvvresearch.lconfig.model.Dev_Data;
import ru.yugsys.vvvresearch.lconfig.model.Dev_DataDao;
import ru.yugsys.vvvresearch.lconfig.model.Interfaces.BaseModel;
import ru.yugsys.vvvresearch.lconfig.model.Interfaces.ModelListener;
import ru.yugsys.vvvresearch.lconfig.views.Interfaces.IEditView;

import java.util.List;

import static android.content.ContentValues.TAG;


public class DataActivityPresenter extends ListPresenter<BaseModel, IEditView> {


    private Dev_DataDao mDevDao;
    private Query<Dev_Data> devsQuery;
    public DaoSession daoSession;
    private List<Dev_Data> devList;
    private boolean NFCenable;

    @Override
    public void loadData() {
        model.load();
        // devList = devsQuery.list();

    }

    public void AddDev(String n, String g) {
        if (n.trim().equals("") || g.trim().equals("")) {
            getView().ShowError();
        }
        Dev_Data dev = new Dev_Data();
        dev.setName(n);
        dev.setGeo(Float.parseFloat(g));
        this.model.save(dev);
        this.loadData();
        //updateView();
    }

    @Override
    public void resetStat() {

    }

    @Override
    public void updateView() {
        getView().update(devList);
    }

    @Override
    public void getSession(DaoSession s) {
        this.daoSession = s;

    }
    // Implements method from Model Listener

    @Override
    public void OnDataRecived(List<Dev_Data> devList) {
        Log.d("MyTag", "OnDataRecived: " + " work!");
        this.devList = devList;
        updateView();
    }

    @Override
    public void OnNFCconnected(boolean flag) {
        this.NFCenable = flag;

    }

    @Override
    public void OnCheckedDevData(boolean check) {
        Log.d("MyTag", "OnCheckedDevData: " + " work!" + " " + Boolean.toString(check));

    }
}
