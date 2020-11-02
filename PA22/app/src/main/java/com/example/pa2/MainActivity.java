package com.example.pa2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static ListView BList;
    public static ArrayAdapter<Book> bookAdapter;
    public static ArrayList<Book> bookDataList;
    public String selectedFilter = "all";
    FirebaseFirestore db;
    public EditText ISBNNum;
    public Button ISBNSearchButton;
    public String isbnValue;
    public RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BList = findViewById(R.id.listview);
        db = FirebaseFirestore.getInstance();
        CollectionReference books = db.collection("books");


        ISBNSearchButton = findViewById(R.id.ISBNSearchButton);
        ISBNNum = findViewById(R.id.ISBNSearch);


//        Map<String, Object> data1 = new HashMap<>();
//        data1.put("author", "NAJWA ZEBIAN");
//        data1.put("ISBN", "12345");
//        data1.put("Borrower","Riky");
//        data1.put("status","Yes");
//
//        books.document("Mind Playter").set(data1);
//
//
//        String[] name = {"Mind Playter","Maybe you should talk to someone","The Silent Patient","I'm Dead, Now What?"};
//        String[] author = {"NAJWA ZEBIAN","Lori Gottlieb","Alex Michaelides"," Peter Pauper Press"};
//        String[] ISBN = {"12345","666666","88h8h8","loki999"};


        bookDataList = new ArrayList<>();
//        final CollectionReference collectionReference = db.collection("books");
//        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
//                    FirebaseFirestoreException error) {
//                bookDataList.clear();
//                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
//                {
//
//                    String BookName = doc.getId();
//                    String author = (String) doc.getData().get("author");
//                    String ISBN = (String) doc.getData().get("ISBN");
//                    String Borrower = (String) doc.getData().get("Borrower");
//                    String status = (String) doc.getData().get("status");
//
//                    Book book = new Book(BookName, author,ISBN);
//                    book.setBorrower(Borrower);
//                    Log.i("Test",status);
//                    if (status == "Yes" ) {
//                        book.setState(true);
//                    }
//                    else{
//                        book.setState(false);
//                    }
//                    bookDataList.add(book); // Adding the cities and provinces from FireStore
//
//                }
//                bookAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
//
//            }
//        });





//
//        for (int i = 0; i < name.length; i++) {
//            Book book = new Book(name[i], author[i], ISBN[i]);
//            if (i==3){
//                book.setState(true);
//                book.setBorrower("Riky");
//            }
//            else{
//                book.setState(false);
//                book.setBorrower("UnKnown");
//            }
//
//            bookDataList.add(book);
//
//        }

        bookAdapter = new BookAdapter(this, bookDataList);

        BList.setAdapter(bookAdapter);
        isbnSearch();


        ISBNSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isbnValue = ISBNNum.getText().toString(); // get the isbn on click
                Log.i("Riky",isbnValue);
                if (isbnValue.length() < 10) {
                    Toast.makeText(MainActivity.this, "ISBN must be 10 or 13 digits", Toast.LENGTH_SHORT).show();
                } else {


                    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbnValue;
                    //Do http request
                    Log.i("Riky",url);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            url,null, // here
                            new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            String[] result = jsonParser(response);
                            if (result[0] != null) {

                                Intent intent = new Intent(MainActivity.this,ISBNaddBook.class);

                                intent.putExtra("name",result[0]);
                                intent.putExtra("des",result[1]);
                                intent.putExtra("author",result[2]);
                                intent.putExtra("isbn",result[3]);
                                startActivityForResult(intent,0);

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    AppController.getInstance(MainActivity.this).addToRequestQueue(request);
                }
            }
        });



    }


    private void isbnSearch(){
        SearchView searchView = (SearchView) findViewById(R.id.isbnSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String inputIsbn) {
                ArrayList<Book> SearchBookIsbn = new ArrayList<Book>();
                for (Book book:bookDataList ){
                    if(book.getISBN().toLowerCase().contains(inputIsbn)){
                        Log.i("aaa","zzzzz");
                        SearchBookIsbn.add(book);
                    }
                }

                bookAdapter = new BookAdapter(getApplicationContext(), SearchBookIsbn);

                BList.setAdapter(bookAdapter);
                return false;
            }
        });
    }

    private void FilterList(String Clicked){
        selectedFilter = Clicked;
        ArrayList<Book> stateFilter = new ArrayList<Book>();
        for(Book book:bookDataList){
            if (Clicked == "UNBORROWED"){
                if (!book.getState()){
                    stateFilter.add(book);
                }
            }
            else if (Clicked == "BORROWED"){
                if (book.getState()){
                    stateFilter.add(book);
                }
            }

        }
        bookAdapter = new BookAdapter(this, stateFilter);

        BList.setAdapter(bookAdapter);

    }
    public void all_button(View view){
        bookAdapter = new BookAdapter(this, bookDataList);

        BList.setAdapter(bookAdapter);

    }

    public void UB_button(View view){
        FilterList("UNBORROWED");

    }
    public void B_button(View view){
        FilterList("BORROWED");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1) {
            if(data != null){
                String name = data.getStringExtra("name");
                String author = data.getStringExtra("author");
                String isbn = data.getStringExtra("isbn");
                String des = data.getStringExtra("des");

                Book newBook = new Book(name,author,isbn);
                bookDataList.add(newBook);
                bookAdapter.notifyDataSetChanged();
            }
        }
    }

    public String[] jsonParser(JSONObject response) {
        String[] result = new String[5]; // volume information holder
        try {
            String totalItems = response.optString("totalItems");
            if (totalItems.equalsIgnoreCase("0")) {

                Toast.makeText(MainActivity.this, "Invalid ISBN", Toast.LENGTH_LONG).show();
            } else {
                JSONArray jsonArray = response.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject items = jsonArray.getJSONObject(i);

                    // get title info
                    String title = items.getJSONObject("volumeInfo").optString("title");
                    String subtitle = items.getJSONObject("volumeInfo").optString("subtitle");
                    result[0] = title + " : " + subtitle;

                    // get author info
                    result[1] = items.getJSONObject("volumeInfo").optString("description");

                    // get category and page count info
                    result[2] = items.getJSONObject("volumeInfo").optString("authors");
                    result[3] = items.getJSONObject("volumeInfo").optString("identifier");


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}