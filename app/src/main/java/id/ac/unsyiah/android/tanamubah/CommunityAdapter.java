package id.ac.unsyiah.android.tanamubah;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ac.unsyiah.android.tanamubah.model.PostModel;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PostModel> list;

    public CommunityAdapter(Context context, ArrayList<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        PostModel p = list.get(pos);

        h.tvName.setText(p.getUserName());
        h.tvContent.setText(p.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvContent;

        ViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvUsername);
            tvContent = v.findViewById(R.id.tvContent);
        }
    }
}
