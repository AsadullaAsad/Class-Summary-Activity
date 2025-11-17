package edu.ewubd.cse489118;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassSummaryActivity extends AppCompatActivity {

    private EditText clDate, clLecture, clTopic, Summary, clName;

    private TextView tvCourse;
    private RadioButton Theory, Lab;
    private Button classSave, classCancel;
    private String lectureID = "";
    private String summary="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_summary);
        clName = findViewById(R.id.clName);
        clDate = findViewById(R.id.clDate);
        clLecture = findViewById(R.id.clLecture);
        clTopic = findViewById(R.id.clTopic);
        Summary = findViewById(R.id.Summary);


        classSave = findViewById(R.id.classSave);
        classCancel = findViewById(R.id.classCancel);
        tvCourse = findViewById(R.id.tvCourse);

        Theory = findViewById(R.id.theory);
        Lab = findViewById(R.id.lab);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("courseName")) {
            String courseName = intent.getStringExtra("courseName");
            tvCourse.setText(courseName);
        }

        Intent i = getIntent();
        if (i.hasExtra("_LECTURE_ID_")) {
            lectureID = i.getStringExtra("_LECTURE_ID_");
        }

        if (i.hasExtra("_COURSE_")) {
            String course = i.getStringExtra("_COURSE_");
            tvCourse.setText(course);
        }

        if (i.hasExtra("_LECTURE_NO_")) {
            String val = i.getStringExtra("_LECTURE_NO_");
            clLecture.setText(val);
        }

        if (i.hasExtra("_TOPIC_")) {
            String val = i.getStringExtra("_TOPIC_");
            clTopic.setText(val);
        }

        if (i.hasExtra("_SUMMARY_")) {
            String val = i.getStringExtra("_SUMMARY_");
            Summary.setText(val);
        }

        if (i.hasExtra("_TYPE_")) {
            String val = i.getStringExtra("_TYPE_");
            if (val.equals("Lab")) {
                Lab.setChecked(true);
            } else {
                Theory.setChecked(true);
            }
        }

        if (i.hasExtra("_DATE_")) {
            String val = i.getStringExtra("_DATE_");
            clDate.setText(val);
        }


        classSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "";
                String topic = clTopic.getText().toString().trim();
                String lecture = clLecture.getText().toString().trim();
                String summary = Summary.getText().toString().trim();
                String date = clDate.getText().toString().trim();
                String course = tvCourse.getText().toString();

                boolean isTheory = Theory.isChecked();
                boolean isLab = Lab.isChecked();

                String err = "";
                long _date = 0;

                if (TextUtils.isEmpty(topic) || topic.length() < 4 || topic.length() > 12) {
                    err += "Invalid Topic (4-12 characters)\n";
                    showErrorDialog(err, "Topic");
                    return;
                }

                if (!isTheory && !isLab) {
                    err += "Please select event type\n";
                    showErrorDialog(err, "Type");
                    return;
                }
                if (isTheory) {
                    type=Theory.getText().toString().trim();
                }else if(isLab){
                    type= Lab.getText().toString().trim();
                }

                String format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setLenient(false);
                try {
                    Date inputDate = sdf.parse(date);
                    _date = inputDate.getTime();
                    Date currentDate = new Date();
                    if (inputDate.before(currentDate)) {
                        err += "Input date is before the current date\n";
                        showErrorDialog(err, "Date");
                        return;
                    }
                } catch (ParseException e) {
                    err += "Invalid date format (yyyy-MM-dd)\n";
                    showErrorDialog(err, "Date");
                    return;
                }

                if (TextUtils.isEmpty(lecture)) {
                    err += "Please enter a valid lecture\n";
                    showErrorDialog(err, "Lecture");
                    return;
                }

                if (TextUtils.isEmpty(summary) || summary.length() < 10 || summary.length() > 1000) {
                    err += "Invalid description format (10-1000 characters)\n";
                    showErrorDialog(err, "Summary");
                    return;
                }

                ClassSummaryDB summaryDB = new ClassSummaryDB(ClassSummaryActivity.this);
                if (lectureID.isEmpty()) {
                    lectureID = topic + System.currentTimeMillis();
                   // summaryDB.insertLecture(lectureID, course,type, _date, lecture, topic, summary);
                    Toast.makeText(ClassSummaryActivity.this, "New lecture is inserted", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    summaryDB.updateLecture(lectureID, course, type, _date, lecture, topic, summary);
                    Toast.makeText(ClassSummaryActivity.this, "Lecture info is updated", Toast.LENGTH_LONG).show();
                    finish();
                }
                String keys[] = {"action", "sid", "semester", "id", "course", "type", "date", "lecture", "topic", "summary"};
                String values[] = {"backup", "2021-2-60-117", "2024-1",lectureID,  course, type, date, lecture,topic, summary};

                httpRequest(keys, values);
            }

        });

        classCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }
    private void displayCurrentDate(){
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
    }
    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {

                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data= RemoteAccess.getInstance().makeHttpRequest(url,"POST",params);
                    Log.d("response",data);
                    Log.d("request",params.toString());
                    return data;//key value data
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Increment monthOfYear by 1 because DatePickerDialog returns zero-based index
                        monthOfYear += 1;


                        String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, monthOfYear, dayOfMonth);
                        clDate.setText(selectedDate);
                    }
                }, currentYear, currentMonth, currentDay);


        datePickerDialog.show();
    }

    private void showErrorDialog(String errorMessage, String fieldName) {
        String title = "Error";
        String message = errorMessage;

        switch (fieldName) {
            case "Name":
                message = "Invalid Name (4-12 characters)";
                break;
            case "Type":
                message = "Please select event type";
                break;
            case "Date":
                message = "Invalid date format (yyyy-MM-dd) or input date is before the current date";
                break;
            case "Lecture":
                message = "Please enter a valid lecture";
                break;
            case "Summary":
                message = "Invalid description format (10-1000 characters)";
                break;
            default:
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(true);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
