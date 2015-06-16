'use strict';

var productServices = angular.module('productServices', ['ngResource']);

//product factory
productServices.factory('productsFactory', function($resource) {
    return $resource(baseUrl + 'resources/products', {}, {
        query: {method: 'GET', isArray: true,
            params: {
                productNumber: '@productNumber', 
                productName: '@productName',
                sellable: '@sellable',
                productType: '@productType',
                resultLength: '@resultLength',
                shopId:'@shopId'}},
        create: {method: 'POST'}
    });
});

productServices.factory('productFactory', function($resource) {
    return $resource(baseUrl + 'resources/products/:productNumber', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {productNumber: '@productNumber'}},
        delete: {method: 'DELETE', params: {productNumber: '@productNumber'}}
    });
});

productServices.factory('productImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/products/:productNumber/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {productNumber: '@productNumber', imageName: '@imageName'}}
    });
});

//productType factory
productServices.factory('productTypesFactory', function($resource) {
    return $resource(baseUrl + 'resources/producttypes', {}, {
        query: {method: 'GET', isArray: true, params: {typeId: '@typeId',shopId:'@shopId'}},
        create: {method: 'POST'}
    });
});

productServices.factory('productTypeFactory', function($resource) {
    return $resource(baseUrl + 'resources/producttypes/:typeId', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {typeId: '@typeId'}},
        delete: {method: 'DELETE', params: {typeId: '@typeId'}}
    });
});

productServices.factory('productTypeImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/producttypes/:typeId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {typeId: '@typeId', imageName: '@imageName'}}
    });
});