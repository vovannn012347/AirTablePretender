package com.vovik.airtablepretender.TableModel.Table.Cells;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.vovik.airtablepretender.TableModel.Table.AirMockColumn;

import java.util.Timer;
import java.util.TimerTask;

public class AirMockNumberCell extends AirMockCellData implements TextWatcher {

    public String number;
    private Timer timer;
    boolean consume_update;

    public AirMockNumberCell(AirMockColumn column){
        consume_update = false;
    }

    @Override
    public View createView(Context context){
        EditText cell_view = new EditText(context);

        cell_view.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);

        cell_view.addTextChangedListener(this);

        if(number != null){
            cell_view.setText(number.toString());
        }

        return cell_view;
    }

    @Override
    public void readValue(DataSnapshot data){
        if(consume_update){
            consume_update = false;
            return;
        }
        number = data.getValue(String.class);
        if (number!=null) {
            ((EditText)cell_view).setText(number);
        }
    }

    @Override
    public  void discardValue(){
        number = null;
        ((EditText)cell_view).setText("");
    }

    @Override
    public void readData(String data){

        number = data;
        ((EditText)cell_view).setText(data);
    }

    @Override
    public String getData(){
        if(number != null){
            return number.toString();
        }else{
            return "";
        }
    }

    private void uploadChanges(){
        consume_update = true;
        String text = ((EditText)cell_view).getText().toString();
        try{
            mCellReference.setValue(Long.parseLong(text));
        }catch (Exception e) {
            Log.d("BLAH", "blah");
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
