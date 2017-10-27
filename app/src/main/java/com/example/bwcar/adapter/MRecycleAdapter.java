package com.example.bwcar.adapter;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.bwcar.R;
import com.example.bwcar.activity.MainActivity;
import com.example.bwcar.bean.ItemEntity;
import com.example.bwcar.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */

public class MRecycleAdapter extends RecyclerView.Adapter<MRecycleAdapter.ViewHolder> {

    private static final String TAG = "MRecycleAdapter";
    private Context context;
    private SQLiteDatabase db;
    private MyDatabaseHelper myDatabaseHelper;

    private LayoutInflater inflater;
    private List<ItemEntity> itemEntityList;
    private List<ItemEntity> entityList;

    private MainActivity mainActivity;
    private boolean isExit;

    public MRecycleAdapter(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
        inflater = LayoutInflater.from(context);

        entityList = new ArrayList<>();
        itemEntityList = new ArrayList<>();

        myDatabaseHelper = new MyDatabaseHelper(context, "button.db", null, 13);
        db = myDatabaseHelper.getWritableDatabase();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_set, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.i(TAG,"onBindViewHolder方法执行！");
        if (holder != null){
            final ItemEntity entity = getItemEntity(position);
            if (entity != null){
                holder.actionBtn.setText(entity.getName());
            }

            holder.actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (entity != null){
                        String message = entity.getMessage();
                        mainActivity.sendMessage(message);
                    }
                }
            });

            holder.actionBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    set(position);
                    return true;
                }
            });
        }
    }

    /**
     * 将editView上的信息存进数据库
     */
    public void set(final int position){

        Log.i(TAG,"set方法执行");
        View view = inflater.inflate(R.layout.dialog_set,null);
        final EditText setName = view.findViewById(R.id.dialog_set_name);
        final EditText setMessage = view.findViewById(R.id.dialog_set_msg);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("自定义设置");
        builder.setView(view);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isExit = false;//默认是添加，下边会判断是否存在数据，如果已有则进行修改操作
                String name = setName.getText().toString().trim();//细节去掉空格，防止客户输入空格
                String msg = setMessage.getText().toString().trim();//

                ContentValues values = new ContentValues();
                values.put(MyDatabaseHelper.NAME_KEY, name);
                values.put(MyDatabaseHelper.MESSAGE_KEY, msg);
                values.put(MyDatabaseHelper.POSITION_KEY, position);

                Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

                if (cursor.moveToFirst()){
                    do {

                        int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.POSITION_KEY));
                        if (id == position){
                            isExit = true;
                            String oldName = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.NAME_KEY));
                            db.update(MyDatabaseHelper.TABLE_NAME, values, "name = ?", new String[]{oldName});
                        }
                    }while (cursor.moveToNext());
                }
                if (!isExit){
                    db.insert(MyDatabaseHelper.TABLE_NAME, null, values);
                }
                values.clear();
                MRecycleAdapter.this.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    //添加数据
    public void addData(List<ItemEntity> ie){
        entityList.addAll(ie);
        notifyDataSetChanged();
    }

    /**
     * 取出数据库数据
     */
    public ItemEntity getItemEntity(int position){

        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME,null,null,null,null,null,null);
        Log.i(TAG, "getItemEntity: count=="+cursor.getCount());

        itemEntityList.clear();
        //将数据封装在实体类中
        if (cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.NAME_KEY));
                String message = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.MESSAGE_KEY));
                int id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.POSITION_KEY));
                itemEntityList.add(new ItemEntity(name, message, id));
            }while (cursor.moveToNext());
        }

        //数据中id和app中position相等的值
        if (itemEntityList.size() > 0){
            for (ItemEntity itemEntity:itemEntityList){
                if (itemEntity.getId() == position){
                    return itemEntity;
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

   public class ViewHolder extends RecyclerView.ViewHolder{

       private Button actionBtn;

       public ViewHolder(View view) {
           super(view);
           actionBtn = view.findViewById(R.id.action_btn);
       }
   }
}
