package com.app.assist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterDemo extends ArrayAdapter<BeanDemo> {
    BeanDemo bd;
    Context ctx;
    ArrayAdapter<BeanDemo> data;

    public AdapterDemo(Context context, int resource, ArrayList<BeanDemo> objects) {
        super(context, resource, objects);
        this.ctx = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = ((LayoutInflater) this.ctx.getSystemService("layout_inflater")).inflate(R.layout.gridsingle, parent, false);
        TextView name = (TextView) v.findViewById(R.id.tn1);
        ImageView image = (ImageView) v.findViewById(R.id.imageView1);
        this.bd = (BeanDemo) getItem(position);
        name.setText(this.bd.getName());
        image.setBackgroundResource(this.bd.getImage());
        return v;
    }
}
