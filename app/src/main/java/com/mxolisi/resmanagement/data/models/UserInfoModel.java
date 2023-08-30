package com.mxolisi.resmanagement.data.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.mxolisi.resmanagement.data.Resident;
import com.mxolisi.resmanagement.data.UserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UserInfoModel {
    private @NotNull final static MutableLiveData<String> uid = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<String> email = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<String> names = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<String> lastName = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<String> phone = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<Boolean> isAdmin = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private @NotNull final static MutableLiveData<Resident> resident = new MutableLiveData<>();

    private @NotNull final static MutableLiveData<Boolean> isProfileComplete = new MutableLiveData<>();

    private static final DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    private static ListenerRegistration listenerRegistration = null;
    public static void getData() {
        userDoc.get().addOnSuccessListener(snapshot -> {
            UserInfo userInfo = snapshot != null ? snapshot.toObject(UserInfo.class) : null;
            if (userInfo != null) {
                isProfileComplete.postValue(userInfo.getNames() != null &&
                        userInfo.getLastName() != null &&
                        userInfo.getPhone() != null &&
                        userInfo.getImageUrl() != null);

                uid.postValue(userInfo.getUid());
                email.postValue(userInfo.getEmail());
                names.postValue(userInfo.getNames());
                lastName.postValue(userInfo.getLastName());
                phone.postValue(userInfo.getPhone());
                isAdmin.postValue(userInfo.isAdmin());
                imageUrl.postValue(userInfo.getImageUrl());
                resident.postValue(userInfo.getResident());
            } else {
                isProfileComplete.postValue(false);
            }
        });
    }
    public static void registerListener() {
        listenerRegistration = userDoc.addSnapshotListener((snapshot, error) -> {
                    if (error != null) return;

                    UserInfo userInfo = snapshot != null ? snapshot.toObject(UserInfo.class) : null;
                    if (userInfo != null) {
                        isProfileComplete.postValue(userInfo.getNames() != null &&
                                userInfo.getLastName() != null &&
                                userInfo.getPhone() != null &&
                                userInfo.getImageUrl() != null);

                        uid.postValue(userInfo.getUid());
                        email.postValue(userInfo.getEmail());
                        names.postValue(userInfo.getNames());
                        lastName.postValue(userInfo.getLastName());
                        phone.postValue(userInfo.getPhone());
                        isAdmin.postValue(userInfo.isAdmin());
                        imageUrl.postValue(userInfo.getImageUrl());
                        resident.postValue(userInfo.getResident());
                    } else {
                        isProfileComplete.postValue(false);
                    }
                });
    }
    public static void removeListener() {
        if (listenerRegistration != null) listenerRegistration.remove();
    }

    /*public UserInfoModel() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
        listenerRegistration = firestore.collection("users")
                .document(firebaseUser.getUid())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) return;

                    UserInfo userInfo = snapshot != null ? snapshot.toObject(UserInfo.class) : null;
                    this.userInfo.postValue(userInfo);
                    if (userInfo != null) {
                        isProfileComplete.postValue(userInfo.getNames() != null &&
                                userInfo.getLastName() != null &&
                                userInfo.getPhone() != null &&
                                userInfo.getImageUrl() != null);

                        uid.postValue(userInfo.getUid());
                        email.postValue(userInfo.getEmail());
                        names.postValue(userInfo.getNames());
                        lastName.postValue(userInfo.getLastName());
                        phone.postValue(userInfo.getPhone());
                        isAdmin.postValue(userInfo.isAdmin());
                        imageUrl.postValue(userInfo.getImageUrl());
                    } else {
                        isProfileComplete.postValue(false);
                    }
                });
    }*/


    @NotNull
    public static LiveData<String> getUid() {
        return uid;
    }

    @NotNull
    public static LiveData<String> getEmail() {
        return email;
    }

    @NotNull
    public static LiveData<String> getNames() {
        return names;
    }

    @NotNull
    public static LiveData<String> getLastName() {
        return lastName;
    }

    @NotNull
    public static LiveData<String> getPhone() {
        return phone;
    }

    @NotNull
    public static LiveData<Boolean> getIsAdmin() {
        return isAdmin;
    }

    @NotNull
    public static LiveData<String> getImageUrl() {
        return imageUrl;
    }

    @NotNull
    public static LiveData<Boolean> getIsProfileComplete() {
        return isProfileComplete;
    }

    @NotNull
    public static LiveData<Resident> getResident() {
        return resident;
    }
}