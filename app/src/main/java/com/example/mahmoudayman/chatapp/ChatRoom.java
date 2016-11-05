package com.example.mahmoudayman.chatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {

    private Button btn_send;
    private EditText editText_inputMsg;
    private TextView chat_txts;

    private String userName, roomName;
    private DatabaseReference root; //to reference the room it self

    private String temp_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_send = (Button) findViewById(R.id.button_send);
        editText_inputMsg = (EditText) findViewById(R.id.editText_inputMsg);
        chat_txts = (TextView)findViewById(R.id.textView_chatTxts);

        userName = getIntent().getExtras().get("user_name").toString();
        roomName = getIntent().getExtras().get("room_name").toString();

        setTitle(roomName); //to change the title bar to the room name

        root = FirebaseDatabase.getInstance().getReference().child(roomName); // without .child htrg3 awl children bs e.g Android (room name)
        //with .child htrg3 elly gwa el room name (Android)
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                DatabaseReference msg_root = root.child(temp_key);

                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name", userName);
                map2.put("msg",editText_inputMsg.getText().toString());

                msg_root.updateChildren(map2);
            }
        }); //end button listner

      root.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 append_chat_coversation(dataSnapshot);
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            append_chat_coversation(dataSnapshot);
          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });//end addChildEventListener

    }//end onCreate

    private String chat_msg, chat_user_name;
    //to read the children
    private void append_chat_coversation(DataSnapshot dataSnapshot){
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue(); //the 1st child msg in firedb
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue(); //the 2nd child in firedb

            chat_txts.append(chat_user_name +": "+chat_msg +" \n");
        }//end while
    }//end method append_chat_coversation
}//end class ChatRoom
