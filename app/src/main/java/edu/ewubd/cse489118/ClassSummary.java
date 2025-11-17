package edu.ewubd.cse489118;

public class ClassSummary {
    String id = "";
    String course = "";
    String type = "";
    long date = 0;
    String lecture = "";
    String topic = "";
    String summary = "";

    public ClassSummary(String id, String course, String type, long date, String lecture, String topic, String summary) {
        this.id = id;
        this.course = course;
        this.type = type;
        this.date = date; // Assign the provided date value directly
        this.lecture = lecture;
        this.topic = topic;
        this.summary = summary;
    }


}