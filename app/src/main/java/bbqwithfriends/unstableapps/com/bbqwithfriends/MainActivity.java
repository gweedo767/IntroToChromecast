package bbqwithfriends.unstableapps.com.bbqwithfriends;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.media.MediaRouter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.CastDevice;
import com.google.sample.castcompanionlibrary.cast.BaseCastManager;
import com.google.sample.castcompanionlibrary.cast.DataCastManager;
import com.google.sample.castcompanionlibrary.cast.callbacks.DataCastConsumerImpl;
import com.google.sample.castcompanionlibrary.cast.callbacks.IDataCastConsumer;


public class MainActivity extends ActionBarActivity {
    private static final String APP_ID = "1474497976100304";
    private static final String APP_NAMESPACE = "ua_picturecast";
    private static String CHROMECAST_NAMESPACE;
    private static final String PROPERTY_ID = "UA-51053412-1";

    private static String CHROMECAST_APPLICATION_ID;

    private static DataCastManager mCastMgr = null;
    private DataCastManager mCastManager;
    private IDataCastConsumer mCastConsumer;

    private MenuItem mediaRouteMenuItem;

    private Button btnViewMyPhotos;
    private Button btnViewFriendsPhotos;
    private Button btnViewPhotoScores;
    private Button btnIgnoreTheEdgeTest;
    private Button btnBigIsBad;
    private Button btnSmallIsGood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseCastManager.checkGooglePlayServices(this);

        btnViewMyPhotos = (Button)findViewById(R.id.btnViewMyPhotos);
        btnViewFriendsPhotos = (Button)findViewById(R.id.btnViewFriendsPhotos);
        btnViewPhotoScores = (Button)findViewById(R.id.btnViewPhotoScores);
        btnIgnoreTheEdgeTest = (Button)findViewById(R.id.btnIgnoreTheEdge);
        btnBigIsBad = (Button)findViewById(R.id.btnBigIsBad);
        btnSmallIsGood = (Button)findViewById(R.id.btnSmallIsGood);

        btnViewMyPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("myPhotos");
            }
        });
        btnViewFriendsPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("friendsPhotos");
            }
        });
        btnViewPhotoScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("scoreBoard");
            }
        });
        btnIgnoreTheEdgeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("ignoreTheEdgeTest");
            }
        });
        btnBigIsBad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("bigIsBadTest");
            }
        });
        btnSmallIsGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage("smallIsGoodTest");
            }
        });

        CHROMECAST_APPLICATION_ID = getString(R.string.chromecast_app_id);
        CHROMECAST_NAMESPACE = getString(R.string.namespace);

        mCastManager = getCastManager(this);

        mCastConsumer = new DataCastConsumerImpl() {
            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata, String applicationStatus,
                                               String sessionId, boolean wasLaunched) {
                Toast.makeText(getBaseContext(), "The application is connected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApplicationDisconnected(int errorCode) {
                Toast.makeText(getBaseContext(), "The application is disconnected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int resourceId, int statusCode) {
            }

            @Override
            public void onConnectionSuspended(int cause) {
            }

            @Override
            public void onConnectivityRecovered() {
            }

            @Override
            public void onCastDeviceDetected(final MediaRouter.RouteInfo info) {
                Toast.makeText(getBaseContext(), "I see a Chromecast!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
                Toast.makeText(getBaseContext(), "Received: " + message, Toast.LENGTH_SHORT).show();
            }
        };
        mCastManager.addDataCastConsumer(mCastConsumer);
        mCastManager.reconnectSessionIfPossible(this, false, 5);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start media router discovery
        mCastManager = getCastManager(this);
        if (null != mCastManager) {
            mCastManager.addDataCastConsumer(mCastConsumer);
            mCastManager.incrementUiCounter();
        }
    }

    @Override
    protected void onPause() {
        mCastManager.decrementUiCounter();
        mCastManager.removeDataCastConsumer(mCastConsumer);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (null != mCastManager) {
            mCastManager.clearContext(this);
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mediaRouteMenuItem = mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public static DataCastManager getCastManager(Context context) {
        if (null == mCastMgr) {
            mCastMgr = DataCastManager.initialize(context, CHROMECAST_APPLICATION_ID,
                    CHROMECAST_NAMESPACE);
            mCastMgr.enableFeatures(
                    DataCastManager.FEATURE_NOTIFICATION |
                            DataCastManager.FEATURE_LOCKSCREEN |
                            DataCastManager.FEATURE_DEBUGGING);
        }
        mCastMgr.setContext(context);
        mCastMgr.setStopOnDisconnect(false);
        return mCastMgr;
    }

    public void sendMessage(String message) {
        if (mCastManager.isConnected()) {
            try {
                mCastManager.sendDataMessage(message, getString(R.string.namespace));
            } catch (Exception e) {
                //do something because it all went wrong man
            }
        } else {
            Toast.makeText(MainActivity.this, "Please connect to your Chromecast first", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
