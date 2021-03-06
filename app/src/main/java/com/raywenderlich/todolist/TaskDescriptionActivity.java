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
    // 1
    String taskDescription = mDescriptionView.getText().toString();

    if (!taskDescription.isEmpty()) {
      // 2
      Intent result = new Intent();
      result.putExtra(EXTRA_TASK_DESCRIPTION, taskDescription);
      setResult(RESULT_OK, result);
    } else {
      // 3
      setResult(RESULT_CANCELED);
    }
    // 4
    finish();
  }

}

