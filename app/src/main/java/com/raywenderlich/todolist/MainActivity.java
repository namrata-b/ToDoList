/*
 * Copyright (c) 2015 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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

// This is a sample project for the Introduction to Activities tutorial.


public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getName();
  private final int ADD_TASK_REQUEST = 1;
  private final String PREFS_TASKS = "prefs_tasks";
  private final String KEY_TASKS_LIST = "list";

  private ArrayList<String> mList;
  private ArrayAdapter<String> mAdapter;
  private TextView mDateTimeTextView;
  private BroadcastReceiver mTickReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // 1
    super.onCreate(savedInstanceState);

    // 2
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);

    // 3
    mDateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
    final Button addTaskBtn = (Button) findViewById(R.id.addTaskBtn);
    final ListView listview = (ListView) findViewById(R.id.taskListview);
    mList = new ArrayList<String>();
    // load whatever was saved on disk. This also takes care of rotations and other config changes
    String savedList = getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).getString(KEY_TASKS_LIST, null);
    if (savedList != null) {
      String[] items = savedList.split(",");
      mList = new ArrayList<String>(Arrays.asList(items));
    }
    // Create a broadcast receiver to handle change in time
    mTickReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
          mDateTimeTextView.setText(getCurrentTimeStamp());
        }

      }
    };

    // 4
    mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
    listview.setAdapter(mAdapter);

    // 5
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        taskSelected(i);
      }
    });
  }

  public void addTaskClicked(View view) {
    // Launch another activity to enter Task

    Intent intent = new Intent(MainActivity.this, TaskDescriptionActivity.class);
    startActivityForResult(intent, ADD_TASK_REQUEST);
  }

  private static String getCurrentTimeStamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdf.format(now);
    return strDate;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 1 - Check which request we're responding to
    if (requestCode == ADD_TASK_REQUEST) {
      // 2 - Make sure the request was successful
      if (resultCode == RESULT_OK) {
        // 3 - The user entered a task. Add task to the list.
        String task = data.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION);
        mList.add(task);
        // 4
        mAdapter.notifyDataSetChanged();
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    // 1
    mDateTimeTextView.setText(getCurrentTimeStamp());
    // 2 - Register the broadcast receiver to receive TIME_TICK
    registerReceiver(mTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
  }

  @Override
  protected void onPause() {
    super.onPause();
    // 3 - unregister broadcast receiver.
    if (mTickReceiver != null) {
      try {
        unregisterReceiver(mTickReceiver);
      } catch (IllegalArgumentException e) {
        Log.e(TAG, "Timetick Receiver not registered", e);
      }
    }
  }
  @Override
  protected void onStop() {
    super.onStop();

    // Save all data which you want to persist.
    StringBuilder savedList = new StringBuilder();
    for (String s : mList) {
      savedList.append(s);
      savedList.append(",");
    }
    getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).edit().putString(KEY_TASKS_LIST, savedList.toString()).commit();
  }

  private void taskSelected(final int position) {
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

    // set title
    alertDialogBuilder.setTitle(R.string.alert_title);

    // set dialog message
    alertDialogBuilder
        .setMessage(mList.get(position))
        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          // if this button is clicked, delete the task from the list
          mList.remove(position);
          mAdapter.notifyDataSetChanged();
          }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          // if this button is clicked, just close
          // the dialog box and do nothing
          dialog.cancel();
          }
        });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show the alert dialog
    alertDialog.show();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }
}

