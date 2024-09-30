package com.example.myplant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
public class MainActivity extends AppCompatActivity {

    private static final String SERVER_IP = "192.168.31.242"; // 替换为树莓派的IP地址
    private static final int SERVER_PORT = 8888;
    private Handler handler;

    private String recv_buff=null;
    TextView oxygen,carbondixode,humidity,temperature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        oxygen=findViewById(R.id.oxy_val);
        handler = new Handler(Looper.getMainLooper());

        new Thread(new ClientThread()).start();
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while (true){
                    InputStream inputStream = null;
                    try {
                        inputStream = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (inputStream!=null){
                        try {
                            byte[] buffer = new byte[1024];
                            int count = inputStream.read(buffer);//count是传输的字节数
                            recv_buff = new String(buffer);//socket通信传输的是byte类型，需要转为String类型
                            System.out.println(recv_buff);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //将受到的数据显示在TextView上
                    if (recv_buff!=null){
                        handler.post(runnableUi);

                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Runnable runnableUi = new Runnable() {
            @Override
            public void run() {
                String ss=recv_buff;
                ss=ss.trim();
                ss=ss.replace("b","");
                ss=ss.replace("\\r","");
                ss=ss.replace("\\n","");
                ss=ss.replace("'","");
                String[] val_arr =ss.split(";");


                oxygen.setText(val_arr[1]+"%");
            }
        };
    }
}