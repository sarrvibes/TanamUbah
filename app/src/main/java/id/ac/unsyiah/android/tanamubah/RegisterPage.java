package id.ac.unsyiah.android.tanamubah;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
//Database
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterPage extends AppCompatActivity {

    private EditText nameForm, emailForm, passwordForm, confirmpw;
    public Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameForm = findViewById(R.id.nameForm);
        emailForm = findViewById(R.id.emailForm);
        passwordForm = findViewById(R.id.passwordForm);
        confirmpw = findViewById(R.id.confirmpw);
        btnRegister = findViewById(R.id.button);

        btnRegister.setOnClickListener(v -> {
            attemptRegistration();
        });



    }

    private void attemptRegistration() {
        String name = nameForm.getText().toString().trim();
        String email = emailForm.getText().toString().trim();
        String password = passwordForm.getText().toString();
        String confirmPassword = confirmpw.getText().toString();

        // Reset error messages
        nameForm.setError(null);
        emailForm.setError(null);
        passwordForm.setError(null);
        confirmpw.setError(null);

        boolean isValid = true;

        if (name.isEmpty()) {
            nameForm.setError("Nama lengkap harus diisi");
            isValid = false;
        }

        // Validasi Email
        if (email.isEmpty()) {
            emailForm.setError("Email harus diisi");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailForm.setError("Format email tidak valid");
            isValid = false;
        }

        // Validasi Password
        if (password.isEmpty()) {
            passwordForm.setError("Kata sandi harus diisi");
            isValid = false;
        } else if (password.length() < 6) { // Contoh: minimal 6 karakter
            passwordForm.setError("Kata sandi minimal 6 karakter");
            isValid = false;
        }

        // Validasi Konfirmasi Password
        if (confirmPassword.isEmpty()) {
            confirmpw.setError("Konfirmasi kata sandi harus diisi");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmpw.setError("Kata sandi tidak cocok");
            isValid = false;
        }

        if (isValid) {
            btnRegister.setEnabled(false);
            registerUser(name, email, password);
        } else {
            Toast.makeText(this, "Pendaftaran Gagal. Mohon periksa kembali input Anda.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(
                                RegisterPage.this,
                                mapRegisterError(task.getException()),
                                Toast.LENGTH_LONG
                        ).show();
                        btnRegister.setEnabled(true);
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        Toast.makeText(
                                RegisterPage.this,
                                "User tidak valid.",
                                Toast.LENGTH_SHORT
                        ).show();
                        btnRegister.setEnabled(true);
                        return;
                    }


                    user.sendEmailVerification()
                            .addOnSuccessListener(unused -> {
                                saveUserDataToFirestore(user.getUid(), name, email);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(
                                        RegisterPage.this,
                                        "Gagal mengirim email verifikasi: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                                btnRegister.setEnabled(true);
                            });
                });
    }



    private String mapRegisterError(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            return "Email sudah terdaftar.";
        } else if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Kata sandi terlalu lemah.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Format email tidak valid.";
        }
        return "Pendaftaran gagal. Silakan coba lagi.";
    }

    private void saveUserDataToFirestore(String userId, String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("profileComplete", false);
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RegisterPage.this,
                            "Registrasi berhasil. Silakan cek email untuk verifikasi.",
                            Toast.LENGTH_LONG).show();

                    mAuth.signOut();
                    startActivity(new Intent(RegisterPage.this, LoginPage.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterPage.this,
                            "Gagal menyimpan data.",
                            Toast.LENGTH_LONG).show();

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) user.delete();

                    btnRegister.setEnabled(true);
                });
    }


}