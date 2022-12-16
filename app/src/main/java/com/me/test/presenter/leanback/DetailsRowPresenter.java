package com.me.test.presenter.leanback;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.Presenter;

import com.me.test.R;

public class DetailsRowPresenter extends DetailsOverviewLogoPresenter {


    static class ViewHolder extends DetailsOverviewLogoPresenter.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }

        public FullWidthDetailsOverviewRowPresenter getParentPresenter() {
            return mParentPresenter;
        }

        public FullWidthDetailsOverviewRowPresenter.ViewHolder getParentViewHolder() {
            return mParentViewHolder;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(androidx.leanback.R.layout.lb_fullwidth_details_overview_logo, parent,
                        false);

        Resources res = parent.getResources();
        int width = res.getDimensionPixelSize(R.dimen.detail_thumb_width);
        int height = res.getDimensionPixelSize(R.dimen.detail_thumb_height);
        imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(width, height));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new DetailsRowPresenter.ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        DetailsOverviewRow row = (DetailsOverviewRow) item;
        ImageView imageView = ((ImageView) viewHolder.view);
        imageView.setImageDrawable(row.getImageDrawable());
        if (isBoundToImage((DetailsRowPresenter.ViewHolder) viewHolder, row)) {
            DetailsRowPresenter.ViewHolder vh =
                    (DetailsRowPresenter.ViewHolder) viewHolder;
            vh.getParentPresenter().notifyOnBindLogo(vh.getParentViewHolder());
        }
    }
}
