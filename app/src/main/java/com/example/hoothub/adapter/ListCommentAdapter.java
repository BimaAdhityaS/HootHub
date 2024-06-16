package com.example.hoothub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoothub.R;
import com.example.hoothub.activity.Activity.CommentFragment;
import com.example.hoothub.model.comment;
import com.example.hoothub.model.post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.ListViewHolder>{
    private Context context;
    private ArrayList<comment> commentList;
    public ListCommentAdapter(Context context, ArrayList<comment> commentList){
        this.commentList = commentList;
        this.context = context;
    }
    @NonNull
    @Override
    public ListCommentAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCommentAdapter.ListViewHolder holder, int position) {
        comment currentPost = commentList.get(position);

        // Use a dummy image from drawable resources
        holder.tvimg.setImageResource(R.drawable.dummy_image);  // Replace 'dummy_image' with your actual drawable resource name

        holder.tvname.setText(currentPost.getUsername());
        holder.tvcontent.setText(currentPost.getContent());
        holder.tvcomment.setText(currentPost.getReply_count() + " replies");
        holder.tvliked.setText(currentPost.getLike_count() + " likes");
        String formattedDate = formatDate(currentPost.getCreated_at());
        holder.tvtime.setText(formattedDate);

//        holder.btnComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Fragment commentFragment = CommentFragment.newInstance(Integer.parseInt(currentPost.getId()));
//                FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
//                        .getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frame_layout, commentFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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
        TextView tvname, tvcontent, tvtime, tvliked, tvcomment;
        ImageButton btnComment;
        ImageView tvimg;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvcomment = itemView.findViewById(R.id.post_comment);
            tvcontent = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            tvliked = itemView.findViewById(R.id.post_liked);
            tvimg = itemView.findViewById(R.id.user_profile_image1);
            btnComment = itemView.findViewById(R.id.comment);
        }
    }
}
