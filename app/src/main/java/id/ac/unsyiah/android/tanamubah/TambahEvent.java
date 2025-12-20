package id.ac.unsyiah.android.tanamubah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TambahEvent extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    ImageView imagePreview;
    Button btnChooseImage, btnNext;

    EditText etNama, etPenyelenggara, etEmail, etNoHp, etDesc;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tambah_event);

        imagePreview = findViewById(R.id.image_preview);
        btnChooseImage = findViewById(R.id.button_choose_image);
        btnNext = findViewById(R.id.btnNext);

        etNama = findViewById(R.id.inputNama);
        etPenyelenggara = findViewById(R.id.inputPenyelenggara);
        etEmail = findViewById(R.id.inputEmail);
        etNoHp = findViewById(R.id.inputNo);
        etDesc = findViewById(R.id.inputDesc);
        ImageView btnBack = findViewById(R.id.btnBack);


        btnBack.setOnClickListener(v -> finish());
        btnChooseImage.setOnClickListener(v -> openGallery());
        btnNext.setOnClickListener(v -> validateAndNext());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            // 🔥 INI BAGIAN PALING PENTING
            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            imagePreview.setImageURI(imageUri);
        }
    }

    private void validateAndNext() {
        if (imageUri == null) {
            toast("Pilih gambar event");
            return;
        }

        if (etNama.getText().toString().trim().isEmpty()) {
            etNama.setError("Nama event wajib diisi");
            return;
        }
        if (etPenyelenggara.getText().toString().trim().isEmpty()) {
            etPenyelenggara.setError("Penyelenggara wajib diisi");
            return;
        }
        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email wajib diisi");
            return;
        }
        if (etNoHp.getText().toString().trim().isEmpty()) {
            etNoHp.setError("No HP wajib diisi");
            return;
        }
        if (etDesc.getText().toString().trim().isEmpty()) {
            etDesc.setError("Deskripsi wajib diisi");
            return;
        }

        Intent intent = new Intent(this, TambahEvent2.class);
        intent.putExtra("imageUri", imageUri.toString());
        intent.putExtra("nama", etNama.getText().toString());
        intent.putExtra("penyelenggara", etPenyelenggara.getText().toString());
        intent.putExtra("email", etEmail.getText().toString());
        intent.putExtra("noHp", etNoHp.getText().toString());
        intent.putExtra("desc", etDesc.getText().toString());
        startActivity(intent);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
