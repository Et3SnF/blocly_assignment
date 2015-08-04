package io.bloc.android.blocly.api.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GetFeedsNetworkRequest extends NetworkRequest {

    String[] feedsUrls;

    public GetFeedsNetworkRequest(String... feedsUrls) {
        this.feedsUrls = feedsUrls;
    }

    @Override
    public Object performRequest() {

        for(String feedUrlString : feedsUrls) {

            InputStream inputStream = openStream(feedUrlString);

            if(inputStream == null) {
                return null;
            }

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();

                ArrayList<String> test = new ArrayList<String>();
                int lineNums = 0;

                while(line != null) {
                    Log.v(getClass().getSimpleName(), "Line " + (lineNums+1) + ": " + line);
                    test.add(line);
                    lineNums++;
                    line = bufferedReader.readLine();
                }

                // Count number of items in feed. Split into array based on <item> tags

                String[] items = test.get(1).split("<item>");
                Log.v(getClass().getSimpleName(), "Number of items in feed: " + items.length);

                bufferedReader.close();
            }
            catch(IOException e) {
                e.printStackTrace();
                setErrorCode(ERROR_IO);
                return null;
            }

        }

        return null;

    }

}
