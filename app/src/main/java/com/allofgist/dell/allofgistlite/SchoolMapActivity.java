package com.allofgist.dell.allofgistlite;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;

public class SchoolMapActivity extends AppCompatActivity {

    private ImageButton backButton;
    private PhotoView schoolMap;
    private ImageButton zoomButton;
    private boolean select = false;
    float zoom_in = 2.0F;
    float zoom_out = 1.0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_map);

        InitialSetting();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select = !select;
                if (select) {
                    zoomButton.setSelected(select);
                    schoolMap.setScale(zoom_in);
                }
                else{
                    zoomButton.setSelected(select);
                    schoolMap.setScale(zoom_out);
                }
            }
        });

        schoolMap.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {

                if(scaleFactor>1)
                    select = true;

                zoomButton.setSelected(select);
            }
        });


    }

    private void InitialSetting() {
        backButton = (ImageButton)findViewById(R.id.school_map_back_button);
        zoomButton = (ImageButton)findViewById(R.id.zoom_button_school_map);
        schoolMap = (PhotoView)findViewById(R.id.image_school_map);
        schoolMap.setImageResource(R.drawable.map);
    }
}
