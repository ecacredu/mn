package com.mynagarsevak.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mynagarsevak.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sd on 25-04-2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

  View mView;
  RelativeLayout msgLayout;
  LinearLayout msgWrapLayout;
  TextView msg,msgTime;

  public MessageViewHolder(View itemView) {
    super(itemView);
    mView = itemView;
    msgLayout = (RelativeLayout) mView.findViewById(R.id.chat_recycler_layout);
    msgWrapLayout = (LinearLayout) mView.findViewById(R.id.msgWrapLayout);
    msg = (TextView) mView.findViewById(R.id.text_view_chat_message);
    msgTime = (TextView) mView.findViewById(R.id.text_view_chat_time);
  }


  public void setLayout(String senderType) {
    if(senderType.equals("user")){
      msgLayout.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
      msgWrapLayout.setBackgroundResource(R.drawable.chat_rounded_rect_bg_mine);
    }else if(senderType.equals("agent")){
      msgLayout.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
      msgWrapLayout.setBackgroundResource(R.drawable.chat_rounded_rect_bg_other);
    }
  }

  public void setMessage(String message) {
    msg.setText(message);
  }

  public void setMsgTime(String date) {
    msgTime.setText(date.substring(11, 16));

//    try {
//      DateFormat df = new SimpleDateFormat("HH:mm");
//      Date d= df.parse(date);
//      String time = df.format(d);
//      msgTime.setText(date);
//    } catch (ParseException e) {
//      e.printStackTrace();
//    }
  }
}
