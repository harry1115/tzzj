'use strict';

var configurationControllers = angular.module('configurationControllers', []);

configurationControllers.controller('configurationController',
        ['$scope', '$routeParams', 'configurationFactory', '$location',
            function($scope, $routeParams, configurationFactory, $location) {
                $scope.currentNav="configuration";
                /* callback for ng-click 'updateConfiguration': */
                $scope.updateConfiguration = function() {
                    configurationFactory.save($scope.globalConfiguration);
                };
                $scope.globalConfiguration = configurationFactory.query();
            }]);

