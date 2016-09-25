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

/**
 * @author 咖枯
 * @version 1.0 2016/9/17
 */
public class Star implements IMovieStar{
    private String mName;

    public Star(String name) {
        mName = name;
    }

    @Override
    public void movieShow(int money) {
        System.out.println(mName + " 出演了部片酬 " + money + " 元的电影");
    }

    @Override
    public void tvShow(int money) {
        System.out.println(mName + " 出演了部片酬 " + money + " 元的电视剧");
    }
}
