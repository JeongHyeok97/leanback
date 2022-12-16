package com.me.test.presenter.leanback;

import android.view.View;
import android.widget.TextView;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.me.test.model.Item;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object obj) {
        Item item = (Item) obj;
        View rootView = viewHolder.view;
        if (item != null) {
            viewHolder.getTitle().setText(item.name);
            viewHolder.getSubtitle().setText(item.category);
            TextView body = viewHolder.getBody();
            body.setText(item.desc);



        }


    }
}
