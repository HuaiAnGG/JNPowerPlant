package google.architecture.pending.console.video.videolist;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import google.architecture.common.base.ARouterPath;
import google.architecture.common.base.BaseActivity;
import google.architecture.common.util.SPUtils;
import google.architecture.coremodel.datamodel.http.entities.VideoListData;
import google.architecture.coremodel.viewmodel.ViewModelProviders;
import google.architecture.pending.R;
import google.architecture.pending.databinding.ActivityListVideoBinding;

@Route(path = ARouterPath.CONSOLE_VIDEO_LIST_ATY)
public class VideoListActivity extends BaseActivity {

    ActivityListVideoBinding activityListVideoBinding;
    private VideoListAdapter videoListAdapter;
    private VideoListViewModel videoListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListVideoBinding = DataBindingUtil
                .setContentView(VideoListActivity.this, R.layout.activity_list_video);
        // ARouter
        ARouter.getInstance().inject(this);

        setSupportActionBar(activityListVideoBinding.toolbar);
        // 返回导航键
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("监控列表");

        SearchFragment searchFragment = SearchFragment.newInstance();
        searchFragment.setOnSearchClickListener(iOnSearchClickListener);
        // 显示搜索框
//        searchFragment.showFragment(getSupportFragmentManager(), SearchFragment.TAG);

        // 获取Video Model
        videoListViewModel = ViewModelProviders.of(VideoListActivity.this).get(VideoListViewModel.class);

        // 视频列表adapter
        videoListAdapter = new VideoListAdapter(videoListClickCallback);
        activityListVideoBinding.setRecyclerAdapter(videoListAdapter);
        // 传入Video ViewModel
        subscribeToModel(videoListViewModel);
    }

    VideoListClickCallback videoListClickCallback
            = new VideoListClickCallback() {
        @Override
        public void onClick(VideoListData.BodyBean.Equipment equipment, int position) {
            switch (position) {
                case 1:
                    break;
                case 2:
                    /**
                     * 转跳到 VideoDetailActivity
                     */
                    SPUtils.saveInDisk("VIDEO_LIST_POSITION", "position", position);
                    Log.d("VideoListClickCallback", "onClick: " + equipment.getDeviceName() + "\t" + position);
                    ARouter.getInstance()
                            .build(ARouterPath.CONSOLE_VIDEO_DETAIL_ATY)
                            // 传参position
                            .withInt("position", position)
                            .withTransition(R.anim.activity_up_in, R.anim.activity_up_out)
                            .navigation(getApplicationContext());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * search dialog
     */
    IOnSearchClickListener iOnSearchClickListener = new IOnSearchClickListener() {
        @Override
        public void OnSearchClick(String keyword) {

        }
    };

    /**
     * back to prev
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 订阅数据变化来刷新UI
     *
     * @param model
     */
    private void subscribeToModel(final VideoListViewModel model) {
        //观察数据变化来刷新UI
        model.getLiveObservableData().observe(VideoListActivity.this, new Observer<VideoListData>() {
            @Override
            public void onChanged(@Nullable VideoListData videoListData) {
                Log.i("danxx", "subscribeToModel onChanged onChanged");
                model.setUiObservableData(videoListData);
                // 设置数据列表
                videoListAdapter.setVideoList(videoListData.getBody().getList());
            }
        });
    }
}
