package id.ac.unsyiah.android.tanamubah;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DetailEventActivity extends AppCompatActivity {

    private String eventId;
    private ProgressBar progressBar;


    ImageView imgEvent;
    TextView tvNama, tvOrganizer, tvDesc, tvLokasi, tvRelawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        eventId = getIntent().getStringExtra("eventId");
        imgEvent = findViewById(R.id.fotoEvent);
        tvNama = findViewById(R.id.tvNamaEvent);
        tvOrganizer = findViewById(R.id.tvOrganizer);
        tvDesc = findViewById(R.id.tvDesc);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvRelawan = findViewById(R.id.tvRelawan);
        progressBar = findViewById(R.id.progressBar);


        Button btnDaftar = findViewById(R.id.btnDaftar);

        btnDaftar.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah kamu yakin ingin mendaftar event ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        daftarEvent();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });



        if (eventId == null) {
            Toast.makeText(this, "Event tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
        loadEventDetail();


    }





    private void daftarEvent() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan login dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference regRef = db
                .collection("events")
                .document(eventId)
                .collection("registrations")
                .document(userId);

        regRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Toast.makeText(this, "Kamu sudah terdaftar", Toast.LENGTH_SHORT).show();
            } else {
                simpanPendaftaran(db, regRef, eventId, userId);
            }
        });
    }

    private void simpanPendaftaran(
            FirebaseFirestore db,
            DocumentReference regRef,
            String eventId,
            String userId
    ) {
        DocumentReference eventRef = db.collection("events").document(eventId);

        db.runTransaction(transaction -> {
            // baca event dulu
            DocumentSnapshot eventSnap = transaction.get(eventRef);
            if (!eventSnap.exists()) throw new RuntimeException("Event tidak ditemukan");

            long current = eventSnap.getLong("currentRelawan") != null
                    ? eventSnap.getLong("currentRelawan") : 0;

            // simpan registrasi di events
            transaction.set(regRef, new HashMap<String, Object>() {{
                put("userId", userId);
                put("registeredAt", FieldValue.serverTimestamp());
            }});

            // update jumlah relawan
            transaction.update(eventRef, "currentRelawan", current + 1);

            // simpan di user_registrations
            DocumentReference userRegRef = db
                    .collection("user_registrations")
                    .document(userId)
                    .collection("events")
                    .document(eventId);

            transaction.set(userRegRef, new HashMap<String, Object>() {{
                put("status", "aktif");
                put("registeredAt", FieldValue.serverTimestamp());
                put("eventName", eventSnap.getString("nama")); // opsional, buat UI
            }});

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Berhasil mendaftar 🎉", Toast.LENGTH_SHORT).show();
             loadEventDetail();

            if (getParent() instanceof Home) {
                ((Home) getParent()).refresh();
            }
        }).addOnFailureListener(e -> {
            Log.e("REGISTER", "Daftar gagal", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void loadEventDetail() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) return;

                    // ambil data
                    String nama = snapshot.getString("nama");
                    String organizer = snapshot.getString("penyelenggara");
                    String desc = snapshot.getString("deskripsi");
                    String lokasi = snapshot.getString("lokasi");
                    String imageUri = snapshot.getString("imageUri");

                    long current = snapshot.getLong("currentRelawan") != null
                            ? snapshot.getLong("currentRelawan") : 0;

                    long target = snapshot.getLong("targetRelawan") != null
                            ? snapshot.getLong("targetRelawan") : 0;

                    // set UI
                    tvNama.setText(nama);
                    tvOrganizer.setText(organizer);
                    tvDesc.setText(desc);
                    tvLokasi.setText(lokasi);
                    tvRelawan.setText(current + " / " + target + " Relawan");

                    if (imageUri != null && !imageUri.isEmpty()) {
                        Glide.with(this).load(imageUri).into(imgEvent);
                    }

                    progressBar.setMax(100);

                    if (target > 0) {
                        progressBar.setMax((int) target);
                        progressBar.setProgress((int) current);
                    } else {
                        progressBar.setMax(1);
                        progressBar.setProgress(0);
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal load event", Toast.LENGTH_SHORT).show()
                );
    }



}
