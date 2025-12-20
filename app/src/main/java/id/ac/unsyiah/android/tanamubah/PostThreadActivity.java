package id.ac.unsyiah.android.tanamubah;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostThreadActivity extends AppCompatActivity {

    EditText etContent;
    Button btnPost;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etContent = findViewById(R.id.etContent);
        btnPost = findViewById(R.id.btnPost);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnPost.setOnClickListener(v -> postThread());
    }

    private void postThread() {

        String content = etContent.getText().toString().trim();

        Log.d(TAG, "Content: " + content);

        if (content.isEmpty()) {
            etContent.setError("Tulisan tidak boleh kosong");
            return;
        }

        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "User belum login");
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        String postId = UUID.randomUUID().toString();
        String userId = auth.getCurrentUser().getUid();

        Log.d(TAG, "UserID: " + userId);
        Log.d(TAG, "PostID: " + postId);

        Map<String, Object> data = new HashMap<>();
        data.put("postId", postId);
        data.put("userId", userId);
        data.put("userName", "Ubahers");
        data.put("content", content);
        data.put("imageUrl", "");
        data.put("timestamp", System.currentTimeMillis());

        Log.d(TAG, "Data siap dikirim ke Firestore");

        db.collection("posts")
                .document(postId)
                .set(data)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Thread berhasil dipost");
                    Toast.makeText(this, "Thread dipost 🚀", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal post thread", e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}