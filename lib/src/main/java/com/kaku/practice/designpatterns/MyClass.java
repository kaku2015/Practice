package com.kaku.practice.designpatterns;

public class MyClass {
    public static void main(String args[]) {
        Star huangBo = new Star("HuangBo");
        ProxyHandler proxyHandler = new ProxyHandler(huangBo);
        IMovieStar agent = (IMovieStar) proxyHandler.getProxy();
        agent.movieShow(1000000000);
        agent.tvShow(100);
    }
}
