package io.bloc.android.blocly;

import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;

import com.nostra13.universalimageloader.core.ImageLoader;

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
}