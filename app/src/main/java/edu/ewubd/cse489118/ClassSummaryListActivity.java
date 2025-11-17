package edu.ewubd.cse489118;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassSummaryListActivity extends AppCompatActivity {
    private ListView lvLectureList;
    private ArrayList<ClassSummary> lectures;
    private Button loginExit, New;
    private ClassSummaryAdapter csAdapter;
    String course = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_summary_list);
       // listView = findViewById(R.id.listView);
        TextView tvTitle = findViewById(R.id.tvTitle);
        lvLectureList = findViewById(R.id.lvLectureList);

        Intent i = this.getIntent();
        //if(i.hasExtra("_COURSE_")){
        course = i.getStringExtra("_COURSE_");
        tvTitle.setText(course+" : Class Lectures");
        //}

        findViewById(R.id.btnCreateNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassSummaryListActivity.this, ClassSummaryActivity.class);
                i.putExtra("_COURSE_", course);
                startActivity(i);
                //finish();
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lectures = new ArrayList<>();
        csAdapter = new ClassSummaryAdapter(this, lectures);
        lvLectureList.setAdapter(csAdapter);

        loadClassSummary();


    }
    @Override
    protected void onResume(){
        super.onResume();
        loadClassSummary();
    }
    private void loadClassSummary() {
        String q = "SELECT * FROM lectures WHERE course='"+course+"';";
        ClassSummaryDB db = new ClassSummaryDB(this);
        Cursor cur = db.selectLectures(q);
        lectures.clear();
        if(cur!=null && cur.getCount() > 0){
            while (cur.moveToNext()){
                String id = cur.getString(0);
                String course = cur.getString(1);
                String type = cur.getString(2);
                long date = cur.getLong(3);
                String lecture = cur.getString(4);
                String topic = cur.getString(5);
                String summary = cur.getString(6);

                String formattedDate = formatDate(date);

                ClassSummary cs = new ClassSummary(id, course, type, date, lecture, topic, summary);
                lectures.add(cs);
            }
            csAdapter.notifyDataSetInvalidated();
            csAdapter.notifyDataSetChanged();
        }
    }
    private String formatDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }

}

