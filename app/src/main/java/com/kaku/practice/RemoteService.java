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
            // 通过AIDL实现了一个远程服务端的接口，然后有另外一个客户端进程调用了该接口中的方法，因为客户端和你所实现的服务端处于两个不同的进程，
            // 因此客户端对于你而言，就是一个远程进程。当客户端对接口进行调用时，调用过程并不是由客户端进程进行处理的。而是由系统进行封装后，
            // 传递到服务端进程所持有的一个线程池中进行处理。最终线程池中的其中一个线程会被用来执行调用的具体逻辑。 而具体选择哪个线程来进行处理，是无法提前预知的。
            // 因此作为服务端接口的实现者，应该能够处理多线程并发的情况，时刻准备好处理来自未知线程的调用，并能保证AIDL接口的实现是线程安全的。
            synchronized (this) {
                return new HelloMsg("msg from service at Thread "
                        + Thread.currentThread().toString() + "\n" +
                        "tid is " + Thread.currentThread().getId()
                        + "\n" + "main thread id is " + getMainLooper().getThread().getId(), Process.myPid());
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
