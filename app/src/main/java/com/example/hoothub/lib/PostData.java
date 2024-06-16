package com.example.hoothub.lib;

import com.example.hoothub.R;

import java.util.ArrayList;

public class PostData {
    private static String[] PostName = {
    "Bobby Rafael", "Bima Adhitya"
    };

    private static String[] PostContent = {
            "content bobby", "content bima"
    };

    private static int[] PostLiked = {
            1, 2
    };

    private static int[] PostComment = {
            3, 4
    };

    private static String[] PostTime = {
            "3m", "4m"
    };

    private static int[] PostImage = {
            R.drawable.img_dummyprofilepic,
            R.drawable.img_dummyprofilepic
    };



    public static ArrayList<Post> getListData() {
        ArrayList<Post> list = new ArrayList<>();
        for (int position = 0; position < PostName.length; position++){
            Post post = new Post();
            post.setName(PostName[position]);
            post.setPost_comment(PostComment[position]);
            post.setPost_content(PostContent[position]);
            post.setPost_like(PostLiked[position]);
            post.setTime(PostTime[position]);
            post.setImg(PostImage[position]);
            list.add(post);
        }
        return list;
    }
}
