'use strict';
var tzMediaApp = angular.module('tzMediaApp',
['ngRoute','ngCookies', 'ngInputDate','ngTouch','angularFileUpload','ui.bootstrap','ui.grid','ui.grid.selection','ui.grid.pagination','ui.grid.resizeColumns', 'ui.grid.autoResize',
    'ui.bootstrap.datetimepicker','textAngular','loginControllers','shopControllers', 'playbillControllers','userControllers', 'productControllers', 
    'orderControllers', 'configurationControllers',
    'loginServices', 'shopServices', 'userServices','productServices', 'orderServices', 'fileUploadServices', 'tzmeidaFilters','tzmediaDirectives','configurationServices','MenuController']);

        
tzMediaApp
        .constant("authenticateUrl", baseUrl + "resources/authentication/administrator/")
        .config(['$routeProvider', '$httpProvider',
            function($routeProvider, $httpProvider) {
   
                $routeProvider.when('/login', {
                    templateUrl: 'login/layout.html',
                    controller: 'loginControllerWithToken'
                }).when('/main', {
                    templateUrl: 'main/shop/shop-list.html',
                    controller: 'shopListController'
                }).when('/inshop', {
                      templateUrl: 'main/order/order-list.html',
                    controller: 'orderListController'
                }).when('/singer', {
                    templateUrl: 'main/singer/singer-list.html',
                    controller: 'singerListController'
                }).when('/singer-creation', {
                    templateUrl: 'main/singer/singer-creation.html',
                    controller: 'singerCreationController'
                }).when('/singer-detail/:singerId', {
                    templateUrl: 'main/singer/singer-detail.html',
                    controller: 'singerDetailController'
                }).when('/music', {
                    templateUrl: 'main/music/music-list.html',
                    controller: 'musicListController'
                }).when('/music-creation', {
                    templateUrl: 'main/music/music-creation.html',
                    controller: 'musicCreationController'
                }).when('/music-detail/:singerId', {
                    templateUrl: 'main/music/music-detail.html',
                    controller: 'musicDetailController'
                }).when('/activity', {
                    templateUrl: 'main/activity/activity-list.html',
                    controller: 'activityListController'
                }).when('/activity-detail/:activityName', {
                    templateUrl: 'main/activity/activity-detail.html',
                    controller: 'activityDetailController'
                }).when('/activity-creation', {
                    templateUrl: 'main/activity/activity-creation.html',
                    controller: 'activityCreationController'
                }).when('/system-activity', {
                    templateUrl: 'main/activity-system/activity-list.html',
                    controller: 'system-activityListController'
                }).when('/system-activity-detail/:activityName', {
                    templateUrl: 'main/activity-system/activity-detail.html',
                    controller: 'system-activityDetailController'
                }).when('/system-activity-creation', {
                    templateUrl: 'main/activity-system/activity-creation.html',
                    controller: 'system-activityCreationController'
                }).when('/playbill', {
                    templateUrl: 'main/playbill/playbill-list.html',
                    controller: 'playbillListController'
                }).when('/playbill-creation', {
                    templateUrl: 'main/playbill/playbill-creation.html',
                    controller: 'playbillCreationController'
                }).when('/playbill-detail/:playbillId', {
                    templateUrl: 'main/playbill/playbill-detail.html',
                    controller: 'playbillDetailController'
                }).when('/product', {
                    templateUrl: 'main/product/product-list.html',
                    controller: 'productListController'
                }).when('/product-creation', {
                    templateUrl: 'main/product/product-creation.html',
                    controller: 'productCreationController'
                }).when('/product-detail/:productNumber', {
                    templateUrl: 'main/product/product-detail.html',
                    controller: 'productDetailController'
                }).when('/producttype', {
                    templateUrl: 'main/producttype/producttype-list.html',
                    controller: 'producttypeListController'
                }).when('/producttype-creation', {
                    templateUrl: 'main/producttype/producttype-creation.html',
                    controller: 'producttypeCreationController'
                }).when('/producttype-detail/:typeId', {
                    templateUrl: 'main/producttype/producttype-detail.html',
                    controller: 'producttypeDetailController'
                }).when('/order', {
                    templateUrl: 'main/order/order-list.html',
                    controller: 'orderListController'
                }).when('/order-product-creation', {
                    templateUrl: 'main/order/order-product-creation.html',
                    controller: 'orderProductCreationController'
                }).when('/order-song-creation', {
                    templateUrl: 'main/order/order-song-creation.html',
                    controller: 'orderSongCreationController'
                }).when('/order-song-detail/:orderNumber', {
                    templateUrl: 'main/order/order-song-detail.html',
                    controller: 'orderSongDetailController'
                }).when('/order-product-detail/:orderNumber', {
                    templateUrl: 'main/order/order-product-detail.html',
                    controller: 'orderProductDetailController'
                }).when('/shop', {
                    templateUrl: 'main/shop/shop-list.html',
                    controller: 'shopListController'
                }).when('/shop-creation', {
                    templateUrl: 'main/shop/shop-creation.html',
                    controller: 'shopCreationController'
                }).when('/shop-detail/:shopId', {
                    templateUrl: 'main/shop/shop-detail.html',
                    controller: 'shopDetailController'
                }).when('/user', {
                    templateUrl: 'main/user/user-list.html',
                    controller: 'userListController'
                }).when('/user-detail/:userId', {
                    templateUrl: 'main/user/user-detail.html',
                    controller: 'userDetailController'
                }).when('/userfeedback', {
                    templateUrl: 'main/userfeedback/userfeedback-list.html',
                    controller: 'userfeedbackListController'
                }).when('/coupondefinition', {
                    templateUrl: 'main/coupondefinition/coupondefinition-list.html',
                    controller: 'couponDefinitionListController'
                }).when('/coupondefinition-detail/:couponDefinitionNumber', {
                    templateUrl: 'main/coupondefinition/coupondefinition-detail.html',
                    controller: 'couponDefinitionDetailController'
                }).when('/coupondefinition-creation', {
                    templateUrl: 'main/coupondefinition/coupondefinition-creation.html',
                    controller: 'couponDefinitionCreationController'
                }).when('/administrator', {
                    templateUrl: 'main/administrator/administrator-list.html',
                    controller: 'administratorListController'
                }).when('/administrator-detail/:userId', {
                    templateUrl: 'main/administrator/administrator-detail.html',
                    controller: 'administratorDetailController'
                }).when('/administrator-creation', {
                    templateUrl: 'main/administrator/administrator-creation.html',
                    controller: 'administratorCreationController'
                }).when('/password', {
                    templateUrl: 'main/password/changepassword.html',
                    controller: 'changePasswordController'
                }).when('/configuration', {
                    templateUrl: 'main/configuration/configuration-detail.html',
                    controller: 'configurationController'    
                }).when('/ticket', {
                    templateUrl: 'main/ticket/ticket-list.html',
                    controller: 'ticketListController'
                }).when('/ticket-detail/:ticketId', {
                    templateUrl: 'main/ticket/ticket-detail.html',
                    controller: 'ticketDetailController'
                }).when('/ticket-creation', {
                    templateUrl: 'main/ticket/ticket-creation.html',
                    controller: 'ticketCreationController'
                }).when('/system-password', {
                    templateUrl: 'main/password_System/changepassword.html',
                    controller: 'changePasswordController'
                }).when('/logout', {
                    redirectTo: '/login'
                }).otherwise({
                    redirectTo: '/login'
                });
                $httpProvider.interceptors.push('sessionInjector');

//                $httpProvider.defaults.withCredentials = true;
                /* CORS... */
                /* http://stackoverflow.com/questions/17289195/angularjs-post-data-to-external-rest-api */
                $httpProvider.defaults.useXDomain = true;
                delete $httpProvider.defaults.headers.common['X-Requested-With'];
            }]);



tzMediaApp.run(['$rootScope', '$location', 'sessionService',
    function($rootScope, $location, sessionService) {

        /*var routesThatForSupers = ['administrator','/user','/configuration'];
        var routesThatForBackends = ['/singer','/song','/activity','/frontpage'];
        var routeSuper = function(url) {
            for(var i=0;i<routesThatForSupers.length;i++){
                if(url.indexOf(routesThatForSupers[i])>=0){
                    return true;
                }
            }
            return false;
        };
        var routeBackend = function(url) {
            for(var i=0;i<routesThatForBackends.length;i++){
                if(url.indexOf(routesThatForBackends[i])>=0){
                    return true;
                }
            }
            return false;
        };*/
        
   
        // keep user logged in after page refresh
        $rootScope.$on('$routeChangeStart', function(event, next, current) {
            // redirect to login page if not logged in
            if($location.path()=="/login"){
                $rootScope.isLoginView=true;
                $rootScope.pageClass="login-bg";
            }else{
                $rootScope.isLoginView=false;
                if($location.path()=="/order"||$location.path()=="/inshop"){
                    $rootScope.pageClass="oh bg_color";
                }else if($location.path()=="/playbill"||$location.path()=="/product"){
                    $rootScope.pageClass="";
                }else{
                    $rootScope.pageClass="bg_color";
                }
            }
            if(sessionService.getShopId()==0){
                $rootScope.currentShopId = sessionStorage.currentShopId;
            }else{
                $rootScope.currentShopId = sessionService.getShopId();
            }
            /*if (sessionService.isAnonymus()) {
//                alert(next.templateUrl);
                if (next.templateUrl === 'login/layout.html'
                        //mobile user reset password, let it go.
                        ||next.templateUrl === 'main/forgetpswd/changepassword.html') {
                    // already going to #login, no redirect needed
                } else {
                    // not going to #login, we should redirect now
                    $location.path("/login");
                }
            }
            if (sessionService.getAdminRole() === 'INSHOP'
                    && (typeof next.templateUrl!=='undefined') 
                    &&(routeBackend(next.templateUrl)
                            || routeSuper(next.templateUrl))) {
                //$location.path("/login");
            }
            if (sessionService.getAdminRole() === 'BACKEND'
                    && (typeof next.templateUrl!=='undefined')
                    && routeSuper(next.templateUrl)) {
                $location.path("/login");
            }
            if (sessionService.getAdminRole() === 'INSHOP'
                    && next.templateUrl === 'main/layout.html') {
                $location.path("/inshop");
            }
            if (sessionService.getAdminRole() === 'BACKEND'
                    && next.templateUrl === 'main/layout.html') {
             
                $location.path("/backend");
            }*/
          
        });
    }]);

tzMediaApp.factory('sessionInjector', ['sessionService', function(sessionService) {
        var sessionInjector = {
            request: function(config) {
                if (!sessionService.isAnonymus()) {
                    config.headers['AUTHENTICATIONTOKEN'] = sessionService.getSessionId();
                    config.headers['Authorization'] = '';
                }
                //Identify request come from web.
                config.headers['AUTHENTICATIONWEB'] = "REQUESTCOMEFROMWEB";
                return config;
            }
        };
        return sessionInjector;
    }]);
tzMediaApp.userTypes = [
            {id: 'DUDU', name: '嘟嘟'},
            {id: 'QQ', name: 'QQ'},
            {id: 'WEIBO', name: '微博'},
            {id: 'MOBILE', name: '手机'},
            {id: 'EMAIL', name: '邮箱'}];  

tzMediaApp.userRoles = [
            {id: 'NORMAL', name: '普通'},
            {id: 'SINGER', name: '歌手'},
            {id: 'INTERNAL', name: '内部'}];

tzMediaApp.badgeTypes = [
            {id: 'NORMAL', name: '普通'},
            {id: 'FAMOUS', name: '著名'}];
tzMediaApp.frontPageTypes = [
            {id: 'SHOP', name: '店铺'},
            {id: 'ACTIVITY',     name: '活动'},
            {id: 'SINGER',     name: '歌手'}];

tzMediaApp.orderTypes = [
            {id: 'NORMAL', name: '普通'},
            {id: 'GRABSONG', name: '抢歌'}];  
tzMediaApp.paymentTerms = [
            {id: 'CASH', name: '现金'},
            {id: 'ONLINE', name: '在线支付'}];
tzMediaApp.genders = [
            {id: 'M', name: '男'},
            {id: 'F', name: '女'}];

tzMediaApp.adminRoles = [
            {id: 'INSHOP', name: '店铺管理员'},
            {id: 'SUPER', name: '平台管理员'}];
        
tzMediaApp.promotionTypes = [
            {id: 'BYQUANTITY', name: '买M送N'},
            {id: 'BYPRODUCT', name: '买1送N'},
            {id: 'BYAMOUNT', name: '买满优惠'},
            {id: 'BYDISCOUNT', name: '促销价'}];                  
tzMediaApp.productShowTypes = [
            {id: 'NORMAL', name: '普通'},
            {id: 'SUPERSCRIPT', name: '角标'},
            {id: 'POPUP', name: '弹窗'}];  
tzMediaApp.playbillStates = [
            {id: 'ACTIVE', name: '可抢歌'},
            {id: 'INACTIVE', name: '不可抢'},
            {id: 'PAID', name: '已支付'}]; 
        
tzMediaApp.couponStatusEnum = [
            {id: 'NEW', name: '未使用'},
            {id: 'USED', name: '已使用'},
            {id: 'EXPIRED', name: '已过期'}];       
        
tzMediaApp.actionTypes = [
            {id: 'PRAISE', name: '赞'},
            {id: 'COMMENT', name: '评论'},
            {id: 'GRABSONG', name: '抢歌'},
            {id: 'PAY', name: '支付'}];
        
 tzMediaApp.productStates = [
            {id: true, name: '正常'},
            {id: false, name: '下架'}];  
        
tzMediaApp.orderStatusEnum = [
            {id: 'TOBEPAID', name: '待支付'},
            {id: 'TOBECOLLECTED', name: '待收款'},
            {id: 'PAID', name: '已开单'},
            {id: 'PROCESSED', name: '已处理'},
            {id: 'OVERTIME', name: '已超时'},
            {id: 'CLOSED', name: '已关闭'},
            {id: 'REFUNDED', name: '已退款'}];
tzMediaApp.ticketStatus = [
    {id: 'PORESELL', name: '预售'},
    {id: 'HOT', name: '热卖'},
    {id: 'SELLOUT', name: '售完'}];

tzMediaApp.ticketData=[
    {'ticketId':'1','ticketName':'情人节演出入场券','count':'500','residue':'0','prize':'200','ActivityId':'52','shopId':'1','status':'SELLOUT','fromTime':'2014-06-01','toTime':'2014-06-01','remark':'备注信息'},
    {'ticketId':'2','ticketName':'情人节演出入场券2','count':'500','residue':'200','prize':'200','ActivityId':'52','shopId':'1','status':'PORESELL','fromTime':'2014-06-01','toTime':'2014-06-01','remark':'备注信息'}
];

//Used for set how many lines per page        
//tzMediaApp.pageSize=10;
tzMediaApp.rowsPerPage=100;
//The max pages show below the table
//tzMediaApp.maxPages=10;

//milliseconds waiting after image upload.
tzMediaApp.imageUploadWaitTime=500;

//Run with cookie
//tzMediaApp.run(['$rootScope', '$location', '$cookieStore', '$http',
//    function($rootScope, $location, $cookieStore, $http) {
//        // keep user logged in after page refresh
//        $rootScope.globals = $cookieStore.get('globals') || {};
//        if ($rootScope.globals.currentUser) {
//            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata; // jshint ignore:line
//        }
//        $rootScope.$on('$routeChangeStart', function(event, next, current) {
//            // redirect to login page if not logged in
//            if (!$rootScope.globals.currentUser) {
//                if (next.templateUrl !== "login/layout.html") {
//                    // already going to #login, no redirect needed
//                } else {
//                    // not going to #login, we should redirect now
//                    $location.path("/login");
//                }
//            }
//        });
//    }]);
