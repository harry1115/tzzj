/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.orderprocessing.entity;

import java.io.Serializable;

/**
 *
 * @author pcnsh197
 */
public class OrderNotification implements Serializable{

    private int newOrders;
    private int grabSongOrders;

    public OrderNotification() {

    }

    public OrderNotification(int newOrders, int grabSongOrders) {
        this.newOrders = newOrders;
        this.grabSongOrders = grabSongOrders;
    }

    public int getNewOrders() {
        return newOrders;
    }

    public void setNewOrders(int newOrders) {
        this.newOrders = newOrders;
    }

    public int getGrabSongOrders() {
        return grabSongOrders;
    }

    public void setGrabSongOrders(int grabSongOrders) {
        this.grabSongOrders = grabSongOrders;
    }
}
