package com.project.acelerometro;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Sensor accelerometer;
    SensorManager sensorManager;
    float sensorX, sensorY, sensorZ;
    private Button botaoEnviar, botaoIniciar, botaoPausar;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;

    ArrayList<Float> arrayList = new ArrayList<Float>();

    TextView valorX, valorY, valorZ, valorSpeed, valorSpeed2;

    // Declarações para a criação do Socket
    private static final String hostname = "192.168.11.11"; //"192.168.0.108";
    private static final int portaServidor = 4500;
    Socket socket = null;
    // Fim Declarações Socket

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botaoEnviar = findViewById(R.id.botaoEnviarId);
        botaoIniciar = findViewById(R.id.botaoIniciarID);
        botaoPausar = findViewById(R.id.botaoPausarID);

        botaoIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeed(v);
            }
        });

        botaoPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSpeed();
            }
        });

        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar_dados(v);
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        valorSpeed = findViewById(R.id.speedID);

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 100;

                valorSpeed.setText("Speed: " + speed);

                arrayList.add(x);
                arrayList.add(y);
                arrayList.add(z);
                arrayList.add(speed);

                // Escrevendo pra acompanhar no terminal
//                System.out.println(x);
                for (int i=0; i<arrayList.size(); i++)
                    System.out.println(arrayList.get(i));
//                System.out.println(arrayList.get(1)[0]);


                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

        sensorX = event.values[0];
        sensorY = event.values[1];
        sensorZ = event.values[2];

        valorX = findViewById(R.id.valorXID);
        valorY = findViewById(R.id.valorYID);
        valorZ = findViewById(R.id.valorZID);

        valorX.setText(String.valueOf(sensorX));
        valorY.setText(String.valueOf(sensorY));
        valorZ.setText(String.valueOf(sensorZ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void enviar_dados(View v) {
        BackgroundTask enviar = new BackgroundTask();
        enviar.execute(arrayList);
    }

    class BackgroundTask extends AsyncTask<ArrayList,Void,Void> {
        PrintWriter writer;

        @Override
        protected Void doInBackground(ArrayList... voids) {

            try {
                Socket soc = new Socket(hostname, portaServidor);
                if( soc.isConnected() ) {

                    writer = new PrintWriter(soc.getOutputStream());
                    for (int i = 0; i < arrayList.size(); i++){
                        Float mensagem = arrayList.get(i);
                        writer.write(String.valueOf(mensagem + " "));
                    }

                    writer.flush();
                    writer.close();
                    arrayList.clear();
                }
                else{
                    Toast.makeText(MainActivity.this, "Impossível conectar ao Socket", Toast.LENGTH_SHORT).show();
                }
//                            ObjectOutputStream objectOutput = new ObjectOutputStream(soc.getOutputStream());
//                            for(int i=0;i<arrayList.size(); i++){
//                                objectOutput.writeObject(arrayList.get(i));
//                            }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return null;
        }

    }

    public void startSpeed(View v) {
        // TODO Auto-generated method stub
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void pauseSpeed() {
        sensorManager.unregisterListener(this);
    }
//
//    protected void onResume() {
//        super.onResume();
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }
}


