package com.example.bwcar.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bwcar.R;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2017/10/26.
 */

public class TitleLayout extends LinearLayout implements View.OnClickListener{

    private TextView backText, titleText;
    private Button backButton;

    public TitleLayout(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.activity_head, this);

        backText = findViewById(R.id.back_text);
        titleText = findViewById(R.id.activity_title);
        backButton = findViewById(R.id.back_btn);

        backText.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    public void setTitle(String text){
        titleText.setText(text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
            case R.id.back_text:
                ((Activity)getContext()).finish();
                break;
            default:
                break;
        }
    }
}
