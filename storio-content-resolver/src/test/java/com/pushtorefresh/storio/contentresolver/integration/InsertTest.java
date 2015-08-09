package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class InsertTest extends IntegrationTest {

    @Test
    public void insertContentValues() {
        TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOContentResolver
                .observeChangesOfUri(TestItem.CONTENT_URI)
                .take(1)
                .subscribe(testSubscriber);

        TestItem testItem = TestItem.create(null, "value");
        ContentValues cv = testItem.toContentValues();

        PutResult putResult = storIOContentResolver
                .put()
                .contentValues(cv)
                .withPutResolver(new PutResolver<ContentValues>() {
                    @NonNull
                    @Override
                    public PutResult performPut(@NonNull StorIOContentResolver storIOContentResolver, @NonNull ContentValues object) {
                        return PutResult.newInsertResult(storIOContentResolver.internal().insert(
                                        InsertQuery.builder()
                                                .uri(TestItem.CONTENT_URI)
                                                .build(),
                                        object),
                                TestItem.CONTENT_URI
                        );
                    }
                })
                .prepare()
                .executeAsBlocking();

        assertThat(putResult.wasInserted()).isTrue();

        Cursor cursor = contentResolver.query(TestItem.CONTENT_URI, null, null, null, null);

        assertThat(cursor.getCount()).isEqualTo(1);

        cursor.moveToFirst();

        assertThat(testItem.equalsWithoutId(TestItem.fromCursor(cursor))).isTrue();

        cursor.close();

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValue(Changes.newInstance(TestItem.CONTENT_URI));
    }
}
