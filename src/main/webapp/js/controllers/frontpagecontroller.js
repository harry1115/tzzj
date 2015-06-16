'use strict';

var frontPageControllers = angular.module('frontPageControllers', []);

//Frontpage controller
frontPageControllers.controller('frontpageListController', 
['$rootScope','$scope', 'frontpagesFactory', 'frontpageFactory', '$location', 'shopsFactory', 'activitiesFactory', 'singersFactory',
    function($rootScope,$scope, frontpagesFactory, frontpageFactory, $location, shopsFactory, activitiesFactory, singersFactory) {
        
        var editTemplate='<button id="editBtn" type="button" class="btn btn-small btn-primary" ng-click="getExternalScopes().editFrontpage(row.entity.frontPageId)" >编辑</button> ';
        var viewSPTemplate='<button id="viewSPBtn" type="button" class="btn btn-small btn-primary" ng-click="getExternalScopes().viewSharePage(row.entity.frontPageId)" >分享页</button> ';
        var deleteTemplate='<button id="deleteBtn" type="button" class="btn btn-small btn-danger" ng-click="getExternalScopes().deleteFrontpage(row.entity.frontPageId)" ng-confirm-click="删除的首页不能找回，你确定要删除么？">删除</button> ';
        
        $scope.columns = [{field: 'frontPageId', displayName: '首页序号'}, 
                          {field: 'title' , displayName: '标题'},
                          {field: 'ordering', displayName: '查询顺序'},
                          {field: 'push', displayName: '客户端推送', cellFilter:'booleanFilter',enableFiltering: false},
                          {field: 'type', displayName: '类型',cellFilter: 'frontPageTypeFilter',enableFiltering: false}, 
                          {field: 'occurredDate', displayName: '发布日期',cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'',enableFiltering: false},
                          {name: 'detail', displayName: '编辑', cellTemplate: editTemplate,enableFiltering: false,enableSorting:false},
                          {name: 'viewSP', displayName: '分享页', cellTemplate: viewSPTemplate,enableFiltering: false,enableSorting:false},
                          {name: 'delete', displayName: '删除', cellTemplate: deleteTemplate,enableFiltering: false,enableSorting:false}];
    
        $scope.gridOptions = {
            enableSorting: true,
            enableFiltering: true,
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            showSelectionCheckbox:true,
            enableCellSelection: true,
            showFooter: true,
            rowsPerPage: tzMediaApp.rowsPerPage,
            columnDefs: $scope.columns};    
        
        $scope.gridOptions.onRegisterApi = function(gridApi){
          $scope.gridApi = gridApi;
          gridApi.selection.on.rowSelectionChanged($scope,function(row){
            //
          });
        };
        $scope.nextPage = function(){
            if($scope.currentPage ===$scope.gridApi.pagination.getTotalPages()){
                return;
            }
            $scope.gridApi.pagination.nextPage();
            $scope.currentPage =$scope.currentPage +1;
        };
        $scope.previousPage = function(){
            if($scope.currentPage ===1){
                return;
            }
            $scope.gridApi.pagination.previousPage();
            $scope.currentPage =$scope.currentPage -1;
        };
        
        $scope.frontPageListScope={
            editFrontpage: function(frontPageId) {
                $rootScope.frontPageSearchCondition=$scope.frontPageSearchCondition;
                $location.path('/frontpage-detail/' + frontPageId);
            },
            viewSharePage: function(frontPageId) {
                $rootScope.frontPageSearchCondition=$scope.frontPageSearchCondition;
                $location.path('/frontpage-share/' + frontPageId);
            },
            deleteFrontpage: function(frontPageId) {
                frontpageFactory.delete({frontPageId: frontPageId});
                $scope.searchFrontpage();
            }
        };
     
        /* callback for ng-click 'createFrontpage': */
        $scope.createNewFrontpage = function() {
            $rootScope.frontPageSearchCondition=$scope.frontPageSearchCondition;
            $location.path('/frontpage-creation');
        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $rootScope.frontPageSearchCondition=null;
            $location.path('/main');
        };

        $scope.searchFrontpage = function() {
            if ((typeof $rootScope.frontPageSearchCondition !== 'undefined') 
                    && $rootScope.frontPageSearchCondition !== null) {
                $scope.frontPageSearchCondition = $rootScope.frontPageSearchCondition;
            }
            if((typeof $scope.frontPageSearchCondition ==='undefined')
                    ||$scope.frontPageSearchCondition ===null){
                $scope.frontPageSearchCondition={};
            }
            
            $scope.gridOptions.data = frontpagesFactory.query({
                frontPageId: $scope.frontPageSearchCondition.frontPageId,
                shopId: $scope.frontPageSearchCondition.shopId,
                activityName: $scope.frontPageSearchCondition.activityName,
                singerId: $scope.frontPageSearchCondition.singerId,
                title: $scope.frontPageSearchCondition.title,
                push: $scope.frontPageSearchCondition.push,
                type: $scope.frontPageSearchCondition.type});
            
        };
        $scope.clearFrontpage = function() {
            $scope.gridOptions.data = [];
            $scope.currentPage = 1;
            $scope.frontPageSearchCondition={};
            $rootScope.frontPageSearchCondition={};
        };

        

        $scope.currentPage = 1;
        $scope.searchFrontpage();
        $scope.shops = shopsFactory.query();
        $scope.activities = activitiesFactory.query();
        $scope.singers = singersFactory.query();
        $scope.frontPageTypes = tzMediaApp.frontPageTypes;
    }]);

frontPageControllers.controller('frontpageCreationController',
        ['$scope', 'frontpagesFactory', '$location', 'shopsFactory', 'activitiesFactory', 'singersFactory','administratorsFactory',
            function($scope, frontpagesFactory, $location, shopsFactory, activitiesFactory, singersFactory,administratorsFactory) {

                /* callback for ng-click 'createFrontpage': */
                $scope.createFrontpage = function() {
                    if ($scope.frontpage.type === 'SHOP'
                            && ((typeof $scope.frontpage.activityName !== 'undefined' && $scope.frontpage.activityName > 0)
                                    || (typeof $scope.frontpage.singerId !== 'undefined'&& $scope.frontpage.singerId>0))) {
                        alert("此首页的类型是店铺，请只关联店铺。");
                        return;
                    }
                    if ($scope.frontpage.type === 'ACTIVITY'
                            && ((typeof $scope.frontpage.shopId !== 'undefined' && $scope.frontpage.shopId > 0)
                                    || (typeof $scope.frontpage.singerId !== 'undefined' && $scope.frontpage.singerId >0))) {
                        alert("此首页的类型是活动，请只关联活动");
                        return;
                    }
                    if ($scope.frontpage.type === 'SINGER'
                            && ((typeof $scope.frontpage.shopId !== 'undefined' && $scope.frontpage.shopId>0)
                                    || (typeof $scope.frontpage.activityName !== 'undefined' && $scope.frontpage.activityName >0))) {
                        alert("此首页的类型是歌手，请只关联歌手");
                        return;
                    }

                    frontpagesFactory.create($scope.frontpage);
                    $location.path('/frontpage');
                };

                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/frontpage');
                };
                $scope.shops = shopsFactory.query();
                $scope.activities = activitiesFactory.query();
                $scope.singers = singersFactory.query();
                $scope.administrators=administratorsFactory.query();
                $scope.frontPageTypes = tzMediaApp.frontPageTypes;
            }]);

frontPageControllers.controller('frontpageDetailController',
        ['$scope', '$routeParams', 'frontpageFactory', '$location', '$upload', 
            'frontpageImageFactory', 'fileUploadFactory', 'shopsFactory', 'activitiesFactory', 'singersFactory','administratorsFactory',
            function($scope, $routeParams, frontpageFactory, $location, $upload, frontpageImageFactory, 
            fileUploadFactory, shopsFactory, activitiesFactory, singersFactory,administratorsFactory) {

                /* callback for ng-click 'updateFrontpage': */
                $scope.updateFrontpage = function() {
                    if ($scope.frontpage.type === 'SHOP'
                            && ((typeof $scope.frontpage.activityName !== 'undefined' && $scope.frontpage.activityName > 0)
                                    || (typeof $scope.frontpage.singerId !== 'undefined'&& $scope.frontpage.singerId>0))) {
                        alert("此首页的类型是店铺，请只关联店铺。");
                        return;
                    }
                    if ($scope.frontpage.type === 'ACTIVITY'
                            && ((typeof $scope.frontpage.shopId !== 'undefined' && $scope.frontpage.shopId > 0)
                                    || (typeof $scope.frontpage.singerId !== 'undefined' && $scope.frontpage.singerId >0))) {
                        alert("此首页的类型是活动，请只关联活动");
                        return;
                    }
                    if ($scope.frontpage.type === 'SINGER'
                            && ((typeof $scope.frontpage.shopId !== 'undefined' && $scope.frontpage.shopId>0)
                                    || (typeof $scope.frontpage.activityName !== 'undefined' && $scope.frontpage.activityName >0))) {
                        alert("此首页的类型是歌手，请只关联歌手");
                        return;
                    }
                    frontpageFactory.update($scope.frontpage);
                    $location.path('/frontpage');
                };

                //Use ng-file-upload plugin to upload file
                $scope.onFileSelect = function($files) {
                    //$files: an array of files selected, each file has name, size, and type.
                    var frontpagePictureUploadUrl = baseUrl + "resources/frontpages/" + $scope.frontpage.frontPageId + "/images/?pictureType=" + 'MAIN';
                    fileUploadFactory.upload($scope, $files, $upload, frontpagePictureUploadUrl);
                    setTimeout(function(){$scope.frontpage = frontpageFactory.show({frontPageId: $routeParams.frontPageId});},tzMediaApp.imageUploadWaitTime);
                };

                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/frontpage');
                };

                $scope.removeImage = function(frontpageImage) {
                    frontpageImageFactory.delete({frontPageId: $routeParams.frontPageId, imageName: frontpageImage.imageName});
                    $scope.frontpage = frontpageFactory.show({frontPageId: $routeParams.frontPageId});
                };

                $scope.shops = shopsFactory.query();
                $scope.activities = activitiesFactory.query();
                $scope.frontpage = frontpageFactory.show({frontPageId: $routeParams.frontPageId});
                $scope.singers = singersFactory.query();
                $scope.administrators=administratorsFactory.query();
                $scope.pictureTypes = pictureType;
                $scope.frontPageTypes = tzMediaApp.frontPageTypes;
            }]);
frontPageControllers.controller('frontpageShareController',
        ['$scope', '$routeParams', 'frontpageFactory',
            function($scope, $routeParams, frontpageFactory) {
                $scope.frontpage = frontpageFactory.show(
                        {frontPageId: $routeParams.frontPageId, displaySharePage: true});
            }]);          