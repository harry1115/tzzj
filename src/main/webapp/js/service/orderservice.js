'use strict';

var orderServices = angular.module('orderServices', ['ngResource']);

orderServices.factory('ordersFactory', function($resource) {
    return $resource(baseUrl + 'resources/orders', {}, {
        query: {method: 'GET', isArray: true, params: {
                orderNumber: '@orderNumber',
                userId: '@userId',
                playbillId: '@playbillId',
                orderStatus: '@orderStatus',
                fromOrderDate: '@fromOrderDate',
                toOrderDate: '@toOrderDate',
                hasToBeProcessed: '@hasToBeProcessed',
                tableNumber: '@tableNumber',
                userName: '@userName',
                orderType: '@orderType',
                paymentTerm: '@paymentTerm',
                shopId: '@shopId'
            }},
        create: {method: 'POST'}
    });
});

orderServices.factory('ordersReportFactory', function($resource) {
    return $resource(baseUrl + 'resources/orders/report', {}, {
        runReport: {method: 'GET',params: {
                orderNumber: '@orderNumber',
                userId: '@userId',
                playbillId: '@playbillId',
                orderStatus: '@orderStatus',
                fromOrderDate: '@fromOrderDate',
                toOrderDate: '@toOrderDate',
                hasToBeProcessed: '@hasToBeProcessed',
                tableNumber: '@tableNumber',
                userName: '@userName',
                orderType: '@orderType',
                paymentTerm: '@paymentTerm',
                shopId: '@shopId'
            }}
    });
});

orderServices.factory('orderFactory', function($resource) {
    return $resource(baseUrl + 'resources/orders/:orderNumber', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {orderNumber: '@orderNumber'}},
        delete: {method: 'DELETE', params: {orderNumber: '@orderNumber'}}
    });
});
