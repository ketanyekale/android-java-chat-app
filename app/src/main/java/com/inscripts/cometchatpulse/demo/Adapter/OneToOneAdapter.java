package com.inscripts.cometchatpulse.demo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cometchat.cometchatpulse.constants.CometChatConstants;
import com.cometchat.cometchatpulse.core.Call;
import com.cometchat.cometchatpulse.models.BaseMessage;
import com.cometchat.cometchatpulse.models.MediaMessage;
import com.cometchat.cometchatpulse.models.TextMessage;
import com.inscripts.cometchatpulse.demo.R;
import com.inscripts.cometchatpulse.demo.Activity.VideoViewActivity;
import com.inscripts.cometchatpulse.demo.Contracts.StringContract;
import com.inscripts.cometchatpulse.demo.CustomView.CircleImageView;
import com.inscripts.cometchatpulse.demo.CustomView.StickyHeaderAdapter;
import com.inscripts.cometchatpulse.demo.Utils.DateUtils;
import com.inscripts.cometchatpulse.demo.Utils.FontUtils;
import com.inscripts.cometchatpulse.demo.Utils.Logger;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftAudioViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftFileViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.LeftImageVideoViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightAudioViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightFileViewHolder;
import com.inscripts.cometchatpulse.demo.ViewHolders.RightImageVideoViewHolder;

import java.util.Date;
import java.util.List;

import static com.inscripts.cometchatpulse.demo.Utils.MediaUtils.getVideoThumbnail;


public class OneToOneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyHeaderAdapter<OneToOneAdapter.DateItemHolder> {


    private static final String TAG = "OneToOneAdapter";

    private static final int RIGHT_TEXT_MESSAGE = 334;

    private static final int LEFT_TEXT_MESSAGE = 734;

    private static final int LEFT_IMAGE_MESSAGE = 528;

    private static final int RIGHT_IMAGE_MESSAGE = 834;

    private static final int LEFT_VIDEO_MESSAGE = 580;

    private static final int RIGHT_VIDEO_MESSAGE = 797;

    private static final int RIGHT_AUDIO_MESSAGE = 70;

    private static final int LEFT_AUDIO_MESSAGE = 79;

    private static final int LEFT_FILE_MESSAGE = 24;

    private static final int RIGHT_FILE_MESSAGE = 55;

    private final String ownerUid;

    private List<BaseMessage> messageArrayList;

    private String friendUid;

    private Context context;

    private int position;

    private MediaPlayer player;
    private long currentlyPlayingId = 0l;

    private String currentPlayingSong;
    private Runnable timerRunnable;
    private Handler seekHandler = new Handler(Looper.getMainLooper());


    public OneToOneAdapter(Context context, List<BaseMessage> messageArrayList, String friendUid, String ownerUid) {
        this.messageArrayList = messageArrayList;
        this.friendUid = friendUid;
        this.ownerUid = ownerUid;
        this.context = context;

        if (null == player) {
            player = new MediaPlayer();
        }

        new FontUtils(context);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        switch (i) {

            case RIGHT_TEXT_MESSAGE:
                View rightTextMessageView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.cc_text_layout_right, viewGroup, false);
                return new RightMessageViewHolder(rightTextMessageView);


            case LEFT_TEXT_MESSAGE:
                View leftTextMessageView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.cc_text_layout_left, viewGroup, false);
                return new LeftMessageViewHolder(leftTextMessageView);


            case LEFT_IMAGE_MESSAGE:
                View leftImageMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_left, viewGroup, false);
                return new LeftImageVideoViewHolder(context, leftImageMessageView);

            case RIGHT_IMAGE_MESSAGE:
                View rightImageMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_right, viewGroup, false);
                return new RightImageVideoViewHolder(context, rightImageMessageView);

            case RIGHT_VIDEO_MESSAGE:
                View rightVideoMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_right, viewGroup, false);
                return new RightImageVideoViewHolder(context, rightVideoMessageView);


            case LEFT_VIDEO_MESSAGE:
                View leftVideoMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_image_video_layout_left, viewGroup, false);
                return new LeftImageVideoViewHolder(context, leftVideoMessageView);

            case LEFT_AUDIO_MESSAGE:
                View leftAudioMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_audionote_layout_left, viewGroup, false);
                return new LeftAudioViewHolder(context, leftAudioMessageView);


            case RIGHT_AUDIO_MESSAGE:
                View rightAudioMessageView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cc_audionote_layout_right, viewGroup, false);
                return new RightAudioViewHolder(context, rightAudioMessageView);


            case RIGHT_FILE_MESSAGE:
                View rightFileMessage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.right_file_layout, viewGroup, false);
                return new RightFileViewHolder(context, rightFileMessage);

            case LEFT_FILE_MESSAGE:
                View leftFileMessage = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.left_file_layout, viewGroup, false);
                return new LeftFileViewHolder(context, leftFileMessage);

            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {

        position = holder.getAdapterPosition();


        BaseMessage baseMessage = messageArrayList.get(i);
        String messge = null;
        String mediaFile = null;
        String imageUrl = null;
        if (baseMessage instanceof TextMessage) {
            messge = ((TextMessage) baseMessage).getText();
        }
        if (baseMessage instanceof MediaMessage) {
            imageUrl = ((MediaMessage) baseMessage).getUrl();
            mediaFile = ((MediaMessage) baseMessage).getUrl();

        }

        if (baseMessage instanceof Call) {

            messge = "Call";
        }


        String timeStampString = DateUtils.getTimeStringFromTimestamp(baseMessage.getSentAt(),
                "hh:mm a");
        final long timeStampLong = baseMessage.getSentAt();


        switch (holder.getItemViewType()) {
            case LEFT_TEXT_MESSAGE:
                LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
                leftMessageViewHolder.textMessage.setTypeface(FontUtils.openSansRegular);
                leftMessageViewHolder.textMessage.setText(messge);
                leftMessageViewHolder.messageTimeStamp.setText(timeStampString);
                leftMessageViewHolder.senderName.setVisibility(View.GONE);
                leftMessageViewHolder.avatar.setVisibility(View.GONE);
                break;

            case RIGHT_TEXT_MESSAGE:

                RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
                rightMessageViewHolder.textMessage.setTypeface(FontUtils.openSansRegular);
                rightMessageViewHolder.textMessage.setText(messge);
                rightMessageViewHolder.messageTimeStamp.setText(timeStampString);
                rightMessageViewHolder.messageStatus.setImageResource(R.drawable.ic_check_white_24dp);

                break;

            case LEFT_IMAGE_MESSAGE:
                LeftImageVideoViewHolder leftImageViewHolder = (LeftImageVideoViewHolder) holder;
                leftImageViewHolder.senderName.setVisibility(View.GONE);
                leftImageViewHolder.messageTimeStamp.setText(timeStampString);
                leftImageViewHolder.btnPlayVideo.setVisibility(View.GONE);
                leftImageViewHolder.avatar.setVisibility(View.GONE);
                leftImageViewHolder.imageTitle.setVisibility(View.GONE);
                leftImageViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {

                    RequestOptions requestOptions = new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_broken_image_black).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                    Glide.with(context).load(imageUrl).apply(requestOptions).into(leftImageViewHolder.imageMessage);
                }
                break;

            case RIGHT_IMAGE_MESSAGE:
                RightImageVideoViewHolder rightImageVideoViewHolder = (RightImageVideoViewHolder) holder;
                rightImageVideoViewHolder.messageTimeStamp.setText(timeStampString);
                rightImageVideoViewHolder.btnPlayVideo.setVisibility(View.GONE);
                rightImageVideoViewHolder.imageTitle.setVisibility(View.GONE);

                rightImageVideoViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);

                if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
                    String url = imageUrl.replace("/", "");
                    Logger.error("Image", url);
                    RequestOptions requestOptions = new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_broken_image).diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(context).load(imageUrl).apply(requestOptions).into(rightImageVideoViewHolder.imageMessage);
                }
                break;


            case LEFT_VIDEO_MESSAGE:
                final LeftImageVideoViewHolder leftVideoViewHolder = (LeftImageVideoViewHolder) holder;
                leftVideoViewHolder.messageTimeStamp.setText(timeStampString);
                leftVideoViewHolder.btnPlayVideo.setVisibility(View.VISIBLE);
                leftVideoViewHolder.imageTitle.setVisibility(View.GONE);
                leftVideoViewHolder.senderName.setVisibility(View.GONE);
                leftVideoViewHolder.avatar.setVisibility(View.GONE);
                leftVideoViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);

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
                final RightImageVideoViewHolder rightVideoViewHolder = (RightImageVideoViewHolder) holder;
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
                final RightAudioViewHolder rightAudioViewHolder = (RightAudioViewHolder) holder;
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

                final LeftAudioViewHolder leftAudioViewHolder = (LeftAudioViewHolder) holder;
                leftAudioViewHolder.fileLoadingProgressBar.setVisibility(View.GONE);
                leftAudioViewHolder.messageTimeStamp.setText(timeStampString);
                leftAudioViewHolder.senderName.setVisibility(View.GONE);
                leftAudioViewHolder.avatar.setVisibility(View.GONE);
                leftAudioViewHolder.audioSeekBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                leftAudioViewHolder.audioSeekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                if (!player.isPlaying()) {
                    leftAudioViewHolder.playAudio.setImageResource(R.drawable.ic_play_arrow);
                }
                leftAudioViewHolder.audioSeekBar.setProgress(0);
                final String finalMediaFile1 = mediaFile;
                leftAudioViewHolder.playAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


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
                                    leftAudioViewHolder.playAudio.setImageResource(R.drawable.ic_pause);
                                    playAudio(finalMediaFile1, timeStampLong, player, leftAudioViewHolder.playAudio,
                                            leftAudioViewHolder.audioLength, leftAudioViewHolder.audioSeekBar);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

//
                    }
                });
                break;

            case RIGHT_FILE_MESSAGE:
                try {

                    RightFileViewHolder rightFileViewHolder = (RightFileViewHolder) holder;
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

                    LeftFileViewHolder leftFileViewHolder = (LeftFileViewHolder) holder;
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

    }

    private void startIntent(String url) {
        Intent videoIntent=new Intent(context,VideoViewActivity.class);
        videoIntent.putExtra(StringContract.IntentStrings.MEDIA_URL,url);
        context.startActivity(videoIntent);
    }

    public void playAudio(String message, long sentTimeStamp, final MediaPlayer player,
                          final ImageView playButton, final TextView audioLength, final SeekBar audioSeekBar) {
        try {
            currentPlayingSong = message;
            currentlyPlayingId = sentTimeStamp;
            if (timerRunnable != null) {
                seekHandler.removeCallbacks(timerRunnable);
                timerRunnable = null;
            }

            player.reset();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(currentPlayingSong);
            player.prepare();
            player.start();


            final int duration = player.getDuration();
            audioSeekBar.setMax(duration);
            timerRunnable = new Runnable() {
                @Override
                public void run() {

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
                }

            };
            seekHandler.postDelayed(timerRunnable, 100);
            notifyDataSetChanged();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPlayingSong = "";
                    currentlyPlayingId = 0l;
//                    setBtnColor(viewtype, playBtn, true);
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


    @Override
    public int getItemCount() {

        if (messageArrayList != null) {
            return messageArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (messageArrayList.get(position).getCategory().equals(CometChatConstants.CATEGORY_MESSAGE)) {

            if (ownerUid.equalsIgnoreCase(messageArrayList.get(position).getSender().getUid())) {


                switch (messageArrayList.get(position).getType()) {
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
                switch (messageArrayList.get(position).getType()) {

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

        return super.getItemViewType(position);

    }

    @Override
    public long getItemId(int position) {

        return messageArrayList.get(position).getId();
    }


    public void refreshData(List<BaseMessage> userArrayList) {
        messageArrayList.addAll(0, userArrayList);
        notifyItemRangeInserted(0, userArrayList.size());
        notifyItemChanged(userArrayList.size());

    }


    @Override
    public long getHeaderId(int var1) {

        return Long.parseLong(DateUtils.getDateId(messageArrayList.get(var1).getSentAt() * 1000));
    }

    @Override
    public DateItemHolder onCreateHeaderViewHolder(ViewGroup var1) {
        View view = LayoutInflater.from(var1.getContext()).inflate(R.layout.cc_message_list_header,
                var1, false);

        return new DateItemHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(DateItemHolder var1, int var2, long var3) {

        Date date = new Date(messageArrayList.get(var2).getSentAt() * 1000);
        String formattedDate = DateUtils.getCustomizeDate(date.getTime());
        var1.txtMessageDate.setBackground(context.getResources().getDrawable(R.drawable.cc_rounded_date_button));
        var1.txtMessageDate.setTypeface(FontUtils.robotoMedium);
        var1.txtMessageDate.setText(formattedDate);
    }


    public void addMessage(BaseMessage baseMessage) {
        messageArrayList.add(baseMessage);
        notifyDataSetChanged();
    }

    public class LeftMessageViewHolder extends RecyclerView.ViewHolder {

        public TextView textMessage;
        public TextView messageTimeStamp;
        public TextView senderName;
        public CircleImageView avatar;

        public LeftMessageViewHolder(View leftTextMessageView) {

            super(leftTextMessageView);
            textMessage = leftTextMessageView.findViewById(R.id.textViewMessage);
            messageTimeStamp = leftTextMessageView.findViewById(R.id.timeStamp);
            avatar = leftTextMessageView.findViewById(R.id.imgAvatar);
            senderName = leftTextMessageView.findViewById(R.id.senderName);
        }
    }


    public class RightMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textMessage;
        public TextView messageTimeStamp;
        public CircleImageView messageStatus;

        public RightMessageViewHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textViewMessage);
            messageStatus = itemView.findViewById(R.id.img_message_status);
            messageTimeStamp = itemView.findViewById(R.id.timestamp);
        }

    }


    public class DateItemHolder extends RecyclerView.ViewHolder {

        public TextView txtMessageDate;

        public DateItemHolder(@NonNull View itemView) {
            super(itemView);

            txtMessageDate = (TextView) itemView.findViewById(R.id.txt_message_date);

        }
    }

}
