package com.vovik.airtablepretender.table.TableModels.ColumnCells;

import android.app.Activity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.vovik.airtablepretender.table.TableControllers.CellController;
import com.vovik.airtablepretender.table.TableViews.CellView;
import com.vovik.airtablepretender.table.TableViews.ColumnCells.DateCellView;

public class ColumnDateCellModel {

    ColumnDateCellModel(){
        super();

    }
    public View getView(Activity context, CellController controller){

        CellView cv = new DateCellView(context, controller);

        return cv.getView();

    }
    public void setData(DataSnapshot data){}
    public void setData(String data){}
    public String getData(){
        return "";
        //TODO
    }
    public void eraseData(){}
}
