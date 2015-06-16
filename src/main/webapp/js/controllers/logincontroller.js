'use strict';

var loginControllers = angular.module('loginControllers', []);
        
//login controller with token.
loginControllers.controller('loginControllerWithToken',
        ['$rootScope','$scope', '$location', 'authenticationServiceWithToken',
            function($rootScope,$scope, $location, authenticationServiceWithToken) {
                // reset login status
                authenticationServiceWithToken.clearCredentials();
                $rootScope.currentShopId = sessionStorage.currentShopId='';
                $scope.loginUser = function(administrator) {
                    $scope.dataLoading = true;
                    $rootScope.administratorId='';
                    $scope.resetError();
                    authenticationServiceWithToken.login(administrator.userId, administrator.password)
                            .success(function(data) {
                                authenticationServiceWithToken.setCredentials(data);
                                $rootScope.administratorId=administrator.userId;
                                $scope.administrator.userId = '';
                                $scope.administrator.password = '';

                                if (data.adminRole === 'SUPER') {
                                     $location.path('/main');
                                } else {
                                    $location.path('/inshop');
                                }
                            })
                            .error(function(data) {
                                if(data==="1001"){
                                    $scope.setError("用户名或密码没有输入");
                                }else if(data==="1002"){
                                    $scope.setError("用户名或密码无效");
                                }else if(data==="1003"){
                                    $scope.setError("用户名或密码格式不正确");
                                }else if(data==="1004"){
                                    $scope.setError("认证请求参数无效");
                                }else if(data==="1005"){
                                    $scope.setError("需要认证凭证");
                                }else{
                                    $scope.setError(data);
                                }                            
                                $scope.dataLoading = false;
                            });
                };

                $scope.resetError = function() {
                    $scope.error = false;
                    $scope.errorMessage = '';
                };

                $scope.setError = function(message) {
                    $scope.error = true;
                    $scope.errorMessage = message;
                };
            }]);
