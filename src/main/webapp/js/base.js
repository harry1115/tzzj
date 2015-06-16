var MenuController = angular.module('MenuController', []);

MenuController.controller('LoadMenu',
    ['sessionService', '$scope', 'administratorFactory', 'shopFactory', 'shopsFactory', '$rootScope', '$location', '$route',
        function (sessionService, $scope, administratorFactory, shopFactory, shopsFactory, $rootScope, $location, $route) {
            if($location.path()!=="/login") {
                var currentUserId = sessionService.getUserId();
                $rootScope.currentAdminRole = sessionService.getAdminRole();
                $rootScope.shops = $scope.shops = shopsFactory.query();
                $scope.administrator = administratorFactory.show({userId: currentUserId});
                $scope.getShopName = function (shopId) {

                    for (var i = 0; i < $scope.shops.length; i++) {
                        if (shopId === $scope.shops[i].shopId) {
                            return $scope.shops[i].shopName;
                        }
                    }
                }
                if ($location.path() == "/main") {
                    $rootScope.currentEntrance = sessionStorage.currentEntrance = "admin";
                } else {
                    $rootScope.currentEntrance = sessionStorage.currentEntrance;
                }
                $scope.changeAdmin = function () {
                    $('.dropdown-menu').fadeOut();
                    $rootScope.currentEntrance = sessionStorage.currentEntrance = "admin";
                    $location.path("/main");
                };
                $scope.changeShop = function (shop) {
                    $('.dropdown-menu').fadeOut();
                    if ($rootScope.currentEntrance == "admin") {
                        $rootScope.currentEntrance = sessionStorage.currentEntrance = shop.shopName;
                        $rootScope.currentShopId = sessionStorage.currentShopId = shop.shopId;
                        $location.path("/order");
                    } else {
                        $rootScope.currentEntrance = sessionStorage.currentEntrance = shop.shopName;
                        $rootScope.currentShopId = sessionStorage.currentShopId = shop.shopId;
                        $route.reload();
                    }


                };
                $scope.msg = {};
                $scope.handleCallback = function (event) {
                    $scope.$apply(function () {
                        $scope.msg = JSON.parse(event.data);
                    });
                };

                $scope.listen = function () {
                    $scope.orderNotification = new EventSource(baseUrl + "resources/ordernotification/events");
                    $scope.orderNotification.addEventListener("order-notification", $scope.handleCallback, false);
                };

                $rootScope.closeConnection = function () {
                    $scope.orderNotification.close();
                };
                $scope.listen();

                $scope.searchNewOrder = function () {
                    $rootScope.orderType = 'NORMAL';
                    if ($location.path() == '/order') {
                        $route.reload();
                    } else {
                        $location.path('/order');
                    }

                };
                $scope.searchGrabSongOrder = function () {
                    $rootScope.orderType = 'GRABSONG';
                    if ($location.path() == '/order') {
                        $route.reload();
                    } else {
                        $location.path('/order');
                    }

                };
                $scope.openSubmenu = function (event) {
                    if ($(event.target).hasClass("active")) {
                        return;
                    }
                    $(".menu a").removeClass("active");
                    $(".menu a").next(".submenu").slideUp("fast");
                    $(event.target).addClass("active");
                    $(event.target).next(".submenu").slideDown("fast");
                };
                $scope.submenuClick = function (event) {
                    $(".submenu a").removeClass("active");
                    $(event.target).addClass("active");
                    $(".menu>li>a").removeClass("active");
                    $(event.target).closest(".submenu").prev().addClass("active");
                };
                $rootScope.initTableHeight=function() {
                    var tableHeight = $(window).height() - $("#cgGrid").offset().top - $(".page").outerHeight();
                    return {
                        height: tableHeight + "px"
                    };
                }
            }

        }]);
Date.prototype.format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(), //day
        "h+": this.getHours(), //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1,
            (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(format))
            format = format.replace(RegExp.$1,
                RegExp.$1.length == 1 ? o[k] :
                    ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}
Date.prototype.DateAdd = function (strInterval, Number) {
    var dtTmp = this;
    switch (strInterval) {
        case 's' :
            return new Date(Date.parse(dtTmp) + (1000 * Number));
        case 'n' :
            return new Date(Date.parse(dtTmp) + (60000 * Number));
        case 'h' :
            return new Date(Date.parse(dtTmp) + (3600000 * Number));
        case 'd' :
            return new Date(Date.parse(dtTmp) + (86400000 * Number));
        case 'w' :
            return new Date(Date.parse(dtTmp) + ((86400000 * 7) * Number));
        case 'q' :
            return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number * 3, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());
        case 'm' :
            return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());
        case 'y' :
            return new Date((dtTmp.getFullYear() + Number), dtTmp.getMonth(), dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());
    }
}

       
        
      