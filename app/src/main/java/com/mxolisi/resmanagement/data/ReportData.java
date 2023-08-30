package com.mxolisi.resmanagement.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReportData implements Parcelable {
    private @NotNull String id = "";
    private @NotNull String title = "";
    private @NotNull String location = "";
    private @NotNull String description = "";
    private @Nullable UserInfo userInfo = null;
    private @Nullable String imageUrl = null;
    private long viewsCount = 0;
    private @NotNull Timestamp timestamp = Timestamp.now();

    @Override
    public String toString() {
        return "ReportData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", userInfo=" + userInfo +
                ", imageUrl='" + imageUrl + '\'' +
                ", viewsCount=" + viewsCount +
                ", timestamp=" + timestamp +
                '}';
    }

    public ReportData() { }

    public ReportData(@NotNull String id, @NotNull String title, @NotNull String location, @NotNull String description, @Nullable UserInfo userInfo) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.userInfo = userInfo;
    }

    public ReportData(@NotNull String id, @NotNull String title, @NotNull String location, @NotNull String description, @Nullable UserInfo userInfo, @Nullable String imageUrl) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.userInfo = userInfo;
        this.imageUrl = imageUrl;
    }

    public ReportData(@NotNull String id, @NotNull String title, @NotNull String location, @NotNull String description, @Nullable UserInfo userInfo, @Nullable String imageUrl, long viewsCount, @NotNull Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.description = description;
        this.userInfo = userInfo;
        this.imageUrl = imageUrl;
        this.viewsCount = viewsCount;
        this.timestamp = timestamp;
    }

    protected ReportData(@NonNull Parcel in) {
        id = in.readString();
        title = in.readString();
        location = in.readString();
        description = in.readString();
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        imageUrl = in.readString();
        viewsCount = in.readLong();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(description);
        dest.writeParcelable(userInfo, flags);
        dest.writeString(imageUrl);
        dest.writeLong(viewsCount);
        dest.writeParcelable(timestamp, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReportData> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public ReportData createFromParcel(Parcel in) {
            return new ReportData(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public ReportData[] newArray(int size) {
            return new ReportData[size];
        }
    };

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public String getLocation() {
        return location;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @Nullable
    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    @NotNull
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
