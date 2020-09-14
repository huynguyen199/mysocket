package com.example.mysocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
        private Socket mSocket;
        ListView mlistview,listviewchat;
        EditText edtcontent;
        ImageView madduser,msenduser;
        ArrayList<String> arrayUser,arraychat;
        ArrayAdapter adapteruser,adapterchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        try {
            mSocket = IO.socket("http://192.168.1.6:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.on("server-send-result",onRetrieveResult);
        mSocket.on("server-send-user",onListUser);
        mSocket.on("server-send-chat",onListChat);
        arrayUser = new ArrayList<>();
        adapteruser = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayUser);
        mlistview.setAdapter(adapteruser);
        arraychat = new ArrayList<>();
        adapterchat = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arraychat);
        listviewchat.setAdapter(adapterchat);
//        mSocket.emit("client-send-data","Lap trinh android");
        msenduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtcontent.getText().toString().trim().length() > 0) {
                    mSocket.emit("client-send-chat", edtcontent.getText().toString());
                }
            }
        });
        madduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtcontent.getText().toString().trim().length() > 0) {
                    mSocket.emit("client-register-user", edtcontent.getText().toString());
                }
            }
        });
    }
    private  Emitter.Listener onListChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String noidung = object.getString("chatContent");
                        arraychat.add(noidung);
                        adapterchat.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private Emitter.Listener onListUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray array = object.getJSONArray("danhsach");
                        arrayUser.clear();
                        for(int i =0;i<array.length();i++){
                            String username = array.getString(i);
                            arrayUser.add(username);
                            Toast.makeText(MainActivity.this,username,Toast.LENGTH_SHORT).show();
                        }
                        adapteruser.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    };
    private Emitter.Listener onRetrieveResult = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        boolean exits = object.getBoolean("ketqua");
                        if(exits){
                            Toast.makeText(MainActivity.this,"tai khoan nay da ton tai",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"dang ky thanh cong",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
    private void anhxa(){
        mlistview = findViewById(R.id.listv1);
        listviewchat = findViewById(R.id.listv2);
        edtcontent = findViewById(R.id.editcontent);
        madduser = findViewById(R.id.adduser);
        msenduser = findViewById(R.id.sendlectter);
    }

}
