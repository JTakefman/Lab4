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
import java.util.List;

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

    List<Product> productList;

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

        productList = new ArrayList<>();
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
                productList.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                ProductList productsAdapter = new ProductList(MainActivity.this, productList);
                listViewProducts.setAdapter(productsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void addProduct() {
        String productName= editProductName.getText().toString();
        String productPrice = editProductPrice.getText().toString();

        // =======  check if the course name is empty or not  =======
        if (productName.equals("")) {
            editProductName.setError("The product name cannot be blank.");
            editProductName.requestFocus();
            return;
        }

        else if (productPrice.equals("")) {
            editProductPrice.setError("The price cannot be blank.");
            editProductPrice.requestFocus();
            return;
        }

        // NOTE: The input is case sensitive, which means the course "Tennis" and "tennis" may co-exist at the same time

        // =======  check if the course exists in the database or not  =======

        // fetches instance of database.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

        // Orders in search for the course name
        Query checkCourse = reference.orderByChild("productName").equalTo(productName);

        checkCourse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // checks whether the username already exists or not
                if (dataSnapshot.exists()) {
                    editProductName.setError("This product already exists. Please re-enter a new product name.");
                    editProductName.requestFocus();
                } else {
                    editProductName.setError(null);
                    editProductPrice.setError(null);
                    DatabaseReference ref = reference.push(); // add new course here
                    ref.setValue(new Product(productList.size(), productName, Double.parseDouble(productPrice)));
                    editProductName.setText("");
                    editProductPrice.setText("");
                } // end of outer if/else

            } // end of onDataChange()

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            } // end of onCalled()
        }); // end of checkCourse listener

    }

    public void findProduct() {
        String productName= editProductName.getText().toString();

        // =======  check if the course name is empty or not  =======
        if (productName.equals("")) {
            editProductName.setError("The product name cannot be blank.");
            editProductName.requestFocus();
            return;
        }

        // NOTE: The input is case sensitive, which means the course "Tennis" and "tennis" may co-exist at the same time

        // =======  check if the course exists in the database or not  =======

        // fetches instance of database.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

        // Orders in search for the course name
        Query checkCourse = reference.orderByChild("productName").equalTo(productName);

        checkCourse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // checks whether the username already exists or not
                if (!dataSnapshot.exists()) {
                    editProductName.setError("Product does not exist");
                    editProductName.requestFocus();
                } else {
                    editProductName.setError(null);
                    editProductPrice.setError(null);
                    Product temp = (Product) dataSnapshot.getChildren().iterator().next().getValue(Product.class);

                    textViewDisplayId.setText( String.valueOf(temp.getID()) );

                    editProductPrice.setText( String.valueOf(temp.getPrice()) );
                } // end of outer if/else

            } // end of onDataChange()

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            } // end of onCalled()
        }); // end of checkCourse listener
    }

    public void deleteProduct() {

        String productName= editProductName.getText().toString();
        String productPrice = editProductPrice.getText().toString();

        // =======  check if the course name is empty or not  =======
        if (productName.equals("")) {
            editProductName.setError("The product name cannot be blank.");
            editProductName.requestFocus();
            return;
        }

        else if (productPrice.equals("")) {
            editProductPrice.setError("The price cannot be blank.");
            editProductPrice.requestFocus();
            return;
        }

        // NOTE: The input is case sensitive, which means the course "Tennis" and "tennis" may co-exist at the same time

        // =======  check if the course exists in the database or not  =======

        // fetches instance of database.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

        // Orders in search for the course name
        Query checkCourse = reference.orderByChild("productName").equalTo(productName);

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