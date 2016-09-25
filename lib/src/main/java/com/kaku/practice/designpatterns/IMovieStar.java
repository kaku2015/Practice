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
public interface IMovieStar {
    /**
     * 演电影
     * @param money 演电影的片酬,以 int 为单位就够了，除了星爷，没听说谁的片酬能上亿
     */
    void movieShow(int money);

    /**
     * 演电视剧
     * @param money 演电视剧的片酬
     */
    void tvShow(int money);
}
