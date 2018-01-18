package com.example.admin.batteryalarm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Window window;
    ActionBar bar;
    TextView tvlevel,tvalarm,tvstatus;
    Button butalarm;
    String alarmlevel="100";
    EditText edper;
    int level,val,val2;
    boolean isCharging;
    public MediaPlayer mediaPlayer;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar=getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#283747")));
        window=getWindow();
        window.setStatusBarColor(Color.parseColor("#212F3C"));

        tvlevel=(TextView)findViewById(R.id.tvlevel);
        butalarm=(Button)findViewById(R.id.butalarm);
        tvalarm=(TextView)findViewById(R.id.tvalarm);
        tvstatus=(TextView)findViewById(R.id.tvstatus);

        mediaPlayer=MediaPlayer.create(this,R.raw.audio);
        tvalarm.setText("Alarm set at 100%");

        final NotificationCompat.Builder notibuilder=new NotificationCompat.Builder(getApplicationContext());
        notibuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        notibuilder.setSmallIcon(R.drawable.charging);
        notibuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.charging));
        notibuilder.setOngoing(true);

        final NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);



        BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                level=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                tvlevel.setText(String.valueOf(level)+"%");

                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                     status == BatteryManager.BATTERY_STATUS_FULL;

                notibuilder.setContentTitle("Charging ( "+level+"% )");
                notibuilder.setContentText("Alarm set at "+alarmlevel+"%");

                if(isCharging){
                    val=1;
                    notificationManager.notify(1,notibuilder.build());
                    tvalarm.setText("Alarm set at 100%");
                    tvstatus.setText("Charging");
                    butalarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder dialogbuilder=new AlertDialog.Builder(getApplicationContext());
                            LayoutInflater inflater=getParent().getLayoutInflater();
                            View dialogview=inflater.inflate(R.layout.dialogbox,null);
                            dialogbuilder.setView(dialogview);

                            edper=(EditText)dialogview.findViewById(R.id.editText4);
                            dialogbuilder.setTitle("Set Alarm");
                            dialogbuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (edper.getText().toString().isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "please enter battery percentage", Toast.LENGTH_LONG).show();
                                    } else {
                                        alarmlevel = edper.getText().toString();
                                        Toast.makeText(getApplicationContext(), "Alarm set at "+alarmlevel+"%", Toast.LENGTH_LONG).show();
                                        tvalarm.setText("Alarm set at "+alarmlevel+"%");
                                        notificationManager.notify(1,notibuilder.build());
                                    }
                                    if(level>=Integer.parseInt(alarmlevel)){
                                        val2=1;
                                        mediaPlayer.start();
                                        mediaPlayer.setLooping(true);
                                    }else {
                                        mediaPlayer.pause();
                                        val2=0;
                                    }

                                }
                            });
                            AlertDialog dialog=dialogbuilder.create();
                            dialog.show();
                        }
                    });

                }else {
                    val=0;
                    notificationManager.cancel(1);
                    tvalarm.setText("");
                    mediaPlayer.pause();
                    tvstatus.setText("On Battery");
                    butalarm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(),"Plug in the charger first.",Toast.LENGTH_LONG).show();
                         }
                    });
                }
            }
        };
        this.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.optionsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings: settings();
            return true;
            case R.id.about : about();
            return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    void settings(){

    }

    void about(){
        mediaPlayer.stop();
    }

    void makenotification(){

    }
}
