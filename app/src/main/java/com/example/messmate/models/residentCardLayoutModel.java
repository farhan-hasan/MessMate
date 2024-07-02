package com.example.messmate.models;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.messmate.R;

public class residentCardLayoutModel extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resident_card_view);

        final Button paidButton = findViewById(R.id.paidButton);
        final Button removeButton = findViewById(R.id.removeButton);
        final RelativeLayout rootLayout = findViewById(R.id.residentCardLayout);

        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int parentWidth = rootLayout.getWidth();
                int buttonWidth = parentWidth / 2;

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) paidButton.getLayoutParams();
                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) removeButton.getLayoutParams();

                params1.width = buttonWidth;
                params2.width = buttonWidth;

                paidButton.setLayoutParams(params1);
                removeButton.setLayoutParams(params2);
            }
        });
    }

}
