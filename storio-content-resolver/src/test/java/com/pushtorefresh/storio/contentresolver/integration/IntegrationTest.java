package com.pushtorefresh.storio.contentresolver.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public abstract class IntegrationTest {

    @NonNull
    protected StorIOContentResolver storIOContentResolver;

    @Before
    public void setUp() {
        storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(RuntimeEnvironment.application.getContentResolver())
                .build();

        IntegrationContentProvider contentProvider = new IntegrationContentProvider();
        contentProvider.onCreate();

        ShadowContentResolver.registerProvider(IntegrationContentProvider.AUTHORITY, contentProvider);
    }
}
