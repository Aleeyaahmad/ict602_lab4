package com.example.lab4aleeyaahmad;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    Button buttonAdd;
    DatabaseReference databaseArtists;

    List<Artist> artistList;
    ListView listViewArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        // Create database reference
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        editTextName = findViewById(R.id.editTextName);
        buttonAdd = findViewById(R.id.buttonAddArtist);

        listViewArtists = findViewById(R.id.ListViewArtist);
        artistList = new ArrayList<>();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });

        onStart(); // Call onStart method to attach the listener
    }

    private void addArtist() {
        String name = editTextName.getText().toString().trim();

        if (!TextUtils.isEmpty(name)) {
            String id = databaseArtists.push().getKey();
            Artist artist = new Artist(id, name);
            databaseArtists.child(id).setValue(artist);
            Toast.makeText(MainActivity.this, "Artist added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // attaching value event listener
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                artistList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Artist artist = postSnapshot.getValue(Artist.class);
                    artistList.add(artist);
                }
                ArtistList artistAdapter = new ArtistList(MainActivity.this, artistList);
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }
}
