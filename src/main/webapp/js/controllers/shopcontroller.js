'use strict';

/* Controllers */

var shopControllers = angular.module('shopControllers', []);

//Shop controller
shopControllers.controller('shopListController', ['$rootScope','$scope', 'shopsFactory', 'shopFactory', '$location', 'uiGridConstants',
    function($rootScope,$scope, shopsFactory, shopFactory, $location, uiGridConstants) {
        $rootScope.currentNav = "shop";
        var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editShop(row.entity.shopId)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteShop(row.entity.shopId)" ng-confirm-click="删除的店铺不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';
        $scope.columns = [
            {field: 'shopId', displayName: '序号',suppressRemoveSort: true, sort: {direction: uiGridConstants.DESC},visible: false},
            {field: 'shopName', displayName: '店铺名', width: '15%', suppressRemoveSort: true},
            {field: 'o2o', displayName: 'o2o', width: '10%', cellFilter: 'booleanFilter', suppressRemoveSort: true },
            {field: 'ordering', displayName: '显示顺序', width: '10%', suppressRemoveSort: true},
            {field: 'address', displayName: '地址', suppressRemoveSort: true},
            {field: 'phoneNumber1', displayName: '电话', width: '15%', suppressRemoveSort: true},
            {name: 'detail', displayName: '编辑', width: '15%', cellTemplate: editTemplate, enableSorting: false}];

        $scope.gridOptions = {
            enableSorting: true,
            enableFiltering: false,
            enableRowSelection: true,
            enableSelectAll: true,
            enableRowHeaderSelection: true,
            multiSelect: true,
            showSelectionCheckbox: true,
            enableCellSelection: false,
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

        $scope.shopListScope = {
            editShop: function(shopId) {
                $location.path('/shop-detail/' + shopId);
            },
            deleteShop: function(shopId) {
                shopFactory.delete({shopId: shopId}).$promise
                    .then(function(result) {
                        $scope.searchShop();
                    }, function() {
                        alert('删除失败，该店铺有关联数据！');
                    });
            }
        };

        /* callback for ng-click 'createShop': */
        $scope.createShop = function() {
            $location.path('/shop-creation');
        };

        $scope.searchShop = function() {
            $scope.isLoading=true;
            $scope.gridOptions.data = shopsFactory.query(function(){
                $scope.isLoading=false;
            });
        };

        $scope.currentPage = 1;
        $scope.searchShop();

    }]);

shopControllers.controller('shopCreationController', ['$scope', 'shopsFactory', '$location',
    function($scope, shopsFactory, $location) {

        /* callback for ng-click 'createShop': */
        $scope.createShop = function() {
            $scope.shop.shareContent="";
            $scope.shop.shareContent=JSON.stringify($scope.shop);
            shopsFactory.create($scope.shop).$promise
                .then(function(result) {
                    $location.path('/shop');
                }, function() {
                });
        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/shop');
        };
    }]);

shopControllers.controller('shopDetailController',
        ['$scope', '$routeParams', 'shopFactory', '$location', '$upload', 'shopImageFactory', 'fileUploadFactory',
            function($scope, $routeParams, shopFactory, $location, $upload, shopImageFactory, fileUploadFactory) {

                /* callback for ng-click 'updateShop': */
                $scope.updateShop = function() {
                    $scope.shop.shareContent="";
                    $scope.shop.shareContent=JSON.stringify($scope.shop);
                    shopFactory.update($scope.shop).$promise
                        .then(function(result) {
                            $location.path('/shop');
                        }, function() {
                        });
                   
                };
                $scope.UploadImage=function(type)
                {
                    $scope.pictureType = type;
                    $("#onfileUpload").trigger("click");
                }
                //Use ng-file-upload plugin to upload file
                $scope.onFileSelect = function($files) {
                    //$files: an array of files selected, each file has name, size, and type.
                    var shopPictureUploadUrl = baseUrl + "resources/shops/" + $scope.shop.shopId + "/images/?pictureType=" + $scope.pictureType ;
                    fileUploadFactory.upload($scope, $files, $upload, shopPictureUploadUrl,reloadShopImages);
                    /*setTimeout(function() {
                        $scope.shop = shopFactory.show({shopId: $routeParams.shopId});
                    }, tzMediaApp.imageUploadWaitTime);*/
                    function reloadShopImages(){
                        $scope.shop = shopFactory.show({shopId: $routeParams.shopId});
                    }
                };

                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/shop');
                };

                $scope.removeImage = function(shopImage) {
                    shopImageFactory.delete({shopId: $routeParams.shopId,
                        imageName: shopImage.imageName}).$promise
                            .then(function(result) {
                                $scope.shop = shopFactory.show({shopId: $routeParams.shopId});
                            }, function() {
                            });
                };

                $scope.shop = shopFactory.show({shopId: $routeParams.shopId});
                $scope.pictureTypes = pictureType;
            }]);

//Shop  activity controller
shopControllers.controller('activityListController',
        ['$rootScope', '$scope', 'activitiesFactory', 'activityFactory', '$location', 'uiGridConstants',
            function($rootScope, $scope, activitiesFactory, activityFactory, $location, uiGridConstants) {
                var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editActivity(row.entity.activityName)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteActivity(row.entity.activityName)" ng-confirm-click="删除的活动不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';

                $scope.columns = [
                    {field: 'activitySubject', displayName: '活动主题', suppressRemoveSort: true},
                    {field: 'activityType', displayName: '活动类型', width: '10%',  suppressRemoveSort: true},
                    {field: 'fromDate', displayName: '开始日期', width: '15%', cellFilter: 'date:\'yyyy-MM-dd\'', sort: {direction: uiGridConstants.DESC}, suppressRemoveSort: true},
                    {field: 'toDate', displayName: '结束日期', width: '15%', cellFilter: 'date:\'yyyy-MM-dd\'',suppressRemoveSort: true},
                    {field: 'ordering', displayName: '显示顺序', width: '10%', suppressRemoveSort: true},
                    {name: 'detail', displayName: '操作', width: '15%', cellTemplate: editTemplate, enableSorting: false}];

                $scope.gridOptions = {
                    enableSorting: true,
                    enableFiltering: false,
                    enableColumnMenus: false,
                    enableRowSelection: true,
                    enableSelectAll: true,
                    enableRowHeaderSelection: true,
                    multiSelect: true,
                    showSelectionCheckbox: true,
                    enableCellSelection: false,
                    showFooter: false,
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

                $scope.activityListScope = {
                    editActivity: function(activityName) {
                        $rootScope.activitySearchCondition = $scope.activitySearchCondition;
                        $location.path('/activity-detail/' + activityName);
                    },
                    deleteActivity: function(activityName) {
                        activityFactory.delete({activityName: activityName},function(){
                            $scope.searchActivity();
                        });
                    }
                };

                $scope.createNewActivity = function() {
                    $rootScope.activitySearchCondition = $scope.activitySearchCondition;
                    $location.path('/activity-creation');
                };

                $scope.searchActivity = function() {
                    $scope.isLoading=true;
                    if ((typeof $rootScope.activitySearchCondition !== 'undefined')
                            && $rootScope.activitySearchCondition !== null) {
                        $scope.activitySearchCondition = $rootScope.activitySearchCondition;
                    }
                    if ((typeof $scope.activitySearchCondition === 'undefined')
                            || $scope.activitySearchCondition === null) {
                        $scope.activitySearchCondition = {};
                    }
                    var date = null;
                    if ($scope.activitySearchCondition.activityDate !== null
                            && $scope.activitySearchCondition.activityDate !== ''
                            && (typeof $scope.activitySearchCondition.activityDate !== 'undefined')) {
                        date = moment($scope.activitySearchCondition.activityDate).format("YYYY-MM-DD");
                    }
                    $scope.gridOptions.data = activitiesFactory.query({
                        activityName: $scope.activitySearchCondition.activityName,
                        activitySubject: $scope.activitySearchCondition.activitySubject,
                        activityDate: date,
                        shopId:$rootScope.currentShopId},function(){
                        $scope.isLoading=false;
                    });
                };
                $scope.clearActivity = function() {
                    $scope.currentPage = 1;
                    $scope.activitySearchCondition = {};
                    $rootScope.activitySearchCondition = {};
                    $scope.searchActivity();
                };

                $scope.currentPage = 1;
                $scope.searchActivity();

            }]);

shopControllers.controller('activityDetailController',
        ['$scope', '$routeParams', 'activityFactory', '$location', '$upload', 'shopsFactory', 'activityImageFactory', 'fileUploadFactory',
            function($scope, $routeParams, activityFactory, $location, $upload, shopsFactory, activityImageFactory, fileUploadFactory) {

                /* callback for ng-click 'updateActivity': */
                $scope.updateActivity = function() {
                    $scope.activity.shareContent="";
                    $scope.activity.shareContent=JSON.stringify($scope.activity);
                    activityFactory.update($scope.activity).$promise
                        .then(function(result) {
                            $location.path('/activity');
                        }, function() {
                        });

                };


                //Use ng-file-upload plugin to upload file
                $scope.onFileSelect = function($files) {
                    //$files: an array of files selected, each file has name, size, and type.
                    //alert( $scope.pictureType.id);
                    var activityPictureUploadUrl = baseUrl + "resources/activities/" + $scope.activity.activityName + "/images/?pictureType=" + $scope.uploadType;
                    fileUploadFactory.upload($scope, $files, $upload, activityPictureUploadUrl, reloadActivityImages);
                    /*setTimeout(function() {
                        $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                    }, tzMediaApp.imageUploadWaitTime);*/

                    function reloadActivityImages(){
                        $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                    }
                };
                $scope.uploadImg = function(type) {
                    $scope.uploadType = type;
                    $("#onfileUpload").trigger("click");
                }

                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/activity');
                };

                $scope.removeImage = function(activityImage) {
                    activityImageFactory.delete({activityName: $routeParams.activityName,
                        imageName: activityImage.imageName}).$promise
                            .then(function(result) {
                                $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                            }, function() {
                            });

                };

                $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                $scope.pictureTypes = pictureType;

            }]);

shopControllers.controller('activityCreationController', ['$rootScope','$scope', 'activitiesFactory', '$location',
    function($rootScope,$scope, activitiesFactory, $location) {

        /* callback for ng-click 'createNewActivity': */
        $scope.createNewActivity = function() {
            $scope.activity.shopId = $rootScope.currentShopId;
            $scope.activity.shareContent="";
            $scope.activity.fromDate = $scope.newFromDate;
            $scope.activity.toDate =  $scope.newToDate;
            $scope.activity.shareContent=JSON.stringify($scope.activity);
            activitiesFactory.create($scope.activity).$promise
                .then(function(result) {
                    $location.path('/activity');
                }, function() {
                });

        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/activity');
        };
        $scope.newFromDate = new Date();
        $scope.newToDate = new Date().DateAdd("d",1);
    }]);


shopControllers.controller('system-activityListController',
    ['$rootScope', '$scope', 'activitiesFactory', 'activityFactory', '$location', 'uiGridConstants',
        function($rootScope, $scope, activitiesFactory, activityFactory, $location, uiGridConstants) {
            var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editActivity(row.entity.activityName)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteActivity(row.entity.activityName)" ng-confirm-click="删除的活动不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';

            $scope.columns = [
                {field: 'activitySubject', displayName: '活动主题', suppressRemoveSort: true},
                {field: 'activityType', displayName: '活动类型', width: '10%',  suppressRemoveSort: true},
                {field: 'fromDate', displayName: '开始日期', width: '15%', cellFilter: 'date:\'yyyy-MM-dd\'', sort: {direction: uiGridConstants.DESC}, suppressRemoveSort: true},
                {field: 'toDate', displayName: '结束日期', width: '15%', cellFilter: 'date:\'yyyy-MM-dd\'',suppressRemoveSort: true},
                {field: 'ordering', displayName: '显示顺序', width: '10%', suppressRemoveSort: true},
                {name: 'detail', displayName: '操作', width: '15%', cellTemplate: editTemplate, enableSorting: false}];

            $scope.gridOptions = {
                enableSorting: true,
                enableFiltering: false,
                enableColumnMenus: false,
                enableRowSelection: true,
                enableSelectAll: true,
                enableRowHeaderSelection: true,
                multiSelect: true,
                showSelectionCheckbox: true,
                enableCellSelection: false,
                showFooter: false,
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

            $scope.activityListScope = {
                editActivity: function(activityName) {
                    $rootScope.activitySearchCondition = $scope.activitySearchCondition;
                    $location.path('/system-activity-detail/' + activityName);
                },
                deleteActivity: function(activityName) {
                    activityFactory.delete({activityName: activityName},function(){
                        $scope.searchActivity();
                    });
                }
            };

            $scope.createNewActivity = function() {
                $rootScope.activitySearchCondition = $scope.activitySearchCondition;
                $location.path('/system-activity-creation');
            };

            $scope.searchActivity = function() {
                $scope.isLoading=true;
                if ((typeof $rootScope.activitySearchCondition !== 'undefined')
                    && $rootScope.activitySearchCondition !== null) {
                    $scope.activitySearchCondition = $rootScope.activitySearchCondition;
                }
                if ((typeof $scope.activitySearchCondition === 'undefined')
                    || $scope.activitySearchCondition === null) {
                    $scope.activitySearchCondition = {};
                }
                var date = null;
                if ($scope.activitySearchCondition.activityDate !== null
                    && $scope.activitySearchCondition.activityDate !== ''
                    && (typeof $scope.activitySearchCondition.activityDate !== 'undefined')) {
                    date = moment($scope.activitySearchCondition.activityDate).format("YYYY-MM-DD");
                }
                $scope.gridOptions.data = activitiesFactory.query({
                    activityName: $scope.activitySearchCondition.activityName,
                    activitySubject: $scope.activitySearchCondition.activitySubject,
                    activityDate: date
                },function(){
                    $scope.isLoading=false;
                });
            };
            $scope.clearActivity = function() {
                $scope.currentPage = 1;
                $scope.activitySearchCondition = {};
                $rootScope.activitySearchCondition = {};
                $scope.searchActivity();
            };

            $scope.currentPage = 1;
            $scope.searchActivity();

        }]);

shopControllers.controller('system-activityDetailController',
    ['$scope', '$routeParams', 'activityFactory', '$location', '$upload', 'shopsFactory', 'activityImageFactory', 'fileUploadFactory',
        function($scope, $routeParams, activityFactory, $location, $upload, shopsFactory, activityImageFactory, fileUploadFactory) {

            /* callback for ng-click 'updateActivity': */
            $scope.updateActivity = function() {
                $scope.activity.shareContent="";
                $scope.activity.shareContent=JSON.stringify($scope.activity);
                activityFactory.update($scope.activity).$promise
                    .then(function(result) {
                        $location.path('/system-activity');
                    }, function() {
                    });

            };


            //Use ng-file-upload plugin to upload file
            $scope.onFileSelect = function($files) {
                //$files: an array of files selected, each file has name, size, and type.
                //alert( $scope.pictureType.id);
                var activityPictureUploadUrl = baseUrl + "resources/activities/" + $scope.activity.activityName + "/images/?pictureType=" + $scope.uploadType;
                fileUploadFactory.upload($scope, $files, $upload, activityPictureUploadUrl, reloadActivityImages);
                /*setTimeout(function() {
                    $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                }, tzMediaApp.imageUploadWaitTime);*/


                function reloadActivityImages(){
                    $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                }
            };
            $scope.uploadImg = function(type) {
                $scope.uploadType = type;
                $("#onfileUpload").trigger("click");
            }

            /* callback for ng-click 'cancel': */
            $scope.cancel = function() {
                $location.path('/system-activity');
            };

            $scope.removeImage = function(activityImage) {
                activityImageFactory.delete({activityName: $routeParams.activityName,
                    imageName: activityImage.imageName}).$promise
                    .then(function(result) {
                        $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
                    }, function() {
                    });

            };

            $scope.activity = activityFactory.show({activityName: $routeParams.activityName});
            $scope.pictureTypes = pictureType;

        }]);

shopControllers.controller('system-activityCreationController', ['$rootScope','$scope', 'activitiesFactory', '$location',
    function($rootScope,$scope, activitiesFactory, $location) {

        /* callback for ng-click 'createNewActivity': */
        $scope.createNewActivity = function() {
            $scope.activity.shareContent="";
            $scope.activity.fromDate = $scope.newFromDate;
            $scope.activity.toDate =  $scope.newToDate;
            $scope.activity.shareContent=JSON.stringify($scope.activity);
            activitiesFactory.create($scope.activity).$promise
                .then(function(result) {
                    $location.path('/system-activity');
                }, function() {
                });

        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/system-activity');
        };
        $scope.newFromDate = new Date();
        $scope.newToDate = new Date().DateAdd("d",1);
    }]);




