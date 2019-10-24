package com.example.gayrat.memoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.List;

import db.DatabaseAccess;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnAdd;
    private Button btnBack;
    private DatabaseAccess databaseAccess;
    private List<memo> memos;
    NiftyDialogBuilder materialDesignAnimatedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        materialDesignAnimatedDialog = NiftyDialogBuilder.getInstance(this);



        this.databaseAccess = DatabaseAccess.getInstance(this);
        //initialize
        this.listView = (ListView) findViewById(R.id.listView);
        this.btnAdd = (Button) findViewById(R.id.btnAdd);
        //set event for add button
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClicked();
            }
        });
        //if item clickked full text will be displayed
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                memo mem = memos.get(position);
                TextView txtMemo = (TextView) view.findViewById(R.id.txtMemo);
                if (mem.isFullDisplayed()) {
                    txtMemo.setText(mem.getShortText());
                    mem.setFullDisplayed(false);
                } else {
                    txtMemo.setText(mem.getText());
                    mem.setFullDisplayed(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        //Get all memos from db and display them in listview
        super.onResume();
        databaseAccess.open();
        this.memos = databaseAccess.getAllMemos();
        databaseAccess.close();
        MemoAdapter adapter = new MemoAdapter(this, memos);
        this.listView.setAdapter(adapter);
    }

    public void onAddClicked() {
        //start new intent to add new memo
        Intent intent = new Intent(this, WritingMemoActivity.class);
        startActivity(intent);
    }

    public void onDeleteClicked(memo mem) {
        //delete memo when delete button clicked
        databaseAccess.open();
        databaseAccess.delete(mem);
        databaseAccess.close();

        ArrayAdapter<memo> adapter = (ArrayAdapter<memo>) listView.getAdapter();
        adapter.remove(mem);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(memo mem) {
        //call intent to edit selected memo
        Intent intent = new Intent(this, WritingMemoActivity.class);
        intent.putExtra("MEMO", mem);//send data which belong to selected memo
        startActivity(intent);
    }

    public void animation(View view) {
        materialDesignAnimatedDialog
                .withTitle("Thanks for your attention!")
                .withMessage("Click the 'Ok' button or the 'Cancel' button!")
                .withDialogColor("#f46904")
                .withButton1Text("OK")
                .setButton1Click ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        onBackPressed ();
                    }
                } )
                .withButton2Text("Cancel")
                .setButton2Click ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        materialDesignAnimatedDialog.dismiss ();
                    }
                } )
                .withDuration(700)
                .withEffect(Effectstype.Newspager)
                .show();

    }

    private class MemoAdapter extends ArrayAdapter<memo> {


        public MemoAdapter(Context context, List<memo> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }
            //initialize
            ImageView btnEdit = (ImageView) convertView.findViewById(R.id.btnEdit);
            ImageView btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtMemo = (TextView) convertView.findViewById(R.id.txtMemo);
            ImageView image = (ImageView) convertView.findViewById(R.id.mimoImage);
            //set data to show
            final memo mem = memos.get(position);
            mem.setFullDisplayed(false);
            txtDate.setText(mem.getDate());
            txtMemo.setText(mem.getShortText());

            String path = mem.getImageName().toString();
            if(path.length() > 10) {
                //convert string to bitmap to show image
                Bitmap imager = memo.decodeBase64(path);
                image.setImageBitmap(imager);
            }
            //edit and delete buttons events
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClicked(mem);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClicked(mem);
                }
            });
            return convertView;
        }
    }
}