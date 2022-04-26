package com.project.coterie2.view.walkthrough.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.project.coterie2.R;
import com.project.coterie2.databinding.WalkthroughSliderLayoutBinding;

public class WalkthroughAdapter extends PagerAdapter {

    String title[] = {"Create", "Express"};
    String description[] = {"Don't stop with just the thought", "Present you view in front of a similar minded audience"};
    int photos[] = {R.drawable.ic_coterie_vector_man1, R.drawable.ic_coterie_vector_man2};

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        WalkthroughSliderLayoutBinding binding = WalkthroughSliderLayoutBinding.inflate(inflater);

        binding.walkthroughLogoId.setImageResource(photos[position]);
        binding.walkthroughTitleId.setText(title[position]);
        binding.walkthroughDescriptionId.setText(description[position]);

        if(position == 0){
            binding.walkthoughBackgroundLayout.setBackgroundColor(Color.parseColor("#2C3238"));
            binding.walkthroughTitleId.setTextColor(Color.parseColor("#FFFFFF"));
            binding.walkthroughDescriptionId.setTextColor(Color.parseColor("#FFFFFF"));
            binding.walkthoughSwipeTVId.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else{
            binding.walkthoughBackgroundLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            binding.walkthroughTitleId.setTextColor(Color.parseColor("#111111"));
            binding.walkthroughDescriptionId.setTextColor(Color.parseColor("#111111"));
            binding.walkthoughSwipeTVId.setTextColor(Color.parseColor("#111111"));
        }

        container.addView(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);

        container.removeView((LinearLayout)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
