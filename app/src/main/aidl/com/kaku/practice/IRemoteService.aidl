// IRemoteService.aidl
package com.kaku.practice;

// Declare any non-default types here with import statements

//导入所需要使用的非默认支持数据类型的包
import com.kaku.practice.HelloMsg;

interface IRemoteService {

 //所有的返回值前都不需要加任何东西，不管是什么数据类型
    HelloMsg sayHello();

 //传参时除了Java基本类型以及String，CharSequence之外的类型
    //都需要在前面加上定向tag，具体加什么量需而定
//    void setBookPrice(in Book book , int price);
//    void setBookName(in Book book , String name);
//    void addBookIn(in Book book);
//    void addBookOut(out Book book);
//    void addBookInout(inout Book book);
}
