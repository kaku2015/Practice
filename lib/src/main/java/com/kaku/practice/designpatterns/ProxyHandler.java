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
package com.kaku.practice.designpatterns;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author 咖枯
 * @version 1.0 2016/9/17
 */
public class ProxyHandler implements InvocationHandler {
    //被代理对象
    private Object mTarget;

    public ProxyHandler(Object target) {
        this.mTarget = target;
    }

    /**
     * 方法拦截，可以进行一些额外操作
     *
     * @param proxy
     * @param method 拦截的方法
     * @param args   方法对应的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("movieShow") || methodName.equals("tvShow")) {
            if (args[0] instanceof Integer && ((int) args[0]) < 300000000) {
                System.out.println(((int) args[0]) + "块钱？！你雇 HuangZiTao 演去吧！");
                return null;
            }
        }
        Object result = method.invoke(mTarget, args);

        return result;
    }

    /**
     * 获取代理
     *
     * @return
     */
    public Object getProxy() {
        return Proxy.newProxyInstance(mTarget.getClass().getClassLoader(), mTarget.getClass().getInterfaces(), this);
    }
}
