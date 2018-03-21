package com.estyle.teabaike.glide;


import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class TeaBaikeGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context,
                    DiskCache.Factory.DEFAULT_DISK_CACHE_DIR,
                    DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE));
        }
    }

}
