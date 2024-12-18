package com.example.wmp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView subjectRecyclerView;
    private FloatingActionButton addSubjectButton;

    private DatabaseReference subjectRef;
    private ArrayList<Subject> subjectList = new ArrayList<>();
    private SubjectAdapterAdmin adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        subjectRecyclerView = findViewById(R.id.subjectRecyclerView);
        addSubjectButton = findViewById(R.id.addSubjectButton);

        subjectRef = FirebaseDatabase.getInstance().getReference("subjects");

        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapterAdmin(subjectList, this::showEditDeleteDialog);
        subjectRecyclerView.setAdapter(adapter);

        loadSubjects();

        addSubjectButton.setOnClickListener(v -> showAddSubjectDialog());
    }

    private void loadSubjects() {
        subjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Subject subject = ds.getValue(Subject.class);
                    if (subject != null) {
                        subjectList.add(subject);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Failed to load subjects: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddSubjectDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subject, null);
        EditText subjectNameInput = dialogView.findViewById(R.id.subjectNameInput);
        EditText subjectCreditsInput = dialogView.findViewById(R.id.subjectCreditsInput);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Add Subject")
                .setView(dialogView);

        androidx.appcompat.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(v -> {
            String name = subjectNameInput.getText().toString().trim();
            String creditsStr = subjectCreditsInput.getText().toString().trim();

            if (name.isEmpty() || creditsStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid credit value!", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = subjectRef.push().getKey();
            Subject newSubject = new Subject(id, name, credits);

            subjectRef.child(id).setValue(newSubject)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Subject added successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Failed to add subject: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void showEditDeleteDialog(Subject subject) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_subject, null);
        EditText subjectNameInput = dialogView.findViewById(R.id.subjectNameInput);
        EditText subjectCreditsInput = dialogView.findViewById(R.id.subjectCreditsInput);
        Button updateButton = dialogView.findViewById(R.id.updateButton);
        Button deleteButton = dialogView.findViewById(R.id.deleteButton);

        subjectNameInput.setText(subject.getName());
        subjectCreditsInput.setText(String.valueOf(subject.getCredits()));

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setTitle("Edit or Delete Subject")
                .setView(dialogView);

        androidx.appcompat.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        updateButton.setOnClickListener(v -> {
            String name = subjectNameInput.getText().toString().trim();
            String creditsStr = subjectCreditsInput.getText().toString().trim();

            if (name.isEmpty() || creditsStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid credit value!", Toast.LENGTH_SHORT).show();
                return;
            }

            subjectRef.child(subject.getId()).child("name").setValue(name);
            subjectRef.child(subject.getId()).child("credits").setValue(credits)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Subject updated successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Failed to update subject: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        deleteButton.setOnClickListener(v -> subjectRef.child(subject.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Subject deleted successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Failed to delete subject: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
