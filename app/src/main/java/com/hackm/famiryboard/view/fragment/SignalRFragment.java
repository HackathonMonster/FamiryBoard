package com.hackm.famiryboard.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.hackm.famiryboard.model.system.Account;
import com.hackm.famiryboard.model.system.AppConfig;

import java.util.HashMap;
import java.util.Map;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.http.BasicAuthenticationCredentials;
import microsoft.aspnet.signalr.client.http.CookieCredentials;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by shunhosaka on 15/05/09.
 */
public class SignalRFragment extends Fragment {

    private static final String TAG = SignalRFragment.class.getSimpleName();
    private static final String EXTRA_CONNECTION_URL = "extra_signalr_connection_url";
    private static final String EXTRA_PROXY = "extra_signalr_proxy";

    private OnUpdateConnectionListener mListener;
    private HubConnection mConnection;
    private HubProxy mProxy;
    private Logger logger = new Logger() {
        @Override
        public void log(String message, LogLevel level) {
            if (AppConfig.DEBUG) {
                Log.d(TAG, message);
            }
        }
    };

    public static SignalRFragment newInstance(final String signalrProxy, final String connectionUrl) {
        SignalRFragment fragment = new SignalRFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PROXY, signalrProxy);
        args.putString(EXTRA_CONNECTION_URL, connectionUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) return;
        String proxyName = args.getString(EXTRA_PROXY);
        String connectionUrl = args.getString(EXTRA_CONNECTION_URL);
        // Connect to the server
        mConnection = new HubConnection(connectionUrl, "", true, logger);
        Account account = Account.getAccount(getActivity());
        Map<String, String> authorizationHeader = account.getAccountHeader();
        CookieCredentials credentials = new CookieCredentials();
        for (String key : authorizationHeader.keySet()) {
            credentials.addCookie(key, authorizationHeader.get(key));
        }
        mConnection.setCredentials(credentials);
        // Create the hub proxy
        mProxy = mConnection.createHubProxy(proxyName);
        setSubscribe();
        setConnectionListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start the connection
        mConnection.start()
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        if (AppConfig.DEBUG) {
                            Log.d(TAG, "STARTED");
                        }
                        if (mListener != null) {
                            mListener.onConnectionStarted();
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        try {
            mConnection.stop();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnUpdateConnectionListener) {
            mListener = (OnUpdateConnectionListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setConnectionListener() {
        // Subscribe to the error event
        mConnection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                if (AppConfig.DEBUG) {
                    Log.e(TAG, error.toString());
                }
                if (mListener != null) {
                    mListener.onConnectionError();
                }
            }
        });
        // Subscribe to the connected event
        mConnection.connected(new Runnable() {
            @Override
            public void run() {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, "CONNECTED");
                }
                if (mListener != null) {
                    mListener.onConnected();
                }
            }
        });
        // Subscribe to the closed event
        mConnection.closed(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onConnectionClosed();
                }
            }
        });
        /*
        // Subscribe to the all received event
        mConnection.received(new MessageReceivedHandler() {
            @Override
            public void onMessageReceived(JsonElement json) {
                System.out.println("RAW received message: " + json.toString());
            }
        });
        */
    }

    private void setSubscribe() {
        // Proxy Subscriding
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onEcho(String message) {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, message);
                }
                if (mListener != null) {
                    mListener.onMessageReceived(message);
                }
            }
        });
    }

    /**
     * メッセージを送るメソッド
     *
     * @param message
     */
    public void sendMessage(String message) {
        mProxy.invoke("echo", message).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                if (mListener != null) {
                    mListener.onMessageSented();
                }
            }
        });
    }

    public interface OnUpdateConnectionListener {
        //TODO:今回は使わない
        // public void onReceiveMessage(JsonElement message);

        //TODO Add some method
        public void onMessageReceived(String message);

        public void onMessageSented();

        public void onConnectionStarted();

        public void onConnected();

        public void onConnectionError();

        public void onConnectionClosed();
    }


}
