package com.project.coterie2.view.walkthrough.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.project.coterie2.R;
import com.project.coterie2.databinding.ActivityWalkthroughBinding;
import com.project.coterie2.view.loginandsignup.MainActivity;
import com.project.coterie2.view.walkthrough.adapter.WalkthroughAdapter;

public class WalkthroughActivity extends AppCompatActivity {
    private ActivityWalkthroughBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalkthroughBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        // set adapter
        WalkthroughAdapter adapter = new WalkthroughAdapter();
        binding.walkthoughViewpagerId.setAdapter(adapter);

        // change UI Controls according to swipe
        binding.walkthoughViewpagerId.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    binding.walkthroughDot1Id.setBackground(getApplicationContext().getDrawable(R.drawable.grey_circle));
                    binding.walkthroughDot2Id.setBackground(getApplicationContext().getDrawable(R.drawable.blue_circle));
                }
                else{
                    binding.walkthroughDot1Id.setBackground(getApplicationContext().getDrawable(R.drawable.blue_circle));
                    binding.walkthroughDot2Id.setBackground(getApplicationContext().getDrawable(R.drawable.grey_circle));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.walkthroughNextButtonId.setOnClickListener(v -> {
            Intent intent = new Intent(WalkthroughActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
