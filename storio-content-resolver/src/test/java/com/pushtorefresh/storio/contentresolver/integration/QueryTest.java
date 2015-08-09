package com.pushtorefresh.storio.contentresolver.integration;

import android.database.Cursor;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

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

        Assertions.assertThat(cursor).hasCount(1);

        cursor.moveToFirst();

        assertThat(testItemToInsert.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }

    @Test
    public void getListOfObjectsExecuteAsBlocking() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(testSubscriber);

        TestItem testItemToInsert = TestItem.create(null, "value");
        contentResolver.insert(TestItem.CONTENT_URI, testItemToInsert.toContentValues());

        List<TestItem> list = storIOContentResolver
                .get()
                .listOfObjects(TestItem.class)
                .withQuery(Query.builder()
                        .uri(TestItem.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(list).hasSize(1);

        assertThat(testItemToInsert.equalsWithoutId(list.get(0))).isTrue();

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }
}
