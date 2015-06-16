'use strict';
userControllers.controller('administratorListController',
    ['$scope', 'administratorsFactory', 'administratorFactory', '$location', 'shopsFactory', 'uiGridConstants',
        function ($scope, administratorsFactory, administratorFactory, $location, shopsFactory, uiGridConstants) {
            var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editAdministrator(row.entity.userId)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteAdministrator(row.entity.userId)" ng-confirm-click="删除的管理员不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';
            var nameTemplate = '<div class="p10" ng-bind-html="getExternalScopes().getShopName(row.entity.shopId)"></div>';
            $scope.columns = [{field: 'userId', displayName: '用户名', enableSorting: false},
                {field: 'userName', displayName: '昵称', enableSorting: false},
                {
                    field: 'adminRole',
                    displayName: '角色',
                    cellFilter: 'adminRoleFilter',
                    suppressRemoveSort: true,
                    sort: {direction: uiGridConstants.DESC}
                },
                {name: 'shopId', displayName: '所属店铺', cellTemplate: nameTemplate, enableSorting: false},
                {name: 'detail', displayName: '操作', width: '15%', cellTemplate: editTemplate, enableSorting: false}];

            $scope.gridOptions = {
                enableSorting: true,
                enableFiltering: false,
                showFooter: false,
                enableColumnMenus: false,
                enableRowSelection: false,
                enableRowHeaderSelection: false,
                //rowsPerPage: tzMediaApp.rowsPerPage,
                headerRowHeight: 50,
                rowHeight: 50,
                enableHorizontalScrollbar: false,
                columnDefs: $scope.columns
            };

            $scope.gridOptions.onRegisterApi = function (gridApi) {
                $scope.gridApi = gridApi;
                gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                    //
                });
            };

            $scope.administratorListScope = {
                getShopName: function (shopId) {

                    for (var i = 0; i < $scope.shops.length; i++) {
                        if (shopId === $scope.shops[i].shopId) {
                            return $scope.shops[i].shopName;
                        }
                    }
                },
                editAdministrator: function (userId) {
                    $location.path('/administrator-detail/' + userId);
                },
                deleteAdministrator: function (userId) {
                    administratorFactory.delete({userId: userId}).$promise.then(function(){
                        $scope.searchAdministrator();
                    });

                }
            };

            /* callback for ng-click 'createNewAdministrator': */
            $scope.createNewAdministrator = function () {
                $location.path('/administrator-creation');
            };

            $scope.searchAdministrator = function () {
                $scope.gridOptions.data = administratorsFactory.query();
            };

            $scope.searchAdministrator();
        }]);

userControllers.controller('administratorDetailController',
    ['$scope', '$routeParams', 'administratorFactory', '$location', 'administratorImageFactory', 'fileUploadFactory', '$upload', 'shopsFactory',
        function ($scope, $routeParams, administratorFactory, $location, administratorImageFactory, fileUploadFactory, $upload, shopsFactory) {


            $scope.ResetShop = function () {
                if ($scope.administrator.adminRole != "INSHOP") {
                    $scope.administrator.shopId = 0;
                }
            }

            /* callback for ng-click 'updateAdministrator': */
            $scope.updateAdministrator = function () {
                administratorFactory.update($scope.administrator).$promise.then(function(){
                    $location.path('/administrator');
                },function(){

                });
            };

            /* callback for ng-click 'cancel': */
            $scope.cancel = function () {
                $location.path('/administrator');
            };

            //Use ng-file-upload plugin to upload file
            $scope.onFileSelect = function ($files) {
                //$files: an array of files selected, each file has name, size, and type.
                var administratorPictureUploadUrl = baseUrl + "resources/administrators/" + $scope.administrator.userId + "/images/?pictureType=" + $scope.pictureType;
                fileUploadFactory.upload($scope, $files, $upload, administratorPictureUploadUrl,reloadAdministratorImages);
                function reloadAdministratorImages(){
                    $scope.administrator = administratorFactory.show({userId: $routeParams.userId});
                }
                /*setTimeout(function () {
                    $scope.administrator = administratorFactory.show({userId: $routeParams.userId});
                }, tzMediaApp.imageUploadWaitTime);*/
            };


            $scope.uploadImage = function () {
                $scope.pictureType = "PORTRAIT";
                $("#onfileUpload").trigger("click");
            }

            $scope.removeImage = function (administratorImage) {
                administratorImageFactory.delete({
                    userId: $routeParams.userId,
                    imageName: administratorImage.imageName
                });
                $scope.administrator = administratorFactory.show({userId: $routeParams.userId});
            };

            $scope.administrator = administratorFactory.show({userId: $routeParams.userId});
            $scope.adminRoles = tzMediaApp.adminRoles;
            $scope.pictureTypes = pictureType;
            $scope.shops = shopsFactory.query();
        }]);

userControllers.controller('administratorCreationController', ['$scope', 'administratorsFactory', '$location', 'shopsFactory',
    function ($scope, administratorsFactory, $location, shopsFactory) {
        /* callback for ng-click 'createNewAdministrator': */
        $scope.createNewAdministrator = function () {
                administratorsFactory.create($scope.administrator).$promise.then(function(){
                    $location.path('/administrator');
                },function(){

                });

        };


        /* callback for ng-click 'cancel': */
        $scope.cancel = function () {
            $location.path('/administrator');
        };
        $scope.adminRoles = tzMediaApp.adminRoles;
        $scope.shops = shopsFactory.query();
    }]);

userControllers.controller('changePasswordController', ['Base64', '$rootScope', '$scope', 'administratorFactory',
    function (Base64, $rootScope, $scope, administratorFactory) {

        /* callback for ng-click 'changePassword': */
        $scope.changePassword = function () {
            var newPassword = Base64.encode($scope.newPassword);
            if ($scope.administrator.password !== Base64.encode($scope.oldPassword)) {
                $scope.errorMessage = "旧密码错误,请重新输入。";
                $scope.successMessage = null;
                return;
            }
            alert(1);
            $scope.administrator.password = newPassword;
            administratorFactory.update($scope.administrator)
                .$promise.then(function (data) {
                    $scope.successMessage = "密码修改成功！！";
                    $scope.errorMessage = null;
                    $scope.oldPassword='';
                    $scope.newPassword='';

                }, function (error) {
                    $scope.errorMessage = "密码修改失败。" + error;
                    $scope.successMessage = null;
                });
        };
        $scope.administrator = administratorFactory.show({userId: $rootScope.administratorId});
    }]);
