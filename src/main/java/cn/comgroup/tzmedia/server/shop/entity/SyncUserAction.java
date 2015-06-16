/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.shop.entity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author pcnsh197
 */
public class SyncUserAction {
     private final static ReentrantLock lock = new ReentrantLock();
    public static Lock getLock(){
       return lock;
    }
    
}
