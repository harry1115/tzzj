'use strict';

/* Controllers */

var ticketControllers = angular.module('ticketControllers', []);


// Clear browser cache (in development mode)
//
// http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
//app.run(function ($rootScope, $templateCache) {
//  $rootScope.$on('$viewContentLoaded', function () {
//    $templateCache.removeAll();
//  });
//});
//Mobile user controller
ticketControllers.controller('ticketListController',
        ['$rootScope', '$scope', /*'ticketsFactory', 'ticketFactory',*/ 'activitiesFactory','$location', 'uiGridConstants',
            function($rootScope, $scope,/* ticketsFactory, ticketFactory,*/ activitiesFactory,$location, uiGridConstants) {
                var activityTemplate = '<div class="p10" ng-bind-html="getExternalScopes().getActivityName(row.entity.activityId)"></div>';
                var countTemplate='<div class="p10"><span ng-class="{\'color_red\':row.entity.residue==0}">{{row.entity.residue}}</span>/{{row.entity.count}}</div>';
                var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editTicket(row.entity.ticketId)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteTicket(row.entity.ticketId)" ng-confirm-click="删除的票务不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';
                $scope.columns = [{field: 'ticketName', displayName: '票务名称', suppressRemoveSort: true, sort: {direction: uiGridConstants.ASC}},
                    {name: 'activityId', displayName: '活动主题',cellTemplate: activityTemplate, suppressRemoveSort: true},
                    {field: 'prize', displayName: '价格', cellFilter: 'currency:"￥"', suppressRemoveSort: true},
                    {name: 'count', displayName: '剩余/总量',cellTemplate:countTemplate, suppressRemoveSort: true},
                    {field: 'status', displayName: '状态', cellFilter: 'ticketStatusFilter', suppressRemoveSort: true},
                    {field: 'fromTime', displayName: '开始日期', cellFilter: 'date:\'yyyy-MM-dd\'', suppressRemoveSort: true},
                    {field: 'toTime', displayName: '结束时间', cellFilter: 'date:\'yyyy-MM-dd\'', suppressRemoveSort: true},
                    {name: 'detail', displayName: '操作', cellTemplate: editTemplate, enableFiltering: false, enableSorting: false}];
                $scope.gridOptions = {
                    enableSorting: true,
                    enableFiltering: false,
                    enableRowSelection: true,
                    enableSelectAll: true,
                    showFooter: false,
                    enableColumnMenus: false,
                    rowsPerPage: tzMediaApp.rowsPerPage,
                    selectionRowHeaderWidth: 50,
                    headerRowHeight: 50,
                    rowHeight: 50,
                    enableHorizontalScrollbar: false,
                    columnDefs: $scope.columns};
                $scope.gridOptions.onRegisterApi = function(gridApi) {
                    $scope.gridApi = gridApi;
                    gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                        //
                    });
                };
                $scope.nextPage = function() {
                    if ($scope.currentPage === $scope.gridApi.pagination.getTotalPages()) {
                        return;
                    }
                    $scope.gridApi.pagination.nextPage();
                    $scope.currentPage = $scope.currentPage + 1;
                };
                $scope.previousPage = function() {
                    if ($scope.currentPage === 1) {
                        return;
                    }
                    $scope.gridApi.pagination.previousPage();
                    $scope.currentPage = $scope.currentPage - 1;
                };
                $scope.ticketListScope = {
                    getActivityName: function (activityId) {

                        for (var i = 0; i < $scope.activitys.length; i++) {
                            if (activityId === $scope.activitys[i].activityId) {
                                return $scope.activitys[i].activitySubject;
                            }
                        }
                    },
                    editUser: function(ticketId) {
                        $rootScope.ticketSearchCondition = $scope.ticketSearchCondition;
                        $location.path('/ticket-detail/' + ticketId);
                    },
                    deleteUser: function(ticketId) {
                        userFactory.delete({userId: ticketId}).$promise
                                .then(function(result) {
                                    $scope.searchTicket();
                                }, function(result) {
                                });
                    }
                };
                /* callback for ng-click 'createUser': */
                $scope.createNewTicket = function() {
                    $rootScope.ticketSearchCondition = $scope.ticketSearchCondition;
                    $location.path('/ticket-creation');
                };
                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $rootScope.ticketSearchCondition = null;
                    $location.path('/main');
                };
                $scope.searchUser = function() {
                    $scope.isLoading=true;
                    if ((typeof $rootScope.ticketSearchCondition !== 'undefined')
                            && $rootScope.ticketSearchCondition !== null) {
                        $scope.ticketSearchCondition = $rootScope.ticketSearchCondition;
                    }
                    if ((typeof $scope.ticketSearchCondition === 'undefined')
                            || $scope.ticketSearchCondition === null) {
                        $scope.ticketSearchCondition = {};
                    }

                    var fDate = null;
                    var tDate = null;
                    if ($scope.ticketSearchCondition.fromCreationDate !== null &&
                            $scope.ticketSearchCondition.fromCreationDate !== ''
                            && (typeof $scope.ticketSearchCondition.fromCreationDate !== 'undefined')) {
                        fDate = moment($scope.ticketSearchCondition.fromCreationDate).format("YYYY-MM-DD");
                    }

                    if ($scope.ticketSearchCondition.toCreationDate !== null
                            && $scope.ticketSearchCondition.toCreationDate !== ''
                            && (typeof $scope.ticketSearchCondition.toCreationDate !== 'undefined')) {
                        tDate = moment($scope.ticketSearchCondition.toCreationDate).format("YYYY-MM-DD");
                    }

                    $scope.gridOptions.data =tzMediaApp.ticketData;
                };
                $scope.clearUser = function() {
                    $scope.currentPage = 1;
                    $scope.ticketSearchCondition = {};
                    $rootScope.ticketSearchCondition = {};
                    $scope.searchUser();
                };
                $scope.currentPage = 1;
                $scope.searchUser();
                $scope.activitys = activitiesFactory.query();

            }]);

ticketControllers.controller('ticketDetailController', ['$scope', '$routeParams', 'ticketFactory', '$location', '$http',
    function($scope, $routeParams, ticketFactory, $location, $http) {
        /* callback for ng-click 'updateUser': */
        $scope.updateUser = function() {
            ticketFactory.update($scope.ticket,function(){$location.path('/ticket');});
            
        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/ticket');
        };

        $scope.ticket = ticketFactory.show({ticketId: $routeParams.ticketId});
    }]);

ticketControllers.controller('ticketCreationController', ['$scope', 'ticketsFactory', '$location',
    function($scope, ticketsFactory, $location) {

        /* callback for ng-click 'createNewUser': */
        $scope.createNewTicket = function() {
            ticketsFactory.create($scope.ticket);
            $location.path('/ticket-list');
        };
    }]);



