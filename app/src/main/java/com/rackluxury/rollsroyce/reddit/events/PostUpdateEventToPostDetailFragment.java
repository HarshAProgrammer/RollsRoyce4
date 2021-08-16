package com.rackluxury.rollsroyce.reddit.events;

import com.rackluxury.rollsroyce.reddit.post.Post;

public class PostUpdateEventToPostDetailFragment {
    public final Post post;

    public PostUpdateEventToPostDetailFragment(Post post) {
        this.post = post;
    }
}
