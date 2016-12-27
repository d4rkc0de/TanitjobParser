package nachos.com.tanitjobparser.application;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by abdelnacer on 9/26/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(MyApplication.this)
                .applicationId(ParseConstants.PARSE_APP_ID)
                .server(ParseConstants.PARSE_SERVER_URL)
                .clientKey(ParseConstants.CLIENT_KEY)
                .build()
        );

    }
}
