package com.me.test.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.me.test.R;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new MainFragment()).commitNow();
        }
    }
}
