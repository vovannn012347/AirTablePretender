package com.vovik.airtablepretender.TableModel.Table.ColumnCells;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vovik.airtablepretender.TableModel.Table.AirMockColumn;

public class AirMockColumnImageCell extends AirMockColumnCellData {

    StorageReference firestoreReference;
    AirMockColumn column;

    //todo: implement this class properly
    public void ReadData(DataSnapshot columnData){
        firestoreReference = FirebaseStorage.getInstance().getReference().child("data");
    }

    public void setParentCell(AirMockColumn column){
        this.column = column;
    }

    public AirMockColumn getParentCell(){
        return column;
    }

    public StorageReference getStorageReference(){
        return firestoreReference;
    }
}
