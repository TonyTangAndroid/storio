package com.pushtorefresh.storio.contentresolver.integration;

import android.database.Cursor;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;

import static org.assertj.android.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class QueryTest extends IntegrationTest {

    @Test
    public void getCursorExecuteAsBlocking() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(testSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        Cursor cursor = storIOContentResolver
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        Assertions.assertThat(testItemToInsert.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }
}
