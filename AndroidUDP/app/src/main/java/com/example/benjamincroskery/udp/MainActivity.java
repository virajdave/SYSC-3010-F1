package com.example.benjamincroskery.udp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_UDP_DATAGRAM_LEN = 1500;
    private Server server = null;
    private TextView log;
    private ScrollView logScroll;
    private String lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("IP", Utils.getIPAddress());

        log = (TextView) findViewById(R.id.log);
        logScroll = (ScrollView) findViewById(R.id.log_scroll);
        server = new Server();
        server.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    lastMessage = server.recvWait();
                    if (lastMessage != null) {
                        runOnUiThread(updateLog);
                    }
                }
            }
        }).start();
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        server.sendMessage(message);
    }

    /** Called when the user clicks the Set button */
    public void setAddr(View view) {
        try {
            EditText editPort = (EditText) findViewById(R.id.edit_port);
            String[] split = editPort.getText().toString().split("/");
            if (split.length == 1) {
                server.setAddr(new InetSocketAddress(split[0], server.getAddr().getPort()));
            } else if (split.length == 2) {
                server.setAddr(new InetSocketAddress(split[0], Integer.parseInt(split[1])));
            }
        } catch (Exception e) {
            Log.e("FAIL", "wat", e);
        }
    }

    private Runnable updateLog = new Runnable() {
        public void run() {
            if (server == null) return;
            log.setText(log.getText() + lastMessage + "\n");
            logScroll.fullScroll(View.FOCUS_DOWN);
        }
    };
}
