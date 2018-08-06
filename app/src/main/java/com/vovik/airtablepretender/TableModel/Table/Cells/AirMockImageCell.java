package com.vovik.airtablepretender.TableModel.Table.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.UploadTask;
import com.vovik.airtablepretender.R;
import com.vovik.airtablepretender.TableModel.Table.AirMockColumn;
import com.vovik.airtablepretender.TableModel.Table.ColumnCells.AirMockColumnImageCell;

import java.io.ByteArrayOutputStream;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AirMockImageCell extends AirMockCellData implements View.OnClickListener {

    private String image_name;
    private AirMockColumnImageCell parentColumn;

    public AirMockImageCell(AirMockColumn column){
        parentColumn = (AirMockColumnImageCell)column.data;
    }

    @Override
    public View createView(Context context){
        ImageButton cell_view = new ImageButton(context);
        cell_view.setOnClickListener(this);
        cell_view.setImageResource(R.mipmap.ic_launcher);
        return cell_view;
    }

    @Override
    public void readValue(DataSnapshot data){
        image_name = data.getValue(String.class);
        if(cell_view != null){
            Glide.with(cell_view.getContext())
                    .using(new FirebaseImageLoader())
                    .load(parentColumn.getStorageReference()
                            .child("thumbnail_" + image_name)
                    ).into((ImageView) cell_view);
        }
    }

    @Override
    public  void discardValue(){
        ((ImageView)cell_view).setImageResource(R.mipmap.ic_launcher);
        image_name = "";
    }

    @Override
    public void readData(String data){
    }

    @Override
    public String getData(){
        //no secrests will leak
        return "";
    }

    @Override
    public void onClick(View v){

        if(image_name != null){

        }else{
            parentColumn.getParentCell().parentTable.parentActivity.loadImage(this);
        }
    }

    public void setImage(Bitmap imageBitmap,final String name){

        int THUMBNAIL_SIZE = 128;

        int outWidth;
        int outHeight;
        int inWidth = imageBitmap.getWidth();
        int inHeight = imageBitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = THUMBNAIL_SIZE;
            outHeight = (inHeight * THUMBNAIL_SIZE) / inWidth;
        } else {
            outHeight = THUMBNAIL_SIZE;
            outWidth = (inWidth * THUMBNAIL_SIZE) / inHeight;
        }

        Bitmap thumbnail =
                Bitmap.createScaledBitmap(imageBitmap, outWidth, outHeight,false);

        this.image_name = name;
        ((ImageButton)this.cell_view).setImageBitmap(thumbnail);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] arr = baos.toByteArray();


        parentColumn.
                getStorageReference()
                .child("thumbnail_" + name).putBytes(arr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mCellReference.setValue(name);
            }
        });

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        arr = baos.toByteArray();

        parentColumn
                .getStorageReference()
                .child(name).putBytes(arr);

    }
}
