package com.vovik.airtablepretender.TableModel.Table;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableRow;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.LinkedList;
import java.util.List;

public class AirMockRow implements CompoundButton.OnCheckedChangeListener {

    String rowKey;
    private DatabaseReference mTableRow;
    private AirMockTable parentTable;
    public TableRow row_view;
    private List<AirMockCell> cells;
    private RowChildChangeListener childListener;
    private CheckBox checkBox;
    final int ADDITIONAL_COLUMNS = 1;

    boolean checked;
    boolean consume_checked;

    public AirMockRow(DatabaseReference newRow, AirMockTable parent, TableRow row){
        rowKey = newRow.getKey();
        mTableRow = newRow;
        parentTable = parent;
        row_view = row;
        consume_checked = false;

        checkBox = new CheckBox(parent.tableView.getContext());
        row_view.addView(checkBox);
        checkBox.setOnCheckedChangeListener(this);

        cells = new LinkedList<>();
        for(int i = 0; i < parent.columns.size(); ++i){
            columnAdded(i);
        }

        childListener = new RowChildChangeListener(this);
        mTableRow.addChildEventListener(childListener);
    }
    public void deleteSelf(){
        unbindListeners();
        row_view.removeAllViews();
        cells.clear();
        mTableRow.removeValue();
        Log.d("PING", "ping");
    }

    public void unbindListeners(){

        mTableRow.removeEventListener(childListener);
    }

    public void setChecked(boolean checkedState){
        checked = checkedState;
        checkBox.setChecked(checkedState);
    }

    public void columnAdded(int position){
        AirMockCell newCell = new AirMockCell(
                parentTable.columns.get(position),
                mTableRow.child(parentTable
                        .columns.get(position).columnId),
                parentTable.tableView.getContext()
        );
        newCell.getView();

        cells.add(position, newCell);
        row_view.addView(newCell.getView(), position+ADDITIONAL_COLUMNS);
    }

    public void columnRemoved(int position){
        cells.remove(position);
        row_view.removeViewAt(position+ADDITIONAL_COLUMNS);
    }

    public void columnUpdated(int position){
        cells.get(position).columnUpdated(parentTable.tableView.getContext());
        row_view.removeViewAt(position+ADDITIONAL_COLUMNS);
        row_view.addView(cells.get(position).getView(), position+ADDITIONAL_COLUMNS);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checked = isChecked;
        parentTable.rowChecked(isChecked);
    }

    class RowChildChangeListener implements ChildEventListener {
        AirMockRow parentRow;

        RowChildChangeListener(AirMockRow row){
            parentRow = row;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

            String columnKey = dataSnapshot.getKey();

            for (AirMockCell cell : parentRow.cells) {
                if(cell.parentColumn.columnId.equals(columnKey)){
                    cell.data.readValue(dataSnapshot);
                    break;
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            String columnKey = dataSnapshot.getKey();
            for (AirMockCell cell : cells) {
                if(cell.parentColumn.columnId.equals(columnKey)){
                    cell.data.readValue(dataSnapshot);
                    break;
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

//            String columnKey = dataSnapshot.getKey();
//
//            for (AirMockCell cell : cells) {
//                if(cell.parentColumn.columnId.equals(columnKey)){
//                    cell.data.discardValue();
//                    break;
//                }
//            }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            //this will not happen
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("CELL", "Something happened " + databaseError);
        }
    }
}
