package id.ac.unsyiah.android.tanamubah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import id.ac.unsyiah.android.tanamubah.model.PostModel;

public class Komunitas extends AppCompatActivity {

    private RecyclerView rvCommunity;
    private TextView tvEmptyPost;
    private FloatingActionButton fabPost;

    private CommunityAdapter adapter;
    private ArrayList<PostModel> postList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_komunitas);

        // INIT VIEW (MATCH XML)
        rvCommunity = findViewById(R.id.rvCommunity);
        fabPost = findViewById(R.id.fabPost);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        db = FirebaseFirestore.getInstance();
        tvEmptyPost = findViewById(R.id.tvEmptyPost);

        setupRecyclerView();
        loadPosts();

        // FAB → POST THREAD
        fabPost.setOnClickListener(v ->
                startActivity(new Intent(this, PostThreadActivity.class))
        );

        // NAVBAR
        bottomNav.setSelectedItemId(R.id.nav_kom);
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Home.class));
                finish();
                return true;
            }

            if (id == R.id.nav_posting) {
                startActivity(new Intent(this, TambahEvent.class));
                return true;
            }

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
                return true;
            }

            return true;
        });
    }

    // =============================
    // SETUP RECYCLER VIEW
    // =============================
    private void setupRecyclerView() {
        adapter = new CommunityAdapter(this, postList);
        rvCommunity.setLayoutManager(new LinearLayoutManager(this));
        rvCommunity.setAdapter(adapter);
    }

    // =============================
    // LOAD POST DARI FIRESTORE
    // =============================
    private void loadPosts() {

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    postList.clear();

                    for (var doc : value.getDocuments()) {
                        PostModel post = doc.toObject(PostModel.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }
                    if (postList.isEmpty()) {
                        rvCommunity.setVisibility(View.GONE);
                        tvEmptyPost.setVisibility(View.VISIBLE);
                    } else {
                        rvCommunity.setVisibility(View.VISIBLE);
                        tvEmptyPost.setVisibility(View.GONE);
                    }


                    adapter.notifyDataSetChanged();
                });
    }
}
