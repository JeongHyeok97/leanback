package com.me.test.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.CursorObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.me.test.model.data.listener.OnWebQueryListener;
import com.me.test.model.Item;
import com.me.test.model.ItemCursorMapper;
import com.me.test.model.data.FetchService;
import com.me.test.model.data.ItemContract.ItemList;
import com.me.test.presenter.leanback.CardPresenter;
import com.me.test.view.background.OnPrepareUIElement;

import java.util.HashMap;
import java.util.Map;

public class MainFragment extends BrowseSupportFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, OnWebQueryListener, OnPrepareUIElement
{


    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Runnable mBackgroundTask;
    private Uri mBackgroundURI;
    private BackgroundManager mBackgroundManager;
    private LoaderManager mLoaderManager;
    private Map<Integer, CursorObjectAdapter> mItemCursorAdapters;
    private static final int ROW_LOADER = 111;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mItemCursorAdapters = new HashMap<>();
        mLoaderManager = LoaderManager.getInstance(this);
        mLoaderManager.initLoader(ROW_LOADER, null, this);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();
        setupUI();
        setupEventListener();
        prepareEntranceTransition();
        mRowAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowAdapter);
    }

    private void setupUI(){
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
    }



    private void setupEventListener(){
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder,
                                      Object item,
                                      RowPresenter.ViewHolder rowViewHolder,
                                      Row row) {
                Item obj = (Item) item;
                Intent intent = new Intent(requireActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.ITEM, obj);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                requireActivity().startActivity(intent, bundle);
            }
        });
        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder,
                                       Object item,
                                       RowPresenter.ViewHolder rowViewHolder,
                                       Row row) {
                if (item instanceof Item) {
                    startBackgroundTimer();
                }
            }
        });
    }

    private void startBackgroundTimer() {
        mHandler.removeCallbacks(mBackgroundTask);
        mHandler.postDelayed(mBackgroundTask, BACKGROUND_UPDATE_DELAY);
    }

    @Override
    public void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(requireActivity());
        mBackgroundManager.attach(requireActivity().getWindow());
        /*mDefaultBackground = getResources().getDrawable(R.drawable.main_bg, null);
        mBackgroundTask = new UpdateBackgroundTask();*/
        mMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        if (id == ROW_LOADER) {
            return new CursorLoader(
                    getContext(),
                    ItemList.PROVIDE_URI,
                    new String[]{"DISTINCT " + ItemList.COLUMN_CATEGORY},
                    null,
                    null,
                    null
            );
        }

        else {
            String category = args.getString(ItemList.COLUMN_CATEGORY);
            return new CursorLoader(
                    getContext(),
                    ItemList.PROVIDE_URI,
                    null,
                    ItemList.COLUMN_CATEGORY + " = ?",
                    new String[]{category},
                    null
            );
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data!= null && data.moveToFirst()) {
            final int loaderId = loader.getId();
            if (loaderId == ROW_LOADER) {
                mRowAdapter.clear();
                while (!data.isAfterLast()){
                    int categoryIndex =
                            data.getColumnIndex(ItemList.COLUMN_CATEGORY);
                    String category = data.getString(categoryIndex);
                    HeaderItem headerItem = new HeaderItem(category);
                    int itemLoaderId = category.hashCode();
                    CursorObjectAdapter exAdapter = mItemCursorAdapters.get(itemLoaderId);
                    if (exAdapter == null){
                        CursorObjectAdapter itemCursorAdapter =
                                new CursorObjectAdapter(new CardPresenter());
                        itemCursorAdapter.setMapper(new ItemCursorMapper());
                        mItemCursorAdapters.put(itemLoaderId, itemCursorAdapter);
                        ListRow row = new ListRow(headerItem, itemCursorAdapter);
                        mRowAdapter.add(row);
                        Bundle args = new Bundle();
                        args.putString(ItemList.COLUMN_CATEGORY, category);
                        mLoaderManager.initLoader(itemLoaderId, args, this);
                    }
                    else {
                        ListRow row = new ListRow(headerItem, exAdapter);
                        mRowAdapter.add(row);
                    }
                    data.moveToNext();
                }
            }
            else {
                mItemCursorAdapters.get(loaderId).changeCursor(data);
            }
        }
        else {
            webQuery();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        int loaderId = loader.getId();
        if (loaderId != ROW_LOADER) {
            mItemCursorAdapters.get(loaderId).changeCursor(null);
        }
        else {
            mRowAdapter.clear();
        }
    }

    @Override
    public void webQuery() {
        Intent serviceIntent = new Intent(getActivity(), FetchService.class);
        getActivity().startService(serviceIntent);
    }
}
