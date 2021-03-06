package com.vovik.airtablepretender.table.TableModels.Cells;

import android.content.Context;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public abstract class AirMockCellData {
    @Exclude
    public DatabaseReference mCellReference;
    @Exclude
    public View cell_view;

    public AirMockCellData(){
    }

    public abstract View createView(Context context);
    public abstract void readValue(DataSnapshot dataSnapshot);
    public abstract void discardValue();//sets cell to state as if value was null or not there
    public abstract void readData(String data);
    public abstract String getData();
}
