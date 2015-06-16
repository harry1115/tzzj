'use strict';

/* Directives */


//angular.module('TZMediaApp.directives', []).
//  directive('appVersion', ['version', function(version) {
//    return function(scope, elm, attrs) {
//      elm.text(version);
//    };
//  }]);
angular.module('tzmediaDirectives', []).directive('ngConfirmClick', [
    function() {
        return {
            priority: -1,
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('click', function(e) {
                    var message = attrs.ngConfirmClick;
                    if (message && !confirm(message)) {
                        e.stopImmediatePropagation();
                        e.preventDefault();
                    }
                });
            }
        };
    }
]);



//function myCtrl($scope, dataService) {
//    $scope.name = "None";
//    $scope.isBusy = true;
//    dataService.callMe()
//      .then(function () {
//        // Successful
//        $scope.name = "success";
//      }, 
//      function () {
//        // failure
//        $scope.name = "failure";
//      })
//      .then(function () {
//        // Like a Finally Clause
//        $scope.isBusy = false;
//      });
//}

//Check product type id unique
angular.module('tzmediaDirectives')
        .directive('tzzjPtUnique', ['productTypesFactory', function(productTypesFactory) {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function(scope, element, attrs, ngModel) {
                        element.bind('blur', function(e) {
                            if (!ngModel || !element.val()) {
                                return;
                            }
                            var keyProperty = scope.$eval(attrs.tzzjPtUnique);
                            var currentValue = element.val();
                            productTypesFactory.query({typeId: keyProperty.key}).$promise
                                    .then(function(result) {
                                        if (currentValue === element.val()) {
                                            if (result.length > 0) {
                                                //the second parameter is isValid
                                                ngModel.$setValidity('unique', false);
                                            }
                                            else {
                                                ngModel.$setValidity('unique', true);
                                            }
                                        }
                                    }, function() {
                                    });
                        });
                    }
                };
            }]);

//Check product number unique and syntax correct
angular.module('tzmediaDirectives')
        .directive('tzzjPUnique', ['productsFactory', function(productsFactory) {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function(scope, element, attrs, ngModel) {
                        element.bind('blur', function(e) {
                            if (!ngModel || !element.val()) {
                                return;
                            }
                            var keyProperty = scope.$eval(attrs.tzzjPUnique);
                            var currentValue = element.val();
                            var re=/^[a-z0-9]+$/i;
                            if (!re.test(currentValue)) {
                                ngModel.$setValidity('patternValid', false);
                            }else{
                                ngModel.$setValidity('patternValid', true);
                            }
                            
                            productsFactory.query({productNumber: keyProperty.key}).$promise
                                    .then(function(result) {
                                        if (currentValue === element.val()) {
                                            if (result.length > 0) {
                                                //the second parameter is isValid
                                                ngModel.$setValidity('unique', false);
                                            }
                                            else {
                                                ngModel.$setValidity('unique', true);
                                            }
                                        }
                                    }, function() {
                                    });
                        });
                    }
                };
            }]);

//This directive is used to handle the password autofill on firefox does not set the dirty
//to true, then enable the login button. ticket 602315
//http://stackoverflow.com/questions/14965968/angularjs-browser-autofill-workaround-by-using-a-directive/14966711#14966711
//A minor modification to this answer (http://stackoverflow.com/a/14966711/3443828): 
//use an $interval instead of a $timeout so you don't have to race the browser.
angular.module('tzmediaDirectives')
        .directive('autoFillSync', function($interval) {
            return {
                require: 'ngModel',
                link: function(scope, element, attrs, ngModel) {
                    var origVal = element.val();
                    var refresh = $interval(function() {
                        if (!ngModel.$pristine) {
                            $interval.cancel(refresh);
                        } else {
                            var newVal = element.val();
                            if (origVal !== newVal) {
                                ngModel.$setViewValue(newVal);
                                $interval.cancel(refresh);
                            }
                        }
                    }, 100);
                }
            };
        });
  