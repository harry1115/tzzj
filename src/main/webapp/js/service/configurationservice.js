'use strict';

var configurationServices = angular.module('configurationServices', ['ngResource']);

//configuration factory
configurationServices.factory('configurationFactory', function($resource) {
    return $resource(baseUrl + 'resources/configuration', {}, {
        query: {method: 'GET', isArray: false},
        save: {method: 'POST'}
    });
});
