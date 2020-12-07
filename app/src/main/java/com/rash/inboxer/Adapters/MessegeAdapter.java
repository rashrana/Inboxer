package com.rash.inboxer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rash.inboxer.MessegeActivity;
import com.rash.inboxer.Model.Chat;
import com.rash.inboxer.Model.UserAccount;
import com.rash.inboxer.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MessegeAdapter extends RecyclerView.Adapter<MessegeAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat> chatList;
    private String imageurl;
    private FirebaseUser fuser;

    public MessegeAdapter(Context context, List<Chat> chatList, String imageurl) {
        this.context = context;
        this.chatList = chatList;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessegeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MSG_TYPE_RIGHT) {
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);

        } else {
            v = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t=v.findViewById(R.id.date);
                int vis= t.getVisibility();
                if(vis== View.GONE){
                    vis= View.VISIBLE;

                }else{
                    vis=View.GONE;
                }
                t.setVisibility(vis);

                TextView s= v.findViewById(R.id.time);
                int visi= s.getVisibility();
                if(visi== View.GONE){
                    visi= View.VISIBLE;

                }else{
                    visi=View.GONE;
                }
                s.setVisibility(visi);

            }
        });
        return new MessegeAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MessegeAdapter.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.show_messege.setText(chat.getMessege());
        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.profiledefault);
        } else {
            Picasso.with(context).load(imageurl).placeholder(R.drawable.profiledefault).into(holder.profile_image);
        }

        @SuppressLint("SimpleDateFormat") String ti= new SimpleDateFormat("hh:mm").format(chat.getTimes());
        @SuppressLint("SimpleDateFormat") String day= new SimpleDateFormat("dd MMM, yyyy").format(chat.getTimes());
        @SuppressLint("SimpleDateFormat") String hr1= new SimpleDateFormat("HH").format(chat.getTimes());
        if(Integer.parseInt(hr1)>11){
            ti+=" pm";
        }else{
            ti+=" am";
        }
        holder.date.setText(day);
        holder.time.setText(ti);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView show_messege,time,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_messege = itemView.findViewById(R.id.messege);
            time= itemView.findViewById(R.id.time);
            date= itemView.findViewById(R.id.date);
            date.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fuser.getEmail())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
