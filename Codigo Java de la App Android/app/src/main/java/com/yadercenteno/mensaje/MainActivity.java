package com.yadercenteno.mensaje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    public String mensaje_firebase = "";
    private AlertDialog.Builder alertDialog5;
    private AlertDialog.Builder adb3;
    public static final String TAG = "NOTICIAS";
    public TextView txtMensaje;
    public TextView textViewMensaje0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSalir = (Button) findViewById(R.id.btnSalir);
        txtMensaje = (TextView) findViewById(R.id.txtMensaje);
        textViewMensaje0 = (TextView) findViewById(R.id.textViewMensaje0);

        // Instancio Firebase y obtengo el token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId fallo", task.getException());
                            return;
                        }

                        // Obtengo el token
                        String token = task.getResult().getToken();
                    }
                });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        // Suscribo la App al grupo de mensajeria para recibir las notificaciones
        FirebaseMessaging.getInstance().subscribeToTopic("Mensaje")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        // Creamos el mensaje para confirmar
        adb3 = new AlertDialog.Builder(MainActivity.this);

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salir_sistema();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Intent intentInicial = intent;
        mostrarMensaje(intentInicial);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        mostrarMensaje(intent);
    }

    // Con esto obtengo los mensajes de Firebase y los muestro en pantalla
    public void mostrarMensaje(Intent intent) {
        // Muestro los mensajes de Firebase
        String Titulo="";
        String Fecha="";
        String Lugar="";

        if (getIntent().getStringExtra("Titulo")!=null) {
            Titulo = getIntent().getStringExtra("Titulo");
        }
        if (getIntent().getStringExtra("Fecha")!=null) {
            Fecha = getIntent().getStringExtra("Fecha");
        }
        if (getIntent().getStringExtra("Lugar")!=null) {
            Lugar = getIntent().getStringExtra("Lugar");
        }

        mensaje_firebase = mensaje_firebase  + "Titulo : " + Titulo + ".%1$s%1$s";
        mensaje_firebase = mensaje_firebase  + "Fecha : " + Fecha + ".%1$s%1$s";
        mensaje_firebase = mensaje_firebase  + "Lugar : " + Lugar + ".%1$s%1$s";

        if (!Titulo.equals("") && !Fecha.equals("") && !Lugar.equals("")) {
            txtMensaje.setText(String.format(mensaje_firebase, "\n"));
        }
    }

    public void salir_sistema() {
        adb3.setTitle("Salir de la App");
        adb3.setMessage("¿Estás seguro que querés cerrar esta App?");
        adb3.setNegativeButton("No", null);
        adb3.setPositiveButton("Si", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Estos nos SACA del sistema, cierra la App
                Intent intent = new Intent(Intent.ACTION_MAIN);
                finish();
                System.exit(0);
            }
        });
        adb3.show();
    }

    //Definimos que para cuando se presione la tecla BACK no volvamos para atras
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // no hacemos nada.
            salir_sistema();
        }

        return super.onKeyDown(keyCode, event);
    }
}