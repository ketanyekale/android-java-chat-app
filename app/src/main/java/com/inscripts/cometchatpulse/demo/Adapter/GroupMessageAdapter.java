package com.inscripts.cometchatpulse.demo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.models.Action;
import com.cometchat.cometchatpulse.models.BaseMessage;
import com.cometchat.cometchatpulse.models.MediaMessage;
import com.cometchat.cometchatpulse.models.TextMessage;
import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Activity.VideoViewActivity;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;
import com.inscripts.cometchatpulse.demo.CustomView.StickyHeaderAdapter;
import com.inscripts.cometchatpulse.demo.Utils.DateUtils;
import com.inscripts.cometchatpulse.demo.Utils.FontUtils;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftAudioViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftFileViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftImageVideoViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftMessageViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightAudioViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightFileViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightImageVideoViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightMessageViewHolder;

import java.util.Date;
import java.util.List;

import static com.inscripts.cometchatpulse.demo.Utils.MediaUtils.getVideoThumbnail;

public class GroupMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<GroupMessageAdapter.DateItemHolder> {

    private static final int RIGHT_TEXT_MESSAGE = 940;

    private static final int LEFT_TEXT_MESSAGE = 489;

    private static final int ACTION_MESSAGE = 123;

    private static final int LEFT_IMAGE_MESSAGE = 510;

    private static final int RIGHT_IMAGE_MESSAGE = 380;

    private static final int LEFT_AUDIO_MESSAGE = 230;

    private static final int RIGHT_AUDIO_MESSAGE = 393;

    private static final int LEFT_VIDEO_MESSAGE = 580;

    private static final int RIGHT_VIDEO_MESSAGE = 797;

    private static final int LEFT_FILE_MESSAGE = 24;

    private static final int RIGHT_FILE_MESSAGE = 55;

    private final Drawable drawable;


    private List<BaseMessage> messageList;

    private Context context;

    private String groupId;

    private String ownerId;

    private BaseMessage baseMessage;

    private int position;

    private String currentSenderId;

    private String previousSenderId;

    private String senderUid;

    private String currentData;

    private String previousDate;

    private int currentViewType;

    private int previousViewType;

    private MediaPlayer player;

    private long currentlyPlayingId = 0l;

    private String currentPlayingSong;

    private Runnable timerRunnable;

    private Handler seekHandler = new Handler(Looper.getMainLooper());

    private static final String TAG = "GroupMessageAdapter";

    public GroupMessageAdapter(List<BaseMessage> groupList, Context context, String groupId, String ownerId) {
        this.messageList = groupList;
        this.context = context;
        this.groupId = groupId;
        this.ownerId = ownerId;
        drawable = context.getResources().getDrawable(R.drawable.default_avatar);
        if (player == null) {
            player = new MediaPlayer();
        }

        new FontUtils(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        RecyclerView.ViewHolder groupHolder = null;

        switch (i) {
            case LEFT_TEXT_MESSAGE:
                View leftTextMessage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_text_layout_left, viewGroup, false);
                groupHolder = new LeftMessageViewHolder(leftTextMessage);
                break;
            case RIGHT_TEXT_MESSAGE:
                View rightTextMessage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_text_layout_right, viewGroup, false);
                groupHolder = new RightMessageViewHolder(rightTextMessage);
                break;

            case LEFT_IMAGE_MESSAGE:
                View leftImageMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_left, viewGroup, false);
                groupHolder = new LeftImageVideoViewHolder(context, leftImageMessageView);
                break;
            case RIGHT_IMAGE_MESSAGE:
                View rightImageMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_right, viewGroup, false);
                groupHolder = new RightImageVideoViewHolder(context, rightImageMessageView);
                break;

            case ACTION_MESSAGE:
                View actionMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_message_list_header, viewGroup, false);
                groupHolder = new DateItemHolder(actionMessageView);
                break;

            case RIGHT_VIDEO_MESSAGE:
                View rightVideoMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_right, viewGroup, false);
                groupHolder = new RightImageVideoViewHolder(context, rightVideoMessageView);
                break;

            case LEFT_VIDEO_MESSAGE:
                View leftVideoMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_left, viewGroup, false);
                groupHolder = new LeftImageVideoViewHolder(context, leftVideoMessageView);
                break;

            case LEFT_AUDIO_MESSAGE:
                View leftAudioMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_audionote_layout_left, viewGroup, false);
                groupHolder = new LeftAudioViewHolder(context, leftAudioMessageView);
                break;

            case RIGHT_AUDIO_MESSAGE:
                View rightAudioMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_audionote_layout_right, viewGroup, false);
                groupHolder = new RightAudioViewHolder(context, rightAudioMessageView);
                break;

            case RIGHT_FILE_MESSAGE:
                View rightFileMessage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.right_file_layout, viewGroup, false);
                groupHolder = new RightFileViewHolder(context, rightFileMessage);
                break;

            case LEFT_FILE_MESSAGE:
                View leftFileMessage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.left_file_layout, viewGroup, false);
                groupHolder = new LeftFileViewHolder(context, leftFileMessage);
                break;

            default:
                break;
        }
        return groupHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        baseMessage = messageList.get(i);
        String messge = null;
        String mediaFile = null;
        String avatar = baseMessage.getSender().getAvatar();
        String senderName = baseMessage.getSender().getName();
        senderUid = baseMessage.getSender().getUid();

        position = viewHolder.getAdapterPosition();

        currentViewType = getItemViewType(position);

        currentSenderId = baseMessage.getSender().getUid();
        currentData = new java.text.SimpleDateFormat("yyyy/MM/dd")
                .format(new java.util.Date(messageList.get(position).getSentAt() * 1000));

        if (position > 0) {
            previousViewType = getItemViewType(position - 1);
            previousSenderId = messageList.get(position - 1).getSender().getUid();
            previousDate = new java.text.SimpleDateFormat("yyyy/MM/dd")
                    .format(new java.util.Date(messageList.get(position - 1).getSentAt() * 1000));
        }

        if (baseMessage instanceof TextMessage) {
            messge = ((TextMessage) baseMessage).getText();
        }
        if (baseMessage instanceof MediaMessage) {
            mediaFile = ((MediaMessage) baseMessage).getUrl();

        }
        if (baseMessage instanceof Action) {

            messge = ((Action) baseMessage).getMessage();
        }

        String timeStampString = DateUtils.getTimeStringFromTimestamp(baseMessage.getSentAt(),
                "hh:mm a");
        final long timeStampLong = baseMessage.getSentAt();


        switch (viewHolder.getItemViewType()) {
            case LEFT_TEXT_MESSAGE:
                LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) viewHolder;
                leftMessageViewHolder.textMessage.setText(messge);
                leftMessageViewHolder.messageTimeStamp.setText(timeStampString);
                leftMessageViewHolder.avatar.setVisibility(View.VISIBLE);
                leftMessageViewHolder.textMessage.setTypeface(FontUtils.openSansRegular);
                leftMessageViewHolder.senderName.setVisibility(View.VISIBLE);
                leftMessageViewHolder.senderName.setText(senderName);
                leftMessageViewHolder.senderName.setTypeface(FontUtils.robotoMedium);
                if (avatar!=null)
                {
                    Glide.with(context).load(avatar).into(leftMessageViewHolder.avatar);
                }

                break;
            case RIGHT_TEXT_MESSAGE:
                RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) viewHolder;
                rightMessageViewHolder.textMessage.setText(messge);
                rightMessageViewHolder.messageTimeStamp.setText(timeStampString);
                rightMessageViewHolder.messageStatus.setVisibility(View.VISIBLE);
                rightMessageViewHolder.textMessage.setTypeface(FontUtils.openSansRegular);

                break;
            case LEFT_IMAGE_MESSAGE:
                LeftImageVideoViewHolder leftImageViewHolder = (LeftImageVideoViewHolder) viewHolder;
                leftImageViewHolder.senderName.setVisibility(View.VISIBLE);
                leftImageViewHolder.senderName.setText(senderName);
                leftImageViewHolder.senderName.setTypeface(FontUtils.robotoMedium);
                leftImageViewHolder.messageTimeStamp.setText(timeStampString);
                leftImageViewHolder.btnPlayVideo.setVisibility(View.GONE);
                leftImageViewHolder.avatar.setVisibility(View.VISIBLE);
                leftImageViewHolder.imageTitle.setVisibility(View.GONE);
                leftImageViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                RequestOptions requestOptions = new RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_broken_image_black);

                if (avatar!=null)
                {
                    Glide.with(context).load(avatar).into(leftImageViewHolder.avatar);
                }

                if (mediaFile != null) {

                    Glide.with(context).load(mediaFile).apply(requestOptions).into(leftImageViewHolder.imageMessage);
                }

                break;
            case RIGHT_IMAGE_MESSAGE:
                RightImageVideoViewHolder rightImageVideoViewHolder = (RightImageVideoViewHolder) viewHolder;
                rightImageVideoViewHolder.messageTimeStamp.setText(timeStampString);
                rightImageVideoViewHolder.btnPlayVideo.setVisibility(View.GONE);
                rightImageVideoViewHolder.imageTitle.setVisibility(View.GONE);
                rightImageVideoViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);

                if (mediaFile != null) {

                    RequestOptions RightrequestOptions = new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_broken_image);

                    Glide.with(context).load(mediaFile).apply(RightrequestOptions).into(rightImageVideoViewHolder.imageMessage);
                }
                break;
            case ACTION_MESSAGE:
                DateItemHolder dateItemHolder = (DateItemHolder) viewHolder;
                dateItemHolder.txtMessageDate.setText(messge);
                dateItemHolder.txtMessageDate.setTypeface(FontUtils.robotoMedium);
                dateItemHolder.txtMessageDate.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                break;

            case LEFT_VIDEO_MESSAGE:
                LeftImageVideoViewHolder leftVideoViewHolder = (LeftImageVideoViewHolder) viewHolder;
                leftVideoViewHolder.messageTimeStamp.setText(timeStampString);
                leftVideoViewHolder.btnPlayVideo.setVisibility(View.VISIBLE);
                leftVideoViewHolder.imageTitle.setVisibility(View.GONE);
                leftVideoViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                if (avatar!=null)
                {
                    Glide.with(context).load(avatar).into(leftVideoViewHolder.avatar);
                }

                try {
                    getVideoThumbnail(leftVideoViewHolder, mediaFile);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                final String finalMediaFile3 = mediaFile;
                leftVideoViewHolder.btnPlayVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startIntent(finalMediaFile3);
                    }
                });
                break;

            case RIGHT_VIDEO_MESSAGE:
                RightImageVideoViewHolder rightVideoViewHolder = (RightImageVideoViewHolder) viewHolder;
                rightVideoViewHolder.messageTimeStamp.setText(timeStampString);
                rightVideoViewHolder.btnPlayVideo.setVisibility(View.VISIBLE);
                rightVideoViewHolder.imageTitle.setVisibility(View.GONE);
                rightVideoViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                try {
                    getVideoThumbnail(rightVideoViewHolder, mediaFile);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                final String finalMediaFile4 = mediaFile;
                rightVideoViewHolder.btnPlayVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startIntent(finalMediaFile4);
                    }
                });
                break;

            case RIGHT_AUDIO_MESSAGE:
                final RightAudioViewHolder rightAudioViewHolder = (RightAudioViewHolder) viewHolder;
                rightAudioViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                rightAudioViewHolder.messageTimeStamp.setText(timeStampString);
                rightAudioViewHolder.audioSeekBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                rightAudioViewHolder.audioSeekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                if (!player.isPlaying()) {
                    rightAudioViewHolder.playAudio.setImageResource(R.drawable.ic_play_arrow);
                }
                rightAudioViewHolder.audioSeekBar.setProgress(0);

                final String tempMediaFile = mediaFile;
                rightAudioViewHolder.playAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (!TextUtils.isEmpty(tempMediaFile)) {
                            try {
                                if (timeStampLong == currentlyPlayingId) {
                                    Logger.error(TAG, "onClick: currently playing");
                                    currentPlayingSong = "";
//                                        currentlyPlayingId = 0l;
//                                        setBtnColor(holder.viewType, playBtn, true);
                                    try {
                                        if (player.isPlaying()) {
                                            player.pause();
                                            Logger.error(TAG, "onClick: paused");
                                            rightAudioViewHolder.playAudio.setImageResource(R.drawable.ic_play_arrow);
                                        } else {
//                                                player.setDataSource(message);
//                                                player.prepare();
                                            player.seekTo(player.getCurrentPosition());
                                            rightAudioViewHolder.audioSeekBar.setProgress(player.getCurrentPosition());
                                            rightAudioViewHolder.audioLength.setText(DateUtils.convertTimeStampToDurationTime(player.getDuration()));
                                            rightAudioViewHolder.audioSeekBar.setMax(player.getDuration());
                                            rightAudioViewHolder.playAudio.setImageResource(R.drawable.ic_pause);
                                            timerRunnable = new Runnable() {
                                                @Override
                                                public void run() {

                                                    int pos = player.getCurrentPosition();
                                                    rightAudioViewHolder.audioSeekBar.setProgress(pos);

                                                    if (player.isPlaying() && pos < player.getDuration()) {
                                                        rightAudioViewHolder.audioLength.setText(DateUtils.convertTimeStampToDurationTime(player.getCurrentPosition()));
                                                        seekHandler.postDelayed(this, 250);
                                                    } else {
                                                        seekHandler
                                                                .removeCallbacks(timerRunnable);
                                                        timerRunnable = null;
                                                    }
                                                }

                                            };
                                            seekHandler.postDelayed(timerRunnable, 100);
                                            notifyDataSetChanged();
                                            player.start();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                                        int audioDuration = player.getDuration();

                                } else {
                                    rightAudioViewHolder.playAudio.setImageResource(R.drawable.ic_pause);
                                    playAudio(tempMediaFile, timeStampLong, player, rightAudioViewHolder.playAudio,
                                            rightAudioViewHolder.audioLength, rightAudioViewHolder.audioSeekBar);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });


                break;

            case LEFT_AUDIO_MESSAGE:

                final LeftAudioViewHolder leftAudioViewHolder = (LeftAudioViewHolder) viewHolder;
                leftAudioViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                leftAudioViewHolder.messageTimeStamp.setText(timeStampString);
                leftAudioViewHolder.senderName.setVisibility(View.GONE);
                leftAudioViewHolder.audioSeekBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                leftAudioViewHolder.audioSeekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                if (!player.isPlaying()) {
                    leftAudioViewHolder.playAudio.setImageResource(R.drawable.ic_play_arrow);
                }

                  if (avatar!=null)
                  {
                      Glide.with(context).load(avatar).into(leftAudioViewHolder.avatar);
                  }
                leftAudioViewHolder.audioSeekBar.setProgress(0);
                final String finalMediaFile1 = mediaFile;
                leftAudioViewHolder.playAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (!player.isPlaying()) {

                        if (!TextUtils.isEmpty(finalMediaFile1)) {
                            try {
                                if (timeStampLong == currentlyPlayingId) {
                                    Logger.error(TAG, "onClick: currently playing");
                                    currentPlayingSong = "";
//                                        currentlyPlayingId = 0l;
//                                        setBtnColor(holder.viewType, playBtn, true);
                                    try {
                                        if (player.isPlaying()) {
                                            player.pause();
                                            Logger.error(TAG, "onClick: paused");
                                            leftAudioViewHolder.playAudio.setImageResource(R.drawable.ic_play_arrow);
                                        } else {
//                                                player.setDataSource(message);
//                                                player.prepare();
                                            player.seekTo(player.getCurrentPosition());
                                            leftAudioViewHolder.audioSeekBar.setProgress(player.getCurrentPosition());
                                            leftAudioViewHolder.audioLength.setText(DateUtils.convertTimeStampToDurationTime(player.getDuration()));
                                            leftAudioViewHolder.audioSeekBar.setMax(player.getDuration());
                                            leftAudioViewHolder.playAudio.setImageResource(R.drawable.ic_pause);
                                            timerRunnable = new Runnable() {
                                                @Override
                                                public void run() {

                                                    int pos = player.getCurrentPosition();
                                                    leftAudioViewHolder.audioSeekBar.setProgress(pos);

                                                    if (player.isPlaying() && pos < player.getDuration()) {
                                                        leftAudioViewHolder.audioLength.setText(DateUtils.convertTimeStampToDurationTime(player.getCurrentPosition()));
                                                        seekHandler.postDelayed(this, 250);
                                                    } else {
                                                        seekHandler.removeCallbacks(timerRunnable);
                                                        timerRunnable = null;
                                                    }
                                                }

                                            };
                                            seekHandler.postDelayed(timerRunnable, 100);
                                            notifyDataSetChanged();
                                            player.start();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    leftAudioViewHolder.playAudio.setImageResource(R.drawable.ic_pause);
                                    playAudio(finalMediaFile1, timeStampLong, player, leftAudioViewHolder.playAudio, leftAudioViewHolder.audioLength, leftAudioViewHolder.audioSeekBar);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
                break;

            case RIGHT_FILE_MESSAGE:
                try {

                    RightFileViewHolder rightFileViewHolder = (RightFileViewHolder) viewHolder;
                    Logger.error("OneToOne", mediaFile);
                    Logger.error("OneToOne", mediaFile.substring(mediaFile.lastIndexOf("/")) + 1);
                    String t[] = mediaFile.substring(mediaFile.lastIndexOf("/")).split("_");
                    rightFileViewHolder.fileType.setTypeface(FontUtils.robotoRegular);
                    rightFileViewHolder.fileName.setTypeface(FontUtils.robotoRegular);
                    rightFileViewHolder.fileName.setText(t[2]);
                    rightFileViewHolder.messageTimeStamp.setText(timeStampString);
                    rightFileViewHolder.fileType.setText(mediaFile.substring(mediaFile.lastIndexOf(".") + 1));

                    final String finalMediaFile = mediaFile;
                    rightFileViewHolder.fileName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalMediaFile)));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case LEFT_FILE_MESSAGE:
                try {

                    LeftFileViewHolder leftFileViewHolder = (LeftFileViewHolder) viewHolder;
                    leftFileViewHolder.avatar.setVisibility(View.GONE);
                    leftFileViewHolder.senderName.setVisibility(View.GONE);
                    String t1[] = mediaFile.substring(mediaFile.lastIndexOf("/")).split("_");
                    leftFileViewHolder.fileType.setTypeface(FontUtils.robotoRegular);
                    leftFileViewHolder.fileName.setTypeface(FontUtils.robotoRegular);
                    leftFileViewHolder.fileName.setText(t1[2]);
                    leftFileViewHolder.messageTimeStamp.setText(timeStampString);
                    leftFileViewHolder.fileType.setText(mediaFile.substring(mediaFile.lastIndexOf(".") + 1));
                    final String finalMediaFile2 = mediaFile;
                    leftFileViewHolder.fileName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalMediaFile2)));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        setAvatarAndName(viewHolder, position, avatar);

    }

    private void startIntent(String url) {
        Intent videoIntent=new Intent(context,VideoViewActivity.class);
        videoIntent.putExtra(StringContract.IntentStrings.MEDIA_URL,url);
        context.startActivity(videoIntent);
    }

    public void playAudio(String message, long sentTimeStamp, final MediaPlayer player, final ImageView playButton, final TextView audioLength, final SeekBar audioSeekBar) {
        try {
            currentPlayingSong = message;
            currentlyPlayingId = sentTimeStamp;
            if (timerRunnable != null) {
                seekHandler.removeCallbacks(timerRunnable);
                timerRunnable = null;
            }

            try {
                player.reset();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(currentPlayingSong);
                player.prepare();
                player.start();

            } catch (IllegalStateException ie) {
                ie.printStackTrace();
            }

            final int duration = player.getDuration();
            audioSeekBar.setMax(duration);
            timerRunnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        int pos = player.getCurrentPosition();
                        audioSeekBar.setProgress(pos);

                        if (player.isPlaying() && pos < duration) {
                            audioLength.setText(DateUtils.convertTimeStampToDurationTime(player.getCurrentPosition()));
                            seekHandler.postDelayed(this, 250);
                        } else {
                            seekHandler
                                    .removeCallbacks(timerRunnable);
                            timerRunnable = null;
                        }
                    } catch (IllegalStateException ie) {
                        ie.printStackTrace();
                    }

                }

            };
            seekHandler.postDelayed(timerRunnable, 100);
            notifyDataSetChanged();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPlayingSong = "";
                    currentlyPlayingId = 0l;
                    seekHandler
                            .removeCallbacks(timerRunnable);
                    timerRunnable = null;
                    mp.stop();
                    audioLength.setText(DateUtils.convertTimeStampToDurationTime(duration));
                    audioSeekBar.setProgress(0);
                    playButton.setImageResource(R.drawable.ic_play_arrow);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private <P> void setAvatarAndName(P holder, int position, String avatar) {

        try {

            if (getItemViewType(position) == LEFT_IMAGE_MESSAGE || getItemViewType(position) == LEFT_TEXT_MESSAGE
                    || getItemViewType(position) == LEFT_VIDEO_MESSAGE || getItemViewType(position) == LEFT_AUDIO_MESSAGE) {
                if (currentSenderId.equals(previousSenderId) && currentData.equals(previousDate) &&
                        currentViewType != ACTION_MESSAGE && previousViewType != ACTION_MESSAGE) {
                    if (holder instanceof LeftMessageViewHolder) {
                        ((LeftMessageViewHolder) holder).senderName.setVisibility(View.GONE);
                        ((LeftMessageViewHolder) holder).avatar.setVisibility(View.INVISIBLE);
                    } else if (holder instanceof LeftImageVideoViewHolder) {
                        ((LeftImageVideoViewHolder) holder).avatar.setVisibility(View.INVISIBLE);
                        ((LeftImageVideoViewHolder) holder).senderName.setVisibility(View.GONE);
                    } else if (holder instanceof LeftAudioViewHolder) {
                        ((LeftAudioViewHolder) holder).avatar.setVisibility(View.INVISIBLE);
                        ((LeftAudioViewHolder) holder).senderName.setVisibility(View.GONE);
                    } else if (holder instanceof LeftFileViewHolder) {
                        ((LeftFileViewHolder) holder).avatar.setVisibility(View.INVISIBLE);
                        ((LeftFileViewHolder) holder).senderName.setVisibility(View.GONE);
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemViewType(int position) {

        if (messageList.get(position).getCategory().equals(CometChatConstants.CATEGORY_ACTION)) {

            return ACTION_MESSAGE;

        } else if (messageList.get(position).getCategory().equals(CometChatConstants.CATEGORY_MESSAGE)) {

            if (ownerId.equalsIgnoreCase(messageList.get(position).getSender().getUid())) {


                switch (messageList.get(position).getType()) {
                    case CometChatConstants.MESSAGE_TYPE_TEXT:

                        return RIGHT_TEXT_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_IMAGE:

                        return RIGHT_IMAGE_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_VIDEO:

                        return RIGHT_VIDEO_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_AUDIO:

                        return RIGHT_AUDIO_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_FILE:
                        return RIGHT_FILE_MESSAGE;
                }
            } else {
                switch (messageList.get(position).getType()) {

                    case CometChatConstants.MESSAGE_TYPE_TEXT:

                        return LEFT_TEXT_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_IMAGE:

                        return LEFT_IMAGE_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_VIDEO:

                        return LEFT_VIDEO_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_AUDIO:

                        return LEFT_AUDIO_MESSAGE;

                    case CometChatConstants.MESSAGE_TYPE_FILE:
                        return LEFT_FILE_MESSAGE;
                }

            }
        }


        return 0;
    }

    @Override
    public int getItemCount() {

        if (messageList != null) {

            return messageList.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getHeaderId(int var1) {

        return Long.parseLong(DateUtils.getDateId(messageList.get(var1).getSentAt() * 1000));
    }

    @Override
    public GroupMessageAdapter.DateItemHolder onCreateHeaderViewHolder(ViewGroup var1) {

        View view = LayoutInflater.from(var1.getContext()).inflate(R.layout.cc_message_list_header,
                var1, false);
        return new DateItemHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(GroupMessageAdapter.DateItemHolder var1, int var2, long var3) {

        Date date = new Date(messageList.get(var2).getSentAt() * 1000);
        String formattedDate = DateUtils.getCustomizeDate(date.getTime());
        var1.txtMessageDate.setBackground(context.getResources().getDrawable(R.drawable.cc_rounded_date_button));
        var1.txtMessageDate.setTypeface(FontUtils.robotoMedium);
        var1.txtMessageDate.setText(formattedDate);
    }

    public void add(BaseMessage baseMessage) {
        messageList.add(baseMessage);
        notifyDataSetChanged();
    }

    public void refreshData(List<BaseMessage> messageList) {
        this.messageList.addAll(0, messageList);
        notifyItemRangeInserted(0, messageList.size());
        notifyItemChanged(messageList.size());
    }

    class DateItemHolder extends RecyclerView.ViewHolder {

        TextView txtMessageDate;

        DateItemHolder(@NonNull View itemView) {
            super(itemView);

            txtMessageDate = (TextView) itemView.findViewById(R.id.txt_message_date);
        }
    }

}
