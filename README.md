# apklis-util

• API Java Para Verificación De Pagos, Búsqueda De Actualizaciones,
Y Recolección De Información Básica De Apps En Apklis.


# Instalación


 • Descarga El Zip Y Agrega Los Archivos ApklisUtil.java
 Y ApklisUpdatesJobService.java A La Carpeta Java De Tu Proyecto.
 
 • Cambia El Package De Los Archivos ApklisUtil.java
 Y ApklisUpdatesJobService.java Por El Package De Tu App.


# Uso 

• Manifiest.xml


 ```xml
 


        <service android:name=".ApklisUpdatesJobService"
                 android:permission="android.permission.BIND_JOB_SERVICE"
                 android:exported="true"> 
         
 ```



• MainActivity.java


 
 ```java
 


        /* BroadcastReceiver Para Manejo De Eventos Asociados A Existencia De Actualización U Obtención De Información De App */
       
        BroadcastReceiver apklis_update = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                Float info = intent.getFloatExtra("info_value",-1); /* Respuesta Del Método getInfo() Valor De La Constante Solicitada */
                
                
                String version_name = intent.getStringExtra("version_name"); /* Respuesta Del Método startLookingForUpdates() Valor De La Versión Name De La App 
                                                                                Si Existe Una Actualización */
                
                

                if(info!=-1){
                
                   /* Código A Ejecutar Cuando Obtienes Información De La App Usando El Método getInfo()
                      (Ejemplo: Agregar Un Frame Layout Con La Información Obtenida De La App En Apklis) */
                   
                }
                

                if(version_name!=null) {
                
                   /* Código A Ejecutar Cuando Existe Una Nueva Actualización De La App Usando El Método startLookingForUpdates() 
                      (Ejemplo: Agregar Un Frame Layout Con La Información Y Un Botón Que Redirige A La App En Apklis)
                      Si La App No Se Encuentra En Primer Plano Se Lanza Una Notificación Con La Información
                      De Que Existe Una Nueva Versión Y La Version Name */
                      
                }

            }
        };
        
        
        /* Registro De Recibidores Para Manejar Existencia De Actualización U Obtención De Info Respectivamente */
         
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(apklis_update, new IntentFilter("apklis_update"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(apklis_update, new IntentFilter("apklis_app_info"));
        
        
        
        
        /* Instanciar Un Objeto De La Clase ApklisUtil */
        
        ApklisUtil apklis = new ApklisUtil(this,this.getPackageName());
        
        
        
        
         /* Método Para Verificar Pago. Es Necesario Tener Instalada La App De Apklis Y Una Sesión Activa En Esta De Lo Contrario Devolverá false */
        
       boolean paid = apklis.checkPaymentApp();
       
       
       
       
        /* Método Para Obtener UserName. Es Necesario Tener Instalada La App De Apklis Y Una Sesión Activa En Esta De Lo Contrario Devolverá null */
        
       String username = apklis.getUserName();
       
       
       
       
       /* Método Para Verificar Si Existe Una Actualización. Sobrecarga de Métodos. La Respuesta De Este Método Se Maneja En El BroadcastReceiver
          Si La App Está En Primer Plano Si No Se Lanza Una Notificación */
       
       apklis.startLookingForUpdates(int Segundos); /* Se Ejecuta Una Sola Vez Cuando Es Llamado. Recibe Como Parámetro Un Entero Que Representa
                                                       El Tiempo En Segundos Que Debe Transcurrir Como Mínimo Desde La Llamada Hasta La Ejecución */ 
       
       apklis.startLookingForUpdates(int Minutos, boolean Irrelevante); /* Se Ejecuta Periódicamente. Recibe Como Parámetros Un Entero Que Representa
                                                                           El Tiempo En Minutos Que Debe Transcurrir Entre Una Búsqueda Y Otra (Para 
                                                                           Android N Y Versiones Superiores El Tiempo Mínimo Permitido Es 15 Minutos)
                                                                           Y Un Booleano, Este último Valor Es Irrelevante. */
                                                                        
       
       
       
       
       /* Método Para Actualizar Información Básica Sobre La App. La Respuesta De Este Método Se Maneja En El BroadcastReceiver */
       
       apklis.getInfo(apklis.DOWNLOADS); /* Recibe Como Parámetro Un String (Posibles Valores Constantes:
                                                                                              apklis.DOWNLOADS: Número De Descargas
                                                                                              apklis.SALES: Número De Ventas
                                                                                              apklis.RATING: Número De Descargas
                                                                                              apklis.REVIEWS: Número De Reviews
                                                                                              apklis.PRICE: Precio
                                                                                              apklis.REVIEWS_STAR_1: Número De Reviews 1 Estrella
                                                                                              apklis.REVIEWS_STAR_2: Número De Reviews 2 Estrellas
                                                                                              apklis.REVIEWS_STAR_3: Número De Reviews 3 Estrellas
                                                                                              apklis.REVIEWS_STAR_4: Número De Reviews 4 Estrellas
                                                                                              apklis.REVIEWS_STAR_5: Número De Reviews 5 Estrellas */
       
        

```

 
 
