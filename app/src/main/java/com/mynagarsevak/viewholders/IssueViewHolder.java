package com.mynagarsevak.viewholders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mynagarsevak.R;

/**
 * Created by sd on 21-04-2017.
 */

public class IssueViewHolder extends RecyclerView.ViewHolder {

  View mView;

  public IssueViewHolder(View itemView) {
    super(itemView);
    mView = itemView;
  }

  public void setTitle(String title){
    TextView issue_title = (TextView) mView.findViewById(R.id.title);
    issue_title.setText(title);
  }

  public void setDesc(String desc){
    TextView issue_desc = (TextView) mView.findViewById(R.id.desc);
    issue_desc.setText(desc);
  }

  public void setImage(Context context, String image){
    ImageView issue_image = (ImageView) mView.findViewById(R.id.issue_thumb);
//    issue_image.setImageURI(Uri.parse(image));
    Glide.clear(issue_image);
    Glide.with(context)
      .load(image)
      .into(issue_image);
  }
}
