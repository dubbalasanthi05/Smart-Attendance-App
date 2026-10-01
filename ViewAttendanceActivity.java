package com.example.collegeattendanceapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

COLLEGE ATTENDANCE APP

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewAttendanceActivity extends AppCompatActivity {

TextView tvName, tvRoll, tvBranch, tvPercentage;
Button btnLogout;
LinearLayout subjectContainer;
DatabaseReference studentRef;
String roll;

@Override
protected void onCreate(Bundle savedInstanceState) {
// Force light mode
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

super.onCreate(savedInstanceState);
setContentView(R.layout.activity_view_attendance);

// Initialize views

COLLEGE ATTENDANCE APP

tvName = findViewById(R.id.tvName);
tvRoll = findViewById(R.id.tvRoll);
tvBranch = findViewById(R.id.tvBranch);
tvPercentage = findViewById(R.id.tvPercentage);
subjectContainer = findViewById(R.id.subjectContainer);
btnLogout = findViewById(R.id.btnLogout);

// Get roll number from intent
roll = getIntent().getStringExtra("roll");
if (roll == null || roll.isEmpty()) {
Toast.makeText(this, "Invalid roll number", Toast.LENGTH_SHORT).show();
finish();
return;
}

studentRef = FirebaseDatabase.getInstance().getReference("Students").child(roll);

// Fetch student data
studentRef.get().addOnCompleteListener(task -> {
if (task.isSuccessful() && task.getResult().exists()) {
DataSnapshot data = task.getResult();

String name = data.child("name").getValue(String.class);
String branch = data.child("branch").getValue(String.class);
Long attended = data.child("attended").getValue(Long.class);
Long held = data.child("held").getValue(Long.class);
Double percentage = data.child("percentage").getValue(Double.class);

tvName.setText("Name: " + (name != null ? name : "N/A"));

COLLEGE ATTENDANCE APP

tvRoll.setText("Roll: " + roll);
tvBranch.setText("Branch: " + (branch != null ? branch : "N/A"));
tvPercentage.setText("Overall Attendance: " + (percentage != null ? String.format("%.2f",
percentage) : "0.00") + "%");

// Colored "Overall Classes Held" and "Classes Attended"
String overallText = "Overall Classes Held: " + (held != null ? held : 0) +
"\nClasses Attended: " + (attended != null ? attended : 0);

SpannableString spannable = new SpannableString(overallText);

// Cyan for Held
spannable.setSpan(
new ForegroundColorSpan(Color.parseColor("#00BCD4")),
0,
overallText.indexOf("\n"),
Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
);

// Green for Attended
spannable.setSpan(
new ForegroundColorSpan(Color.parseColor("#4CAF50")),
overallText.indexOf("Classes Attended:"),
overallText.length(),
Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
);

TextView tvOverallDays = new TextView(this);
tvOverallDays.setText(spannable);

COLLEGE ATTENDANCE APP

tvOverallDays.setPadding(0, 20, 0, 20);
tvOverallDays.setTextSize(16);
subjectContainer.addView(tvOverallDays);

// Subject-wise attendance
DataSnapshot subjectsSnapshot = data.child("subjects");
if (subjectsSnapshot.exists()) {
for (DataSnapshot subjectSnap : subjectsSnapshot.getChildren()) {
String subjectName = subjectSnap.getKey();
Long subjectHeld = subjectSnap.child("total").getValue(Long.class);
Long subjectAttended = subjectSnap.child("present").getValue(Long.class);

double subPercent = (subjectHeld != null && subjectHeld > 0)
? ((double) (subjectAttended != null ? subjectAttended : 0) / subjectHeld) * 100
: 0.0;

TextView subjectView = new TextView(this);
subjectView.setText(subjectName + ": " +
"\n Classes Held: " + (subjectHeld != null ? subjectHeld : 0) +
"\n Attended: " + (subjectAttended != null ? subjectAttended : 0) +
"\n Percentage: " + String.format("%.2f", subPercent) + "%");
subjectView.setPadding(0, 20, 0, 20);
subjectView.setTextColor(Color.parseColor("#FF9800")); // Orange
subjectView.setTextSize(15);

subjectContainer.addView(subjectView);
}
}

COLLEGE ATTENDANCE APP

} else {
Toast.makeText(ViewAttendanceActivity.this, "Student data not found",
Toast.LENGTH_SHORT).show();
}
});

// Logout button
btnLogout.setOnClickListener(v -> {
Intent intent = new Intent(ViewAttendanceActivity.this, MainActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
finish();
});
}
}