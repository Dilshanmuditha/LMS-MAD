package com.example.lms_system;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModuleData> moduleDataArrayList;

    public ModuleAdapter(Context context, ArrayList<ModuleData> moduleDataArrayList) {
        this.context = context;
        this.moduleDataArrayList = moduleDataArrayList;
    }

    @NonNull
    @Override
    public ModuleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_module,parent,false);
        return new ModuleAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModuleData moduleData = moduleDataArrayList.get(position);

        holder.moduleName.setText(ModuleData.moduleName);

    }

    @Override
    public int getItemCount() {
        return moduleDataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView moduleName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.moduleTv);
        }
    }
}
