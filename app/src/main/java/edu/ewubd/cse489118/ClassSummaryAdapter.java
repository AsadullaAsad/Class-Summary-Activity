package edu.ewubd.cse489118;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ClassSummaryAdapter extends ArrayAdapter<ClassSummary> {

    private final Context context;
    private final ArrayList<ClassSummary> SummaryArrayList;

    public ClassSummaryAdapter(@NonNull Context context, @NonNull ArrayList<ClassSummary> items) {
        super(context, -1, items);
        this.context = context;
        this.SummaryArrayList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_lecture_item, parent, false);

        TextView topic = rowView.findViewById(R.id.tvLectureTitle);
        TextView dateTime = rowView.findViewById(R.id.tvLectureData);
        TextView summary = rowView.findViewById(R.id.tvLectureSummary);
        //TextView eventType = rowView.findViewById(R.id.tvEventType);

        ClassSummary e = SummaryArrayList.get(position);
        topic.setText(e.topic);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(e.date);
        dateTime.setText(formattedDate);
        summary.setText(e.summary);
        //eventType.setText(e.eventType);
        return rowView;
    }
    private String formatDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }
}
