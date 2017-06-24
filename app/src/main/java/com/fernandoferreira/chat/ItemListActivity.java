package com.fernandoferreira.chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.fernandoferreira.chat.dummy.DummyContent;
import com.fernandoferreira.chat.entity.RoomContent;
import com.fernandoferreira.chat.socket.ChatApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private int scanCounter = 0;
    private RoomContent conteudo;

    final View recyclerView = null;

    private Socket mSocket;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        final View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 updateRecyclerView((RecyclerView) recyclerView);

//                Snackbar.make(view, "Replace with your own action", S
// nackbar.LENGTH_LONG)
                //                      .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        //socket
        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();
        mSocket.connect();
        Log.i("ChatLog", "status socket: " + mSocket.connected());
        mSocket.on("login", onLogin);
        mSocket.emit("add user", "oi galera");

//
//        try {
//            mSocket.emit("add user", "meu android");
//        }catch(Exception ec){
//            Log.i("ChatLog", ec.getMessage());
//        }

    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", "Fernando UserName");
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    public void updateRecyclerView(RecyclerView recyclerView){
        recyclerView.getAdapter().notifyDataSetChanged();
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(conteudo.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<RoomContent.RoomItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<RoomContent.RoomItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
//            holder.mIdView.setText(mValues.get(position).bssid);
            holder.mContentView.setText(mValues.get(position).name);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.bssid);
                        arguments.putString("name", holder.mItem.name);

                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.bssid);
                        intent.putExtra("name", holder.mItem.name);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
//            public final TextView mIdView;
            public final TextView mContentView;
            public RoomContent.RoomItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }


    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String TAG = "ChatLog";
            List<ScanResult> resultList = wifiManager.getScanResults();
            Log.i(TAG, "Scanning initiated");
            if (resultList.size() > 0) {
                for (int i = 0; i < resultList.size(); i++) {
                    ScanResult result = resultList.get(i);
                    if (!roomAlreadyExists(result.BSSID)){
                        Log.i(TAG, "SSID: " + result.SSID + " BSSID:" + result.BSSID.toString());
                        conteudo.ITEMS.add(conteudo.createRooomItem(result.BSSID.toString(), result.SSID));
                        final View recyclerView = findViewById(R.id.item_list);
                        updateRecyclerView((RecyclerView) recyclerView);
                    }
                }
            }
            wifiManager.startScan();
        }

        public Boolean roomAlreadyExists(String bssid){
            if (conteudo.ITEMS != null) {
                for (RoomContent.RoomItem room : conteudo.ITEMS) {
                    if (room.bssid.equals(bssid)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }
}
