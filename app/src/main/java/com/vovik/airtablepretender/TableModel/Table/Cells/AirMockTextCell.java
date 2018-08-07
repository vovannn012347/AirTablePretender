package com.vovik.airtablepretender.TableModel.Table.Cells;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.vovik.airtablepretender.TableModel.Table.AirMockColumn;

import java.util.Timer;
import java.util.TimerTask;

public class AirMockTextCell extends AirMockCellData implements TextWatcher {

    String text;
    private Timer timer;
    boolean consume_update;

    public AirMockTextCell(AirMockColumn column){
        consume_update = false;
    }

    @Override
    public View createView(Context context){
        EditText cell_view = new EditText(context);
        cell_view.addTextChangedListener(this);
        if(text != null){
            cell_view.setText(text);
        }
        return cell_view;
    }

    @Override
    public void readValue(DataSnapshot data){
        if(consume_update){
            consume_update = false;
            return;
        }

        text = data.getValue(String.class);
        ((EditText)cell_view).setText(text);
    }

    @Override
    public void discardValue(){
        if(cell_view != null){
            ((EditText)cell_view).setText("");
        }
    }

    @Override
    public void readData(String data){

        text = data;
        if(cell_view != null){
            ((EditText)cell_view).setText(text);
        }
    }

    @Override
    public String getData(){
        return text;
    }

    private void uploadChanges(){
        this.text = ((EditText)cell_view).getText().toString();
        try{
            mCellReference.setValue(text);
        }catch (Exception e) {
            Log.d("BLAH", "changes exception");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, int start, int before, int count) {
        if(timer != null)
            timer.cancel();
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