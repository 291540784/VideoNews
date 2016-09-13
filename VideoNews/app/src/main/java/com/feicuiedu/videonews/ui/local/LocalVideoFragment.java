package com.feicuiedu.videonews.ui.local;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.feicuiedu.videonews.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：yuanchao on 2016/9/9 0009 10:07
 * 邮箱：yuanchao@feicuiedu.com
 */
public class LocalVideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "LocalVideoFragment";

    private Unbinder unbinder;
    @BindView(R.id.gridView)GridView gridView;

    private LocalVideoAdapter adapter;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new LocalVideoAdapter(getContext());
        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_video,container,false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        gridView.setAdapter(adapter);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        adapter.release();
    }

    // loadercallback start ----------------
    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MediaStore.Video.Media._ID, // 视频ID
                MediaStore.Video.Media.DATA, // 视频文件路径
                MediaStore.Video.Media.DISPLAY_NAME,// 视频名称
        };
        return new CursorLoader(
                getContext(),
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,// 视频provider URI
                projection,
                null,null,null
        );
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
    // loadercallback end ----------------
}
