package zw.co.hariplay.hariplay.Share;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.snatik.storage.Storage;

import java.io.File;

import zw.co.hariplay.hariplay.Home.HomeActivity;
import zw.co.hariplay.hariplay.Profile.AccountSettingsActivity;
import zw.co.hariplay.hariplay.R;
import zw.co.hariplay.hariplay.Utils.Permissions;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";

    //constant
    private static final int VIDEO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int VIDEO_REQUEST_CODE = 5;
    //private static final int RESULT_OK = 1;
    //private static final int RESULT_CANCELED = 0;
    View v;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started.");


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v= view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //startRec();
    }

    public void startRec() {

        Log.d(TAG, "onClick: launching camera.");
        if (((ShareActivity) getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])) {
            Log.d(TAG, "onClick: starting camera");
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(cameraIntent, VIDEO_REQUEST_CODE);
        } else {
            Intent intent = new Intent(getActivity(), ShareActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    public void startRecHome() {

        Log.d(TAG, "onClick: starting camera");

        // init
        Storage storage = new Storage(v.getContext());

        // get external storage
        String path = storage.getExternalStorageDirectory();

        // new dir
        String newDir = path + File.separator + "HariPlay";
        storage.createDirectory(newDir);

        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newDir);
        startActivityForResult(cameraIntent, VIDEO_REQUEST_CODE);
        Log.d(TAG, "onClick: launching camera.");


    }

    private boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: done taking a video.");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");

            if (resultCode == RESULT_OK) {
                Toast.makeText(v.getContext(), "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(v.getContext(), "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(v.getContext(), "Failed to record video" + requestCode + data.getData(),
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}



