package com.vovik.airtablepretender.TableModel.Table;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockCellData;

public class AirMockCell {

    AirMockColumn parentColumn;
    AirMockCellData data;

    AirMockCell(AirMockColumn column, DatabaseReference ref, Context context){
        parentColumn = column;
        data = column.getColumnCellData();

        data.mCellReference = ref;
        data.cell_view = data.createView(context);
    }

    void columnUpdated(Context context){
        String dataString = data.getData();
        AirMockCellData newData = parentColumn.getColumnCellData();
        newData.mCellReference = data.mCellReference;
        newData.readData(dataString);
        newData.cell_view = newData.createView(context);
        data = newData;
    }

    View getView(){
        return data.cell_view;
    }
}
