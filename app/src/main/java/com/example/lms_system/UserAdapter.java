package com.example.lms_system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{


    Context context;
    ArrayList<UserData> userDataArrayList;

    public UserAdapter(Context context, ArrayList<UserData> userDataArrayList) {
        this.context = context;
        this.userDataArrayList = userDataArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.studentitem,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        UserData userData = userDataArrayList.get(position);

        holder.Name.setText(userData.Name);
        holder.Id.setText(userData.IdNumber);
        holder.Email.setText(userData.Email);
    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name,Id,Email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.tvName);
            Id = itemView.findViewById(R.id.tvId);
            Email = itemView.findViewById(R.id.tvEmail);


        }
    }

}
