package io.bloc.android.blocly.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Model implements Parcelable {

    private final long rowId;

    public Model(long rowId) {
        this.rowId = rowId;
    }

    public long getRowId() {
        return rowId;
    }

    //---- Parcelable Stuff ---- //

    public Model(Parcel in) {
        rowId = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(rowId);
    }
}
