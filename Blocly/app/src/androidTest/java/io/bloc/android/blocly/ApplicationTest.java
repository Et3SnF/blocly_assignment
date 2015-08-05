package io.bloc.android.blocly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;

import com.nostra13.universalimageloader.core.ImageLoader;

import io.bloc.android.blocly.api.model.database.DatabaseOpenHelper;
import io.bloc.android.blocly.api.model.database.table.RssFeedTable;
import io.bloc.android.blocly.api.model.database.table.RssItemTable;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<BloclyApplication> {

    public ApplicationTest() {
        super(BloclyApplication.class);
    }

    // This setUp() method is executed before every test!
    // This method is intended to reduce unnecessary duplication of code

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(new RenamingDelegatingContext(getContext(), "test_"));
        createApplication();
    }

    // ----- Testing Methods ----- //

    public void testApplicationHasDataSource() {
        BloclyApplication application = getApplication();
        application.onCreate();

        // If this becomes null, test fails
        assertNotNull(application.getDataSource());
    }

    public void testApplicationImageLoaderInitialization() {

        BloclyApplication application = getApplication();
        application.onCreate();

        // If false, the test fails
        assertTrue(ImageLoader.getInstance().isInited());

    }

    // ----- Checkbox Tests ----- //

    public void testFavoriteInsertedIntoRow() {

        // When user presses the checkbox, make it function
        // If checked, store as 1 in database RssItemTable
        // If unchecked, (by default or after checked), change value to 0 for RssItemTable
        // get the RowId of that particular item and insert it into RssItemTable.
        // if unchecked, get the RowID and insert new value into the RssItemTable.

        RssFeedTable rssFeedTable = new RssFeedTable();
        RssItemTable rssItemTable = new RssItemTable();
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(getContext(), rssFeedTable, rssItemTable);
        SQLiteDatabase readDatabase = databaseOpenHelper.getReadableDatabase();

        Cursor cursor = readDatabase.query(true, "rss_items", new String[]{"is_favorite"},
                "is_favorite = ?", new String[]{"1"}, null, null, null, null);

        if(cursor == null) {
            assertNotNull(cursor);
        }

        int i = 0;

        while(cursor.moveToNext()) {
            i++;
        }

        if(i > 0) {
            assertTrue(true);
        }
        else if (!cursor.moveToNext() && i == 0) {
            assertTrue(false);
        }

    }
}