package com.hackm.famiryboard.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hackm.famiryboard.R;
import com.hackm.famiryboard.model.system.Account;
import com.hackm.famiryboard.model.system.AppConfig;
import com.hackm.famiryboard.model.viewobject.Deco;
import com.hackm.famiryboard.model.viewobject.DecoImage;
import com.hackm.famiryboard.model.viewobject.DecoText;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private static final String EXTRA_BOARD_ID = "extra_board_id";

    private OnUpdateConnectionListener mListener;
    private HubConnection mConnection;
    private HubProxy mProxy;
    private String mBoardId;

    private Logger logger = new Logger() {
        @Override
        public void log(String message, LogLevel level) {
            if (AppConfig.DEBUG) {
                Log.d(TAG, message);
            }
        }
    };

    public static SignalRFragment newInstance(final String signalrProxy, final String connectionUrl, final String boardId) {
        SignalRFragment fragment = new SignalRFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PROXY, signalrProxy);
        args.putString(EXTRA_CONNECTION_URL, connectionUrl);
        args.putString(EXTRA_BOARD_ID, boardId);
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
        mBoardId = args.getString(EXTRA_BOARD_ID);
        // Connect to the server
        mConnection = new HubConnection(connectionUrl, "", true, logger);
        /*
        Account account = Account.getAccount(getActivity());
        Map<String, String> authorizationHeader = account.getAccountHeader();
        CookieCredentials credentials = new CookieCredentials();
        for (String key : authorizationHeader.keySet()) {
            credentials.addCookie(key, authorizationHeader.get(key));
        }
        mConnection.setCredentials(credentials);
        */
        // Create the hub proxy
        mProxy = mConnection.createHubProxy(proxyName);
        setSubscribe();
        setConnectionListener();

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
                        //TODO 接続したタイミングでボードを開く
                        openBoard();
                    }
                });

    }

    @Override
    public void onDestroy() {
        if (mConnection != null) {
            closeBoard();
            mConnection.stop();
        }
        super.onDestroy();
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
    }

    private void setSubscribe() {
        // Proxy Subscriding
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onCreateItem(String json) {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, "OnCreateItem : " + json);
                }
                if (mListener != null) {
                    mListener.onCreateItem(json);
                }
            }
        });
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onUpdateItem(String json) {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, "OnUpdateItem : " + json);
                }
                if (mListener != null) {
                    mListener.onUpdateItem(json);
                }
            }
        });
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onDeleteItem(String id) {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, "OnDeleteItem : " + id);
                }
                if (mListener != null) {
                    mListener.onDeleteItem(id);
                }
            }
        });
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onCreateSuccess(String json) {

                if (AppConfig.DEBUG) {
                    Log.d(TAG, "CreateSuccess : " + json);
                }
                if (mListener != null) {
                    mListener.onCreateSucccess(json);
                }
            }
        });
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onUpdateSuccess(String message) {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, "OnUpdateSucces : " + message);
                }
                if (mListener != null) {
                    mListener.onUpdateItem(message);
                }
            }
        });
        mProxy.subscribe(new Object() {
            //Some method
            @SuppressWarnings("unused")
            public void onError(String message) {
                if (AppConfig.DEBUG) {
                    Log.d(TAG, "OnError : " + message);
                }
                if (mListener != null) {
                    mListener.onError(message);
                }
            }
        });
    }

    public void openBoard() {
        mProxy.invoke("openBoard", mBoardId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "OpenBoarded");
            }
        });
    }

    public void closeBoard() {
        mProxy.invoke("closeBoard", mBoardId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "CloseBoarded");
            }
        });
    }

    public void createItem(DecoImage items) {
        mProxy.invoke("createItem", mBoardId, items).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "Create Decoimage Itemed");
            }
        });
    }

    public void createItem(DecoText items) {
        mProxy.invoke("createItem", mBoardId, items).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "Create DecoText Itemed");
            }
        });
    }


    public void updateItem(DecoImage items) {
        mProxy.invoke("updateItem", mBoardId, items).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "Updated");
            }
        });
    }

    public void updateItem(DecoText items) {
        mProxy.invoke("updateItem", mBoardId, items).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "Updated");
            }
        });
    }

    public void deleteItem(String itemId) {
        mProxy.invoke("deleteItem", mBoardId, itemId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d(TAG, "Deleted");
            }
        });
    }

    public interface OnUpdateConnectionListener {
        //TODO Add some method
        public void onCreateItem(String json);

        public void onCreateSucccess(String json);

        public void onUpdateItem(String json);

        public void onDeleteItem(String id);

        public void onError(String message);

        public void onConnectionStarted();

        public void onConnected();

        public void onConnectionError();

        public void onConnectionClosed();
    }

}
