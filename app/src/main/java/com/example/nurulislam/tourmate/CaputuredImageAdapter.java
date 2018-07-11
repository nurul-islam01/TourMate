package com.example.nurulislam.tourmate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.Hourly.List;
import com.example.nurulislam.tourmate.Weekly.Image;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.example.nurulislam.tourmate.TourExpence.TAG;

public class CaputuredImageAdapter extends RecyclerView.Adapter<CaputuredImageAdapter.CapturedHolder> {
    java.util.List<File> fileList;
    private ProgressDialog progressDialog;
    public CaputuredImageAdapter(java.util.List<File> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public CapturedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.image_list_row,parent,false);
        progressDialog = new ProgressDialog(parent.getContext());
        return new CapturedHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CapturedHolder holder, final int position) {
        try {
            Picasso.get().load(fileList.get(position).getAbsoluteFile()).resize(170,160).centerInside().into(holder.imageCapturedIV);
//            Bitmap captureBitmap = BitmapFactory.decodeFile(fileList.get(position).getAbsolutePath());
//            holder.imageCapturedIV.setImageBitmap(captureBitmap);
        }catch (Exception e){
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }
        try {
            holder.caputereImageNameTV.setText(fileList.get(position).getName());
        }catch (Exception e){
            Toast.makeText(holder.itemView.getContext(), "Name not set", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class CapturedHolder  extends RecyclerView.ViewHolder{
        private ImageView imageCapturedIV;
        private ImageView imageBin;
        private TextView caputereImageNameTV;
        private CardView imageCV;
        public CapturedHolder(final View itemView) {
            super(itemView);

            imageCapturedIV = itemView.findViewById(R.id.imageCapturedIV);
            caputereImageNameTV = itemView.findViewById(R.id.caputereImageNameTV);
            imageBin = itemView.findViewById(R.id.imageBin);
            imageCV = itemView.findViewById(R.id.imageCV);


            imageCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertadd = new AlertDialog.Builder(itemView.getContext());
                    LayoutInflater factory = LayoutInflater.from(itemView.getContext());
                    final View view = factory.inflate(R.layout.image_dialoge, null);
                    TouchImageView dialog_imageview = (TouchImageView) view.findViewById(R.id.dialog_imageview);
                    try{
                        Bitmap bitmap = BitmapFactory.decodeFile(fileList.get(getAdapterPosition()).getAbsolutePath());
                        dialog_imageview.setImageBitmap(bitmap);
                        dialog_imageview.setScaleType(ImageView.ScaleType.MATRIX);
                        alertadd.setView(view);
                    }catch(Exception e){
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }

                    alertadd.setCancelable(true);
                    AlertDialog dialog = alertadd.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(true);
                    dialog.show();
                }

            });
            imageBin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(itemView.getContext());
                    builder1.setMessage("Are Your Sure Do This.");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton( "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fileList.get(getAdapterPosition()).delete();
                                    progressDialog.setMessage("Deleting...");
                                    progressDialog.show();
                                    fileList.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    dialog.cancel();
                                    progressDialog.dismiss();
                                    Toast.makeText(itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });

        }
    }
}
