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
import android.widget.Toast;


import com.fernandoferreira.chat.dummy.DummyContent;
import com.fernandoferreira.chat.dummy.MyObject;
import com.fernandoferreira.chat.entity.RoomContent;
import com.fernandoferreira.chat.persistence.model.Room;
import com.fernandoferreira.chat.persistence.repository.RoomRepository;
import com.fernandoferreira.chat.socket.ChatApplication;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
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
    private Boolean isConnected = true;
    private String mUsername = "Customuser";
    final String TAG = "ChatLog";

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

        conteudo = new RoomContent();

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

        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);

        mSocket.on("login", onLogin);
        mSocket.on("login2", onRoomJoined);

        mSocket.connect();

        RoomRepository rp = new RoomRepository<Room>(this);
        try {
            Gson gson = new Gson();
            Room newRoom = new Room("teste","teste");
            rp.save(newRoom);
            List<Room> salas = rp.queryAll();
            int f = 0;
            Log.i(TAG, "salas: " + salas.size());
            Log.i(TAG, "cheguei aqui " + gson.toJson(salas));
        } catch (SQLException e) {
            Log.i(TAG, "sql " + e.getMessage());

            e.printStackTrace();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            e.printStackTrace();
        }
        Log.i(TAG, "terminou");

//        try {
//            RoomRepository rp = new RoomRepository<Room>(this);
//

//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int i =0;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
        ItemListActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if(!isConnected) {
                //    if(null!=mUsername)
                        mSocket.emit("add user", mUsername);
                    Toast.makeText(ItemListActivity.this.getApplicationContext(),
                            R.string.connect, Toast.LENGTH_LONG).show();
                //    isConnected = true;
                //}
            }
        });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ItemListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                    Toast.makeText(ItemListActivity.this.getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onRoomJoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                String message = data.getString("message");
                String bssid = data.getString("bssid");
                Log.i(TAG, message);

                for (RoomContent.RoomItem room: conteudo.ITEMS) {
                    if (room.bssid.equals(bssid)){
                        room.name += "OK";
                    }
                }
                //final View recyclerView = findViewById(R.id.item_list);
                //updateRecyclerView((RecyclerView) recyclerView);

            } catch (JSONException e) {

            }
        }
    };

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
            //setResult(RESULT_OK, intent);
            //finish();
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
            List<ScanResult> resultList = wifiManager.getScanResults();
            Log.i(TAG, "Scanning initiated");
            if (resultList.size() > 0) {
                for (int i = 0; i < resultList.size(); i++) {
                    ScanResult result = resultList.get(i);
                    if (!roomAlreadyExists(result.BSSID)){
                        RoomContent.RoomItem newRoom = conteudo.createRooomItem(result.BSSID.toString(), result.SSID);

                        Gson gson = new Gson();

                        Log.i(TAG, gson.toJson(newRoom));
                        conteudo.ITEMS.add(newRoom);
                        final View recyclerView = findViewById(R.id.item_list);
                        updateRecyclerView((RecyclerView) recyclerView);

                        mSocket.emit("join room", gson.toJson(newRoom));
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
