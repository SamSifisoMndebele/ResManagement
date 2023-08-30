package com.mxolisi.resmanagement.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UserInfo implements Parcelable {
    private @NotNull String uid = "";
    private @NotNull String email = "";
    private @Nullable String names = null;
    private @Nullable String lastName = null;
    private @Nullable String phone = null;
    private boolean admin = false;
    private @Nullable String imageUrl = null;

    private @Nullable Resident resident = null;

    public UserInfo() { }

    public UserInfo(@NotNull String uid, @NotNull String email, @Nullable String names, @Nullable String lastName, @Nullable String phone, boolean admin, @Nullable String imageUrl, @Nullable Resident resident) {
        this.uid = uid;
        this.email = email;
        this.names = (names == null || names.isEmpty()) ? null : names;
        this.lastName = (lastName == null || lastName.isEmpty()) ? null : lastName;
        this.phone = (phone == null || phone.isEmpty()) ? null : phone;
        this.admin = admin;
        this.imageUrl = (imageUrl == null || imageUrl.isEmpty()) ? null : imageUrl;
        this.resident = resident;
    }

    protected UserInfo(@NonNull Parcel in) {
        uid = Objects.requireNonNull(in.readString());
        email = Objects.requireNonNull(in.readString());
        names = in.readString();
        lastName = in.readString();
        phone = in.readString();
        admin = in.readByte() != 0;
        imageUrl = in.readString();
        resident = in.readParcelable(Resident.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(names);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeByte((byte) (admin ? 1 : 0));
        dest.writeString(imageUrl);
        dest.writeParcelable(resident, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @NotNull
    public String getUid() {
        return uid;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getNames() {
        return names;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    public Resident getResident() {
        return resident;
    }
}
