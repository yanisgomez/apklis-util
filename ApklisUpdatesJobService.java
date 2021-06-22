package your.app.package_id;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class ApklisUpdatesJobService extends JobService {

    private Handler ApklisUpdateServiceHandler;

    @Override
    public void onCreate(){
        super.onCreate();

        ApklisUpdateServiceHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        boolean update_exist = msg.getData().getBoolean("update_exist",false);
                        String version_name =  msg.getData().getString("version_name","");
                        JobParameters jobParameters = msg.getData().getParcelable("params");

                        boolean AppActive = false;
                        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(getApplicationContext().ACTIVITY_SERVICE);
                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){

                            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
                            for(ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList){
                                if(processInfo.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                                    for(String activeProcess : processInfo.pkgList){
                                        if(activeProcess.equals(getApplicationContext().getPackageName())){
                                            AppActive = true;
                                        }
                                    }
                                }
                            }
                        }

                        if(update_exist) {
                            if (!AppActive) {

                                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    NotificationChannel chanel = new NotificationChannel("chanel","ApklisUpdate",NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(chanel);
                                }

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"chanel")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle(getApplicationContext().getApplicationInfo().loadLabel(getApplicationContext().getPackageManager()).toString())
                                        .setContentText("Nueva Versión v"+version_name+" Disponible En Apklis")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                notificationManagerCompat.notify(0, builder.build());
                            }
                            else {
                                Intent intent = new Intent("apklis_update");
                                intent.putExtra("version_name",version_name);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        }

                        jobFinished(jobParameters,false);


                    default:super.handleMessage(msg);
                }

            }
        };
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        String APP_PACKAGE = params.getExtras().getString("APP_PACKAGE","");
        NetWorkThread netWorkThread = new NetWorkThread(APP_PACKAGE,params);
        new Thread(netWorkThread).start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


class NetWorkThread extends Thread {

    String APP_PACKAGE;
    JobParameters jobParameters;

    NetWorkThread(String APP_PACKAGE, JobParameters jobParameters) {
        this.APP_PACKAGE = APP_PACKAGE;
        this.jobParameters = jobParameters;
    }

    @Override
    public void run() {

        String api_apklis_json = "";
        boolean update_exist = false;
        int apklis_version_code = 0;
        String apklis_version_name = "";

        PackageInfo pinfo = null;
        try {
            pinfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int this_version_code = pinfo.versionCode;

        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

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

        }catch (UnknownHostException e){

        }
        catch (SocketException se){

        } catch (IOException e){
            e.printStackTrace();
        }

        if(!api_apklis_json.equals("")) {
            String apklis_version_code_tem = "";
            String version_code = "\"version_code\":";
            int cd_Index = api_apklis_json.indexOf(version_code);
            if(cd_Index!=-1) {
                String code = String.valueOf(api_apklis_json.charAt(cd_Index + version_code.length()));
                for (int i = version_code.length(); !code.equals(","); i++) {
                    apklis_version_code_tem = apklis_version_code_tem + code;
                    code = String.valueOf(api_apklis_json.charAt(cd_Index + i + 1));
                }
            } else apklis_version_code_tem = "";
            if(!apklis_version_code_tem.equals("")) {
                apklis_version_code = Integer.parseInt(apklis_version_code_tem);


                if (apklis_version_code > this_version_code) {
                    update_exist = true;

                    int vn_index = api_apklis_json.indexOf("\"version_name\":");
                    String vname = String.valueOf(api_apklis_json.charAt(vn_index +16));
                    for (int i = 17; !vname.equals("\""); i++) {
                        apklis_version_name = apklis_version_name + vname;
                        vname = String.valueOf(api_apklis_json.charAt(vn_index + i));
                    }
                }
            }

        }

        Message message = new Message();

        message.what = 1;
        Bundle bundle = new Bundle();

        bundle.putParcelable("params", jobParameters);
        bundle.putBoolean("update_exist", update_exist);
        bundle.putString("version_name", apklis_version_name);
        message.setData(bundle);
        ApklisUpdateServiceHandler.sendMessage(message);

    }
}
}
