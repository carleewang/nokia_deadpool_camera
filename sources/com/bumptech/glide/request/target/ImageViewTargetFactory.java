package com.bumptech.glide.request.target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;

public class ImageViewTargetFactory {
    public <Z> Target<Z> buildTarget(ImageView view, Class<Z> clazz) {
        if (GlideDrawable.class.isAssignableFrom(clazz)) {
            return new GlideDrawableImageViewTarget(view);
        }
        if (Bitmap.class.equals(clazz)) {
            return new BitmapImageViewTarget(view);
        }
        if (Drawable.class.isAssignableFrom(clazz)) {
            return new DrawableImageViewTarget(view);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unhandled class: ");
        stringBuilder.append(clazz);
        stringBuilder.append(", try .as*(Class).transcode(ResourceTranscoder)");
        throw new IllegalArgumentException(stringBuilder.toString());
    }
}