package zw.co.hariplay.hariplay.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import zw.co.hariplay.hariplay.Profile.AccountSettingsActivity;
import zw.co.hariplay.hariplay.R;
import zw.co.hariplay.hariplay.Utils.FilePaths;
import zw.co.hariplay.hariplay.Utils.FileSearch;
import zw.co.hariplay.hariplay.Utils.GridImageAdapter;
import zw.co.hariplay.hariplay.Utils.GridVideoAdapter;

/**
 * Created by User on 5/28/2017.
 */

public class GalleryFragment2 extends Fragment {
    private static final String TAG = "GalleryFragment";


    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private GridView gridView;
    //private ImageView galleryImage;
    //private SimpleExoPlayerView playerView;
    //private SimpleExoPlayer player;
    private VideoView videoView;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    private View v;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;
    private String mSelectedVideo;
    private String videoURL;
    private int checked = 0;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;
    Uri uri;

    SpinnerInteractionListener interactionListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery2, container, false);
        v = view;
        //galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        //playerView = (SimpleExoPlayerView) view.findViewById(R.id.video_view);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        gridView = (GridView) view.findViewById(R.id.gridView);
        directorySpinner = (Spinner) view.findViewById(R.id.spinnerDirectory);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        interactionListener = new SpinnerInteractionListener();
        Log.d(TAG, "onCreateView: started.");

        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });


        TextView nextScreen = (TextView) view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                if(isRootTask()){
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_video), mSelectedVideo);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });

        init();

        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    ArrayList<String> directoryNames = new ArrayList<>();
    FilePaths filePaths = new FilePaths();
    ArrayAdapter<String> adapter;

    private void setUpDirectories(String dir){

        directories.clear();
        //check for other folders indide "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(dir) != null) {
            directories = FileSearch.getDirectoryPaths(dir);
            //Toast.makeText(v.getContext(),directories.toString(),Toast.LENGTH_SHORT).show();
        }
        //directories.add(filePaths.CAMERA);

        directoryNames.clear();
        for (int i = 0; i < directories.size(); i++) {
            Log.d(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }
    }

    private void init(){
        setUpDirectories(filePaths.THE_DIR);
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnTouchListener(interactionListener);
        directorySpinner.setOnItemSelectedListener(interactionListener);
    }


    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> videoURLs = FileSearch.getVideoPaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridVideoAdapter adapter = new GridVideoAdapter(getActivity(), R.layout.layout_grid_videoview, mAppend, videoURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            setVideo(videoURLs.get(0), mAppend);
            mSelectedVideo = videoURLs.get(0);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + videoURLs.get(position));

                setVideo(videoURLs.get(position), mAppend);
                mSelectedImage = videoURLs.get(position);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (Util.SDK_INT > 23 || videoURL != null) {
            initializePlayer();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        //hideSystemUi();
        /*if ((Util.SDK_INT <= 23 || player == null || videoURL != null)) {
            initializePlayer();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        /*if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }*/
    }


    private void setVideo(String videoURL, String append){
        Log.d(TAG, "setImage: setting image");
        //uri = Uri.parse(append+videoURL);
        //uri = Uri.parse("file://storage/sdcard1/WhatsApp/Media/WhatsApp Video/VID-20180108-WA0007.mp4");

        mSelectedVideo =videoURL;
        videoView.setVideoPath(videoURL);
        //videoView.setZOrderOnTop(true);
        videoView.start();
        //Adding Media Controls
        MediaController vidControl = new MediaController(v.getContext());
        vidControl.setAnchorView(videoView);
        videoView.setMediaController(vidControl);
        //initializePlayer();
    }

    /*private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(v.getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);


        player.setPlayWhenReady(playWhenReady);

        player.seekTo(currentWindow, playbackPosition);

        //Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }*/
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            userSelect = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                // Your selection handling code here
                userSelect = false;

                Log.d(TAG, "onItemClick: selected: " + directories.get(position));
                 //setup our image grid for the directory chosen
                    if (!FileSearch.getVideoPaths(directories.get(position)).isEmpty())
                        setupGridView(directories.get(position));

                    setUpDirectories(directories.get(position));
                    //adapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }

}































