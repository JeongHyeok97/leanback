package com.me.test.presenter.leanback;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.me.test.R;
import com.me.test.model.Item;

public class CardPresenter extends Presenter {

    private Drawable cardImage;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        ImageCardView cardView = new ImageCardView(context){
            @Override
            public void setSelected(boolean selected) {
                /* Todo */
                super.setSelected(selected);
            }
        };
        cardView.setCardType(BaseCardView.CARD_TYPE_INFO_UNDER);
        cardView.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ALWAYS);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object object) {
        Item item = (Item) object;
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setTitleText(item.name);
        cardView.setContentText(item.category);
        Resources resources = cardView.getResources();
        cardView.setMainImageDimensions(resources.getDimensionPixelSize(R.dimen.card_width),
                resources.getDimensionPixelSize(R.dimen.card_height));
        String imageUrl = resources.getString(R.string.base_server)+"/appimage/";
        String bannerPath = "_banner.png";
        String imgPath = ".png";
        Glide.with(cardView.getContext()).load(imageUrl + item.no + bannerPath)
                .apply(RequestOptions.errorOf(cardImage)).into(cardView.getMainImageView());
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
