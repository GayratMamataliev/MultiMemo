package com.example.gayrat.memoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import db.DatabaseAccess;

public class WritingMemoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    //declare
    private EditText etText;
    private Button btnSave;
    private Button btnCancel;
    private memo mem;
    private TextView txtDate;
    private ImageView btnAddImage;
    private ImageView btnAddDate;
    private ImageView viewPhoto;
    private static DateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyy 'at' hh:mm aaa");
    private static final String TAG = WritingMemoActivity.class.getName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GET_IMAGE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_memo);
//initialize
        this.etText = (EditText)findViewById(R.id.etText);
        this.btnSave = (Button)findViewById(R.id.btnSave);
        this.btnCancel = (Button)findViewById(R.id.btnCancel);
        this.txtDate = (TextView)findViewById(R.id.txtDate);
        this.btnAddDate = (ImageView)findViewById(R.id.btnAddDate);
        this.btnAddImage = (ImageView)findViewById(R.id.btnAddImage);
//add date icon button event
        this.btnAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDateSet(v);
            }
        });
        //add image icon button event
        this.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        this.txtDate.setText(dateFormat.format(new Date ()).toString());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mem = (memo) bundle.get("MEMO");
            //check whether memo is sent to create or to update
            if(mem != null) {
                //set data when updating
                this.etText.setText(mem.getText().toString());
                this.txtDate.setText(mem.getDate().toString());
                //store old time to use later
                SharedPreferences.Editor editor = getSharedPreferences("setOldTime", MODE_PRIVATE).edit();
                editor.putLong("oldTime", mem.getTime());
                Date date = new Date(mem.getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                editor.putInt("year", calendar.get(Calendar.YEAR));
                editor.putInt("monthOfYear", calendar.get(Calendar.MONTH));
                editor.putInt("dayOfMonth", calendar.get(Calendar.DAY_OF_MONTH));
                editor.putInt("hourOfDay", calendar.get(Calendar.HOUR_OF_DAY));
                editor.putInt("minute", calendar.get(Calendar.MINUTE));
                editor.apply();
            }
        }

        this.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClicked();//call save event
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();// call cancel event
            }
        });
    }
    //function to select date
    public void onClickDateSet(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        //when updating memo, take old date to show default in datepicker
        SharedPreferences prefsAnother = getSharedPreferences("setOldTime", MODE_PRIVATE);
        int year = prefsAnother.getInt("year", calendar.get(Calendar.YEAR));
        int month = prefsAnother.getInt("monthOfYear", calendar.get(Calendar.MONTH));
        int day = prefsAnother.getInt("dayOfMonth", calendar.get(Calendar.DAY_OF_MONTH));
        new DatePickerDialog(this, this, year,month ,day).show();

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //save date setted to sharedpreferences to use later
        monthOfYear = monthOfYear+1;
        SharedPreferences.Editor editor = getSharedPreferences("setTime", MODE_PRIVATE).edit();
        editor.putInt("year", year);
        editor.putInt("monthOfYear", monthOfYear);
        editor.putInt("dayOfMonth", dayOfMonth);
        editor.apply();
        //call time select event
        timePicker();
    }
    //function to set time
    private void timePicker(){
        Calendar calendar = Calendar.getInstance();
        //when updating memo, take old time to show default in timepickerdialog
        SharedPreferences prefsAnother = getSharedPreferences("setOldTime", MODE_PRIVATE);
        int hour = prefsAnother.getInt("hourOfDay", calendar.get(Calendar.HOUR_OF_DAY));
        int minute = prefsAnother.getInt("minute", calendar.get(Calendar.MINUTE));
        new TimePickerDialog(this, this, hour, minute, false).show();
    }
    //when time is selected
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        //get date from sharedpreferences
        SharedPreferences prefs = getSharedPreferences("setTime", MODE_PRIVATE);
        int year = prefs.getInt("year", calendar.get(Calendar.YEAR));
        int monthOfYear = prefs.getInt("monthOfYear", calendar.get(Calendar.MONTH));
        int dayOfMonth = prefs.getInt("dayOfMonth", calendar.get(Calendar.DAY_OF_MONTH));

        String gDate = year+"/"+monthOfYear+"/"+dayOfMonth+" "+hourOfDay+":"+minute;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = sdf.parse(gDate,new ParsePosition (0));
        long millis = date.getTime();//convert dateTime to milliseconds
        //save milliseconds time in sharedpreferences
        SharedPreferences.Editor editor = getSharedPreferences("setTimeMillis", MODE_PRIVATE).edit();
        editor.putLong("timeInMillis", millis);
        editor.apply();
//show selected time in textview
        this.txtDate.setText(dateFormat.format(date).toString());
    }
    //save button click event
    public void onSaveClicked() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);//check whether db exists or not
        databaseAccess.open();//open db

        Date date = new Date();
        long time = date.getTime();
        //get saved milliseconds time
        SharedPreferences prefs = getSharedPreferences("setTimeMillis", MODE_PRIVATE);
        long millis = prefs.getLong("timeInMillis", 0);
        //get image as string
        SharedPreferences edit = getSharedPreferences("setImagePath", MODE_PRIVATE);
        String path = edit.getString("imagePreferance", "noImage");

        if(mem == null) {
            // Add new memo

            if(millis == 0){
                millis = time;
            }
            memo temp = new memo();
            temp.setText(etText.getText().toString());
            temp.setTime(millis);
            temp.setImageName(path);
            databaseAccess.save(temp);
        } else {
            // Update the memo
            SharedPreferences prefsAnother = getSharedPreferences("setOldTime", MODE_PRIVATE);
            long oldTime = prefsAnother.getLong("oldTime", millis);
            //check whether date is changed or not. If changed, set new date
            if (millis > 0){
                mem.setTime(millis);
            }else{
                mem.setTime(oldTime);
            }
            mem.setText(etText.getText().toString());
            //if image is not selected, set default image for memo
            if(path.length() > 10) {
                mem.setImageName(path);
            }
            mem.setOldTime(oldTime);
            databaseAccess.update(mem);
        }
        databaseAccess.close();//close db

        //clear sharedpreferences
        SharedPreferences.Editor shared = prefs.edit();
        shared.clear();
        shared.commit();

        SharedPreferences.Editor clearimage = edit.edit();
        clearimage.clear();
        clearimage.commit();

        SharedPreferences preferences = getSharedPreferences("setOldTime",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();


        /*I tried to set notification for this app. But i didn't finish because of limited time
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("text",etText.getText().toString());
        intent.putExtra("image",path);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.set(AlarmManager.RTC, millis, alarmIntent);*/

        this.finish();
    }
    //cancel button event
    public void onCancelClicked() {
        this.finish();
    }
    //select image function
    private void selectImage(){
        final CharSequence[] items = { "Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(WritingMemoActivity.this);
        builder.setTitle("Add Image");
        //item selectionresult event
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();//call cameraintent when Take Photo is clicked
                } else if (items[item].equals("Choose from Gallery")) {
                    galleryIntent();//call galleryIntent when Choose from Gallery is clicked
                }
            }
        });
        //set cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();//display AleertDialog
    }

    private void cameraIntent()
    {
        //camera intent
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void galleryIntent(){
        //gallery intent
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GET_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG,"Result code: "+data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {//camera intent result
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");//get taken photo as bitmap
            viewPhoto = new ImageView(this);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);//compress image
            byte[] byteArray = stream.toByteArray();
            final String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);//convert to string
            viewPhoto.setImageBitmap(imageBitmap);
            //Alertdialog to show taken photo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Taken photo");
            //if you don't like taken photo, negative button event is for you to dismiss
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            //when imaage is liked and selected to set
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //save image as string to sharedpreferences
                    SharedPreferences.Editor editor = getSharedPreferences("setImagePath", MODE_PRIVATE).edit();
                    editor.putString("imagePreferance", imageFile);
                    editor.commit();
                }
            });
            builder.setView(viewPhoto);
            builder.create().show();//show alertdialog
        }else if(requestCode == REQUEST_GET_IMAGE && resultCode == RESULT_OK){//gallery intent result
            //get selected image
            Uri selectedImage = data.getData();
            //get path of selected image
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            viewPhoto = new ImageView(this);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);//compress
            byte[] byteArray = stream.toByteArray();
            final String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);//convert to string
            viewPhoto.setImageBitmap(thumbnail);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chosen photo");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //save image as string in sharedpreferences
                    SharedPreferences.Editor editor = getSharedPreferences("setImagePath", MODE_PRIVATE).edit();
                    editor.putString("imagePreferance", imageFile);
                    editor.commit();
                }
            });
            builder.setView(viewPhoto);
            builder.create().show();//show alertdialog
        }
    }
}
