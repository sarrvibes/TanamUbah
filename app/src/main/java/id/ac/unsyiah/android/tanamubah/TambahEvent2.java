package id.ac.unsyiah.android.tanamubah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahEvent2 extends AppCompatActivity {

    EditText etDana, etRelawan, etLokasi, etTglMulai, etTglSelesai;
    Button btnPosting;
    ImageView btnBack;

    // data dari step 1
    String imageUri, nama, penyelenggara, email, noHp, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tambah_event2);

        initView();
        getDataFromIntent();

        btnBack.setOnClickListener(v -> finish());
        btnPosting.setOnClickListener(v -> validateAndSubmit());
    }

    private void initView() {
        etDana = findViewById(R.id.inputDana);
        etRelawan = findViewById(R.id.inputRelawan);
        etLokasi = findViewById(R.id.inputLokasi);
        etTglMulai = findViewById(R.id.inputTgl);
        etTglSelesai = findViewById(R.id.inputSelesai);

        btnPosting = findViewById(R.id.btnPost);
        btnBack = findViewById(R.id.btnBack);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        imageUri = intent.getStringExtra("imageUri");
        nama = intent.getStringExtra("nama");
        penyelenggara = intent.getStringExtra("penyelenggara");
        email = intent.getStringExtra("email");
        noHp = intent.getStringExtra("noHp");
        desc = intent.getStringExtra("desc");
    }

    private void validateAndSubmit() {

        if (etDana.getText().toString().trim().isEmpty()) {
            etDana.setError("Target dana wajib diisi");
            return;
        }

        if (etRelawan.getText().toString().trim().isEmpty()) {
            etRelawan.setError("Jumlah relawan wajib diisi");
            return;
        }

        if (etLokasi.getText().toString().trim().isEmpty()) {
            etLokasi.setError("Lokasi wajib diisi");
            return;
        }

        if (etTglMulai.getText().toString().trim().isEmpty()) {
            etTglMulai.setError("Tanggal mulai wajib diisi");
            return;
        }

        if (etTglSelesai.getText().toString().trim().isEmpty()) {
            etTglSelesai.setError("Tanggal selesai wajib diisi");
            return;
        }


        // ==== SEMUA VALID ====
        submitData();
    }

    private void submitData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        int danaInt;
        try {
            danaInt = Integer.parseInt(etDana.getText().toString().trim());
        } catch (NumberFormatException e) {
            etDana.setError("Dana harus berupa angka");
            return;
        }

        int relawanInt;
        try {
            relawanInt = Integer.parseInt(etRelawan.getText().toString().trim());
        } catch (NumberFormatException e) {
            etRelawan.setError("Relawan harus berupa angka");
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("nama", nama);
        event.put("penyelenggara", penyelenggara);
        event.put("email", email);
        event.put("noHp", noHp);
        event.put("desc", desc);
        event.put("imageUri", imageUri);

        event.put("lokasi", etLokasi.getText().toString());
        event.put("dana", danaInt);
        event.put("targetRelawan", relawanInt);
        event.put("tglMulai", etTglMulai.getText().toString());
        event.put("tglSelesai", etTglSelesai.getText().toString());
        event.put("createdAt", System.currentTimeMillis());

        event.put("currentRelawan", 0);

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Event berhasil diposting 🎉", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Home.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal posting event", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE", e.getMessage());
                });



    }

}