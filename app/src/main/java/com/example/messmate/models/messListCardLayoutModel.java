package com.example.messmate.models;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.messmate.R;

public class messListCardLayoutModel extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mess_list_card);

        final Button messEditButton = findViewById(R.id.messEditButton);
        final Button messRemoveButton = findViewById(R.id.messRemoveButton);
        final RelativeLayout rootLayout = findViewById(R.id.buttonLayout);

        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int parentWidth = rootLayout.getWidth();
                int buttonWidth = parentWidth / 2;

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) messEditButton.getLayoutParams();
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) messRemoveButton.getLayoutParams();

                params1.width = buttonWidth;
                params2.width = buttonWidth;

                messEditButton.setLayoutParams(params1);
                messRemoveButton.setLayoutParams(params2);
            }
        });
    }

}
