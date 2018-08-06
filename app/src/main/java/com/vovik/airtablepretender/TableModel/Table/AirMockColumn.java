package com.vovik.airtablepretender.TableModel.Table;


import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.vovik.airtablepretender.R;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockCellData;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockChoiceCell;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockImageCell;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockNumberCell;
import com.vovik.airtablepretender.TableModel.Table.Cells.AirMockTextCell;
import com.vovik.airtablepretender.TableModel.Table.ColumnCells.AirMockColumnCellData;
import com.vovik.airtablepretender.TableModel.Table.ColumnCells.AirMockColumnChoiceCell;
import com.vovik.airtablepretender.TableModel.Table.ColumnCells.AirMockColumnImageCell;

@IgnoreExtraProperties
public class AirMockColumn {

    public static final int UPDATE_OK = -1;
    public static final int UPDATE_TYPE = 0;

    enum ColumnType { TEXT, NUMBER, CHOICE, IMAGE }

    public ColumnType type;
    public String name;

    @Exclude
    public String columnId;
    @Exclude
    public View columnView;
//    @Exclude
//    private DatabaseReference columnReference;
    //additional data that columns may have
    @Exclude
    public AirMockColumnCellData data;
    @Exclude
    public AirMockTable parentTable;

    public void setMainData(View columnView, DataSnapshot snapshot){

        this.columnId = snapshot.getKey();
        this.columnView = columnView;

        updateData(snapshot);
    }

    public void setAirMockCellData(DataSnapshot snapshot){
        switch (type){
            case CHOICE:
                data = new AirMockColumnChoiceCell();
                ((AirMockColumnChoiceCell)data).setAdapter(columnView.getContext());
                data.ReadData(snapshot.child("data"));
                break;
            case IMAGE:
                data = new AirMockColumnImageCell();
                ((AirMockColumnImageCell)data).setParentCell(this);
                data.ReadData(snapshot);
                break;
            default:
                data= null;
        }
    }

    public AirMockCellData getColumnCellData(){
        switch (type){
            case TEXT:
                return new AirMockTextCell(this);
            case IMAGE:
                return new AirMockImageCell(this);
            case CHOICE:
                return new AirMockChoiceCell(this);
            case NUMBER:
                return new AirMockNumberCell(this);
        }
        return new AirMockTextCell(this);
    }

    //update on column data change or type change
    public int updateData(DataSnapshot updateFrom){

        int return_code = UPDATE_OK;

        TextView columnText = columnView.findViewById(R.id.column_text);
        columnText.setText(updateFrom.child("name").getValue(String.class));

        String newType = updateFrom.child("type").getValue(String.class);
        if(type != null){
            if(!type.toString().equals(newType)){
                setType(newType);
                return_code = UPDATE_TYPE;
            }
        }else{
            setType(newType);
        }

        setAirMockCellData(updateFrom);

        return return_code;
    }

    public String getType(){
        if(type == null){
            return null;
        }else{
            return type.name();
        }
    }

    public void setType(String typeString){
        if(typeString == null){
            type = null;
        }else{
            this.type = ColumnType.valueOf(typeString);
        }
    }
}
