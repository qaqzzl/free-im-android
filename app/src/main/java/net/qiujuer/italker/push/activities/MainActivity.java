package net.qiujuer.italker.push.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.wang.avi.AVLoadingIndicatorView;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.FloatActionButton;
import net.qiujuer.italker.common.app.Activity;
import net.qiujuer.italker.common.widget.PortraitView;
import net.qiujuer.italker.factory.data.helper.AccountHelper;
import net.qiujuer.italker.factory.data.helper.GroupHelper;
import net.qiujuer.italker.factory.data.helper.UpdateHelper;
import net.qiujuer.italker.factory.data.helper.UserHelper;
import net.qiujuer.italker.factory.persistence.Account;
import net.qiujuer.italker.factory.presenter.contact.ContactPresenter;
import net.qiujuer.italker.factory.socket.SocketManager;
import net.qiujuer.italker.push.LaunchActivity;
import net.qiujuer.italker.push.R;
import net.qiujuer.italker.push.frags.assist.PermissionsFragment;
import net.qiujuer.italker.push.frags.main.ActiveFragment;
import net.qiujuer.italker.push.frags.main.ContactFragment;
import net.qiujuer.italker.push.frags.main.GroupFragment;
import net.qiujuer.italker.push.helper.NavHelper;

import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    private AVLoadingIndicatorView avi;

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;

    /**
     * MainActivity ???????????????
     *
     * @param context ?????????
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()) {
            // ?????????????????????????????????????????????????????????
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        avi.setIndicator("BallScaleRippleMultipleIndicator"); // ??????loading??????


        // ??????????????????????????????
        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));


        // ????????????????????????????????????
        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });

    }

    @Override
    protected void initData() {
        super.initData();

        // ??????????????????????????????Menu?????????????????????????????????????????????
        Menu menu = mNavigation.getMenu();
        // ??????????????????Home
        menu.performIdentifierAction(R.id.action_home, 0);

        AccountHelper.checkingAppUpdate( new UpdateHelper(this) );

        // ?????????????????????
        mPortrait.setup(Glide.with(this), Account.getUser());

//        initAccountDataSuccess();
        // ?????????????????????
        waitInitAccountData(true);
    }


    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        // ????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        // ?????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            // ?????????????????????
            GroupCreateActivity.show(this);
        } else {
            // ????????????????????????????????????????????????
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param item MenuItem
     * @return True ????????????????????????????????????
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // ??????????????????????????????
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper ????????????????????????
     *
     * @param newTab ??????Tab
     * @param oldTab ??????Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        // ?????????????????????????????????Title??????Id
        mTitle.setText(newTab.extra);


        // ?????????????????????????????????????????????
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            // ??????????????????
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            // transY ?????????0 ?????????
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                // ???
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                // ?????????
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        // ????????????
        // ?????????Y????????????????????????????????????
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }

    /**
     * ???????????????????????????
     */
    private void waitInitAccountData(boolean status) {
        if (Account.isInitAccountData()) {
            skip();
            return;
        }
        if(status) {
            avi.show();
            Log.e("TEST","?????????????????????");
            // ??????????????????
            UserHelper.refreshContacts();
            // ????????? ????????????
            GroupHelper.refreshGroups();
        }

        // ????????????
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitInitAccountData(false);
                    }
                }, 500);
    }

    /**
     * ??????????????????50%????????????
     */
    private void skip() {
        avi.hide();
        initAccountDataSuccess();
    }


    /**
     * ???????????????????????????
     */
    private void initAccountDataSuccess() {

        // ??????socket
        SocketManager.getInstance(this).startTcpConnection();
    }

}
