package com.app.assist;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;


public class PlaceAdapterItem extends RecyclerView.Adapter<PlaceAdapterItem.MyViewHolder> {

    PlaceAdapterClickCallback callback;
    private Context context;
    private List<HashMap<String, String>> placeList;
    private String type, selectType;

    public PlaceAdapterItem(Context context, List<HashMap<String, String>> placeList, String type, String selectType, PlaceAdapterClickCallback callback) {
        this.context = context;
        this.placeList = placeList;
        this.type = type;
        this.callback = callback;
        this.selectType = selectType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_places, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(placeList.get(position).get("place_name"));
        System.out.println("PlaceAdapterItem.onBindViewHolder " + placeList.get(position));
        String address;
        if (placeList.get(position).get("address") != null)
            address = placeList.get(position).get("address") + ", " + placeList.get(position).get("vicinity");
        else
            address = placeList.get(position).get("vicinity");
        holder.address.setText(address);
        holder.thumbnail.setImageResource(getIconFromType(type));
        if (Constant.EMAIL_ADMIN.equalsIgnoreCase(Utilities.getString(context, Utilities.EMAIL))) {
            if ("false".equalsIgnoreCase(placeList.get(position).get("verified"))) {
                holder.verify.setVisibility(View.VISIBLE);

            } else
                holder.verify.setVisibility(View.GONE);
        }
        holder.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage("Please wait");
                dialog.show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constant.DB_CHILD_PLACES)
                        .child(selectType)
                        .child(placeList.get(position).get("id"));
                reference.child(Constant.DB_CHILD_VERIFIED).setValue("true")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    holder.verify.setVisibility(View.GONE);
                                    placeList.get(position).put("verified", "true");
                                }
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    private int getIconFromType(String type) {
        if (type.equalsIgnoreCase("atm"))
            return R.drawable.atm;
        else if (type.equalsIgnoreCase("bank"))
            return R.drawable.bank;
        else if (type.equalsIgnoreCase("bar"))
            return R.drawable.bar;
        else if (type.equalsIgnoreCase("beauty_salon"))
            return R.drawable.beauty;
        else if (type.equalsIgnoreCase("book_store"))
            return R.drawable.bookstore;
        else if (type.equalsIgnoreCase("doctor"))
            return R.drawable.doctor;
        else if (type.equalsIgnoreCase("school"))
            return R.drawable.education;
        else if (type.equalsIgnoreCase("electronics_store"))
            return R.drawable.elec;
        else if (type.equalsIgnoreCase("gas_station"))
            return R.drawable.petrol;
        else if (type.equalsIgnoreCase("grocery_or_supermarket"))
            return R.drawable.grocery;
        else if (type.equalsIgnoreCase("hospital"))
            return R.drawable.hospital;
        else if (type.equalsIgnoreCase("lodging"))
            return R.drawable.hotel;
        else if (type.equalsIgnoreCase("insurance_agency"))
            return R.drawable.insurance;
        else if (type.equalsIgnoreCase("movie_theater"))
            return R.drawable.movies;
        else if (type.equalsIgnoreCase("park"))
            return R.drawable.park;
        else if (type.equalsIgnoreCase("pharmacy"))
            return R.drawable.pharmacy;
        else if (type.equalsIgnoreCase("police"))
            return R.drawable.police;
        else if (type.equalsIgnoreCase("post_office"))
            return R.drawable.post;
        else if (type.equalsIgnoreCase("restaurant"))
            return R.drawable.restaurant;
        else if (type.equalsIgnoreCase("shopping_mall"))
            return R.drawable.shopping;
        else if (type.equalsIgnoreCase("hindu_temple"))
            return R.drawable.temple;
        else if (type.equalsIgnoreCase("gym"))
            return R.drawable.fitness;
        else if (type.equalsIgnoreCase("pg"))
            return R.drawable.ic_pg;
        else
            return R.mipmap.ic_launcher;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, address, rating;
        ImageView thumbnail;
        Button verify;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name_tv);
            address = itemView.findViewById(R.id.place_address_tv);
//            rating = itemView.findViewById(R.id.place_rating);
            thumbnail = itemView.findViewById(R.id.place_thumbnail_iv);
            verify = itemView.findViewById(R.id.iv_verify);
            itemView.setOnClickListener(this);
            verify.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.iv_verify:
                   /* final ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("Please wait");
                    dialog.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child(Constant.DB_CHILD_PLACES)
                            .child(selectType)
                            .child(placeList.get(getLayoutPosition()).get("id"));
                    reference.child(Constant.DB_CHILD_VERIFIED).setValue("true")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Place verified", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    getLayoutPosition()
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });*/
                    break;
                default:
                    callback.onPlaceAdapterClick(view.getId(), getLayoutPosition());
                    break;
            }
        }
    }

}
