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

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    public RealmResults<ToDoListPOJO> toDoListPOJOS;
    public Realm realm;
    Context context;

    String item_title_text, item_desc_text, item_id;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titletv;
        ImageView deleteImage;
        String desc;

        public MyViewHolder(View view) {
            super(view);
            context = itemView.getContext();

            View itemViewfinal = view;

            titletv = (TextView) view.findViewById(R.id.title_todo);
            deleteImage = (ImageView) view.findViewById(R.id.delete_imageview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ToDoItemActivity.class);
                    intent.putExtra("key", item_id);
                    context.startActivity(intent);
                }
            });

            //notifyDataSetChanged();

        }
    }


    public ReminderAdapter(Context c, RealmResults<ToDoListPOJO> toDoListPOJOS) {
        this.toDoListPOJOS = toDoListPOJOS;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_recyclerview_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ToDoListPOJO item = toDoListPOJOS.get(position);
        item_title_text = item.getTitle();
        item_id = item.getId();
        item_desc_text = item.getDesc();

        holder.titletv.setText(item_title_text);
        holder.desc = item_desc_text;

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(ToDoListPOJO.class).findAll().get(position).deleteFromRealm();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, item_title_text + " is deleted", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                realm.addChangeListener(new RealmChangeListener<Realm>() {
                    @Override
                    public void onChange(Realm realm) {
                        notifyItemRemoved(position);

                    }
                });
                realm.close();

            }
        });


    }

    @Override
    public int getItemCount() {
        return toDoListPOJOS.size();
    }



}
