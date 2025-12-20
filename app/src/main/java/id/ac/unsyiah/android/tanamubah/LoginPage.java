package id.ac.unsyiah.android.tanamubah;

import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private EditText emailForm, passwordForm;
    private Button btnLogin;
    private TextView askSignup;
    private FirebaseAuth mAuth;
    TextView resendVerification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);

        emailForm = findViewById(R.id.emailForm);
        passwordForm = findViewById(R.id.passwordForm);
        btnLogin = findViewById(R.id.btnLogin);
        askSignup = findViewById(R.id.askSignup);
        resendVerification = findViewById(R.id.resendVerification);


        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            attemptLogin();
        });
        askSignup.setOnClickListener(v -> {
            // Arahkan ke halaman pendaftaran
            Intent intent = new Intent(LoginPage.this, RegisterPage.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (!user.isEmailVerified()) {
                Toast.makeText(
                        LoginPage.this,
                        "Silakan verifikasi email terlebih dahulu.",
                        Toast.LENGTH_LONG
                ).show();

                resendVerification.setVisibility(View.VISIBLE);

                resendVerification.setOnClickListener(v -> {
                    user.sendEmailVerification()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(
                                            LoginPage.this,
                                            "Email verifikasi dikirim ulang.",
                                            Toast.LENGTH_SHORT
                                    ).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            LoginPage.this,
                                            "Gagal kirim ulang: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                });

                mAuth.signOut();
                btnLogin.setEnabled(true);
                return;
            }


            startActivity(new Intent(LoginPage.this, Home.class));
            finish();
        }
    }

    private void attemptLogin() {
        String email = emailForm.getText().toString().trim();
        String password = passwordForm.getText().toString();

        // Reset error messages
        emailForm.setError(null);
        passwordForm.setError(null);

        boolean isValid = true;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            emailForm.setError("Email harus diisi");
            focusView = emailForm;
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailForm.setError("Format email tidak valid");
            focusView = emailForm;
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordForm.setError("Kata sandi harus diisi");
            focusView = passwordForm;
            isValid = false;
        } else if (password.length() < 6) {
            passwordForm.setError("Kata sandi minimal 6 karakter");
            focusView = passwordForm;
            isValid = false;
        }

        if (isValid) {
            btnLogin.setEnabled(false);
            loginUser(email, password);
        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
            Toast.makeText(this, "Mohon periksa kembali input login Anda.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUser(String email, String password) {
        // Tampilkan loading indicator di sini jika Anda memilikinya

        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Sembunyikan loading indicator di sini

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) {
                        Toast.makeText(LoginPage.this,
                                "Terjadi kesalahan autentikasi.",
                                Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }

                    if (!user.isEmailVerified()) {
                        Toast.makeText(LoginPage.this,
                                "Silakan verifikasi email terlebih dahulu.",
                                Toast.LENGTH_LONG).show();

                        mAuth.signOut();
                        btnLogin.setEnabled(true);
                        return;
                    }

                    Toast.makeText(LoginPage.this,
                            "Selamat datang, Ubahers! Login berhasil.",
                            Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginPage.this, Home.class));
                    finish();
                } else {
                    String errorMessage = "Login gagal. Silakan coba lagi.";

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Email atau kata sandi salah.";
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        errorMessage = "Akun tidak ditemukan.";
                    }

                    Toast.makeText(LoginPage.this, errorMessage, Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            }
        });
    }


}