package zw.co.hariplay.hariplay.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zw.co.hariplay.hariplay.R;
import zw.co.hariplay.hariplay.Utils.MainfeedListAdapter;
import zw.co.hariplay.hariplay.Utils.MainfeedListAdapter2;
import zw.co.hariplay.hariplay.models.Comment;
import zw.co.hariplay.hariplay.models.Like;
import zw.co.hariplay.hariplay.models.Photo;
import zw.co.hariplay.hariplay.models.UserAccountSettings;
import zw.co.hariplay.hariplay.models.Video;

/**
 * Created by User on 5/28/2017.
 */

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<Video> mVideos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<Video> mPaginatedVideos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private MainfeedListAdapter2 mAdapter2;
    private int mResults;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mVideos = new ArrayList<>();

        getFollowing();

        return view;
    }

    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                    singleSnapshot.child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //get the photos
                //getPhotos();
                getVideos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getVideos(){
        Log.d(TAG, "getVideos: getting videos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_videos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Video video = new Video();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        video.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        video.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        video.setVideo_id(objectMap.get(getString(R.string.field_video_id)).toString());
                        video.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        video.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        video.setVideo_path(objectMap.get(getString(R.string.field_video_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        video.setComments(comments);
                        mVideos.add(video);
                    }
                    if(count >= mFollowing.size() -1){
                        //display our photos
                        //displayPhotos();
                        displayVideos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayVideos(){
        mPaginatedVideos = new ArrayList<>();
        if(mVideos != null){
            try{
                Collections.sort(mVideos, new Comparator<Video>() {
                    @Override
                    public int compare(Video o1, Video o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mVideos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedVideos.add(mVideos.get(i));
                }

                mAdapter2 = new MainfeedListAdapter2(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedVideos);
                mListView.setAdapter(mAdapter2);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMoreVideos(){
        Log.d(TAG, "displayMoreVideos: displaying more videos");

        try{

            if(mVideos.size() > mResults && mVideos.size() > 0){

                int iterations;
                if(mVideos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMoreVideos: there are greater than 10 more videos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMoreVideos: there is less than 10 more videos");
                    iterations = mVideos.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedVideos.add(mVideos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayVideos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayVideos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if(count >= mFollowing.size() -1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void displayPhotos(){
        mPaginatedPhotos = new ArrayList<>();
        if(mPhotos != null){
            try{
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > mResults && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

}





















