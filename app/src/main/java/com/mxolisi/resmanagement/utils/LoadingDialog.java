package com.mxolisi.resmanagement.utils;

import static com.mxolisi.resmanagement.utils.Utils.runThisAfter;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.databinding.LoadingDialogErrorBinding;
import com.mxolisi.resmanagement.databinding.LoadingDialogProgressBinding;
import com.mxolisi.resmanagement.databinding.LoadingDialogSuccessBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class LoadingDialog {
    private final LoadingDialogProgressBinding progressBinding;
    private final LoadingDialogErrorBinding errorBinding;
    private final LoadingDialogSuccessBinding successBinding;
    private static Dialog dialog;
    private ShowingView showingView = ShowingView.NO_VIEW;

    private enum ShowingView {
        NO_VIEW, PROGRESS, SUCCESS, ERROR
    }

    @NotNull Activity activity;
    public LoadingDialog(@NotNull Activity activity) {
        this.activity = activity;
        progressBinding = LoadingDialogProgressBinding.inflate(activity.getLayoutInflater());
        errorBinding = LoadingDialogErrorBinding.inflate(activity.getLayoutInflater());
        successBinding = LoadingDialogSuccessBinding.inflate(activity.getLayoutInflater());

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(progressBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    /**
     * A method to set a message to the current showing view.
     * @param message The message that will be displayed */
    public void setText(String message) {
        if (message != null && !message.isEmpty()){
            switch (showingView) {
                case PROGRESS: {
                    progressBinding.progressText.setText(message);
                    break;
                }
                case SUCCESS: {
                    successBinding.successText.setText(message);
                    break;
                }
                case ERROR: {
                    errorBinding.errorText.setText(message);
                    break;
                }
            }
        }
    }

    /**
     * A method to show the loading progress view. The progress view is not cancellable if onCancel is null.
     * @param message A message that will be displayed.
     * @param onCancel A void function that will run on cancel of the progress.*/
    public void showProgress(@Nullable String message, @Nullable Supplier<Boolean> onCancel) {
        showingView = ShowingView.PROGRESS;
        dialog.setContentView(progressBinding.getRoot());

        if (message != null) progressBinding.progressText.setText(message);
        progressBinding.loadingAnim.playAnimation();

        if (onCancel != null) {
            progressBinding.cancelButton.setOnClickListener(v -> {
                if (onCancel.get()) {
                    showingView = ShowingView.NO_VIEW;
                    dialog.dismiss();
                    Toast.makeText(progressBinding.getRoot().getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                }
            });
            progressBinding.cancelButton.setVisibility(View.VISIBLE);
        }

        if (!dialog.isShowing()) dialog.show();
    }

    /**
     * A method to show the success view.
     * @param message Success message that will be displayed.
     * @param onSuccess A void function that will run after success.*/
    public void showSuccess(@Nullable String message,  @Nullable Consumer<Dialog> onSuccess) {
        showingView = ShowingView.SUCCESS;
        dialog.setContentView(successBinding.getRoot());

        if (message != null) successBinding.successText.setText(message);
        successBinding.successAnim.playAnimation();

        if (!dialog.isShowing()) dialog.show();

        runThisAfter(()->{
            showingView = ShowingView.NO_VIEW;
            dialog.dismiss();
            if (onSuccess != null)
                onSuccess.accept(dialog);
        }, 1);
    }

    /**
     * A method to show the error view.
     * @param message Success message that will be displayed.
     * @param onError A void function that will run after error.*/
    public void showError(@Nullable String message, @Nullable Runnable onCancel, @Nullable Consumer<Dialog> onError) {
        showingView = ShowingView.ERROR;
        dialog.setContentView(errorBinding.getRoot());

        if (message != null) errorBinding.errorText.setText(message);
        errorBinding.errorAnim.playAnimation();
        if (!dialog.isShowing()) dialog.show();

        Runnable runnable = ()-> {
            showingView = ShowingView.NO_VIEW;
            dialog.dismiss();
            if (onError != null)
                onError.accept(dialog);
        };

        if (onCancel == null) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(runnable, 3000);
        } else {
            errorBinding.cancelButton.setOnClickListener(v -> runnable.run());
            errorBinding.cancelButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * A method to cancel the loading dialog view.
     * @param onCancel A Runnable that will run on cancel, if not null.*/
    public void cancel(@Nullable Runnable onCancel) {
        dialog.dismiss();
        if (onCancel != null) {
            onCancel.run();
        }
    }
}
