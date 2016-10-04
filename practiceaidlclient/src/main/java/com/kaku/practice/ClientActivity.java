/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.kaku.practice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @author 咖枯
 * @version 1.0 2016/10/4
 */
public class ClientActivity extends AppCompatActivity {
    private IRemoteService mRemoteService = null;
    private boolean mBind = false;
    private TextView mPidText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        mPidText = (TextView) findViewById(R.id.my_pid_tv);
        mPidText.setText("the client pid is " + Process.myPid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent("com.kaku.practice.RemoteService");
        intent.setPackage("com.kaku.practice");
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBind = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = IRemoteService.Stub.asInterface(service);
            mBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
            mBind = false;
        }
    };

    public void onBtnClick(View view) {
        Log.i("HELLO_MSG", "the client pid is " + Process.myPid());
        switch (view.getId()) {
            case R.id.show_pid_btn:
                if (mBind) {
                    try {
                        Log.i("HELLO_MSG", "the service pid is " + mRemoteService.sayHello().getPid());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.say_hello_btn:
                if (mBind) {
                    try {
                        Log.i("HELLO_MSG", mRemoteService.sayHello().getMsg());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
