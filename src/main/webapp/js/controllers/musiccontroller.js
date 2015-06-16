'use strict';

//Shop singer controller
shopControllers.controller('musicListController',
        ['$rootScope', '$scope', 'singersFactory', 'singerFactory', '$location', 'songsFactory','uiGridConstants',
            function($rootScope, $scope, singersFactory, singerFactory, $location, songsFactory,uiGridConstants) {
                var thumbTemplate = '<div class="p10"><img class="thumb mr20"  ng-src="{{image.filePath}}" ng-repeat="image in row.entity.singerImages" ng-if="image.pictureType === \'PORTRAIT\'" ><img class="thumb mr20" src="img/thumb-user.png" alt="" ng-if="row.entity.singerImages.length==0"/>{{row.entity.singerName}}</div>';
                var editTemplate = '<div class="p10" style="line-height:57px;"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editSinger(row.entity.singerId)"  ><i class="icon icon-white icon-pencil"></i></button><button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteSinger(row.entity.singerId)" ng-confirm-click="删除的歌手不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';
                $scope.columns = [
                    {field: 'singerId', displayName: '序号',suppressRemoveSort: true, sort: {direction: uiGridConstants.DESC},visible: false},
                    {field: 'singerName', displayName: '姓名', cellTemplate: thumbTemplate, suppressRemoveSort: true},
                    {field: 'englishName', displayName: '英文名', suppressRemoveSort: true},
                    {field: 'gender', displayName: '性别', cellFilter: 'genderFilter', suppressRemoveSort: true},
                    {field: 'badgeType', displayName: '徽章等级', cellFilter: 'badgeTypesFilter', suppressRemoveSort: true},
                    {field: 'ordering', displayName: '显示顺序', suppressRemoveSort: true},
                    {name: 'detail', displayName: '操作', cellTemplate: editTemplate, enableFiltering: false, enableSorting: false}];

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
                    rowsPerPage: tzMediaApp.rowsPerPage,
                    selectionRowHeaderWidth: 50,
                    selectionRowHeaderHeight: 80,
                    headerRowHeight: 50,
                    enableColumnMenus: false,
                    rowHeight: 80,
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

                $scope.singerListScope = {
                    editSinger: function(singerId) {
                        $rootScope.singerSearchCondition = $scope.singerSearchCondition;
                        $location.path('/music-detail/' + singerId);
                    },

                    deleteSinger: function(singerId) {
                        singerFactory.delete({singerId: singerId}).$promise.then(function(){
                            $scope.searchSinger();
                        },function(){
                            alert("当前歌手与节目单有关联，所以无法删除！")
                        });

                    }
                };


                $scope.searchSinger = function() {
                    if ((typeof $rootScope.singerSearchCondition !== 'undefined')
                            && $rootScope.singerSearchCondition !== null) {
                        $scope.singerSearchCondition = $rootScope.singerSearchCondition;
                    }
                    if ((typeof $scope.singerSearchCondition === 'undefined')
                            || $scope.singerSearchCondition === null) {
                        $scope.singerSearchCondition = {};
                    }
                    $scope.gridOptions.data = singersFactory.query({
                        singerId: $scope.singerSearchCondition.singerId,
                        singerName: $scope.singerSearchCondition.singerName,
                        isBandsman: true,
                        badgeType: $scope.singerSearchCondition.badgeType,
                        isPc:true});
                };

                $scope.createSinger = function() {
                    $location.path('/music-creation');
                };

                $scope.clearSinger = function() {
                    //$scope.gridOptions.data = [];
                    $scope.currentPage = 1;
                    $scope.singerSearchCondition = {};
                    $rootScope.singerSearchCondition = {};
                    $scope.searchSinger();
                };

                $scope.currentPage = 1;
                $scope.searchSinger();
                $scope.badgeTypes = tzMediaApp.badgeTypes;

            }]);

shopControllers.controller('musicCreationController', ['$scope', 'singersFactory', '$location',
    function($scope, singersFactory, $location) {

        /* callback for ng-click 'createSinger': */
        $scope.createSinger = function() {
            $scope.singer.bandsman = true;
            $scope.singer.shareContent="";
            $scope.singer.shareContent=JSON.stringify($scope.singer);
            singersFactory.create($scope.singer).$promise
                    .then(function(result) {
                        $location.path('/music');
                    }, function(result) {
                        $location.path('/music');
                    });
        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/music');
        };
        $scope.genders = tzMediaApp.genders;
        $scope.badgeTypes = tzMediaApp.badgeTypes;

    }]);

shopControllers.controller('musicDetailController',
        ['$scope', '$routeParams', 'singerFactory', '$location', '$upload', 'singerImageFactory', 'fileUploadFactory',
            function($scope, $routeParams, singerFactory, $location, $upload, singerImageFactory, fileUploadFactory) {
                /* callback for ng-click 'updateSinger': */
                $scope.updateSinger = function() {
                    $scope.singer.shareContent="";
                    $scope.singer.shareContent=JSON.stringify($scope.singer);
                    singerFactory.update($scope.singer).$promise
                        .then(function(result) {
                            $location.path('/music');
                        }, function(result) {
                            $location.path('/music');
                        });
                };

                //Use ng-file-upload plugin to upload file
                $scope.onFileSelect = function($files) {
                    //$files: an array of files selected, each file has name, size, and type.
                    var singerPictureUploadUrl = baseUrl + "resources/singers/" + $scope.singer.singerId + "/images/?pictureType=" + $scope.pictureType;
                    fileUploadFactory.upload($scope, $files, $upload, singerPictureUploadUrl, reloadSingerImages);
                    /*setTimeout(function() {
                        $scope.singer = singerFactory.show({singerId: $routeParams.singerId});
                    }, tzMediaApp.imageUploadWaitTime);*/

                    function reloadSingerImages(){
                        $scope.singer = singerFactory.show({singerId: $routeParams.singerId});
                    }
                };

                $scope.StartUpload = function(type)
                {
                    $scope.pictureType = type;
                    $("#onfileUpload").trigger("click");
                }
                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/music');
                };

                $scope.removeImage = function(singerImage) {
                    singerImageFactory.delete({singerId: $routeParams.singerId,
                        imageName: singerImage.imageName}).$promise
                            .then(function(result) {
                                $scope.singer = singerFactory.show({singerId: $routeParams.singerId});
                            }, function() {
                            });
                };

                $scope.singer = singerFactory.show({singerId: $routeParams.singerId});
                $scope.badgeTypes = tzMediaApp.badgeTypes;
                $scope.pictureTypes = pictureType;
                $scope.genders = tzMediaApp.genders;
            }]);









