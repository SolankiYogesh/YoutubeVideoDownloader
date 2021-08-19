package com.syinfotech.youtubedownloader.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.syinfotech.youtubedownloader.R;
import com.syinfotech.youtubedownloader.Task.DownloadMyFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class VideosFragment extends Fragment {

    private ImageView Start_btn;
    private ImageView Thumbail;
    private TextView titletext;
    public EditText Video_Yoututbe_link;
    private Context context;
    private Button reset;
    private static final int ITAG_FOR_AUDIO = 140;
    private List<YtFragmentClass> formatsToShowList;
    LinearLayout mainLayout;
    public static RoundedProgressBar roundedProgressBar;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public VideosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        titletext = view.findViewById(R.id.txttitle);
        Thumbail = view.findViewById(R.id.thumbail);
        reset = view.findViewById(R.id.reset);
        Video_Yoututbe_link = view.findViewById(R.id.video_youtube_link);
        mainLayout = view.findViewById(R.id.mainLayout);
        Start_btn = view.findViewById(R.id.video_youtube_link_button);
        roundedProgressBar = view.findViewById(R.id.third_bar);
        reset.setOnClickListener(view12 -> {
            Video_Yoututbe_link.getText().clear();
            Video_Yoututbe_link.setVisibility(View.VISIBLE);
            Start_btn.setVisibility(View.VISIBLE);
            reset.setVisibility(View.GONE);
            Thumbail.setVisibility(View.GONE);
            titletext.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
        });
        Bundle extra = requireActivity().getIntent().getExtras();
        if (extra != null) {
            String mlink = extra.getString(Intent.EXTRA_TEXT);
            if (isYoutubeUrl(mlink)) {
                Video_Yoututbe_link.setText(mlink);
                Video_Yoututbe_link.setVisibility(View.GONE);
                Start_btn.setVisibility(View.GONE);
                Start();
            } else {
                ShowDialog();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new VideosFragment())
                        .commit();
            }

        }


        Start_btn.setOnClickListener(view1 -> {
            Video_Yoututbe_link.setVisibility(View.GONE);
            Start_btn.setVisibility(View.GONE);
            Start();
        });
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void Start() {
        if (Video_Yoututbe_link.getText() != null) {
            String link = Video_Yoututbe_link.getText().toString();
            if (isYoutubeUrl(link)) {
                new YouTubeExtractor(context) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles == null) {
                            TextView tv = new TextView(context);
                            tv.setText(R.string.app_name);
                            tv.setMovementMethod(LinkMovementMethod.getInstance());
                            mainLayout.addView(tv);
                            return;
                        } else {
                            Glide.with(context).load(vMeta.getThumbUrl()).into(Thumbail);
                            titletext.setText(vMeta.getTitle());
                            Thumbail.setVisibility(View.VISIBLE);
                            titletext.setVisibility(View.VISIBLE);
                            mainLayout.setVisibility(View.VISIBLE);
                            reset.setVisibility(View.VISIBLE);
                        }
                        formatsToShowList = new ArrayList<>();
                        for (int i = 0, itag; i < ytFiles.size(); i++) {
                            itag = ytFiles.keyAt(i);
                            YtFile ytFile = ytFiles.get(itag);

                            if (ytFile.getFormat().getExt().equals("webm")) {
                                continue;
                            }

                            if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                                addFormatToList(ytFile, ytFiles);
                            }
                        }
                        Collections.sort(formatsToShowList, new Comparator<YtFragmentClass>() {
                            @Override
                            public int compare(YtFragmentClass lhs, YtFragmentClass rhs) {
                                return lhs.height - rhs.height;
                            }
                        });
                        for (YtFragmentClass files : formatsToShowList) {
                            AddToButton(vMeta.getTitle(), files);
                        }
                    }
                }.extract(link);

            } else {
                ShowDialog();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new VideosFragment())
                        .commit();
            }
        }

    }

    public static boolean isYoutubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!youTubeURl.isEmpty() && youTubeURl.matches(pattern)) {
            success = true;
        } else {
            // Not Valid youtube URL
            success = false;
        }
        return success;
    }


    private void ShowDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.errror_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        Button ok = dialog.findViewById(R.id.dialong_ok);
        ok.setOnClickListener(view -> dialog.dismiss());
    }

    private void AddToButton(String title, YtFragmentClass ytFragmentvideo) {
        String btnText ;
        if (ytFragmentvideo.height == -1) {
            btnText = "Audio " + ytFragmentvideo.audioFile.getFormat().getAudioBitrate() + " kbit/s";
        } else
            btnText = (ytFragmentvideo.videoFile.getFormat().getFps() == 60) ? ytFragmentvideo.height + "p60" :
                    ytFragmentvideo.height + "p";
        Button button = new Button(context);
        button.setText(btnText);
        button.setWidth(100);
        button.setHeight(20);
        button.setBackgroundResource(R.drawable.btn_back);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setOnClickListener(view -> {
            String filename;
            if (title.length() == 55) {
                filename = title.substring(0, 55);
            } else {
                filename = title;
            }
            filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
            filename += (ytFragmentvideo.height == -1) ? "" : "-" + ytFragmentvideo.height + "p";
            String downloadIds = "";
            if (ytFragmentvideo.videoFile != null) {
                downloadIds += downloadFromUrl(ytFragmentvideo.videoFile.getUrl(), title, filename + "." + ytFragmentvideo.videoFile.getFormat().getExt());
                downloadIds += "-";
            }
            if (ytFragmentvideo.audioFile != null) {
                downloadIds += downloadFromUrl(ytFragmentvideo.audioFile.getUrl(), title, filename + "." + ytFragmentvideo.audioFile.getFormat().getExt());
            }
            if (ytFragmentvideo.audioFile != null)
                cacheDownloadIds(downloadIds);
        });
        mainLayout.addView(button);

    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(context.getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String downloadFromUrl(String url, String title, String Extention) {
        if (url != null && title != null) {
            new DownloadMyFile(context, title, Extention).execute(url);
        }
        return "kainai";
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentClass frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentClass frVideo = new YtFragmentClass();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }

    public static class YtFragmentClass {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }

}
