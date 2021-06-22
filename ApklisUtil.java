package your.app.package_id;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class ApklisUtil {

    public final String DOWNLOADS = "\"download_count\":";
    public final String SALES = "\"sale_count\":";
    public final String RATING = "\"rating\":";
    public final String PRICE = "\"price\":";
    public final String REVIEWS = "\"reviews_count\":";
    public final String REVIEWS_STAR_1 = "\"reviews_star_1\":";
    public final String REVIEWS_STAR_2 = "\"reviews_star_2\":";
    public final String REVIEWS_STAR_3 = "\"reviews_star_3\":";
    public final String REVIEWS_STAR_4 = "\"reviews_star_4\":";
    public final String REVIEWS_STAR_5 = "\"reviews_star_5\":";

    Context CONTEXT;
    String APP_PACKAGE;

    public ApklisUtil(final Context CONTEXT, String APP_PACKAGE) {

        this.CONTEXT = CONTEXT;
        this.APP_PACKAGE = APP_PACKAGE;

    }


    public void startLookingForUpdates(int SECONDS){

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("APP_PACKAGE",APP_PACKAGE);

        JobScheduler check_for_updates = (JobScheduler) CONTEXT.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo check_for_updates_job = new JobInfo.Builder(0,new ComponentName(CONTEXT, ApklisUpdatesJobService.class))
                .setMinimumLatency(TimeUnit.SECONDS.toMillis(SECONDS))
                .setExtras(bundle)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        check_for_updates.schedule(check_for_updates_job);

    }

    public void startLookingForUpdates(int MINUTES, boolean PERIODIC){

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("APP_PACKAGE",APP_PACKAGE);

        JobScheduler check_for_updates = (JobScheduler) CONTEXT.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo check_for_updates_job = new JobInfo.Builder(0,new ComponentName(CONTEXT, ApklisUpdatesJobService.class))
                .setPeriodic(TimeUnit.MINUTES.toMillis(MINUTES))
                .setExtras(bundle)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        check_for_updates.schedule(check_for_updates_job);

    }

    public boolean checkPaymentApp() {
        boolean paid = false;
        Uri provider_URI = Uri.parse("content://cu.uci.android.apklis.payment.provider/app/" + APP_PACKAGE);
        try {

            ContentProviderClient contentResolver = CONTEXT.getContentResolver().acquireContentProviderClient(provider_URI);

            if (contentResolver != null) {
                Cursor apklisdb = contentResolver.query(provider_URI, null, null, null, null);
                if (apklisdb.moveToFirst()) {
                    paid = apklisdb.getInt(apklisdb.getColumnIndex("paid")) > 0;
                }
                apklisdb.close();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    contentResolver.close();
                } else {
                    contentResolver.release();
                }
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

        return paid;
    }

    public String getUserName() {

        String username = null;
        Uri provider_URI = Uri.parse("content://cu.uci.android.apklis.payment.provider/app/" + APP_PACKAGE);
        try {

            ContentProviderClient contentResolver = CONTEXT.getContentResolver().acquireContentProviderClient(provider_URI);

            if (contentResolver != null) {
                Cursor apklisdb = contentResolver.query(provider_URI, null, null, null, null);
                if (apklisdb.moveToFirst()) {
                    username = apklisdb.getString(apklisdb.getColumnIndex("user_name"));
                }
                apklisdb.close();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    contentResolver.close();
                } else {
                    contentResolver.release();
                }
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

        return username;
    }

    public void getInfo(String option){

        NetWorkThreadInfo netWorkThread = new NetWorkThreadInfo(option);
        new Thread(netWorkThread).start();

    }


    class NetWorkThreadInfo extends Thread{

        String option;

        public NetWorkThreadInfo(String option){

            this.option = option;

        }

        @Override
        public void run() {
            String api_apklis_json = "";
            float value = -1;
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            if (option.equals("\"download_count\":") || option.equals("\"sale_count\":") || option.equals("\"rating\":")
                    || option.equals("\"price\":") || option.equals("\"reviews_count\":") || option.equals("\"reviews_star_1\":")
                    || option.equals("\"reviews_star_2\":") || option.equals("\"reviews_star_3\":") || option.equals("\"reviews_star_4\":")
                    || option.equals("\"reviews_star_5\":")) {
                URL url = null;
                try {
                    url = new URL("https://api.apklis.cu/v1/application/?package_name=" + APP_PACKAGE);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedReader api_apklis = new BufferedReader(new InputStreamReader(url.openStream()));

                    String inputLine;
                    while ((inputLine = api_apklis.readLine()) != null) {
                        api_apklis_json = api_apklis_json + inputLine;
                    }

                } catch (UnknownHostException e) {

                } catch (SocketException se) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!api_apklis_json.equals("")) {
                    String string_value = "";
                    int vn_index = api_apklis_json.indexOf(option);
                    String value_count = String.valueOf(api_apklis_json.charAt(vn_index + option.length()));
                    for (int i = option.length() + 1; !value_count.equals(","); i++) {
                        string_value = string_value + value_count;
                        value_count = String.valueOf(api_apklis_json.charAt(vn_index + i));
                    }
                    Log.e("Main", string_value);
                    value = Float.parseFloat(string_value);

                    Intent intent = new Intent("apklis_app_info");
                    intent.putExtra("info_value",value);
                    LocalBroadcastManager.getInstance(CONTEXT).sendBroadcast(intent);
                }

            }
        }

    }

}

