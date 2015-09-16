package com.raywenderlich.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/** This is a sample project for the Introduction to Activities tutorial.

 Things covered -
 * Creating an activity
 * Starting another activity for result
 * Finishing an activity and passing result to caller activity
 * Lifecycle methods and states - onCreate(), onPause() and onResume()
 * Handling config changes
 * Saving/ Persisting data

 Things not covered in this project. Should I just mention them in the theory section or would you want a more complex project to explain it with an example?
 * Using onSaveInstanceState() and onRestoreInstanceState()
 * OnDestroy() - there was nothing to cleanup in this example. Is it too simple?
 * Using fragments to save state - maybe refer to Google's docs and Huyen's tutorial
 **/

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();
    private final int ADD_TASK_REQUEST = 10;
    private final String PREFS_TASKS = "prefs_tasks";
    private final String KEY_TASKS_LIST = "list";

    private ArrayList<String> mList;
    private ArrayAdapter<String> mAdapter;
    private TextView mDateTimeTextView;
    private BroadcastReceiver mTickReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity full screen
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mDateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
        final Button addTaskBtn = (Button) findViewById(R.id.addTaskBtn);
        final ListView listview = (ListView) findViewById(R.id.taskListview);

        //Create a broadcast receiver to handle change in time
        mTickReceiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    mDateTimeTextView.setText(getCurrentTimeStamp());
                }

            }
        };

        // load whatever was saved on disk. This also takes care of rotations and other config changes
        String savedList = getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).getString(KEY_TASKS_LIST, null);

        if (savedList != null) {
            String[] items = savedList.split(",");
            mList = new ArrayList<String>(Arrays.asList(items));
        } else {
            String[] defaultValues = new String[]{"Sleep", "Code", "Play",
                    "Repeat", "Don't forget to eat!", "Dress up like mario and throw mushrooms at people"};

            mList = new ArrayList<String>();
            for (int i = 0; i < defaultValues.length; ++i) {
                mList.add(defaultValues[i]);
            }
        }

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        listview.setAdapter(mAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                taskSelected(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister broadcast receiver.
        if(mTickReceiver!=null) {
            try {
                unregisterReceiver(mTickReceiver);
            } catch(IllegalArgumentException e) {
                Log.e(TAG, "Timetick Receiver not registered", e);
            }
        }

        // Save all data which you want to persist.
        StringBuilder savedList = new StringBuilder();
        for(String s : mList){
            savedList.append(s);
            savedList.append(",");
        }

        getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).edit().putString(KEY_TASKS_LIST, savedList.toString()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDateTimeTextView.setText(getCurrentTimeStamp());
        //Register the broadcast receiver to receive TIME_TICK
        registerReceiver(mTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // prevents dialog from being dismissed if user has selected a task to modify.
        super.onConfigurationChanged(newConfig);
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }

    private void taskSelected(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Task");

        // set dialog message
        alertDialogBuilder
                .setMessage(mList.get(position))
                .setPositiveButton("Mark as done",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, delete the task from the list
                        mList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void addTaskClicked(View view) {
        // Launch another activity to enter Task

        Intent intent = new Intent(MainActivity.this, TaskDescriptionActivity.class);
        startActivityForResult(intent, ADD_TASK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_TASK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user entered a task. Add task to the list.
                String task = data.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION);
                mList.add(task);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}

