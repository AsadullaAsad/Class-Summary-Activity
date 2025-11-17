package edu.ewubd.cse489118;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> courses = new ArrayList<>();
    private GridLayout gl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courses.add("CSE477");
        courses.add("CSE489");
        courses.add("CSE479");
        courses.add("CSE495");
        courses.add("CSE460");
        courses.add("CSE466");


        gl = findViewById(R.id.grid);
        for (int i = 0; i < gl.getChildCount(); i++) {
            gl.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        int index = 0;
        for (String course : courses) {
            gl.getChildAt(index).setVisibility(View.VISIBLE);
            ((TextView) gl.getChildAt(index)).setText(course);
            gl.getChildAt(index).setOnClickListener(v -> {
                Intent i = new Intent(this, ClassSummaryListActivity.class);
                i.putExtra("_COURSE_", ((TextView) v).getText().toString());
                startActivity(i);
            });
            index++;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        String keys[] = {"action", "sid", "semester"};
        String values[] = {"restore", "2021-2-60-117", "2024-1"};
        httpRequest(keys, values);
    }

    private void httpRequest(final String keys[], final String values[]) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
                String url = "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data = RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String data) {
                if (data != null) {
                    updateLocalDBByServerData(data);
                }
            }
        }.execute();
    }

    private void updateLocalDBByServerData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("classes")) {
                ClassSummaryDB db = new ClassSummaryDB(MainActivity.this);
                JSONArray ja = jo.getJSONArray("classes");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject summary = ja.getJSONObject(i);
                    String id = summary.getString("id");
                    String course = summary.getString("course");
                    String topic = summary.getString("topic");
                    String type = summary.getString("type");

                    // Parse the date string into a long value
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    long date = sdf.parse(summary.getString("date")).getTime();

                    String lecture = summary.getString("lecture");
                    String sum = summary.getString("summary");

                    // Insert or update lecture information in local database
                    try {
                        if (db.isLectureExist(id)) {
                            // If the lecture already exists, update it
                            db.updateLecture(id, course, type, date, lecture, topic, sum);
                            Log.d("UpdateSuccess", "Lecture updated in local database with ID: " + id);
                        } else {
                            // If the lecture doesn't exist, insert it
                            db.insertLecture(id, course, type, date, lecture, topic, sum);
                            Log.d("InsertionSuccess", "New lecture inserted into local database with ID: " + id);
                        }
                    } catch (Exception e) {
                        Log.e("DatabaseError", "Error updating/inserting data into local database: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("JSONParsingError", "Error parsing JSON data: " + e.getMessage());
        }
    }
    public void deleteLecture(String lectureID) {
        // Method to delete a lecture from the local database
        ClassSummaryDB db = new ClassSummaryDB(this);
        db.deleteLecture(lectureID);
        Log.d("DeletionSuccess", "Lecture deleted from local database");
    }


}
