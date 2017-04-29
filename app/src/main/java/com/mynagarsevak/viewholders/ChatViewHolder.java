package com.mynagarsevak.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.mynagarsevak.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sd on 24-04-2017.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {

  View mView;

  public ChatViewHolder(View itemView) {
    super(itemView);
    mView = itemView;
  }

  public void setSender(String sendername){
    TextView message_sender = (TextView) mView.findViewById(R.id.message_sender_name);
    message_sender.setText(sendername.toString());
  }

  public void setMessage(String msg){
    TextView senders_message = (TextView) mView.findViewById(R.id.list_message);
    senders_message.setText(msg);
  }

  public void setTime(String time,Context ctx){
    String timeToSet;
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    DateFormat formatter2 = new SimpleDateFormat("dd MMM", Locale.getDefault());

    try {
      Date startDate = (Date)formatter.parse(time);
      Date startDate2 = (Date)formatter2.parse(time);

      if(startDate.equals(new Date())){
        timeToSet=time.substring(11, 16);
      }else{

        Toast.makeText(ctx,"aaa",Toast.LENGTH_LONG).show();
        timeToSet = startDate2.toString();
      }
      TextView senders_message_time = (TextView) mView.findViewById(R.id.message_list_time);
      senders_message_time.setText(timeToSet);
    } catch (ParseException e) {
      e.printStackTrace();
    }

  }

//  public void setImage(Context context, String image){
//    ImageView issue_image = (ImageView) mView.findViewById(R.id.issue_thumb);
////    issue_image.setImageURI(Uri.parse(image));
//    Glide.clear(issue_image);
//    Glide.with(context)
//      .load(image)
//      .into(issue_image);
//  }
}
