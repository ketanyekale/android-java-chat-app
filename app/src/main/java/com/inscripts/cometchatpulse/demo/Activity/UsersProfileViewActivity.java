package com.inscripts.cometchatpulse.demo.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Contracts.UserProfileViewActivityContract;
import com.inscripts.cometchatpulse.demo.Presenters.UserProfileViewPresenter;
import com.inscripts.cometchatpulse.demo.Utils.DateUtils;
import com.inscripts.cometchatpulse.demo.Utils.FontUtils;
import com.inscripts.cometchatpulse.demo.Utils.MediaUtils;
import com.cometchat.cometchatpulse.constants.CometChatConstants;

public class UsersProfileViewActivity extends AppCompatActivity implements
        UserProfileViewActivityContract.UserProfileActivityView, View.OnClickListener {

    private Toolbar toolbar;

    private RelativeLayout rlClear, rlBlock;

    private ImageView userAvatar, ivStatusIcon;

    private TextView tvStatus;

    private Context context;

    private CollapsingToolbarLayout collapsingToolbar;

    private UserProfileViewActivityContract.UserProfileActivityPresenter userProfileActivityPresenter;

    private String contactUid;

    private TextView tvUid;

    private String userStatus;

    private Drawable statusDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        new FontUtils(this);
        context = this;
        userProfileActivityPresenter = new UserProfileViewPresenter();
        userProfileActivityPresenter.attach(this);

        initViewComponent();

    }

    private void initViewComponent() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(10);
        TextView tvClear = findViewById(R.id.tvClear);
        TextView tvBlock = findViewById(R.id.tvblock);
        TextView setting = findViewById(R.id.settings);
        TextView shared = findViewById(R.id.tvshared);
        userAvatar = findViewById(R.id.ivUserImage);
        ivStatusIcon = findViewById(R.id.imageViewProfileStatus);
        tvStatus = findViewById(R.id.textViewProfileStatusMessage);
        rlBlock = findViewById(R.id.rl_leave);
        rlClear = findViewById(R.id.rl_clear);
        tvUid=findViewById(R.id.tvUid);
        rlBlock.setOnClickListener(this);
        rlClear.setOnClickListener(this);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.primaryLightColor));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.primaryTextColor));
        collapsingToolbar.setExpandedTitleGravity(Gravity.START | Gravity.BOTTOM);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.primaryLightColor));

        collapsingToolbar.setExpandedTitleTypeface(FontUtils.robotoRegular);
        collapsingToolbar.setCollapsedTitleTypeface(FontUtils.robotoMedium);
        tvBlock.setTypeface(FontUtils.robotoRegular);
        tvClear.setTypeface(FontUtils.robotoRegular);
        setting.setTypeface(FontUtils.robotoMedium);
        shared.setTypeface(FontUtils.robotoMedium);
        tvStatus.setTypeface(FontUtils.robotoRegular);
        tvUid.setTypeface(FontUtils.robotoRegular);

        userProfileActivityPresenter.handleIntent(getIntent());




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userProfileActivityPresenter.removeUserPresenceListener(getString(R.string.presenceListener));
        userProfileActivityPresenter.detach();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setTitle(String name) {
        collapsingToolbar.setTitle(name);
    }

    @Override
    public void setStatus(String status,long lastActive) {

        if (status.equals(CometChatConstants.USER_STATUS_OFFLINE)) {
            statusDrawable = context.getResources().getDrawable(R.drawable.cc_status_offline);
            userStatus = DateUtils.getLastSeenDate(lastActive, this);
        } else if (status.equals(CometChatConstants.USER_STATUS_ONLINE)) {
            statusDrawable = context.getResources().getDrawable(R.drawable.cc_status_available);
            userStatus =status;
        }

        tvStatus.setText(userStatus);
        ivStatusIcon.setImageDrawable(statusDrawable);
    }

    @Override
    public void setUserImage(String avatar) {

        if (avatar != null) {

            userProfileActivityPresenter.setContactAvatar(context, avatar, userAvatar);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.default_avatar);

            try {
                Bitmap bitmap = MediaUtils.getPlaceholderImage(this, drawable);
                userAvatar.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userProfileActivityPresenter.addUserPresenceListener(getString(R.string.presenceListener));
    }

    @Override
    public void setUserId(String uid) {
        contactUid = uid;

        if (uid != null) {
            tvUid.setText("Uid:"+contactUid);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
