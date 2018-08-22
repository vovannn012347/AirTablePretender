package com.vovik.airtablepretender.table.TableModels.ColumnCells;

import android.app.Activity;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.vovik.airtablepretender.table.TableControllers.CellController;
import com.vovik.airtablepretender.table.TableViews.CellView;
import com.vovik.airtablepretender.table.TableViews.ColumnCells.DateCellView;

public class ColumnChoiceCellModel {

    ColumnChoiceCellModel(){
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
/*
public List<String> choices;
    private Map<String, String> choicesStuff;//i hate this

    private ArrayAdapter<String> choicesAdapter;

    private ChoiceUpdate updateListener;

    public AirMockColumnChoiceCell(){
        choices = new ArrayList<>();
        choicesStuff = new TreeMap<>();
        updateListener = new ChoiceUpdate(this);
    }

    public int getChoiceIndex(String choice){
        return choices.indexOf(choice);
    }

    public String getChoice(int index){
        return choices.get(index);
    }

    public void setAdapter(Context context){
        if(choicesAdapter == null){
            choicesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, choices);
        }
    }

    public ArrayAdapter<String> getAdapter(){
        return choicesAdapter;
    }

    @Override
    public void ReadData(DataSnapshot columnData){
        choices.clear();
        choices.add("");
        for (DataSnapshot child:columnData.getChildren()) {
            choices.add(child.getValue().toString());
            choicesStuff.put(child.getKey(),child.getValue().toString());
        }

        columnData.getRef().addChildEventListener(updateListener);
    }

    class ChoiceUpdate implements ChildEventListener {
        AirMockColumnChoiceCell parent;

        ChoiceUpdate(AirMockColumnChoiceCell parent){
            this.parent = parent;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName){

            if(!choicesStuff.containsKey(dataSnapshot.getKey())) {
                choices.add(dataSnapshot.getValue().toString());
                choicesStuff.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
                if (choicesAdapter != null) {
                    choicesAdapter.notifyDataSetChanged();
                }
            }
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName){
            if(choicesStuff.containsKey(dataSnapshot.getKey())){
                choices.set(
                        choices.indexOf(choicesStuff.get(dataSnapshot.getKey())),
                        dataSnapshot.getValue().toString());
                choicesStuff.put(dataSnapshot.getKey(),dataSnapshot.getValue().toString());

                if(choicesAdapter!= null){
                    choicesAdapter.notifyDataSetChanged();
                }
            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot){
            if(choicesStuff.containsKey(dataSnapshot.getKey())){
                choices.remove(
                        choices.indexOf(choicesStuff.get(dataSnapshot.getKey())));
                choicesStuff.remove(dataSnapshot.getKey());
                if(choicesAdapter!= null){
                    choicesAdapter.notifyDataSetChanged();
                }
            }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s){
            ///?who cares
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError){
            //maybe i should log this
        }
    }
 */
