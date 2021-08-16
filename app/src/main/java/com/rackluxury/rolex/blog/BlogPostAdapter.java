package com.rackluxury.rolex.blog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rackluxury.rolex.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.PostViewHolder> {

    private Context context;
    private List<BlogItem> blogItems;
    boolean isShimmer = true;


    public BlogPostAdapter(Context context, List<BlogItem> blogItems) {
        this.context = context;
        this.blogItems = blogItems;

    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.blog_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final BlogItem blogItem = blogItems.get(position);

        if (isShimmer){
            holder.shimmerFrameLayout.startShimmer();
        }else{
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);

        }

        holder.blogPostTitle.setText(blogItem.getTitle());


        Document document = Jsoup.parse(blogItem.getContent());

        Elements elements = document.select("img");
        Glide.with(context).load(elements.get(0).attr("src")).into(holder.blogPostImage);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BlogDetailActivity.class);
                intent.putExtra("url", blogItem.getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogItems.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout blogPostLayout;
        public TextView blogPostTitle;
        public ImageView blogPostImage;
        public ImageView blogPostAuthorImage;
        ShimmerFrameLayout shimmerFrameLayout;


        public PostViewHolder(View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.sflBlogPost);
            blogPostLayout = itemView.findViewById(R.id.rlBlogPost);
            blogPostTitle = (TextView) itemView.findViewById(R.id.tvTitleBlogPost);
            blogPostImage = (ImageView) itemView.findViewById(R.id.ivBlogPost);
            blogPostAuthorImage = itemView.findViewById(R.id.imgAuthorPostDetail);

        }
    }
}
