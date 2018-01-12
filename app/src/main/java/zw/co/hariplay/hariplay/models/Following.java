package zw.co.hariplay.hariplay.models;

import com.orm.SugarRecord;

/**
 * Created by Leo on 1/12/2018.
 */

public class Following extends SugarRecord<Following> {
    private String mUserID;

    public Following() {
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }
}
