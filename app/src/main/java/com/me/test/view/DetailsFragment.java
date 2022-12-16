package com.me.test.view;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.CursorObjectAdapter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.me.test.R;
import com.me.test.model.Item;
import com.me.test.model.ItemCursorMapper;
import com.me.test.model.data.ItemContract.ItemList;
import com.me.test.model.data.listener.OnVersionCheckCallback;
import com.me.test.presenter.leanback.CardPresenter;
import com.me.test.presenter.leanback.DetailsDescriptionPresenter;
import com.me.test.presenter.leanback.DetailsRowPresenter;
import com.me.test.view.background.OnPrepareUIElement;

import java.util.List;

public class DetailsFragment extends DetailsSupportFragment
        implements LoaderManager.LoaderCallbacks<Cursor> , OnVersionCheckCallback, OnPrepareUIElement {
    private static final int NO_NOTIFICATION = -1;
    private static final int ACTION_DOWNLOAD = 1;
    private static final int ACTION_UPDATE = 2;
    private static final int ACTION_OPEN_APP = 3;
    private static final int RELATED_VIDEO_LOADER = 1;

    private int mGlobalSearchItemId = 2;

    private Item mSelectedItem;
    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private CursorObjectAdapter mItemCursorAdapter;
    private FullWidthDetailsOverviewSharedElementHelper mHelper;
    private final ItemCursorMapper mItemCursorMapper = new ItemCursorMapper();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String imageUrl = getResources().getString(R.string.base_server) +"/appimage/";
        prepareBackgroundManager();
        mItemCursorAdapter = new CursorObjectAdapter(new CardPresenter());
        mItemCursorAdapter.setMapper(mItemCursorMapper);

        mSelectedItem = (Item) getActivity().getIntent()
                .getParcelableExtra(DetailsActivity.ITEM);



        if (mSelectedItem != null || !hasGlobalSearchIntent()) {
            removeNotification(getActivity().getIntent()
                    .getIntExtra(DetailsActivity.NOTIFICATION_ID, NO_NOTIFICATION));
            setupAdapter();
            setupDetailsOverviewRow();
            setupListRow();
            updateBackground(imageUrl+mSelectedItem.no+"_banner.png");

            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object obj, RowPresenter.ViewHolder rowViewHolder, Row row) {

                    if (obj instanceof Item) {
                        Item item = (Item) obj;
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(DetailsActivity.ITEM, item);
                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                        getActivity().startActivity(intent, bundle);
                    }
                }
            });

        }

    }
    private void removeNotification(int notificationId) {
        if (notificationId != NO_NOTIFICATION) {
            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        }
    }
    @Override
    public void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(androidx.leanback.R.drawable.lb_ic_sad_cloud, null);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupAdapter() {
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter(),
                        new DetailsRowPresenter());

        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getActivity(), R.color.selected_background));
        detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_HALF);

        mHelper = new FullWidthDetailsOverviewSharedElementHelper();
        mHelper.setSharedElementEnterTransition(getActivity(),
                DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(mHelper);
        detailsPresenter.setParticipatingEntranceTransition(false);
        prepareEntranceTransition();


        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_DOWNLOAD || action.getId() == ACTION_UPDATE) {
                  /*  dialog.show();
                    Intent intent = new Intent(getActivity(), DownloadService.class);
                    intent.putExtra(DetailsActivity.ITEM, mSelectedItem);
                    intent.putExtra(DownloadService.downloadReceiver, new DownloadReceiver(new Handler()));
                    getActivity().startService(intent);*/
                }

                if (action.getId() == ACTION_OPEN_APP) {
                   /* PackageManager pm = getActivity().getPackageManager();
                    Intent intent = pm.
                            getLaunchIntentForPackage(mSelectedItem.packagename);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                }
            }
        });

        mPresenterSelector = new ClassPresenterSelector();
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        String imageUrl = getResources().getString(R.string.base_server) +"/appimage/";
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedItem);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.default_background)
                .dontAnimate();

        Glide.with(this)
                .asBitmap()
                .load(imageUrl + mSelectedItem.no + ".png")
                .apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            Bitmap resource,
                            Transition<? super Bitmap> transition) {
                        row.setImageBitmap(getActivity(), resource);
                        startEntranceTransition();
                    }
                });

        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();

        int ACTION_WHAT = checkVersion();
        if (ACTION_WHAT == ACTION_DOWNLOAD ){
            adapter.set(ACTION_DOWNLOAD, new Action(ACTION_DOWNLOAD, "Download"));
        }
        if (ACTION_WHAT == ACTION_UPDATE) {
            adapter.set(ACTION_UPDATE, new Action(ACTION_UPDATE, "Update"));
        }
        if (ACTION_WHAT == ACTION_OPEN_APP){
            adapter.set(ACTION_OPEN_APP, new Action(ACTION_OPEN_APP, "Open", "Application"));
        }
        row.setActionsAdapter(adapter);

        mAdapter.add(row);
    }
    private void setupListRow() {
        String[] subcategories = {"related"};

        String category = mSelectedItem.category;

        Bundle args = new Bundle();
        args.putString(ItemList.COLUMN_CATEGORY, category);
        getLoaderManager().initLoader(RELATED_VIDEO_LOADER, args, this);

        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, mItemCursorAdapter));
    }


    @Override
    public int checkVersion() {
        boolean isExist = false;
        PackageManager pkgMgr = getContext().getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);
        try {
            for (int i = 0; i < mApps.size(); i++) {
                if (mApps.get(i).activityInfo.packageName.startsWith(mSelectedItem.packagename)){
                    isExist = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            isExist = false;
        }
        int action_what = 0;
        Pair packageTuple = getPackageVersionInfo(getContext(), mSelectedItem.packagename);
        if (isExist){
            if (packageTuple.first == mSelectedItem.ver ||
                    (long)packageTuple.second == mSelectedItem.code){
                action_what = ACTION_OPEN_APP;
            }
            else{
                action_what = ACTION_UPDATE;
            }
        }
        else{
            action_what = ACTION_DOWNLOAD;
        }
        return action_what;
    }

    @Override
    public Pair getPackageVersionInfo(Context context, String packageName) {
        String version = "Unknown";
        long versionCode = 0;
        PackageInfo packageInfo;
        if (context == null) {
            return null;
        }
        try {
            packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(packageName, 0 );
            version = packageInfo.versionName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = packageInfo.getLongVersionCode();
            } else {
                versionCode = (long)packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new Pair(version, versionCode);
    }

    private void updateBackground(String uri) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(mDefaultBackground);

        Glide.with(this)
                .asBitmap()
                .load(uri)
                .apply(options)
                .error(androidx.leanback.R.drawable.lb_ic_sad_cloud)
                .into(new SimpleTarget<Bitmap>(mMetrics.widthPixels, mMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(
                            Bitmap resource,
                            Transition<? super Bitmap> transition) {
                        mBackgroundManager.setBitmap(resource);
                    }
                });
    }

    private boolean hasGlobalSearchIntent() {
        Intent intent = getActivity().getIntent();
        String intentAction = intent.getAction();
        String globalSearch = "GlobalSearch";

        if (globalSearch.equalsIgnoreCase(intentAction)) {
            Uri intentData = intent.getData();
            String itemId = intentData.getLastPathSegment();

            Bundle args = new Bundle();
            args.putString(ItemList._ID, itemId);
            getLoaderManager().initLoader(mGlobalSearchItemId++, args, this);
            return true;
        }
        return false;
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case RELATED_VIDEO_LOADER: {
                String category = args.getString(ItemList.COLUMN_CATEGORY);
                return new CursorLoader(
                        getActivity(),
                        ItemList.PROVIDE_URI,
                        null,
                        ItemList.COLUMN_CATEGORY + " = ?",
                        new String[]{category},
                        null
                );
            }
            default: {
                String itemId = args.getString(ItemList._ID);
                return new CursorLoader(
                        getActivity(),
                        ItemList.PROVIDE_URI,
                        null,
                        ItemList._ID + " = ?",
                        new String[]{itemId},
                        null
                );
            }
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToNext()) {
            if (loader.getId() == RELATED_VIDEO_LOADER) {
                mItemCursorAdapter.changeCursor(cursor);
            } else {
                mSelectedItem = (Item) mItemCursorMapper.convert(cursor);
                setupAdapter();
                setupDetailsOverviewRow();
                setupListRow();
                updateBackground(R.string.base_server + "/appimage" + mSelectedItem.no + "_banner");
                setOnItemViewClickedListener(new OnItemViewClickedListener() {
                    @Override
                    public void onItemClicked(Presenter.ViewHolder itemViewHolder,
                                              Object obj,
                                              RowPresenter.ViewHolder rowViewHolder,
                                              Row row) {
                        if (obj instanceof Item) {
                            Item item = (Item) obj;
                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                            intent.putExtra(DetailsActivity.ITEM, item);
                            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    getActivity(),
                                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                    DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                            getActivity().startActivity(intent, bundle);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
