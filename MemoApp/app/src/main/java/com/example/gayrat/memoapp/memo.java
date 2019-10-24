package com.example.gayrat.memoapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class memo implements Serializable {
    //initialize
    private Date date;
    private Date oldDate;
    private String text;
    private String imageName;
    private boolean fullDisplayed;
    private static DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyy 'at' hh:mm aaa");

    public memo() {
        this.date = new Date();
    }//constructor

    public memo(long time, String text, String imageName) {
        this.date = new Date(time);
        this.text = text;
        this.imageName = imageName;
    }//constructor

    public String getDate() {
        return dateFormat.format(date);
    }//get date function

    public long getTime() {
        return date.getTime();
    }//get time function

    public void setTime(long time) {
        this.date = new Date(time);
    }//set time function

    public void setText(String text) {
        this.text = text;
    }//set text function

    public String getText() {
        return this.text;
    }//get text function
    /*Actually these below two functions' names should be different. But because of some reasons i didn't change*/
    public void setImageName(String imageName){
        this.imageName = imageName;//Here imageName is a string form of image
    }//function to set image as string

    public String getImageName(){
        return this.imageName;
    }//function to get image as string

    public String getShortText() {//function to make short text to display
        String temp = text.replaceAll("\n", " ");
        if (temp.length() > 25) {
            return temp.substring(0, 25) + "...";
        } else {
            return temp;
        }
    }

    public void setFullDisplayed(boolean fullDisplayed) {
        this.fullDisplayed = fullDisplayed;
    }

    public boolean isFullDisplayed() {
        return this.fullDisplayed;
    }
    @Override
    public String toString() {
        return this.text;
    }

    public void setOldTime(long time) {
        this.oldDate = new Date(time);
    }//set old time
    public long getOldTime() {
        return oldDate.getTime();
    }//get old time

    public static String encodeTobase64(Bitmap image) {// function to convert bitmap image to string
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);//compress image 100 times
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {//function to convert string to bitmap image
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

