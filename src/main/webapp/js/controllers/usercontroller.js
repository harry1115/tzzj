'use strict';

/* Controllers */

var userControllers = angular.module('userControllers', []);


// Clear browser cache (in development mode)
//
// http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
//app.run(function ($rootScope, $templateCache) {
//  $rootScope.$on('$viewContentLoaded', function () {
//    $templateCache.removeAll();
//  });
//});
//Mobile user controller
userControllers.controller('userListController',
        ['$rootScope', '$scope', 'usersFactory', 'userFactory', '$location', 'uiGridConstants',
            function($rootScope, $scope, usersFactory, userFactory, $location, uiGridConstants) {
                var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editUser(row.entity.userId)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteUser(row.entity.userId)" ng-confirm-click="删除的用户{{row.entity.userId}}不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';

                $scope.columns = [{field: 'userId', displayName: '用户帐号', suppressRemoveSort: true, sort: {direction: uiGridConstants.ASC}},
                    {field: 'userName', displayName: '用户姓名', suppressRemoveSort: true},
                    {field: 'gender', displayName: '性别', cellFilter: 'genderFilter', suppressRemoveSort: true},
                    {field: 'userType', displayName: '注册渠道', cellFilter: 'userTypeFilter', suppressRemoveSort: true},
                    {field: 'userRole', displayName: '用户角色', cellFilter: 'userRoleFilter', suppressRemoveSort: true},
                    {field: 'creationDate', displayName: '注册日期', cellFilter: 'date:\'yyyy-MM-dd\'', suppressRemoveSort: true},
                    {field: 'score', displayName: '积分', suppressRemoveSort: true},
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
                
                $scope.conditionTypes = [
                    {id: 'USERID', name: '帐号'},
                    {id: 'USERNAME', name: '用户名'},
                    {id: 'QQ', name: 'QQ'},
                    {id: 'WEIBO', name: '微博'},
                    //{id: 'MOBILE', name: '手机'},
                    {id: 'EMAIL', name: '邮箱'}];
                $scope.conditionId=$scope.conditionTypes[0];
                $scope.openDropdown = function() {
                    $("#conditionId").fadeIn().focus();
                };
                $scope.closeDropdown = function(event) {
                    $(event.target).fadeOut();
                };
                $scope.changeCondition = function(condition) {
                    $scope.conditionId = condition;
                    $("#conditionId").hide();
                    $scope.userSearchCondition.fromUserId ="";
                    $scope.userSearchCondition.toUserId ="";
                    $scope.userSearchCondition.userName ="";
                    $scope.userSearchCondition.email ="";
                    $scope.userSearchCondition.qq ="";
                    $scope.userSearchCondition.weibo ="";
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
                $scope.userListScope = {
                    editUser: function(userId) {
                        $rootScope.userSearchCondition = $scope.userSearchCondition;
                        $location.path('/user-detail/' + userId);
                    },
                    deleteUser: function(userId) {
                        userFactory.delete({userId: userId}).$promise
                                .then(function(result) {
                                    $scope.searchUser();
                                }, function(result) {
                                    //response code
                                    //USER_HAS_ORDER=3;
                                    if (result.data === 3 || result.data === "3") {
                                        alert("此用户有订单，不能删除。");
                                        return;
                                    }
                                });
                    }
                };
                /* callback for ng-click 'createUser': */
                $scope.createNewUser = function() {
                    $rootScope.userSearchCondition = $scope.userSearchCondition;
                    $location.path('/user-creation');
                };
                $scope.searchUser = function() {
                    $scope.isLoading=true;
                    if ((typeof $rootScope.userSearchCondition !== 'undefined')
                            && $rootScope.userSearchCondition !== null) {
                        $scope.userSearchCondition = $rootScope.userSearchCondition;
                    }
                    if ((typeof $scope.userSearchCondition === 'undefined')
                            || $scope.userSearchCondition === null) {
                        $scope.userSearchCondition = {};
                    }

                    var fDate = null;
                    var tDate = null;
                    if ($scope.userSearchCondition.fromCreationDate !== null &&
                            $scope.userSearchCondition.fromCreationDate !== ''
                            && (typeof $scope.userSearchCondition.fromCreationDate !== 'undefined')) {
                        fDate = moment($scope.userSearchCondition.fromCreationDate).format("YYYY-MM-DD");
                    }

                    if ($scope.userSearchCondition.toCreationDate !== null
                            && $scope.userSearchCondition.toCreationDate !== ''
                            && (typeof $scope.userSearchCondition.toCreationDate !== 'undefined')) {
                        tDate = moment($scope.userSearchCondition.toCreationDate).format("YYYY-MM-DD");
                    }
                    switch ($scope.conditionId.id)
                    {
                        case 'USERID':
                            $scope.userSearchCondition.fromUserId = $scope.userSearchCondition.id;
                            $scope.userSearchCondition.toUserId = $scope.userSearchCondition.id;
                            break;
                        case 'USERNAME':
                            $scope.userSearchCondition.userName = $scope.userSearchCondition.id;
                            break;
                        case 'EMAIL':
                            $scope.userSearchCondition.email = $scope.userSearchCondition.id;
                            break;
                        case 'QQ':
                            $scope.userSearchCondition.qq = $scope.userSearchCondition.id;
                            break;
                        case 'WEIBO':
                            $scope.userSearchCondition.weibo = $scope.userSearchCondition.id;
                            break;
                    }
                    $scope.gridOptions.data = usersFactory.query({
                        fromUserId: $scope.userSearchCondition.fromUserId,
                        toUserId: $scope.userSearchCondition.toUserId,
                        userName: $scope.userSearchCondition.userName,
                        email: $scope.userSearchCondition.email,
                        qq: $scope.userSearchCondition.qq,
                        weibo: $scope.userSearchCondition.weibo,
                        fromCreationDate: $scope.userSearchCondition.fromCreationDate,
                        toCreationDate: $scope.userSearchCondition.toCreationDate},function(){
                        $scope.isLoading=false;
                    });
                };
                $scope.clearUser = function() {
                    //$scope.gridOptions.data = [];
                    $scope.currentPage = 1;
                    $scope.userSearchCondition = {};
                    $rootScope.userSearchCondition = {};
                    $scope.searchUser();
                };
                $scope.currentPage = 1;
                $scope.userTypes = tzMediaApp.userTypes;
                $scope.userRoles = tzMediaApp.userRoles;
                $scope.genders = tzMediaApp.genders;
                $scope.searchUser();

            }]);

userControllers.controller('userDetailController', ['$scope', '$routeParams', 'userFactory', '$location', '$http','userCouponsFactory','couponDefinitionsFactory',
    function($scope, $routeParams, userFactory, $location, $http,userCouponsFactory,couponDefinitionsFactory) {

        /* callback for ng-click 'updateUser': */
        $scope.updateUser = function() {
            userFactory.update($scope.user).$promise
                .then(function(result) {
                    $location.path('/user');
                }, function() {
                });
            
        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/user');
        };
        
        $scope.delete = function(coupon,index)
        {
              userCouponsFactory.delete({couponNumber: coupon.couponNumber},function(){
                  $scope.myCoupons.splice(index,1)
              });
        }

        $scope.user = userFactory.show({userId: $routeParams.userId});
        $scope.genders=tzMediaApp.genders;
        $scope.userTypes=tzMediaApp.userTypes;
        $scope.userRoles=tzMediaApp.userRoles;
        $scope.myCoupons = userCouponsFactory.query({userId: $routeParams.userId});
    }]);

userControllers.controller('userCreationController', ['$scope', 'usersFactory', '$location',
    function($scope, usersFactory, $location) {

        /* callback for ng-click 'createNewUser': */
        $scope.createNewUser = function() {
            usersFactory.create($scope.user).$promise
                .then(function(result) {
                    $location.path('/user');
                }, function() {
                });
        };
    }]);




userControllers.controller('userchangePasswordController',
        ['Base64', '$scope', 'userPasswordResetFactory',
            function(Base64, $scope, userPasswordResetFactory) {
                /* callback for ng-click 'changePassword': */
                $scope.changePassword = function() {
                    var newPassword = Base64.encode($scope.passwordFirst);
                    if ($scope.passwordFirst !== $scope.passwordSecond) {
                        alert("两次输入的密码不一致!");
                        return;
                    }
                    $scope.user.password = newPassword;
                    $scope.errorMessage=null;
                    $scope.successMessage=null;
                    userPasswordResetFactory.resetPassword($scope.user)
                            .$promise.then(function(data) {
                                // promise fulfilled
                                if (data.status === "1018") {
                                    $scope.successMessage = "密码修改成功，请从手机用新密码登录。";
                                    $scope.errorMessage = null;
                                } else if (data.status === "1019") {
                                    $scope.errorMessage = "本次密码修改失败，原因可能是邮箱地址提供有误，或者重复提交，如果您重复点击重置密码，请从手机尝试用第一次设置的密码登录。";
                                    $scope.successMessage = null;
                                }
                            }, function(error) {
                                $scope.errorMessage = "不晓得什么错误，请尝试用手机找回密码，如果您没有用手机注册，建议您以后其他手机应用都用手机注册，嘟嘟囔囔爱您到永远。" + error;
                                $scope.successMessage = null;
                            });
                };
            }]);
        
//coupon definition controller
userControllers.controller('couponDefinitionListController',
        ['$rootScope', '$scope', 'couponDefinitionsFactory', '$location',
            function($rootScope, $scope, couponDefinitionsFactory, $location) {
                var editTemplate = '<div class="p10"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editCouponDefinition(row.entity.couponDefinitionNumber)" ><i class="icon icon-white icon-pencil"></i></button> <button type="button" class="btn btn-danger hide" ng-click="getExternalScopes().deleteCouponDefinition(row.entity.couponDefinitionNumber)" ng-confirm-click="删除的优惠券不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';

                $scope.columns = [
                    {field: 'couponDefinitionNumber', displayName: '编号',suppressRemoveSort: true},
                    {field: 'couponName', displayName: '名称',suppressRemoveSort: true},
                    {field: 'forAllUser', displayName: '通用', cellFilter: 'booleanFilter',suppressRemoveSort: true},
                    {field: 'meetValue', displayName: '满额', cellFilter: 'currency:"￥"',suppressRemoveSort: true},
                    {field: 'faceValue', displayName: '抵扣额', cellFilter: 'currency:"￥"',suppressRemoveSort: true},
                    {field: 'expiryDate', displayName: '截止日期', cellFilter: 'date:\'yyyy-MM-dd\'',suppressRemoveSort: true},
                    {name: 'detail', displayName: '操作', cellTemplate: editTemplate,enableSorting: false}];
                $scope.gridOptions = {
                    enableSorting: true,
                    enableFiltering: false,
                    enableRowSelection: true,
                    enableSelectAll: true,
                    showFooter: false,
                    enableColumnMenus: false,
                    selectionRowHeaderWidth: 50,
                    headerRowHeight: 50,
                    rowHeight: 50,
                    enableHorizontalScrollbar: false,
                    rowsPerPage: tzMediaApp.rowsPerPage,
                    columnDefs: $scope.columns};
                $scope.gridOptions.onRegisterApi = function(gridApi) {
                    $scope.gridApi = gridApi;
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
                $scope.couponDefListScope = {
                    editCouponDefinition: function(couponDefinitionNumber) {
                        $rootScope.couponDefSearchCondition = $scope.couponDefSearchCondition;
                        $location.path('/coupondefinition-detail/' + couponDefinitionNumber);
                    },
                    deleteCouponDefinition: function(couponDefinitionNumber) {
                        couponDefinitionsFactory
                                .delete({couponDefinitionNumber: couponDefinitionNumber})
                                .$promise
                                .then(function(result) {
                                    $scope.searchCouponDefinition();
                                }, function(result) {
                                    //response code
                                    //COUPON_USED=2;
                                    if (result.data === 2 || result.data === "2") {
                                        alert("已有用户使用此优惠券，不能删除。");
                                        return;
                                    }
                                });
                    }
                };
                /* callback for ng-click 'createCouponDefinition': */
                $scope.createCouponDefinition = function() {
                    $rootScope.couponDefSearchCondition = $scope.couponDefSearchCondition;
                    $location.path('/coupondefinition-creation');
                };
                $scope.searchCouponDefinition = function() {
                    if ((typeof $rootScope.couponDefSearchCondition !== 'undefined')
                            && $rootScope.couponDefSearchCondition !== null) {
                        $scope.couponDefSearchCondition = $rootScope.couponDefSearchCondition;
                    }
                    if ((typeof $scope.couponDefSearchCondition === 'undefined')
                            || $scope.couponDefSearchCondition === null) {
                        $scope.couponDefSearchCondition = {};
                    }
                    var fDate = null;
                    var tDate = null;
                    if ($scope.couponDefSearchCondition.fromDate !== null
                            && $scope.couponDefSearchCondition.fromDate !== ''
                            && (typeof $scope.couponDefSearchCondition.fromDate !== 'undefined')) {
                        fDate = moment($scope.couponDefSearchCondition.fromDate).format("YYYY-MM-DD");
                    }

                    if ($scope.couponDefSearchCondition.toDate !== null
                            && $scope.couponDefSearchCondition.toDate !== ''
                            && (typeof $scope.couponDefSearchCondition.toDate !== 'undefined')) {
                        tDate = moment($scope.couponDefSearchCondition.toDate).format("YYYY-MM-DD");
                    }

                    $scope.gridOptions.data = couponDefinitionsFactory.query({
                        couponDefinitionNumber: $scope.couponDefSearchCondition.couponDefinitionNumber,
                        fromDate: fDate,
                        toDate: tDate
                    });
                };
                $scope.clear = function() {
                    //$scope.gridOptions.data = [];
                    $scope.currentPage = 1;
                    $scope.couponDefSearchCondition = {};
                    $rootScope.couponDefSearchCondition = {};
                    $scope.searchCouponDefinition();
                };
                $scope.currentPage = 1;
                $scope.searchCouponDefinition();

            }]);

userControllers.controller('couponDefinitionCreationController', ['$scope', 'couponDefinitionsFactory', '$location', 
    function($scope, couponDefinitionsFactory, $location) {

        $scope.saveTxt = "保存"
        /* callback for ng-click 'createUserfeedback': */
        $scope.createCouponDefinition = function() {
             $scope.couponDefinition.forAllUser = 1;
             $scope.saveTxt = "添加中..."
            couponDefinitionsFactory.create($scope.couponDefinition).$promise.then(function(result) {
                    $location.path('/coupondefinition');
                }, function(result) {
                });
        };

        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/coupondefinition');
        };
        
    }]);

userControllers.controller('couponDefinitionDetailController',
        ['$scope', '$routeParams', '$location', 'couponDefinitionDetailFactory',
            function($scope, $routeParams, $location, couponDefinitionDetailFactory) {

                /* callback for ng-click 'updateFeedbackp': */
                $scope.updateCouponDefinition = function() {
                    $scope.couponDefinition.forAllUser = 1;
                    couponDefinitionDetailFactory.update($scope.couponDefinition).$promise.then(function(result) {
                        $location.path('/coupondefinition');
                    }, function(result) {
                    });
                   
                };

                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/coupondefinition');
                };

                $scope.couponDefinition = couponDefinitionDetailFactory.show(
                        {couponDefinitionNumber: $routeParams.couponDefinitionNumber});
            }]);



userControllers.controller('userfeedbackListController',
        ['$rootScope', '$scope', 'userfeedbacksFactory', 'userfeedbackFactory', '$location',
            function($rootScope, $scope, userfeedbacksFactory, userfeedbackFactory, $location) {

                $scope.deleteUserfeedback= function(feedback) {
                        userfeedbackFactory.delete({feedbackNumber: feedback.feedbackNumber}).$promise.then(function(){
                            var index = $scope.userfeedbacks.indexOf(feedback);
                            if (index > -1) {
                                $scope.userfeedbacks.splice(index, 1);
                            }
                        });

                    }

                $scope.searchFeedback = function() {
                    if ((typeof $rootScope.feedbackSearchCondition !== 'undefined')
                            && $rootScope.feedbackSearchCondition !== null) {
                        $scope.feedbackSearchCondition = $rootScope.feedbackSearchCondition;
                    }
                    if ((typeof $scope.feedbackSearchCondition === 'undefined')
                            || $scope.feedbackSearchCondition === null) {
                        $scope.feedbackSearchCondition = {};
                    }

                    $scope.userfeedbacks = userfeedbacksFactory.query({
                        userId: $scope.feedbackSearchCondition.userId,
                        fromDate:$scope.feedbackSearchCondition.fromDate,
                        toDate: $scope.feedbackSearchCondition.toDate
                    });
                };
                $scope.clearFeedback = function() {
                    $scope.currentPage = 1;
                    $scope.feedbackSearchCondition = {};
                    $rootScope.feedbackSearchCondition = {};
                    $scope.searchFeedback();
                };
                $scope.searchFeedback();
            }]);
