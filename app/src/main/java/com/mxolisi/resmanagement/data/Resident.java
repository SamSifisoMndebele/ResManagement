package com.mxolisi.resmanagement.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Resident implements Parcelable {
    private @NotNull String residenceId = "residenceId";
    private @NotNull String floorNumber = "floorNumber";
    private @NotNull String roomNumber = "roomNumber";
    private @Nullable String keyNumber = "keyNumber";

    public Resident() {}

    public Resident(@NotNull String residenceId, @NotNull String floorNumber, @NotNull String roomNumber, @Nullable String keyNumber) {
        this.residenceId = residenceId;
        this.floorNumber = floorNumber;
        this.roomNumber = roomNumber;
        this.keyNumber = keyNumber;
    }

    protected Resident(@NonNull Parcel in) {
        residenceId = Objects.requireNonNull(in.readString());
        floorNumber = Objects.requireNonNull(in.readString());
        roomNumber = Objects.requireNonNull(in.readString());
        keyNumber = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(residenceId);
        dest.writeString(floorNumber);
        dest.writeString(roomNumber);
        dest.writeString(keyNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Resident> CREATOR = new Creator<Resident>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Resident createFromParcel(Parcel in) {
            return new Resident(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Resident[] newArray(int size) {
            return new Resident[size];
        }
    };

    @NotNull
    public String getResidenceId() {
        return residenceId;
    }

    @NotNull
    public String getFloorNumber() {
        return floorNumber;
    }

    @NotNull
    public String getRoomNumber() {
        return roomNumber;
    }

    @Nullable
    public String getKeyNumber() {
        return keyNumber;
    }
}
