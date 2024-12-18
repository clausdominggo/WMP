package com.example.wmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private final ArrayList<Subject> subjectList;
    private final OnSubjectSelectListener onSubjectSelectListener;

    public SubjectAdapter(ArrayList<Subject> subjectList, OnSubjectSelectListener onSubjectSelectListener) {
        this.subjectList = subjectList;
        this.onSubjectSelectListener = onSubjectSelectListener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.subjectNameText.setText(subject.getName());
        holder.subjectCreditsText.setText(String.valueOf(subject.getCredits()));

        holder.selectButton.setOnClickListener(v -> onSubjectSelectListener.onSubjectSelected(subject, true));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {

        TextView subjectNameText;
        TextView subjectCreditsText;
        Button selectButton;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            subjectCreditsText = itemView.findViewById(R.id.subjectCreditsText);
            selectButton = itemView.findViewById(R.id.selectButton);
        }
    }

    public interface OnSubjectSelectListener {
        void onSubjectSelected(Subject subject, boolean isSelected);
    }
}
