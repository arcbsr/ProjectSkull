package com.hope.main_ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.engine.CropFileEngine;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;

public class CropEngine implements CropFileEngine {

    @Override
    public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
        UCrop.Options options = buildOptions(fragment.getContext());
        UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
        uCrop.withOptions(options);
        uCrop.setImageEngine(new UCropImageEngine() {
            @Override
            public void loadImage(Context context, String url, ImageView imageView) {
                if (!assertValidRequest(context)) {
                    return;
                }
                Glide.with(context).load(url).override(180, 180).into(imageView);
            }

            @Override
            public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (call != null) {
                            call.onCall(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (call != null) {
                            call.onCall(null);
                        }
                    }
                });
            }
        });
        uCrop.start(fragment.requireActivity(), fragment, requestCode);
    }


    public int stringCount(String str) {
        int count = 0;
        char[] chars = new char[str.length()];
        for (char aChar : chars) {
            if (Character.isLetter(aChar)) {
                count++;
            }
        }
        return count;
    }
    private UCrop.Options buildOptions(Context context) {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(true);
        options.withAspectRatio(1, 1);
        options.isCropDragSmoothToCenter(false);
        options.isForbidSkipMultipleCrop(true);
        options.setMaxScaleMultiplier(100);
        options.setCropOutputPathDir(getSandboxCameraOutputPath(context));
//        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
//            SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
//            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
//            int statusBarColor = mainStyle.getStatusBarColor();
//            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
//            if (StyleUtils.checkStyleValidity(statusBarColor)) {
//                options.setStatusBarColor(statusBarColor);
//                options.setToolbarColor(statusBarColor);
//            } else {
//                options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//                options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//            }
//            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
//            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
//                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
//            } else {
//                options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
//            }
//        } else {
//            options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//            options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//            options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
//        }
        return options;
    }


    /**
     * 创建相机自定义输出目录
     *
     * @return
     */
    private String getSandboxCameraOutputPath(Context context) {
        File externalFilesDir = context.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper) {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity) {
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }
}
