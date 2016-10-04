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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * @author 咖枯
 * @version 1.0 2016/10/4
 */
public class RemoteService extends Service {
    IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public HelloMsg sayHello() throws RemoteException {
            return new HelloMsg("msg from service at Thread "
                    + Thread.currentThread().toString() + "\n" +
                    "tid is " + Thread.currentThread().getId()
                    + "\n" + "main thread id is " + getMainLooper().getThread().getId(), Process.myPid());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
