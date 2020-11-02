package com.example.pa2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pa2.Book;
import com.example.pa2.R;

// this class is use tod define an List Adapter to display gear information on screen nicely
public class BookAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> books;
    private Context context;

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context,0,books);
        this.books = books;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;


        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content,parent,false);
        }


        Book book = books.get(position);



        TextView bookName = view.findViewById(R.id.bookName);

        TextView bookState = view.findViewById(R.id.bookState);

        TextView bookBorrower = view.findViewById(R.id.bookBorrower);

        bookName.setText(book.getBookName());

        bookBorrower.setText(book.getBorrower());
        if(book.getState()) {
            bookState.setText("Borrowed");
        }
        else{
            bookState.setText("UNBorrowed");
        }

        return view;
    }




}
