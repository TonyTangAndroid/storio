package com.pushtorefresh.storio.sqlite.operations.get;

import android.database.Cursor;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.TestUtils;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedGetListOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldGetListOfObjectsByQueryWithoutTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByQueryWithoutTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithoutTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyRawQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithoutTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithoutTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .withGetResolver(getStub.getResolver)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemsObservable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldGetListOfObjectsByQueryWithTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByQueryWithTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.query)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyQueryBehavior(testItemsObservable);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithTypeMappingBlocking() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final List<TestItem> testItems = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .executeAsBlocking();

            getStub.verifyRawQueryBehavior(testItems);
        }

        @Test
        public void shouldGetListOfObjectsByRawQueryWithTypeMappingAsObservable() {
            final GetObjectsStub getStub = GetObjectsStub.newInstanceWithTypeMapping();

            final Observable<List<TestItem>> testItemsObservable = getStub.storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(getStub.rawQuery)
                    .prepare()
                    .createObservable()
                    .take(1);

            getStub.verifyRawQueryBehavior(testItemsObservable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final PreparedGet<List<TestItem>> preparedGet = storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().table("test_table").build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                fail();
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                TestUtils.checkException(expected, IllegalStateException.class);
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final PreparedGet<List<TestItem>> preparedGet = storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare();

            try {
                preparedGet.executeAsBlocking();
                fail();
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                TestUtils.checkException(expected, IllegalStateException.class);
            }

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);
            when(storIOSQLite.observeChangesInTables(any(Set.class)))
                    .thenReturn(Observable.empty());

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(Query.builder().table("test_table").build())
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            TestUtils.checkException(testSubscriber, StorIOException.class, IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).query(any(Query.class));
            verify(storIOSQLite).observeChangesInTables(anySet());
            verifyNoMoreInteractions(storIOSQLite, internal);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAccessingDbWithRawQueryAsObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);
            final StorIOSQLite.Internal internal = mock(StorIOSQLite.Internal.class);

            when(storIOSQLite.get()).thenReturn(new PreparedGet.Builder(storIOSQLite));
            when(storIOSQLite.internal()).thenReturn(internal);

            final TestSubscriber<List<TestItem>> testSubscriber = new TestSubscriber<List<TestItem>>();

            storIOSQLite
                    .get()
                    .listOfObjects(TestItem.class)
                    .withQuery(RawQuery.builder().query("test query").build())
                    .prepare()
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            TestUtils.checkException(testSubscriber, StorIOException.class, IllegalStateException.class);

            verify(storIOSQLite).get();
            verify(storIOSQLite).internal();
            verify(internal).typeMapping(TestItem.class);
            verify(internal, never()).rawQuery(any(RawQuery.class));
            verifyNoMoreInteractions(storIOSQLite, internal);
        }
    }

    // Because we run tests on this class with Enclosed runner, we need to wrap other tests into class
    public static class OtherTests {

        @Test
        public void completeBuilderShouldThrowExceptionIfNoQueryWasSet() {
            PreparedGetListOfObjects.CompleteBuilder completeBuilder = new PreparedGetListOfObjects.Builder<Object>(mock(StorIOSQLite.class), Object.class)
                    .withQuery(Query.builder().table("test_table").build()); // We will null it later;

            completeBuilder.query = null;

            try {
                completeBuilder.prepare();
                fail();
            } catch (IllegalStateException expected) {
                assertEquals("Please specify Query or RawQuery", expected.getMessage());
            }
        }

        @Test
        public void executeAsBlockingShouldThrowExceptionIfNoQueryWasSet() {
            //noinspection unchecked,ConstantConditions
            PreparedGetListOfObjects<Object> preparedGetListOfObjects
                    = new PreparedGetListOfObjects<Object>(
                    mock(StorIOSQLite.class),
                    Object.class,
                    (Query) null,
                    (GetResolver<Object>) mock(GetResolver.class)
            );

            try {
                preparedGetListOfObjects.executeAsBlocking();
                fail();
            } catch (StorIOException expected) {
                IllegalStateException cause = (IllegalStateException) expected.getCause();
                assertEquals("Please specify query", cause.getMessage());
            }
        }

        @Test
        public void createObservableShouldThrowExceptionIfNoQueryWasSet() {
            //noinspection unchecked,ConstantConditions
            PreparedGetListOfObjects<Object> preparedGetListOfObjects
                    = new PreparedGetListOfObjects<Object>(
                    mock(StorIOSQLite.class),
                    Object.class,
                    (Query) null,
                    (GetResolver<Object>) mock(GetResolver.class)
            );

            try {
                preparedGetListOfObjects.createObservable();
                fail();
            } catch (IllegalStateException expected) {
                assertEquals("Please specify query", expected.getMessage());
            }
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForExecuteAsBlocking() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetListOfObjects<Object> preparedGetListOfObjects =
                    new PreparedGetListOfObjects<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").build(),
                            getResolver
                    );

            try {
                preparedGetListOfObjects.executeAsBlocking();
                fail();
            } catch (StorIOException exception) {
                IllegalStateException cause = (IllegalStateException) exception.getCause();
                assertEquals("test exception", cause.getMessage());

                // Cursor must be closed in case of exception
                verify(cursor).close();

                verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
                verify(getResolver).mapFromCursor(cursor);
                verify(cursor).getCount();
                verify(cursor).moveToNext();

                verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
            }
        }

        @Test
        public void cursorMustBeClosedInCaseOfExceptionForObservable() {
            final StorIOSQLite storIOSQLite = mock(StorIOSQLite.class);

            when(storIOSQLite.observeChangesInTables(eq(singleton("test_table"))))
                    .thenReturn(Observable.<Changes>empty());

            //noinspection unchecked
            final GetResolver<Object> getResolver = mock(GetResolver.class);

            final Cursor cursor = mock(Cursor.class);

            when(cursor.getCount()).thenReturn(10);

            when(cursor.moveToNext()).thenReturn(true);

            when(getResolver.performGet(eq(storIOSQLite), any(Query.class)))
                    .thenReturn(cursor);

            when(getResolver.mapFromCursor(cursor))
                    .thenThrow(new IllegalStateException("test exception"));

            PreparedGetListOfObjects<Object> preparedGetListOfObjects =
                    new PreparedGetListOfObjects<Object>(
                            storIOSQLite,
                            Object.class,
                            Query.builder().table("test_table").build(),
                            getResolver
                    );

            final TestSubscriber<List<Object>> testSubscriber = new TestSubscriber<List<Object>>();

            preparedGetListOfObjects
                    .createObservable()
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();

            testSubscriber.assertNoValues();
            testSubscriber.assertError(StorIOException.class);

            StorIOException storIOException = (StorIOException) testSubscriber.getOnErrorEvents().get(0);

            IllegalStateException cause = (IllegalStateException) storIOException.getCause();
            assertEquals("test exception", cause.getMessage());

            // Cursor must be closed in case of exception
            verify(cursor).close();

            //noinspection unchecked
            verify(storIOSQLite).observeChangesInTables(anySet());
            verify(getResolver).performGet(eq(storIOSQLite), any(Query.class));
            verify(getResolver).mapFromCursor(cursor);
            verify(cursor).getCount();
            verify(cursor).moveToNext();

            verifyNoMoreInteractions(storIOSQLite, getResolver, cursor);
        }
    }
}
