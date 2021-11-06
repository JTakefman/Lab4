package com.example.lab4;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductList extends ArrayAdapter<Product> {

    private Activity context;
    List<Product> products;
    //HashMap<String, Product> map;


    public ProductList(Activity context, List<Product> products) {

        super(context, R.layout.product_list, products);
        this.context = context;
        this.products = products;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.product_list, null, true);


        TextView textViewName = (TextView) listViewItem.findViewById(R.id.displayName);
        TextView textViewPrice = (TextView) listViewItem.findViewById(R.id.displayPrice);

        Product product = products.get(position);
        textViewName.setText(product.getProductName());
        textViewPrice.setText(String.valueOf(product.getPrice()));
        return listViewItem;
    }


}
