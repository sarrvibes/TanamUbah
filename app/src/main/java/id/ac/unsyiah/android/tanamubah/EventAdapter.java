package id.ac.unsyiah.android.tanamubah;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import id.ac.unsyiah.android.tanamubah.model.EventModel;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EventModel> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    public EventAdapter(Context context, ArrayList<EventModel> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        EventModel e = list.get(pos);
        String eventId = e.getId();


        h.tvEvent.setText(e.getNama());
        h.tvOrganizer.setText(e.getPenyelenggara());
        h.tvDesc.setText(e.getDesc());

        if (e.getImageUri() != null && !e.getImageUri().isEmpty()) {
            Glide.with(context)
                    .load(e.getImageUri())
                    .placeholder(R.drawable.event1)
                    .error(R.drawable.event1)
                    .into(h.fotoEvent);
        } else {
            h.fotoEvent.setImageResource(R.drawable.event1);
        }

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, DetailEventActivity.class);
            i.putExtra("eventId", e.getId());
            context.startActivity(i);
        });

        db.collection("events")
                .document(eventId)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) return;

                    long current = doc.getLong("currentRelawan") != null
                            ? doc.getLong("currentRelawan") : 0;

                    long target = doc.getLong("targetRelawan") != null
                            ? doc.getLong("targetRelawan") : 0;

                    h.tvRelawan.setText(current + " / " + target + " Relawan");

                    int percent = target > 0
                            ? (int) ((current * 100) / target)
                            : 0;

                    h.progressBar.setProgress(percent);
                });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEvent, tvOrganizer, tvDesc, tvRelawan;
        ImageView fotoEvent;
        ProgressBar progressBar;


        ViewHolder(@NonNull View v) {
            super(v);
            tvEvent = v.findViewById(R.id.tvEvent);
            tvOrganizer = v.findViewById(R.id.tvOrganizer);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvRelawan = v.findViewById(R.id.tvRelawan);
            progressBar = v.findViewById(R.id.progressBar);
            fotoEvent = v.findViewById(R.id.fotoEvent);
        }
    }

}
