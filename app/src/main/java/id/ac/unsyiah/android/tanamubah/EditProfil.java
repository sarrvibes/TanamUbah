package id.ac.unsyiah.android.tanamubah;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class EditProfil extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText inputEmail, inputNama, inputWa;
    private RadioGroup radioGender;
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profil);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputNama  = findViewById(R.id.inputNama);
        inputWa    = findViewById(R.id.inputLokasi);
        radioGender = findViewById(R.id.radioGender);
        btnSimpan  = findViewById(R.id.simpanbtn);

        loadUserProfile();

        btnSimpan.setOnClickListener(v -> simpanProfil());


    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        // Email dari FirebaseAuth (tidak boleh diedit)
        inputEmail.setText(user.getEmail());
        inputEmail.setEnabled(false);

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        inputNama.setText(documentSnapshot.getString("name"));
                        inputWa.setText(documentSnapshot.getString("phone"));

                        String gender = documentSnapshot.getString("gender");
                        if ("wanita".equals(gender)) {
                            radioGender.check(R.id.rbWanita);
                        } else if ("pria".equals(gender)) {
                            radioGender.check(R.id.rbPria);
                        } else {
                            radioGender.check(R.id.rbNonBiner);
                        }
                    } else {
                        // DEFAULT gender
                        radioGender.check(R.id.rbNonBiner);
                    }
                });
    }

    private void simpanProfil(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String email = user.getEmail();
        String name = inputNama.getText().toString().trim();
        String phone = inputWa.getText().toString().trim();

            if (name.isEmpty()) {
            inputNama.setError("Nama tidak boleh kosong");
            return;
        }

        String gender;
        int selectedId = radioGender.getCheckedRadioButtonId();
            if (selectedId == R.id.rbWanita) {
            gender = "wanita";
        } else if (selectedId == R.id.rbPria) {
            gender = "pria";
        } else {
            gender = "non-biner";
        }

        Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("phone", phone);
            data.put("gender", gender);

        db.collection("users").document(uid)
                .set(data)
                .addOnSuccessListener(unused ->
            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
                )
                        .addOnFailureListener(e ->
            Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show()
                );

        finish();
    }




}