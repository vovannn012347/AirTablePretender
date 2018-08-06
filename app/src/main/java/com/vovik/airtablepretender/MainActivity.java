package com.vovik.airtablepretender;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vovik.airtablepretender.TableModel.Table.AirMockTable;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockImageCell;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int LOAD_IMAGE = 11111;

    FirebaseDatabase database;
    DatabaseReference mTableReference;
    DatabaseReference mTableNamesReference;

    NavigationView tableSelectorMenu;
    DrawerLayout mDrawer;
    Menu menuGroup;

    Map<Integer, String> tables;
    Map<Integer, String> tableNames;
    int currentTableId;

    ConstraintLayout mainLayout;
    AirMockTable currentTable;

    AirMockImageCell updatedCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawer = findViewById(R.id.drawerMenu);
        tableSelectorMenu = findViewById(R.id.nav_view);
        tableSelectorMenu.setNavigationItemSelectedListener(this);
        menuGroup = tableSelectorMenu.getMenu();

        database = FirebaseDatabase.getInstance();//.setPersistenceEnabled(true);
        currentTable = new AirMockTable(this);
        tables = new TreeMap<>();
        tableNames = new TreeMap<>();
        currentTableId = -1;

        mainLayout = findViewById(R.id.main_layout);

        loadTableData();
        loadMainScreen();

    }

    protected void loadMainScreen(){
        if (currentTableId != -1){
            selectTable(currentTableId);
        }else{
            putTutorial();
        }
    }

    protected void putTutorial(){

        while(mainLayout.getChildAt(0) != null){
            mainLayout.removeView(mainLayout.getChildAt(0));
        }

        getLayoutInflater().inflate(R.layout.tutorial_layout, mainLayout, true);
    }

    protected void selectTable(int tableId){

        if(tableId == currentTableId){
            return;
        }

        if(currentTableId != -1){
            currentTable.unbindListeners();
            Log.d("TABLE", "" + currentTableId);
        }

        currentTableId = tableId;

        while(mainLayout.getChildAt(0) != null){
            mainLayout.removeView(mainLayout.getChildAt(0));
        }

        mTableReference =
                database.getReference()
                        .child("tables")
                        .child(tables.get(tableId));

        View table_layout
                = getLayoutInflater().inflate(R.layout.table_layout, mainLayout, true);

        currentTable.setTable(mTableReference, mTableNamesReference.child(tables.get(tableId)), table_layout);
    }

    protected void loadTableData(){
        mTableNamesReference = database.getReference()
                .child("data").child("table_names");

        //handle table name changes
        mTableNamesReference.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                tables.put(dataSnapshot.getKey().hashCode(), dataSnapshot.getKey().toString());
                tableNames.put(dataSnapshot.getKey().hashCode(), dataSnapshot.getValue().toString());

                menuGroup.add(R.id.tableSelection, dataSnapshot.getKey().hashCode(), Menu.NONE, dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

                MenuItem item = menuGroup.findItem(dataSnapshot.getKey().hashCode());
                tableNames.put(dataSnapshot.getKey().hashCode(), dataSnapshot.getValue().toString());
                if(item != null) {
                    item.setTitle(dataSnapshot.getValue().toString());
                }
                //will not be used as table renames will change keys
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                tables.remove(dataSnapshot.getKey().hashCode());
                tableNames.remove(dataSnapshot.getKey().hashCode());
                menuGroup.removeItem(dataSnapshot.getKey().hashCode());
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                //???
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //???
            }
        });
    }

    public void loadImage(AirMockImageCell imageCell){
        updatedCell = imageCell;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOAD_IMAGE && resultCode == RESULT_OK && updatedCell != null){

            Uri imageUri = data.getData();
            Bitmap bitmap;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }catch(IOException e){
                return;
            }

            updatedCell.setImage(bitmap,
                    "" +
                    imageUri.getEncodedPath().replaceAll("[/\\:.$%-+*?]", "")
                    + imageUri.hashCode());
            updatedCell = null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //change selected table
        if(item.getItemId() == R.id.new_table_menu){

        }else{
            selectTable(item.getItemId());
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
