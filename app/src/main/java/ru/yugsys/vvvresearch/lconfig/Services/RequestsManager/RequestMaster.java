package ru.yugsys.vvvresearch.lconfig.Services.RequestsManager;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;
import ru.yugsys.vvvresearch.lconfig.Services.RequestsManager.Strategy.DELETE;
import ru.yugsys.vvvresearch.lconfig.Services.RequestsManager.Strategy.GET;
import ru.yugsys.vvvresearch.lconfig.Services.RequestsManager.Strategy.POST;
import ru.yugsys.vvvresearch.lconfig.Services.RequestsManager.Strategy.REST;
import ru.yugsys.vvvresearch.lconfig.model.DataEntity.DeviceEntry;
import ru.yugsys.vvvresearch.lconfig.model.DataEntity.GeoData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class RequestMaster {

    public REST getRequest() {
        if (checkParameter()) {
            switch (func) {
                case AppData:
                    this.request = new GET(HostAPI, parameters);
                    break;
                case CreateDevice:
                    this.request = new POST(HostAPI, parameters, object);
                    break;
            }
            return request;

        }
        return null;

    }

    public enum REST_FUNCTION {
        CreateDevice, DeleteDevice, AppData, CommandTypesOfDevice, SendCommand
    }

    protected final static EnumMap<REST_FUNCTION, String> net868API;
    private static String HostAPI;

    static {
        net868API = new EnumMap<REST_FUNCTION, String>(REST_FUNCTION.class);
        net868API.put(REST_FUNCTION.AppData, "appdata?");
        net868API.put(REST_FUNCTION.CreateDevice, "createdevice?");
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private REST request;
    private JSONObject object;
    private final static EnumMap<REST.REST_PRM, String> parameters = new EnumMap<>(REST.REST_PRM.class);


    private static REST_FUNCTION func;

    /**
     * Default constructor. Private to prevent direct instantiation.
     *
     * @see #newInstance()
     */
    private RequestMaster() {

    }

    /**
     * Returns a new, empty builder.
     *
     * @return the new {@code RequestMaster}
     */
    public static RequestMaster newInstance() {
        return new RequestMaster();
    }

    public static RequestMaster newInstance(String hostAPI, REST_FUNCTION function) {
        func = function;
        HostAPI = (hostAPI + net868API.get(function));
        return new RequestMaster();
    }

    public RequestMaster addQueryParamets(REST.REST_PRM prm, String value) {
        Assert.notNull(value, "'value' must not be null");
        if (!(REST.REST_PRM.startDate.equals(prm) || REST.REST_PRM.endDate.equals(prm))) {
            parameters.put(prm, value);
        }
        return this;
    }

    public RequestMaster addQueryParamets(REST.REST_PRM prm, Date value) {
        Assert.notNull(value, "'Date' must not be null");
        simpleDateFormat.setTimeZone(timeZone);
        parameters.put(prm, simpleDateFormat.format(value));
        return this;
    }

    public RequestMaster addDeviceEntry(DeviceEntry deviceEntry) {
        Assert.notNull(deviceEntry, "'deviceEntry' must not be null");
        if (func.equals(REST_FUNCTION.DeleteDevice)) {
            this.object = new JSONObject();
            try {
                object.put("token", parameters.get(REST.REST_PRM.token));
                object.put("eui", parameters.get(REST.REST_PRM.deviceEui));
                return this;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.object = convert2JSON(deviceEntry, parameters.get(REST.REST_PRM.token));
        return this;
    }

    // Here different strategies are applied
    public void execute() {
        if (checkParameter()) {
            switch (func) {
                case AppData:
                    this.request = new GET(HostAPI, parameters);
                    break;
                case CreateDevice:
                    this.request = new POST(HostAPI, parameters, object);
                    break;
                case DeleteDevice:
                    this.request = new DELETE(HostAPI, parameters, object);
            }
            this.request.send();

        }


    }

    private boolean checkParameter() {
        boolean check = false;
        if (func.equals(REST_FUNCTION.AppData)) {
            if (parameters.get(REST.REST_PRM.token) != null) {
                Pattern pattern = Pattern.compile("((\\d|[a-f]|[A-F]){32})");
                check = pattern.matcher(parameters.get(REST.REST_PRM.token)).matches();
            }
            if (parameters.get(REST.REST_PRM.count) != null) {
                check &= Integer.parseInt(parameters.get(REST.REST_PRM.count)) > 0;
            }
            if (parameters.get(REST.REST_PRM.offset) != null) {
                check &= Integer.parseInt(parameters.get(REST.REST_PRM.offset)) >= 0;
            }
            if (parameters.get(REST.REST_PRM.order) != null) {
                check &= parameters.get(REST.REST_PRM.order).equals("desc");
            }

            if ((parameters.get(REST.REST_PRM.startDate) != null) && (parameters.get(REST.REST_PRM.endDate) != null)) {
                try {
                    check &= simpleDateFormat.parse(parameters.get(REST.REST_PRM.startDate)).before(simpleDateFormat.parse(parameters.get(REST.REST_PRM.endDate)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (parameters.get(REST.REST_PRM.deviceEui) != null) {
                Pattern pattern = Pattern.compile("((\\d|[a-f]|[A-F])(\\d|[a-f]|[A-F])-){7}((\\d|[a-f]|[A-F])(\\d|[a-f]|[A-F])){1}");
                check &= pattern.matcher(parameters.get(REST.REST_PRM.deviceEui)).matches();
            }
            return check;
        } else if (func.equals(REST_FUNCTION.CreateDevice)) {
            if (parameters.get(REST.REST_PRM.token) != null) {
                Pattern pattern = Pattern.compile("((\\d|[a-f]|[A-F]){32})");
                return pattern.matcher(parameters.get(REST.REST_PRM.token)).matches();
            }
            return false;
        } else if (func.equals(REST_FUNCTION.DeleteDevice)) {
            if (parameters.get(REST.REST_PRM.token) != null) {
                Pattern pattern = Pattern.compile("((\\d|[a-f]|[A-F]){32})");
                check = pattern.matcher(parameters.get(REST.REST_PRM.token)).matches();
            }
            if (parameters.get(REST.REST_PRM.deviceEui) != null) {
                Pattern pattern = Pattern.compile("((\\d|[a-f]|[A-F])(\\d|[a-f]|[A-F])-){7}((\\d|[a-f]|[A-F])(\\d|[a-f]|[A-F])){1}");
                check &= pattern.matcher(parameters.get(REST.REST_PRM.deviceEui)).matches();
                return check;
            }
        }
        return check;
    }

    private JSONObject convert2JSON(DeviceEntry dev, String token) {
        JSONObject jsonObject = new JSONObject();
        JSONObject device = new JSONObject();
        JSONObject model = new JSONObject();
        JSONObject geo;

        try {
            if (!dev.getIsOTTA()) {
                geo = new JSONObject();
                //Add method to convert Location -> addres (Google API)
                geo.put("countryddress", "Россия");
                geo.put("region", "КраснодарскийКрай");
                geo.put("city", "Краснодар");
                geo.put("street", "Московская");

                model.put("name", "LC5xx");
                model.put("version", "1.0");

                device.put("activationType", "ABP");
                device.put("alias", (dev.getComment().isEmpty()) ? "LC5xx" : dev.getComment().replace(" ", "_"));
                device.put("eui", correctField(dev.getEui().toLowerCase()));
                device.put("applicationEui", correctField(dev.getAppeui().toLowerCase()));
                device.put("devAddr", correctField(dev.getDevadrMSBtoLSB()));
                device.put("networkSessionKey", dev.getNwkskey());
                device.put("applicationSessionKey", dev.getAppskey());
                device.put("access", "Private");
                device.put("loraClass", "c");
                device.put("model", model);
                device.put("address", geo);

                jsonObject.put("token", token);
                jsonObject.put("device", device);
            } else {

                model.put("name", "LC5xx");
                model.put("version", "1.0");

                device.put("activationType", "OTAA");
                device.put("alias", dev.getComment().isEmpty() ? "LC5xx" : dev.getComment().replace(" ", "_"));
                device.put("eui", correctField(dev.getEui().toLowerCase()));
                device.put("applicationEui", correctField(dev.getAppeui().toLowerCase()));
                device.put("appKey", dev.getAppkey());
                device.put("access", "Private");
                device.put("model", model);

                jsonObject.put("token", token);
                jsonObject.put("device", device);
            }

        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }
        return jsonObject;
    }

    public static String convert2StringJSON(DeviceEntry dev, String token) {
        JSONObject jsonObject = new JSONObject();
        JSONObject device = new JSONObject();
        JSONObject model = new JSONObject();
        JSONObject geo;
        try {
            Log.d("geoService", "convert2StringJSON");
            if (!dev.getIsOTTA()) {
                geo = new JSONObject();
                //Add method to convert Location -> addres (Google API)
                geo.put("countryaddress", dev.getCounty());
                geo.put("region", dev.getRegion());
                geo.put("city", dev.getCity());
                geo.put("street", dev.getAddress());


                model.put("name", "LC5xx");
                model.put("version", "1.0");

                device.put("activationType", "ABP");
                device.put("alias", (dev.getComment().isEmpty()) ? "LC5xx" : dev.getComment().replace(" ", "_"));
                device.put("eui", correctField(dev.getEui().toLowerCase()));
                device.put("applicationEui", correctField(dev.getAppeui().toLowerCase()));
                device.put("devAddr", correctField(dev.getDevadrMSBtoLSB().toLowerCase()));
                device.put("networkSessionKey", dev.getNwkskey());
                device.put("applicationSessionKey", dev.getAppskey());
                device.put("access", "Private");
                device.put("loraClass", "c");
                device.put("model", model);
                device.put("address", geo);

                jsonObject.put("token", token);
                jsonObject.put("device", device);
            } else {

                model.put("name", "LC5xx");
                model.put("version", "1.0");

                device.put("activationType", "OTAA");
                device.put("alias", (dev.getComment().isEmpty()) ? "LC5xx" : dev.getComment().replace(" ", "_"));
                device.put("eui", correctField(dev.getEui().toLowerCase()));
                device.put("applicationEui", correctField(dev.getAppeui().toLowerCase()));
                device.put("appKey", dev.getAppkey().toLowerCase());
                device.put("access", "Private");
                device.put("model", model);

                jsonObject.put("token", token);
                jsonObject.put("device", device);
            }

        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }
        Log.d("geoService", "return jsonObject");
        return jsonObject.toString();
    }

    private static String correctField(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < field.length() - 2; i += 2) {
            stringBuilder.append(field.substring(i, i + 2)).append("-");
        }
        stringBuilder.append(field.substring(field.length() - 2, field.length()));
        return stringBuilder.toString();
    }


}






