package id.ac.unsyiah.android.tanamubah;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ac.unsyiah.android.tanamubah.model.EventModel;


public class Profile extends AppCompatActivity {

    private TextView namaUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView rvEvent;
    private EventAdapter adapter;
    private ArrayList<EventModel> eventList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Init Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // View
        namaUser = findViewById(R.id.namaUser);
        ImageView btnEdit = findViewById(R.id.btnEdit);
        ImageView btnLogout = findViewById(R.id.logout);

        rvEvent = findViewById(R.id.rvEvent);

        adapter = new EventAdapter(this, eventList);
        rvEvent.setLayoutManager(new LinearLayoutManager(this));
        rvEvent.setAdapter(adapter);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditProfil.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
        


        BottomNavigationView Navbar = findViewById(R.id.bottomNav);
        Navbar.setSelectedItemId(R.id.nav_profile);
        Navbar.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                return true;

            } else if (id == R.id.nav_posting) {
                Intent intent = new Intent(Profile.this, TambahEvent.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_kom) {
                Intent intent = new Intent(Profile.this, Komunitas.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(Profile.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserName();
        loadMyEvents();
    }

    private void loadUserName() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        db.collection("users").document(uid)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            String name = documentSnapshot.getString("name");

            if (name != null && !name.isEmpty()) {
                namaUser.setText(name);
            } else {
                namaUser.setText("Ubahers");
            }
        })
        .addOnFailureListener(e ->
                Toast.makeText(
                        this,
                        "Gagal mengambil data profil",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar Akun")
                .setMessage("Apakah kamu yakin ingin keluar dari akun?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logout() {
        mAuth.signOut();

        Intent intent = new Intent(Profile.this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();
    }

    private void loadMyEvents() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String emailUser = user.getEmail();

        db.collection("events")
                .whereEqualTo("email", emailUser)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    eventList.clear(); // ✅ sekarang BENAR

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        EventModel event = doc.toObject(EventModel.class);
                        if (event != null) {
                            event.setId(doc.getId());
                            eventList.add(event); // ✅ sekarang BENAR
                        }
                    }

                    adapter.notifyDataSetChanged(); // ✅ adapter sekarang ada
                });
    }


}