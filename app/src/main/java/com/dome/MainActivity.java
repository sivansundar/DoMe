/*
 * MIT License
 *
 * Copyright (c) 2018 Sivan Chakravarthy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dome;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.reminders_recyclerView)
    RecyclerView remindersRecyclerView;
    @BindView(R.id.addToDo_btn)
    FloatingActionButton addToDoBtn;

    @BindView(R.id.opentext_label)
    TextView opentextLabel;

    private ReminderAdapter rAdapter;
    public  RealmResults<ToDoListPOJO> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Realm realm = Realm.getDefaultInstance();

        results = realm.where(ToDoListPOJO.class).findAll();
        for (int i = 0; i < results.size(); i++) {

            Log.d("Result", "DB Content: Value added : " + results.get(i).getId() + " : " + results.get(i).getTitle());
        }



        checkDBSize();

        // Populate data to the recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        remindersRecyclerView.setLayoutManager(layoutManager);

        rAdapter = new ReminderAdapter(this, results);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        remindersRecyclerView.setAdapter(rAdapter);
        remindersRecyclerView.setHasFixedSize(true);




    }

    private void checkDBSize() {

        if (results.size() != 0) {

            opentextLabel.setText("");
        }

        else {
            opentextLabel.setText("No tasks");
        }


    }


    @OnClick(R.id.addToDo_btn)
    public void onAddButtonClicked() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Get the layout inflater
        final LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.layout_addtodo, null))
                // Add action buttons
                .setPositiveButton("Set reminder", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editTextTitle = (EditText) ((Dialog) dialog).findViewById(R.id.edittext_title_text);
                        EditText editTextDescription = (EditText) ((Dialog) dialog).findViewById(R.id.edittext_desc_text);

                        final String titleTextContent = editTextTitle.getText().toString();
                        final String descTextContext = editTextDescription.getText().toString();
                        Toast.makeText(MainActivity.this, titleTextContent, Toast.LENGTH_SHORT).show();

                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                double random = Math.random();
                                ToDoListPOJO entry = bgRealm.createObject(ToDoListPOJO.class, String.valueOf(random)); //Primary key in the second parameter
                                entry.setTitle(titleTextContent);
                                entry.setDesc(descTextContext);

                            }
                        }, () -> {
                            // Transaction was a success.
                            checkDBSize(); // Needs to check if the list is empty. In background thread and make appropriate changes in the UI

                            Log.d("Transaction Status", "onSuccess: Value added : " + titleTextContent + " Description : " + descTextContext);
                        }, error -> {
                            // Transaction failed and was automatically canceled.
                            Log.d("Transaction Status", "onError: Value rejected : " + titleTextContent + " Error : " + error);
                            realm.close();
                        });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();
    }


}
