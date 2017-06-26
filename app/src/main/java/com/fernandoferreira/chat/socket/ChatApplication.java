package com.fernandoferreira.chat.socket;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.fernandoferreira.chat.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by fernando.ferreira on 23/06/2017.
 */

public class ChatApplication extends Application {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}