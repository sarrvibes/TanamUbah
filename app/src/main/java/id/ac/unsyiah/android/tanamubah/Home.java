package id.ac.unsyiah.android.tanamubah;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.ac.unsyiah.android.tanamubah.model.EventModel;

public class Home extends AppCompatActivity {

    private TextView namaUser, tvEmptyEvent, tvEventAktif;
    private RecyclerView rvEvent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration activeEventListener;

    private EventAdapter adapter;
    private ArrayList<EventModel> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // INIT
        BottomNavigationView navbar = findViewById(R.id.bottomNav);
        EditText etSearch = findViewById(R.id.etSearch);
        ImageView btnSearch = findViewById(R.id.btnSearch);

        namaUser = findViewById(R.id.heading_dash);
        tvEmptyEvent = findViewById(R.id.tvEmptyEvent);
        rvEvent = findViewById(R.id.rvEvent);
        tvEventAktif = findViewById(R.id.tvEventAktif);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setGreeting();
        setupRecyclerView();


        // SEARCH (placeholder)
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Masukkan kata kunci", Toast.LENGTH_SHORT).show();
            }
        });

        // NAVBAR
        navbar.setSelectedItemId(R.id.nav_home);
        navbar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_posting) {
                startActivity(new Intent(this, TambahEvent.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_kom) {
                startActivity(new Intent(this, Komunitas.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
                return true;
            }
            return true;
        });

        TextView today = findViewById(R.id.today);
        today.setText(new SimpleDateFormat(
                "EEEE, dd MMM yyyy",
                new Locale("id", "ID")
        ).format(new Date()));

        loadEventData();
        loadUserActiveEventCount();
        listenUserActiveEvent();

    }

    private void setupRecyclerView() {
        adapter = new EventAdapter(this, eventList);
        rvEvent.setLayoutManager(new LinearLayoutManager(this));
        rvEvent.setAdapter(adapter);
    }

    private void loadEventData() {
        eventList.clear();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;


        db.collection("events")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        rvEvent.setVisibility(View.GONE);
                        tvEmptyEvent.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (var doc : querySnapshot.getDocuments()) {
                        EventModel event = doc.toObject(EventModel.class);
                        if (event != null) {

                            event.setId(doc.getId());

                            eventList.add(event);
                        }
                    }

                    rvEvent.setVisibility(View.VISIBLE);
                    tvEmptyEvent.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Gagal memuat event",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    private void setGreeting() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("name");
                    namaUser.setText(name != null ? "Hai " + name : "Hai Ubahers");
                });
    }

    private void loadUserActiveEventCount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            tvEventAktif.setText("0");
            return;
        }

        db.collection("user_registrations")
                .document(user.getUid())
                .collection("events")
                .whereEqualTo("status", "aktif")
                .get()
                .addOnSuccessListener(snapshot -> {
                    tvEventAktif.setText(String.valueOf(snapshot.size()));
                })
                .addOnFailureListener(e -> {
                    Log.e("HOME", "Gagal load event aktif", e);
                    tvEventAktif.setText("0");
                });
    }




    private void listenUserActiveEvent() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        activeEventListener = db.collection("user_registrations")
                .document(user.getUid())
                .collection("events")
                .whereEqualTo("status", "aktif")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("HOME", "Listener error", e);
                        return;
                    }

                    if (snapshot != null) {
                        tvEventAktif.setText(String.valueOf(snapshot.size()));
                    }
                });
    }
    public void refresh() {
        loadEventData();
        loadUserActiveEventCount();
    }


}
