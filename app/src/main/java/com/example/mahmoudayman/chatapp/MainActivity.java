package com.example.mahmoudayman.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfRooms = new ArrayList<>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.btn_add_room);
        editText = (EditText) findViewById(R.id.edit_text_room);
        listView = (ListView) findViewById(R.id.list_view_chat_rooms);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listOfRooms);
        listView.setAdapter(arrayAdapter);

        request_user_name(); //method we create

        //button to add chat rooms using the Data Base //e.g. Android
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put(editText.getText().toString(),"");
                root.updateChildren(map);
                editText.setText("");
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>(); //Temporary Hash set // to not repeat the room names
                Iterator i = dataSnapshot.getChildren().iterator();    //can pass through the database to read it

                while(i.hasNext()) { //which read the data base line by line.
                 set.add(((DataSnapshot)i.next()).getKey()); //to get the name of each room;
                }//end while
                listOfRooms.clear(); //clear the current list.
                listOfRooms.addAll(set); //and append it using the hash set which contains all the room names.

                arrayAdapter.notifyDataSetChanged(); //to see the changes on the screen update the list view adapter

            }//end onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }//end onCancelled
        });//end addValueEventListener


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //to know whats the name of the chat rooms we would to choose and
                //whats the name of the active user
                Intent intent = new Intent(getApplicationContext(),ChatRoom.class);
                intent.putExtra("room_name",((TextView)view).getText().toString());
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });


    }//end onCreate


        //method to enter the desired user name ... without the user name he cant use the app.
    private void request_user_name(){
        //using alertDialog to ask for the user name

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Name:");
        final EditText input_field = new EditText(this);
        builder.setView(input_field);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.cancel();
                 request_user_name();
            }
        });
        builder.show();
    }//end method request_user_name


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}//end class MainActivity