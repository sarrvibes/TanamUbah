package id.ac.unsyiah.android.tanamubah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        FrameLayout BtnSignIn = findViewById(R.id.btnSignIn);
        FrameLayout BtnSignUp = findViewById(R.id.btnSignUp);

        BtnSignIn.setOnClickListener(v -> HalLogin());
        BtnSignUp.setOnClickListener(v -> Halregis());

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(MainActivity.this, Home.class));
                finish();
            } else {
                mAuth.signOut();
            }
        }
    }

    private void HalLogin() {
        startActivity(new Intent(this, LoginPage.class));
    }

    private void Halregis() {
        startActivity(new Intent(this, RegisterPage.class));
    }
}
