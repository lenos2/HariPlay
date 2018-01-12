package zw.co.hariplay.hariplay.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by User on 7/29/2017.
 */

public class Video extends SugarRecord<Video> implements Parcelable  {

    private String caption;
    private String date_created;
    private String video_path;
    private String video_id;
    private String user_id;
    private String tags;
    private List<Like> likes;
    private List<Comment> comments;


    public Video() {

    }

    public Video(String caption, String date_created, String video_path, String video_id,
                 String user_id, String tags, List<Like> likes, List<Comment> comments) {
        this.caption = caption;
        this.date_created = date_created;
        this.video_path = video_path;
        this.video_id = video_id;
        this.user_id = user_id;
        this.tags = tags;
        this.likes = likes;
        this.comments = comments;
    }

    protected Video(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        video_path = in.readString();
        video_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(date_created);
        dest.writeString(video_path);
        dest.writeString(video_id);
        dest.writeString(user_id);
        dest.writeString(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static Creator<Video> getCREATOR() {
        return CREATOR;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", video_path='" + video_path + '\'' +
                ", video_id='" + video_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                ", likes=" + likes +
                '}';
    }
}
