package com.vovik.airtablepretender.TableModel.Table;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.vovik.airtablepretender.MainActivity;
import com.vovik.airtablepretender.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
  table construction is done to minimize processor usage
 */

@IgnoreExtraProperties
public class AirMockTable implements View.OnClickListener{

    public List<AirMockColumn> columns;

    private List<AirMockRow> rows;

    private ColumnChangeListener columnListener;
    private RowChangeListener rowListener;
    private NameChangeListener nameListener;
    private RowDataListener rowDataListener;
    private OnCheckedListener onCheckedListener;

    public TableLayout tableView;
    private TableRow column_rows_view;
    private String new_row_key;
    private EditText tableName;
    private boolean consume_checked;
    private int checked_amount;
    private CheckBox checkBoxView;
    private ImageButton deleteButton;

    private DatabaseReference databaseTable;
    private DatabaseReference databaseColumns;
    private DatabaseReference databaseRows;
    private DatabaseReference tableRowData;
    private DatabaseReference tableNameData;

    public MainActivity parentActivity;

    public AirMockTable(MainActivity parent){
        parentActivity = parent;
        columns = new ArrayList<>();
        rows = new ArrayList<>();
        databaseTable = null;
        columnListener = new ColumnChangeListener(this);
        rowListener = new RowChangeListener(this);
        nameListener = new NameChangeListener(this);
        rowDataListener = new RowDataListener(this);
        onCheckedListener = new OnCheckedListener(this);
        consume_checked = false;
        checked_amount = 0;
    }

    public void setTable(DatabaseReference table, DatabaseReference tableName, View table_view){

        if(databaseTable != null){
            unbindListeners();
        }

        this.tableView = table_view.findViewById(R.id.table_data);
        column_rows_view = table_view.findViewById(R.id.column_row);
        deleteButton = table_view.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(this);

        checkBoxView = new CheckBox(parentActivity);
        column_rows_view.addView(checkBoxView);
        checkBoxView.setOnCheckedChangeListener(onCheckedListener);

        this.tableName = table_view.findViewById(R.id.nameEdit);

        this.tableName.addTextChangedListener(nameListener);
        tableNameData = tableName;
        tableNameData.addValueEventListener(nameListener);

        databaseTable = table;

        //load columns
        databaseColumns =
                databaseTable.child("columns");
        databaseColumns.addChildEventListener(columnListener);

        //load rows
        databaseRows=
                databaseTable.child("rows");
        databaseRows.addChildEventListener(rowListener);

        tableRowData = databaseTable.child("data").child("new_row_id");
        tableRowData.addValueEventListener(rowDataListener);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.deleteButton && checked_amount > 0){
            for (AirMockRow row:rows) {
                if(row.checked){
                    row.setChecked(false);
                    row.deleteSelf();
                }
            }
        }
        //just to be safe
        checked_amount = 0;
    }

    public void unbindListeners(){

        databaseColumns.removeEventListener(columnListener);
        databaseRows.removeEventListener(rowListener);
        tableRowData.removeEventListener(rowDataListener);
        tableNameData.removeEventListener(nameListener);

        for(AirMockRow row : rows){
            row.unbindListeners();
        }
    }

    public void rowChecked(boolean checked){

        if(checkBoxView.isChecked()){
            if(!checked){
                consume_checked = true;
                checkBoxView.setChecked(false);
            }
        }

        if(checked){
            ++checked_amount;
        }else{
            --checked_amount;
        }

        if(checked_amount>0){
           deleteButton.setColorFilter(parentActivity.getResources().getColor(R.color.colorPrimaryDark));
        }else{
           deleteButton.setColorFilter(parentActivity.getResources().getColor(R.color.colorTransparent));
        }

    }

    private void changeChecked(boolean checkedChanged){

        if(consume_checked) {
            consume_checked = false;
            return;
        }

        for (AirMockRow row:rows) {
            if(!row.rowKey.equals(new_row_key)){
                row.setChecked(checkedChanged);
            }else{
                row.setChecked(false);
            }
        }

    }

    class OnCheckedListener implements CompoundButton.OnCheckedChangeListener {

        AirMockTable parentTable;
        OnCheckedListener(AirMockTable parent){
            parentTable = parent;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            parentTable.changeChecked(isChecked);
        }
    }

    class RowDataListener implements ValueEventListener{

        AirMockTable parentTable;

        RowDataListener(AirMockTable parent){
            parentTable = parent;
        }
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            parentTable.new_row_key = dataSnapshot.getValue().toString();
            parentTable.rowListener.addNewBlankRow();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("D_COL", "column listener failed" + databaseError.getMessage());
        }
    }

    class NameChangeListener implements ValueEventListener, TextWatcher {

        boolean consume_update;
        boolean consume_send;
        Timer timer;
        AirMockTable parentTable;

        NameChangeListener(AirMockTable parent){
            parentTable = parent;
            consume_update = false;
            consume_send = false;
        }

        void uploadChanges(){
            if(consume_send){
                consume_send = false;
                return;
            }
            consume_update = true;
            parentTable.tableNameData.setValue(
                    parentTable.tableName.getText().toString());
        }
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(consume_update){
                consume_update = false;
                return;
            }
            consume_send = true;
            parentTable.tableName.setText(dataSnapshot.getValue().toString());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("D_COL", "column listener failed" + databaseError.getMessage());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            if(timer != null){
                timer.cancel();
            }
        }

        @Override
        public void afterTextChanged(final Editable s) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    uploadChanges();
                }
            }, 3000);
        }
    }

    class ColumnChangeListener implements ChildEventListener{

        AirMockTable parentTable;

        ColumnChangeListener(AirMockTable parent){
            parentTable = parent;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

            AirMockColumn column = dataSnapshot.getValue(AirMockColumn.class);
            column.parentTable = parentTable;

            int columnPosition = -1;

            if(previousChildName == null){
                columnPosition = parentTable.columns.size();
            }else{
                for(int i = 0; i < parentTable.columns.size(); ++i){
                    if(parentTable.columns.get(i).columnId.equals(previousChildName)){
                        columnPosition = i+1;
                        break;
                    }
                }
            }

            if(columnPosition != -1){
                LayoutInflater mInflater=
                    (LayoutInflater) tableView.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                column.setMainData(
                        mInflater.inflate(R.layout.column_view, column_rows_view, false),
                        dataSnapshot
                );

                parentTable.columns.add(columnPosition, column);
                column_rows_view.addView(column.columnView, columnPosition+1);

                for (AirMockRow row : rows){
                    row.columnAdded(columnPosition);
                }
            }
        }
        //data about column changed
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

            int columnPosition = -1;

            for(int i = 0; i < parentTable.columns.size(); ++i){
                if(parentTable.columns.get(i)
                        .columnId.equals(dataSnapshot.getKey())){
                    columnPosition = i;
                    break;
                }
            }
            if(columnPosition != -1){
                int updateCode = parentTable.columns.get(columnPosition).updateData(dataSnapshot);
                if(updateCode == AirMockColumn.UPDATE_TYPE){
                    for(AirMockRow row : rows){
                        row.columnUpdated(columnPosition);
                    }
                }

            }
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String columnId = dataSnapshot.getKey();

            int columnPosition = -1;

            for(int i = 0; i < parentTable.columns.size(); ++i){
                if(parentTable.columns.get(i).columnId.equals(columnId)){
                    columnPosition = i;
                    break;
                }
            }

            if(columnPosition != -1){
                parentTable.columns.remove(columnPosition);
                column_rows_view.removeViewAt(columnPosition);

                for(AirMockRow row : rows){
                    row.columnRemoved(columnPosition);
                }
            }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            //will not happen?
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("D_COL", "column listener failed" + databaseError.getMessage());
        }
    }

    class RowChangeListener implements ChildEventListener {

        AirMockTable parentTable;

        RowChangeListener(AirMockTable parent){
            parentTable = parent;
        }

        public void addNewBlankRow(){

            TableRow new_row_view = new TableRow(tableView.getContext());

            DatabaseReference newRowRef = databaseRows.child(new_row_key);
            AirMockRow row = new AirMockRow(newRowRef, parentTable, new_row_view);

            parentTable.rows.add(row);
            tableView.addView(new_row_view);
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

            //detect new row added
            if(dataSnapshot.getKey().equals(new_row_key)){
                String[] rowKeyParts = new_row_key.split("[_]");
                String newRowKey = rowKeyParts[0]+"_"+(Long.parseLong(rowKeyParts[1])+1);
                tableRowData.setValue(newRowKey);
            }else{
                TableRow new_row_view = new TableRow(tableView.getContext());
                AirMockRow row = new AirMockRow(dataSnapshot.getRef(), parentTable, new_row_view);

                if(previousChildName == null || previousChildName.equals("")){
                        parentTable.rows.add(0,row);
                        tableView.addView(new_row_view, 1);
                }else{
                    boolean rowadded = false;

                    for(int i = 0; i < parentTable.rows.size(); ++i){
                        if(parentTable.rows.get(i).rowKey.equals(previousChildName)){
                            parentTable.rows.add(i+1, row);
                            tableView.addView( new_row_view, i+2);
                            rowadded = true;
                            break;
                        }
                    }
                    if(!rowadded){
                        if (parentTable.rows.size() > 0) {
                            parentTable.rows.add(parentTable.rows.size()-1,row);
                            tableView.addView(new_row_view, tableView.getChildCount()-1);
                        }else{
                            parentTable.rows.add(row);
                            tableView.addView(new_row_view, tableView.getChildCount()-1);
                        }
                    }
                }

            }

        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            //this is handled in cell changes
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String rowKey = dataSnapshot.getKey();

            for(int i = 0; i < parentTable.rows.size(); ++i){
                if(parentTable.rows.get(i).rowKey.equals(rowKey)){
                    tableView.removeView(parentTable.rows.get(i).row_view);
                    parentTable.rows.remove(i);
                    break;
                }
            }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            //this will not happen
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("D_ROW", "Row listener failed" + databaseError.getMessage());
        }

    }

}
