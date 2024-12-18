package com.example.wmp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EnrollActivity extends AppCompatActivity {

    private RecyclerView subjectRecyclerView;
    private TextView selectedSubjectsText, totalCreditsText;
    private Button enrollButton;

    private DatabaseReference subjectRef, enrollmentRef;
    private FirebaseAuth auth;

    private ArrayList<Subject> subjectList = new ArrayList<>();
    private ArrayList<Subject> selectedSubjects = new ArrayList<>();
    private int totalCredits = 0;
    private static final int MAX_CREDITS = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        subjectRecyclerView = findViewById(R.id.subjectRecyclerView);
        selectedSubjectsText = findViewById(R.id.selectedSubjectsText);
        totalCreditsText = findViewById(R.id.totalCreditsText);
        enrollButton = findViewById(R.id.enrollButton);

        auth = FirebaseAuth.getInstance();
        subjectRef = FirebaseDatabase.getInstance().getReference("subjects");
        enrollmentRef = FirebaseDatabase.getInstance().getReference("enrollments");

        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSubjects();

        enrollButton.setOnClickListener(v -> enrollSubjects());
    }

    private void loadSubjects() {
        subjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Subject subject = ds.getValue(Subject.class);
                    subjectList.add(subject);
                }
                SubjectAdapter adapter = new SubjectAdapter(subjectList, EnrollActivity.this::onSubjectSelected);
                subjectRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EnrollActivity.this, "Failed to load subjects: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onSubjectSelected(Subject subject, boolean isSelected) {
        if (isSelected) {
            if (totalCredits + subject.getCredits() > MAX_CREDITS) {
                Toast.makeText(this, "Credit limit exceeded!", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSubjects.add(subject);
            totalCredits += subject.getCredits();
        } else {
            selectedSubjects.remove(subject);
            totalCredits -= subject.getCredits();
        }
        updateSummary();
    }

    private void updateSummary() {
        StringBuilder subjectsBuilder = new StringBuilder();
        for (Subject subject : selectedSubjects) {
            subjectsBuilder.append(subject.getName()).append("\n");
        }
        selectedSubjectsText.setText(subjectsBuilder.toString().trim());
        totalCreditsText.setText("Total Credits: " + totalCredits);
    }

    private void enrollSubjects() {
        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "No subjects selected!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        ArrayList<String> enrolledSubjectIds = new ArrayList<>();
        for (Subject subject : selectedSubjects) {
            enrolledSubjectIds.add(subject.getId());
        }

        Enrollment enrollment = new Enrollment(userId, enrolledSubjectIds, totalCredits);
        enrollmentRef.child(userId).setValue(enrollment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Enrollment successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Enrollment failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
