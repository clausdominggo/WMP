package com.example.wmp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectAdapterAdmin extends RecyclerView.Adapter<SubjectAdapterAdmin.SubjectViewHolder> {

    private final ArrayList<Subject> subjectList;
    private final OnSubjectActionListener listener;

    public interface OnSubjectActionListener {
        void onSubjectAction(Subject subject);
    }

    public SubjectAdapterAdmin(ArrayList<Subject> subjectList, OnSubjectActionListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_admin, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectNameText;
        private final TextView subjectCreditsText;
        private final ImageButton editButton;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            subjectCreditsText = itemView.findViewById(R.id.subjectCreditsText);
            editButton = itemView.findViewById(R.id.editButton);
        }

        public void bind(Subject subject) {
            subjectNameText.setText(subject.getName());
            subjectCreditsText.setText("Credits: " + subject.getCredits());

            // Edit/Delete Actions
            editButton.setOnClickListener(v -> listener.onSubjectAction(subject));
        }
    }
}
