package com.acar_app.flashlight_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // değişkenler oluşturuldu.
    private SensorManager sensorManager; // Yeni sensör verileri olduğunda, SensorManager'dan bildirim almak için kullanılır.
    private CameraManager cameraManager;
    private TextView textView ;
    private Float ChangedVale;
    private Sensor lightsensor;
    private String cameraid; // String: Sorgulanacak kamera cihazının kimliği. Bu, tarafından doğrudan açılabilen bağımsız bir kamera kimliği olmalıdır


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //değişkenler idlerle baglandı.
        textView=findViewById(R.id.text);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        cameraManager=(CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraid=cameraManager.getCameraIdList()[0];
            /* getCameraIdList() : Diğer kamera API istemcileri tarafından kullanımda olabilecek kameralar
             da dahil olmak üzere tanımlayıcıya göre şu anda bağlı olan kamera cihazlarının listesini döndürün.*/

        }catch (CameraAccessException e){ // CameraAccessException : kamera cihazının bağlantısı kesildiyse.
            e.printStackTrace();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!= null){
            lightsensor= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            // Sensor.TYPE_LIGHT : değerler[0]: SI lux birimlerinde ortam ışığı seviyesi
        }else {
            Toast.makeText(this,"Üzgünüm telefonunuzun ışık sensörü yok",Toast.LENGTH_SHORT).show();
        }



    }

    @Override// yYeni bir sensör olayı olduğunda çağrılır.
    public void onSensorChanged(SensorEvent sensorEvent) {
        ChangedVale= sensorEvent.values[0];
        textView.setText(String.valueOf(ChangedVale));

        if (ChangedVale<50){ //sensor degeri 50 den kucukse

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraid,true);
                    //setTorchMode(String cameraId, boolean enabled)
                    //Kamera cihazını açmadan verilen kimliğin kamerasının flaş ünitesinin el feneri modunu true ayarlayın.
                }
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }
        else{ // degilse
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    cameraManager.setTorchMode(cameraid,false);
                    //Kamera cihazını açmadan verilen kimliğin kamerasının flaş ünitesinin el feneri modunu false ayarlayın.

                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    @Override// Kayıtlı sensörün doğruluğu değiştiğinde çağrılır.
    // onSensorChanged()'den farklı olarak, bu yalnızca bu doğruluk değeri değiştiğinde çağrılır.
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,lightsensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override // uygulama durdugunda sensorleri durdur. yoksa sensor calısmaya devam eder ve pili bitirir...
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}