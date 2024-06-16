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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hoothub.R;
import com.example.hoothub.activity.Activity.CommentFragment;
import com.example.hoothub.lib.Post;

import java.util.ArrayList;

public class ListPostAdapter extends RecyclerView.Adapter<ListPostAdapter.ListViewHolder> {
    private ArrayList<Post> postList;
    private boolean isLiked;
    private Context context;
    public ListPostAdapter(Context context, ArrayList<Post> list) {

        this.context = context;
        this.postList = list;
    }
    @NonNull
    @Override
    public ListPostAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPostAdapter.ListViewHolder holder, int position) {

        Post post = postList.get(position);
        Glide.with(holder.itemView.getContext())
                        .load(post.getImg()).apply(
                                new RequestOptions().override(55,55)
                ).into(holder.tvimg);
        holder.tvname.setText(post.getName());
        holder.tvcontent.setText(post.getPost_content());
        holder.tvcomment.setText(String.valueOf(post.getPost_comment()) + " " + "comments");
        holder.tvliked.setText(String.valueOf(post.getPost_like()) + " " + "likes");
        holder.tvtime.setText(post.getTime());

        isLiked = true;
        holder.btnLiked.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(isLiked){
                    holder.btnLiked.setImageResource(R.drawable.like_img);

                }else {
                    holder.btnLiked.setImageResource((R.drawable.like_vektor));
                }
                isLiked = !isLiked;
            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment commentFragment = new CommentFragment();
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, commentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvname, tvcontent, tvtime, tvliked, tvcomment;
        ImageButton btnLiked, btnComment;
        ImageView tvimg;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvcomment = itemView.findViewById(R.id.post_comment);
            tvcontent = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            tvliked = itemView.findViewById(R.id.post_liked);
            tvimg = itemView.findViewById(R.id.user_profile_image1);
            btnLiked = itemView.findViewById(R.id.like);
            btnComment = itemView.findViewById(R.id.comment);
        }
    }
}
