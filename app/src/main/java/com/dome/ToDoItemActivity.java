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

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class ToDoItemActivity extends AppCompatActivity {
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.desc_text)
    EditText descText;
    @BindView(R.id.save_fab)
    FloatingActionButton saveFab;
    @BindView(R.id.timeStamp_text)
    TextView timeStampText;

    @Override
    protected void onStart() {
        super.onStart();
    }

    String item_id, item_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_item);
        ButterKnife.bind(this);

        item_id = getIntent().getStringExtra("key");

        Realm realm1 = Realm.getDefaultInstance();
        RealmResults<ToDoListPOJO> list = realm1.where(ToDoListPOJO.class).equalTo("id", item_id).findAll();
        realm1.close();


        titleText.setText(list.get(0).getTitle());
        descText.setText(list.get(0).getDesc());


    }


    @OnClick(R.id.save_fab)
    public void onViewClicked() {

        Realm mrealm = Realm.getDefaultInstance();

        mrealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ToDoListPOJO update = realm.where(ToDoListPOJO.class).equalTo("id", item_id).findFirst();
                update.setDesc(descText.getText().toString().trim());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ToDoItemActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mrealm.close();

    }
}
