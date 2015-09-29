package com.raywenderlich.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class TaskDescriptionActivity extends Activity {
  public static final String EXTRA_TASK_DESCRIPTION = "task";

  private EditText mDescriptionView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_description);

    mDescriptionView = (EditText) findViewById(R.id.descriptionText);
  }


  public void doneClicked(View view) {
    String taskDescription = mDescriptionView.getText().toString();
    if (!taskDescription.isEmpty()) {
      Intent result = new Intent();
      result.putExtra(EXTRA_TASK_DESCRIPTION, taskDescription);
      setResult(RESULT_OK, result);
    } else {
      setResult(RESULT_CANCELED);
    }
    finish();
  }

}
