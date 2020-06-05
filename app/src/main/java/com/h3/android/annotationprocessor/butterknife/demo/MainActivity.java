package com.h3.android.annotationprocessor.butterknife.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.h3.android.annotationprocessor.butterknife.ButterKnife;
import com.h3.android.annotationprocessor.butterknife.annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hello)
    TextView helloView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        helloView.setText("Hello");
        helloView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
            }
        });
    }
}