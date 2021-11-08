package com.example.lab4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    TextView textViewId;
    TextView textViewProductName;
    TextView textViewProductPrice;
    TextView textViewDisplayId;

    EditText editProductName;
    EditText editProductPrice;

    Button addProductButton;
    Button findProductButton;
    Button deleteProductButton;

    ListView listViewProducts;

    ProductList productList;
    HashSet<Integer> presentIDs;

    DatabaseReference databaseProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewId = (TextView) findViewById(R.id.textViewProductId);
        textViewProductName = (TextView) findViewById(R.id.textViewProductName);
        textViewProductPrice = (TextView) findViewById(R.id.textViewProductPrice);
        textViewDisplayId = (TextView)  findViewById(R.id.displayProductId);

        editProductName = (EditText) findViewById(R.id.editProductName);
        editProductPrice = (EditText) findViewById(R.id.editProductPrice);

        addProductButton = (Button) findViewById(R.id.addButton);
        findProductButton = (Button) findViewById(R.id.findButton);
        deleteProductButton = (Button) findViewById(R.id.deleteButton);

        productList = new ProductList(MainActivity.this, new ArrayList<>());
        presentIDs = new HashSet<>();
        databaseProducts = FirebaseDatabase.getInstance().getReference("Products");

        listViewProducts = findViewById(R.id.productList);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        findProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findProduct();
            }
        });

        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
            }
        });

    }

    protected void onStart() {
        super.onStart();

        databaseProducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashSet<Integer> missingIDs = (HashSet<Integer>) presentIDs.clone();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);

                    missingIDs.remove(product.getID());
                    if (!presentIDs.contains(product.getID())) {
                        productList.add(product);
                        presentIDs.add(product.getID());
                    }
                }
                for (int i = 0; i < productList.getCount(); i++) {
                    Product p = productList.getItem(i);

                    if (missingIDs.contains(p.getID())) {
                        presentIDs.remove(p.getID());
                        productList.remove(p);
                    }
                }

                listViewProducts.setAdapter(productList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
    }

    public void addProduct() {
        textViewDisplayId.setText("Not assigned");

        String productName= editProductName.getText().toString();
        String productPrice = editProductPrice.getText().toString();

        // =======  error trap productname and productprice input text fields  =======
        // product name
        if (productName.equals("")) {
            editProductName.setError("The product name cannot be blank.");
            editProductName.requestFocus();
            return;
        }

        // product price
        else if (productPrice.equals("")) {
            editProductPrice.setError("The price cannot be blank.");
            editProductPrice.requestFocus();
            return;
        }


        // NOTE: The input is case sensitive, which means the course "Tennis" and "tennis" may co-exist at the same time

        // ================= Checking pre-existence of new product, ==================
        // =================== and finding an available ID for it ====================
        int firstAvailableID = -1;

        for (int i = 0; i < productList.getCount(); i++) {
            Product p = productList.getItem(i);

            if (p.getProductName().equals(productName)) {
                editProductName.setError("This product already exists. Please re-enter a new product name.");
                editProductName.requestFocus();

                return;
            }
            if (firstAvailableID == -1 && !presentIDs.contains(i)) {
                firstAvailableID = i;
            }
        }

        // set new product id as the highest value possible, aka the size of the product list
        if (firstAvailableID == -1) {
            firstAvailableID = productList.getCount();
        }

        // ================== Adding the new product to the database =================
        DatabaseReference productRef = databaseProducts.push();
        Product newProduct;

        try {
            newProduct = new Product(firstAvailableID, productName, Double.parseDouble(productPrice));
        } catch (NumberFormatException e) {
            editProductPrice.setError("The price must be in a normal decimal number format.");
            editProductPrice.requestFocus();

            return;
        }

        // check if product price is negative or not
        if (Float.parseFloat(productPrice) < 0) {
            editProductPrice.setError("The price cannot be negative");
            editProductPrice.requestFocus();
            return;
        }

        // add the new product

        productRef.setValue(newProduct);
        editProductName.setText("");
        editProductPrice.setText("");
    } // end of addProduct()


    public void findProduct() {
        String productName= editProductName.getText().toString();

        // =======  check if the course name is empty or not  =======
        if (productName.equals("")) {
            editProductName.setError("The product name cannot be blank.");
            editProductName.requestFocus();
            return;
        }

        // NOTE: The input is case sensitive, which means the course "Tennis" and "tennis" may co-exist at the same time

        for (int i = 0; i < productList.getCount(); i++) {
            Product p = productList.getItem(i);

            if (p.getProductName().equals(productName)) {
                editProductName.setError(null);
                editProductPrice.setError(null);
                textViewDisplayId.setText(String.valueOf(p.getID()));
                editProductPrice.setText(String.valueOf(p.getPrice()));

                return;
            }
        }
    }

    public void deleteProduct() {
        textViewDisplayId.setText("Not assigned");

        String productName= editProductName.getText().toString();
        String productPrice = editProductPrice.getText().toString();

        // =======  check if the course name is empty or not  =======
        if (productName.equals("")) {
            editProductName.setError("The product name cannot be blank.");
            editProductName.requestFocus();
            return;
        }

        // NOTE: The input is case sensitive, which means the course "Tennis" and "tennis" may co-exist at the same time

        // =======  check if the course exists in the database or not  =======

        // Orders in search for the course name
        Query checkCourse = databaseProducts.orderByChild("productName").equalTo(productName);

        checkCourse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // checks whether the username already exists or not
                if (!dataSnapshot.exists()) {
                    editProductName.setError("This product doesn't exist. Please re-enter a new product name.");
                    editProductName.requestFocus();
                } else {
                    editProductName.setError(null);
                    editProductPrice.setError(null);
                    dataSnapshot.getChildren().iterator().next().getRef().removeValue();
                    editProductName.setText("");
                    editProductPrice.setText("");
                } // end of outer if/else

            } // end of onDataChange()

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            } // end of onCalled()
        }); // end of checkCourse listener

    }
}