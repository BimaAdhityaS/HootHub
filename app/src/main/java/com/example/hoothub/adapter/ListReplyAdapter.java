package com.example.hoothub.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoothub.R;
import com.example.hoothub.activity.Activity.AddReplyActivity;
import com.example.hoothub.activity.Activity.MainActivity;
import com.example.hoothub.activity.Activity.OtherProfileFragment;
import com.example.hoothub.activity.Activity.ReplyFragment;
import com.example.hoothub.model.like_reply;
import com.example.hoothub.model.post;
import com.example.hoothub.model.reply;
import com.example.hoothub.model.report;
import com.example.hoothub.model.user;
import com.example.hoothub.retrofit.ApiInterface;
import com.example.hoothub.retrofit.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListReplyAdapter extends RecyclerView.Adapter<ListReplyAdapter.ListViewHolder>{
    private Context context;
    private ArrayList<reply> replylist;
    private SharedPreferences sp;

    public ListReplyAdapter(Context context, ArrayList<reply> replylist, SharedPreferences sp) {
        this.context = context;
        this.replylist = replylist;
        this.sp = sp;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListReplyAdapter.ListViewHolder holder, int position) {
        reply currentReply = replylist.get(position);

        String userId = sp.getString("user_id", null);
        fetchLikeReply(currentReply.getId(), userId, holder);
        getCurrentUser(currentReply.getUser_id(), holder);
        // Use a dummy image from drawable resources
        holder.tvimg.setImageResource(R.drawable.dummy_image);  // Replace 'dummy_image' with your actual drawable resource name

        holder.tvname.setText(currentReply.getUsername());
        holder.tvcontent.setText(currentReply.getContent());

        holder.tvliked.setText(currentReply.getLike_count());
        String formattedDate = formatDate(currentReply.getCreated_at());
        holder.tvtime.setText(formattedDate);

        holder.tvimg.setOnClickListener(view -> {
            String postUserId = currentReply.getUser_id();
            if (userId != null && userId.equals(postUserId)) {
                ((MainActivity) context).navigateToProfileFragment(true);
            } else {
                Fragment otherProfileFragment = new OtherProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", postUserId); // Pass user ID to the fragment
                otherProfileFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, otherProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        holder.btnLike.setOnClickListener(view -> {
            if (holder.isLiked) {
                fetchDeleteLike(currentReply.getId(), userId, holder);
            } else {
                // Check if user exists before adding like
                fetchAddLike(currentReply.getId(), userId, holder);
            }
        });
        holder.btnOption.setImageResource(R.drawable.options);
        holder.btnOption.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnOption);
            if(userId != null && !userId.equals(String.valueOf(currentReply.getUser_id()))) {
                popupMenu.inflate(R.menu.popup_report_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.report) {
                        Log.d("PopupMenu", "Edit clicked");
                        showReportDialog(currentReply.getId(), currentReply.getUser_id());
                    }
                    return false;
                });
            }else{
                popupMenu.inflate(R.menu.popup_edit_delete_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.edit_content){
                        fetchEditReplyById(currentReply.getId(), currentReply.getContent(),currentReply.getComment_id());
                    } else if (item.getItemId() == R.id.delete_content) {
                        fetchDeleteReplyById(currentReply.getId(), userId, currentReply.getComment_id());

                    }
                    return false;
                });
            }
            popupMenu.show();
        });

    }

    private void getCurrentUser(String user_id, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<user>> call = apiInterface.getCurrentUser(
                "eq."+user_id,
                "*"
        );

        call.enqueue(new Callback<List<user>>() {
            @Override
            public void onResponse(Call<List<user>> call, Response<List<user>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    user user_data = response.body().get(0); // Get the first user
                    Log.d("Profile", "Current User Profile: " + response.body());
                    Log.d("Profile", "User ID: " + user_data.getId());
                    Glide.with(holder.itemView.getContext())
                            .load(user_data.getImg_profile())
                            .placeholder(R.drawable.img_dummyprofilepic) // optional placeholder image
                            .error(R.drawable.dummy_image) // optional error image
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(holder.tvimg);
                } else {
                    Log.e("Profile", "Failed to fetch user data: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<user>> call, Throwable t) {
                Log.e("Profile", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    private void fetchDeleteReplyById(String id, String userId, String commentId) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<reply>> call = apiInterface.deleteContentReply(
                "eq." + id, "eq." + userId
        );
        call.enqueue(new Callback<List<reply>>() {
            @Override
            public void onResponse(Call<List<reply>> call, Response<List<reply>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Delete SuccessFully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Delete UnSuccessFully",Toast.LENGTH_SHORT).show();
                }
                Fragment replyfragment = ReplyFragment.newInstance(Integer.parseInt(commentId));
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context)
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, replyfragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure(Call<List<reply>> call, Throwable throwable) {
                Log.e("DeleteReply", "API call failed: " + throwable.getMessage(), throwable);

            }
        });
    }

    private void fetchEditReplyById(String id, String content, String commentId) {
        Intent intent = new Intent(context, AddReplyActivity.class);
        intent.putExtra("comment_id", String.valueOf(commentId));
        intent.putExtra("reply_content",content);
        intent.putExtra("reply_id",id);
        context.startActivity(intent);
    }

    private void showReportDialog(String replyId, String userId) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report, null);
        RadioGroup radioGroupReport = dialogView.findViewById(R.id.radioGroupReport);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        TextView tvDescription = dialogView.findViewById(R.id.tvDescription);

        radioGroupReport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioOther) {
                    tvDescription.setVisibility(View.VISIBLE);
                    etDescription.setVisibility(View.VISIBLE);
                } else {
                    tvDescription.setVisibility(View.GONE);
                    etDescription.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog.Builder builder = getBuilder(dialogView, radioGroupReport, etDescription, replyId, userId);
        builder.create().show();
    }

    @NonNull
    private AlertDialog.Builder getBuilder(View dialogView, RadioGroup radioGroupReport, EditText etDescription, String replyId, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Post");
        builder.setView(dialogView);


        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Handle the report submission
            int selectedId = radioGroupReport.getCheckedRadioButtonId();
            String reportType = getString(selectedId);
            String description = etDescription.getText().toString();
            Log.d("ReportDialog", "Report type: " + reportType);
            Log.d("ReportDialog", "Description: " + description);
            if(reportType == "Other") {
                fetchReportComment(description, replyId, userId);
            }else{
                fetchReportComment(reportType, replyId, userId);
            }
            // Add code to handle report submission
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder;
    }

    private void fetchReportComment(String reason, String replyId, String userId) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<report>> call = apiInterface.createReplyReport(
                userId, replyId, reason, "return=representation"
        );
        call.enqueue(new Callback<List<report>>() {
            @Override
            public void onResponse(Call<List<report>> call, Response<List<report>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "SuccessFully Report", Toast.LENGTH_LONG).show();
                    Log.d("response", String.valueOf(response));
                }else{
                    Toast.makeText(context, "UnSuccessFully Report", Toast.LENGTH_SHORT).show();
                    Log.d("response", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<List<report>> call, Throwable throwable) {
                Log.e("ReportReply", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    @NonNull
    private static String getString(int selectedId) {
        String reportType = "";
        if (selectedId == R.id.radioAbusive) {
            reportType = "Abusive or rude behaviour";
        } else if (selectedId == R.id.radioFraud) {
            reportType = "Possible Fraud";
        } else if (selectedId == R.id.radioPrivacy) {
            reportType = "Privacy Violation";
        } else if (selectedId == R.id.radioSpam) {
            reportType = "Spam";
        } else if (selectedId == R.id.radioOther) {
            reportType = "Other";
        }
        return reportType;
    }

    private void fetchAddLike(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_reply>> call = apiInterface.createLikeReply(
                userId, id, "return=representation"
        );
        call.enqueue(new Callback<List<like_reply>>() {
            @Override
            public void onResponse(Call<List<like_reply>> call, Response<List<like_reply>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("AddLikeReply", "Like created successfully: " + response.body());
                    holder.isLiked = true;
                    holder.btnLike.setImageResource(R.drawable.like_vektor);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes + 1));
                } else {
                    try {
                        Log.e("AddLikeReply", "Failed to create LikeReply: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("AddLikeReply", "Failed to create LikeReply: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_reply>> call, Throwable throwable) {
                Log.e("AddLikeReply", "API call failed: " + throwable.getMessage(), throwable);            }
        });
    }

    private void fetchDeleteLike(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_reply>> call = apiInterface.deleteLikeReply(
                "eq." + userId, "eq." + id
        );
        call.enqueue(new Callback<List<like_reply>>() {
            @Override
            public void onResponse(Call<List<like_reply>> call, Response<List<like_reply>> response) {
                if (response.isSuccessful()) {
                    Log.d("DeleteLikeReply", "Like created successfully: " + response.body());
                    holder.isLiked = false;
                    holder.btnLike.setImageResource(R.drawable.img_like);
                    int currentLikes = Integer.parseInt(holder.tvliked.getText().toString().split(" ")[0]);
                    holder.tvliked.setText(String.valueOf(currentLikes - 1));
                } else {
                    try {
                        Log.e("DeleteLikeReply", "Failed to create LikeReply: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("DeleteLikeReply", "Failed to create LikeReply: errorBody null or not readable", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<like_reply>> call, Throwable throwable) {
                Log.e("DeleteLikeReply", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void fetchLikeReply(String id, String userId, ListViewHolder holder) {
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<like_reply>> call = apiInterface.getLikeReply(
                "eq." + userId,"eq." + id, "*"
        );
        call.enqueue(new Callback<List<like_reply>>() {
            @Override
            public void onResponse(Call<List<like_reply>> call, Response<List<like_reply>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<like_reply> likeReplies = response.body();
                    if (likeReplies.isEmpty()) {
                        holder.isLiked = false;
                        holder.btnLike.setImageResource(R.drawable.img_like);
                    } else {
                        holder.isLiked = true;
                        holder.btnLike.setImageResource(R.drawable.like_vektor);
                    }
                } else {
                    Log.e("FetchLikeReply", "Failed to fetch like status: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<like_reply>> call, Throwable throwable) {
                Log.e("FetchLikeReply", "API call failed: " + throwable.getMessage(), throwable);
            }
        });
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
        public boolean isLiked =false;
        TextView tvname, tvcontent, tvtime, tvliked;
        ImageView tvimg;
        ImageButton btnLike, btnOption;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvcontent = itemView.findViewById(R.id.message);
            tvtime = itemView.findViewById(R.id.time);
            tvliked = itemView.findViewById(R.id.post_liked);
            tvimg = itemView.findViewById(R.id.user_profile_image1);
            btnLike = itemView.findViewById(R.id.like);
            btnOption = itemView.findViewById(R.id.options);
        }
    }
}
