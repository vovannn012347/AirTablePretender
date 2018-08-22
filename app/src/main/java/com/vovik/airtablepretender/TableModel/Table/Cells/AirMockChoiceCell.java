package com.vovik.airtablepretender.table.TableModels.Cells;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.vovik.airtablepretender.TableModel.Table.AirMockColumn;

public class AirMockChoiceCell extends AirMockCellData implements AdapterView.OnItemSelectedListener {

    String choice;
    AirMockColumnChoiceCell parentColumn;
    boolean consume_update;
    boolean first_fired;

    public AirMockChoiceCell(AirMockColumn column){
        parentColumn = (AirMockColumnChoiceCell)column.data;
        consume_update = false;
        first_fired = true;
    }

    @Override
    public View createView(Context context){
        if(this.cell_view == null){
            Spinner cell_view = new Spinner(context);
            this.cell_view = cell_view;
            cell_view.setAdapter(parentColumn.getAdapter());

            if(choice != null){
                cell_view.setSelection(parentColumn.getChoiceIndex(choice));
            }
            cell_view.setOnItemSelectedListener(this);
        }

        return cell_view;
    }

    @Override
    public void readValue(DataSnapshot data){
        if(consume_update){
            consume_update = true;
            return;
        }

        Log.d("BreakPOINT1", "| " + data);
        readData(data.getValue(String.class));
    }

    @Override
    public void discardValue(){
        first_fired = true;
       ((Spinner)cell_view).setSelection(0,false);
    }

    @Override
    public void readData(String data){
        choice = data;
        Log.d("CHOICEUPDATE", "| " + data);

        if (cell_view != null) {
            int index = parentColumn.getChoiceIndex(choice);
            if(index > 0){
                ((Spinner)cell_view).setSelection(index);
            }
        }
    }

    @Override
    public String getData(){
        return choice;
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        if(first_fired){
            first_fired = false;
            return;
        }
        consume_update = true;

        mCellReference.setValue(parentColumn.getChoice(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
    }

    //todo: implement view changes
}
