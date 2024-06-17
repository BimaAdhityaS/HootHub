package com.example.hoothub.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoothub.R;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.reply;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListReplyAdapter extends RecyclerView.Adapter<ListReplyAdapter.ListViewHolder>{
    private Context context;
    private ArrayList<reply> replylist;

    public ListReplyAdapter(Context context, ArrayList<reply> replylist) {
        this.context = context;
        this.replylist = replylist;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListReplyAdapter.ListViewHolder holder, int position) {
        reply currentReply = replylist.get(position);

        // Use a dummy image from drawable resources
        holder.tvimg.setImageResource(R.drawable.dummy_image);  // Replace 'dummy_image' with your actual drawable resource name

        holder.tvname.setText(currentReply.getUsername());
        holder.tvcontent.setText(currentReply.getContent());

        holder.tvliked.setText(currentReply.getLike_count() + " likes");
        String formattedDate = formatDate(currentReply.getCreated_at());
        holder.tvtime.setText(formattedDate);

    }

    @Override
    public int getItemCount() {
        return replylist.size();
    }

    private String formatDate(String inputDateString) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.ENGLISH);
        try {
            Date date = inputDateFormat.parse(inputDateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDateString; // Return the original string in case of error
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvname, tvcontent, tvtime, tvliked;
        ImageView tvimg;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvcontent = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            tvliked = itemView.findViewById(R.id.post_liked);
            tvimg = itemView.findViewById(R.id.user_profile_image1);

        }
    }
}
