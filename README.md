# apklis-util
Api De Apklis Para Verificación De Pago, Búsqueda De Actualizaciones, Y Recolección De Información Básica Sobre Apps.

#Instalación

java

        BroadcastReceiver apklis_update = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Float info = intent.getFloatExtra("info_value",-1);
                String version_name = intent.getStringExtra("version_name");

                if(info!=-1){
                    
                }

                if(version_name!=null) {
          
                }

            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(apklis_update, new IntentFilter("apklis_update"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(apklis_update, new IntentFilter("apklis_app_info"));

        ApklisUtil update = new ApklisUtil(this,this.getPackageName());
        
        
        '''
