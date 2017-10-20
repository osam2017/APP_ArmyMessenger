package com.example.administrator.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    List<Chating> mChating;
    String useremail;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView)itemView.findViewById(R.id.mTextView);
        }
    }

    public MyAdapter(List<Chating> mChating, String email) {
        this.mChating = mChating;
        this.useremail = email;
    }

    @Override
    public int getItemViewType(int position) {
        if(mChating.get(position).getEmail().equals(useremail)){
            return 1;
        }else{
            return 2;
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 1){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_right_text_view, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_text_view, parent, false);
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mChating.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return mChating.size();
    }
}