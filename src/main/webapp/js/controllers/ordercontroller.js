'use strict';
/* Controllers */

var orderControllers = angular.module('orderControllers', []);
//Order Controller
orderControllers.controller('orderListController',
        ['$rootScope', '$scope', 'ordersFactory', 'orderFactory', 'ordersReportFactory', '$location', 'uiGridConstants',
            function($rootScope, $scope, ordersFactory, orderFactory, ordersReportFactory, $location, uiGridConstants) {
                var couponTemplate = '<div class="p10" style="padding-left:20px;" ng-class="{\'mark-coupon\':row.entity.couponAmount>0}"><a ng-mouseover="getExternalScopes().showOrder(row.entity,$event)" ng-mouseleave="getExternalScopes().hideOrder()" style="display:inline-block;height:30px;line-height:30px; text-decoration: none;cursor:default;">{{row.entity.orderNumber}}</a></div>';
                var detailTemplate = '<div class="p10">\n\
                    <button type="button" class="btn btn-success fl mr10" title="编辑" ng-click="getExternalScopes().editOrder(row.entity)"><i class="icon icon-white icon-pencil"></i></button>\n\
                    <button type="button" class="btn btn-warning fl mr10" title="关闭" ng-show="getExternalScopes().disableCloseButton(row.entity)" ng-click="getExternalScopes().closeOrder(row.entity.orderNumber)"><i class="icon icon-white icon-ban-circle"></i></button>\n\
                    <button type="button" class="btn btn-warning fl mr10 hide" title="退款" ng-show="getExternalScopes().disableProcessButton(row.entity)" ng-click="getExternalScopes().processOrder(row.entity.orderNumber)"><i class="icon icon-white icon-exclamation-sign"></i></button>\n\
                    <button type="button" class="btn btn-complete fl mr10" title="已开单" ng-show="getExternalScopes().disableCollectFnButton(row.entity)" ng-click="getExternalScopes().collectFnOrder(row.entity.orderNumber)" ><i class="icon icon-white icon-check"></i> </button>\n\
                    <button type="button" class="btn btn-complete fl mr10" title="已处理" ng-show="getExternalScopes().disableProcessButton(row.entity)" ng-click="getExternalScopes().processOrder(row.entity.orderNumber)"><i class="icon icon-white icon-check"></i></button>\n\
                    <button type="button" class="btn btn-danger fl" title="删除" ng-show="getExternalScopes().disableDeleteButton(row.entity)" ng-confirm-click="删除的订单不能找回，你确定要删除么？" ng-click="getExternalScopes().deleteOrder(row.entity.orderNumber)"><i class="icon icon-white icon-trash"></i></button></div>';
                $scope.columns = [
                    {name: 'orderNumber', displayName: '订单编号',minWidth:200, cellTemplate: couponTemplate, suppressRemoveSort: true},
                    {field: 'orderType', displayName: '订单类型', width: 80, cellFilter: 'orderTypeFilter', suppressRemoveSort: true},
                    {field: 'userName', displayName: '用户名',  width: 150,suppressRemoveSort: true},
                    {field: 'tableNumber', displayName: '桌号', width: 80, suppressRemoveSort: true},
                    {field: 'paymentTerm', displayName: '支付方式', width: 80, cellFilter: 'paymentTermFilter', suppressRemoveSort: true},
                    {field: 'orderAmount', displayName: '总金额', width: 100, cellFilter: 'currency:"￥"', suppressRemoveSort: true},
                    {field: 'orderTime', displayName: '订单时间', width: 150, cellFilter: 'date:\'yyyy-MM-dd HH:mm:ss\'', suppressRemoveSort: true, sort: {direction: uiGridConstants.DESC}},

                    {name: 'detail', displayName: '操作', minWidth: 180, cellTemplate: detailTemplate,enableSorting: false}
                ];
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
                    enableSelectionBatchEvent: false,
                    columnDefs: $scope.columns};
                $scope.gridOptions.onRegisterApi = function(gridApi) {
                    $scope.gridApi = gridApi;
                    gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                        
                        if (row.isSelected) {
                            $scope.selectionAmount = parseInt($scope.selectionAmount)+ parseInt(row.entity.orderAmount);
                        } else {
                            $scope.selectionAmount = parseInt($scope.selectionAmount)- parseInt(row.entity.orderAmount);
                        }
                    });
                };
                
                $scope.selectAll = function() {
                    $scope.gridApi.selection.selectAllRows();
                };
                $scope.clearAll = function() {
                    $scope.gridApi.selection.clearSelectedRows();
                };

                $scope.filterOrderList = function(orderStatusEnum) {
                    $scope.clearAll();
                    $(".ui-grid-all-selected").removeClass("ui-grid-all-selected");

                    $scope.orderSearchCondition.orderStatus = orderStatusEnum;
                    $scope.orderSearchCondition.hasToBeProcessed=false;
                    $scope.searchOrder();
                };
                $scope.nextPage = function() {
                    if ($scope.currentPage === $scope.gridApi.pagination.getTotalPages()) {
                        return;
                    }
                    $scope.gridApi.pagination.nextPage();
                    $scope.currentPage = $scope.currentPage + 1;
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
                $scope.orderListScope = {
                    showOrder: function(customerOrder, event) {
                        event.preventDefault();

                        $scope.customerOrders = customerOrder;
                        $scope.customerOrderLines = customerOrder.customerOrderLines;
                        $scope.showPopover;
                        var currentTop=0;
                        var currentLeft =0;
                        var popoverHeight=0;
                        if (customerOrder.orderType === "NORMAL") {
                            $scope.showPopover = $(".popover-success");
                            popoverHeight=$scope.customerOrderLines.length*45+233;

                        } else {
                            $scope.showPopover = $(".popover-danger");
                            popoverHeight=$scope.showPopover.outerHeight();

                        }
                        if(($(event.target).offset().top+popoverHeight/2)>=$(window).height()){
                            currentTop = $(window).height() - popoverHeight;
                        }else if($(event.target).offset().top <= popoverHeight/2){
                            currentTop = 0;
                        }else{
                            currentTop = $(event.target).offset().top - popoverHeight/2;
                        }


                        currentLeft = $(event.target).offset().left + $(event.target).width();
                        $scope.showPopover.show().offset({top: currentTop, left: currentLeft});
                    },
                    hideOrder: function() {
                        $scope.showPopover.hide();
                    },
                    deleteOrder: function(orderNumber) {
                        orderFactory.delete({orderNumber: orderNumber}).$promise.then(function(){
                                $scope.filterOrderList($scope.orderSearchCondition.orderStatus,$scope);
                            });

                    },
                    editOrder: function(order) {
                        $scope.closeConnection();
                        $rootScope.orderSearchCondition = $scope.orderSearchCondition;
                        if (order.singerId > 0) {
                            $location.path('/order-song-detail/' + order.orderNumber);
                        } else {
                            $location.path('/order-product-detail/' + order.orderNumber);
                        }
                    },
                    processOrder: function(orderNumber) {
                        for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                            if ($scope.gridOptions.data[i].orderNumber === orderNumber) {
                                $scope.gridOptions.data[i].orderStatus = 'PROCESSED';
                                orderFactory.update($scope.gridOptions.data[i]).$promise.then(function(){
                                    $scope.gridOptions.data.splice(i, 1);

                                });
                                break;
                            }
                        }
                    },
                    collectFnOrder: function(orderNumber) {
                        for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                            if ($scope.gridOptions.data[i].orderNumber === orderNumber) {
                                $scope.gridOptions.data[i].orderStatus = 'PAID';
                                orderFactory.update($scope.gridOptions.data[i]).$promise.then(function(){
                                    $scope.gridOptions.data.splice(i, 1);

                                });
                                break;
                            }
                        }
                    },
                    closeOrder: function(orderNumber) {
                        for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                            if ($scope.gridOptions.data[i].orderNumber === orderNumber) {
                                $scope.gridOptions.data[i].orderStatus = 'CLOSED';
                                orderFactory.update($scope.gridOptions.data[i]).$promise.then(function(){
                                    $scope.gridOptions.data.splice(i, 1);

                                });
                                break;
                            }
                        }
                    },
                    disableProcessButton: function(order) {
                        if (order.orderStatus === 'PAID') {
                            return true;
                        }
                        return false;
                    },
                    disableRefundButton: function(order) {
                        if (order.orderStatus === 'PAID') {
                            return true;
                        }
                        return false;
                    },
                    disableCollectFnButton: function(order) {
                        if (order.orderStatus === 'TOBECOLLECTED') {
                            return true;
                        }
                        return false;
                    },
                    disableCloseButton: function(order) {
                        if (order.orderStatus === 'TOBEPAID' || order.orderStatus === 'TOBECOLLECTED') {
                            return true;
                        }
                        return false;
                    },
                    disableDeleteButton: function(order) {
                        if (order.orderStatus === 'PAID' || order.orderStatus === 'PROCESSED') {
                            return false;
                        }
                        return true;
                    }

                };
                /* callback for ng-click 'createOrder': */
                $scope.createProductOrder = function() {
                    $rootScope.orderSearchCondition = $scope.orderSearchCondition;
                    $scope.closeConnection();
                    $location.path('/order-product-creation');
                };
                $scope.createSongOrder = function() {
                    $rootScope.orderSearchCondition = $scope.orderSearchCondition;
                    $scope.closeConnection();
                    $location.path('/order-song-creation');
                };
                $scope.initSearchCondition = function() {
                    if ((typeof $rootScope.orderSearchCondition !== 'undefined')
                            && $rootScope.orderSearchCondition !== null) {
                        $scope.orderSearchCondition = $rootScope.orderSearchCondition;
                    }

                    if ((typeof $scope.orderSearchCondition === 'undefined')
                            || $scope.orderSearchCondition === null) {
                        $scope.orderSearchCondition = {
                            hasToBeProcessed:true,
                            fromOrderDate: moment(new Date()).format("YYYY-MM-DD"),
                            toOrderDate: moment(new Date()).format("YYYY-MM-DD")};
                    }
                    if ((typeof $rootScope.orderType !== 'undefined')
                        && $rootScope.orderType !== null) {
                        $scope.orderSearchCondition.orderType = $rootScope.orderType;
                    }

                };
                $scope.getReport = function() {
                    $scope.initSearchCondition();
                    ordersReportFactory.runReport({
                        orderNumber: $scope.orderSearchCondition.orderNumber,
                        userId: $scope.orderSearchCondition.userId,
                        playbillId: $scope.orderSearchCondition.playbillId,
                        orderStatus: $scope.orderSearchCondition.orderStatus,
                        fromOrderDate: moment($scope.orderSearchCondition.fromOrderDate).format("YYYY-MM-DD"),
                        toOrderDate:moment($scope.orderSearchCondition.toOrderDate).format("YYYY-MM-DD"),
                        tableNumber: $scope.orderSearchCondition.tableNumber,
                        userName: $scope.orderSearchCondition.userName,
                        orderType: $scope.orderSearchCondition.orderType,
                        paymentTerm: $scope.orderSearchCondition.paymentTerm,
                        shopId: $rootScope.currentShopId
                    }).$promise.then(function(data) {
                        window.open(data.reportName);
                    }, function(result) {
                        alert("报表导出失败" + result.data);
                    });
                };
                $scope.searchNewOrder = function() {
                    $scope.orderSearchCondition = {
                        hasToBeProcessed:true,
                        fromOrderDate: moment(new Date()).format("YYYY-MM-DD"),
                        toOrderDate: moment(new Date()).format("YYYY-MM-DD")};
                    $scope.searchOrder();
                };

                $scope.searchOrder = function() {

                    $scope.isLoading=true;

                    $scope.selectionAmount = 0;

                    $scope.gridOptions.data = ordersFactory.query({
                        orderNumber: $scope.orderSearchCondition.orderNumber,
                        userId: $scope.orderSearchCondition.userId,
                        playbillId: $scope.orderSearchCondition.playbillId,
                        orderStatus: $scope.orderSearchCondition.orderStatus,
                        fromOrderDate: moment($scope.orderSearchCondition.fromOrderDate).format("YYYY-MM-DD"),
                        toOrderDate:moment($scope.orderSearchCondition.toOrderDate).format("YYYY-MM-DD"),
                        tableNumber: $scope.orderSearchCondition.tableNumber,
                        userName: $scope.orderSearchCondition.userName,
                        orderType: $scope.orderSearchCondition.orderType,
                        paymentTerm: $scope.orderSearchCondition.paymentTerm,
                        hasToBeProcessed:$scope.orderSearchCondition.hasToBeProcessed,
                        shopId: $rootScope.currentShopId
                    },function(){
                        $scope.isLoading=false;
                    });
                    
                };

                $scope.clearOrder = function() {
                    $scope.currentPage = 1;
                    $scope.orderSearchCondition = {};
                    $scope.orderSearchCondition = {
                        hasToBeProcessed:true,
                        fromOrderDate: moment(new Date()).format("YYYY-MM-DD"),
                        toOrderDate: moment(new Date()).format("YYYY-MM-DD")};
                    $scope.searchOrder();
                };
                $scope.selectionAmount = 0;
                $scope.currentPage = 1;
                $scope.orderStatuz = tzMediaApp.orderStatusEnum;
                $scope.orderTypes = tzMediaApp.orderTypes;
                $scope.paymentTerms = tzMediaApp.paymentTerms;
                $scope.initSearchCondition();
                $scope.searchOrder();


            }]);
orderControllers.controller('orderProductCreationController', ['$rootScope','$scope', 'ordersFactory', '$location',
    function($rootScope,$scope, ordersFactory, $location) {
        $scope.order={};
        $scope.order.orderNumber = new Date().format("yyyyMMddhhmmss");
        $scope.order.shopId=$rootScope.currentShopId;
        $scope.order.orderTime=new Date();
        $scope.createOrder = function() {
            $scope.order.customerOrderLines=$scope.customerOrderLines;
            ordersFactory.create($scope.order).$promise.then(function(result){
                alert(JSON.stringify(result));
                //$location.path('/order');
            },function(){
                alert(1);
            });

        };
        /* callback for ng-click 'cancel': */
        $scope.cancel = function() {
            $location.path('/order');
        };
        $scope.deleteCustomerOrderLine = function(customerOrderLine) {
            var index = $scope.customerOrderLines.indexOf(customerOrderLine);
            if (index > -1) {
                $scope.customerOrderLines.splice(index, 1);
            }
            $scope.clearLineField();
        };
        $scope.openAddOrderLine = function() {
            $scope.isAddOrderLine = true;
        };
        $scope.closeAddOrderLine = function() {
            $scope.isAddOrderLine = false;
            $scope.clearLineField();
        };
        $scope.addOrderLine = function() {
            var nextLineNumber = 0;
            for (var i = 0; i < $scope.customerOrderLines.length; i++) {
                if ($scope.customerOrderLines[i].lineNumber === $scope.orderLine.lineNumber) {
                    alert("当前订单行已经添加到列表中，请不要重复添加。");
                    return;
                }
                if ($scope.customerOrderLines[i].lineNumber > nextLineNumber) {
                    nextLineNumber = $scope.customerOrderLines[i].lineNumber;
                }
            }
            nextLineNumber = nextLineNumber + 1;
            var orderLineTemp = {lineNumber: nextLineNumber,
                productName: $scope.orderLine.productName,
                orderQuantity: $scope.orderLine.orderQuantity,
                price: $scope.orderLine.price,
                lineAmount: $scope.orderLine.price * $scope.orderLine.orderQuantity
            };
            $scope.customerOrderLines.push(orderLineTemp);
            $scope.closeAddOrderLine();
        };
        $scope.closeEditOrderLine = function(index) {
            $scope.currentIndex = -1;
            $scope.customerOrderLines[index] = $scope.currentRowLine;
        };
        $scope.saveOrderLine = function(customerOrderLine, index) {

            $scope.customerOrderLines[index].productNumber = customerOrderLine.productNumber;
            $scope.customerOrderLines[index].productName = customerOrderLine.productName;
            $scope.customerOrderLines[index].orderQuantity = customerOrderLine.orderQuantity;
            $scope.customerOrderLines[index].price = customerOrderLine.price;
            $scope.customerOrderLines[index].lineAmount = customerOrderLine.price * customerOrderLine.orderQuantity;
            $scope.closeEditOrderLine();

        };
        $scope.editCustomerOrderLine = function(customerOrderLine, index) {
            $scope.currentIndex = index;
            $scope.currentRowLine = angular.copy(customerOrderLine);
        };
        $scope.clearLineField = function() {
            $scope.orderLine.productName = '';
            $scope.orderLine.orderQuantity = '';
            $scope.orderLine.price = '';
        };
        $scope.customerOrderLines =[];
        $scope.currentIndex = -1;
        $scope.orderStatuz = tzMediaApp.orderStatusEnum;
        $scope.paymentTerms = tzMediaApp.paymentTerms;
    }]);
orderControllers.controller('orderSongDetailController',
        ['$scope', '$routeParams', 'orderFactory', '$location',
            function($scope, $routeParams, orderFactory, $location) {
                /* callback for ng-click 'updateOrderp': */
                $scope.updateOrder = function() {
                    if ($scope.customerOrderLines.length === 0) {
                        alert("订单不能没有订单行。");
                        return;
                    }
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });

                };
                $scope.deleteOrder = function() {
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("处理完成的订单不能删除。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PAID') {
                        alert("支付完成的订单不能删除。");
                        return;
                    }
                    orderFactory.delete({orderNumber: $scope.order.orderNumber}).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.closeOrder = function() {
                    if ($scope.order.orderStatus === 'CLOSED') {
                        alert("订单已经关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("处理完成的订单不能关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PAID') {
                        alert("支付完成的订单不能关闭。");
                        return;
                    }
                    $scope.order.orderStatus = 'CLOSED';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.processOrder = function() {
                    if ($scope.customerOrderLines.length === 0) {
                        alert("订单不能没有订单行。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("订单已经处理完成。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'CLOSED') {
                        alert("订单已经关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'TOBEPAID') {
                        alert("待支付的订单不能处理。");
                        return;
                    }

                    $scope.order.orderStatus = 'PROCESSED';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.refundOrder = function() {
                    if ($scope.order.orderStatus !== 'PAID') {
                        alert("没付款的订单不能退款。");
                        return;
                    }

                    $scope.order.orderStatus = 'REFUNDED';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/order');
                };
                $scope.deleteCustomerOrderLine = function(customerOrderLine) {
                    var index = $scope.customerOrderLines.indexOf(customerOrderLine);
                    if (index > -1) {
                        $scope.customerOrderLines.splice(index, 1);
                    }
                    $scope.clearLineField();
                };
                $scope.openAddOrderLine = function() {
                    $scope.isAddOrderLine = true;
                };
                $scope.closeAddOrderLine = function() {
                    $scope.isAddOrderLine = false;
                    $scope.clearLineField();
                };
                $scope.addOrderLine = function() {
                    var nextLineNumber = 0;
                    for (var i = 0; i < $scope.customerOrderLines.length; i++) {
                        if ($scope.customerOrderLines[i].lineNumber === $scope.orderLine.lineNumber) {
                            alert("当前订单行已经添加到列表中，请不要重复添加。");
                            return;
                        }
                        if ($scope.customerOrderLines[i].lineNumber > nextLineNumber) {
                            nextLineNumber = $scope.customerOrderLines[i].lineNumber;
                        }
                    }
                    nextLineNumber = nextLineNumber + 1;
                    var orderLineTemp = {lineNumber: nextLineNumber,
                        //productNumber: $scope.orderLine.productNumber,
                        productName: $scope.orderLine.productName,
                        orderQuantity: $scope.orderLine.orderQuantity,
                        price: $scope.orderLine.price,
                        lineAmount: $scope.orderLine.price * $scope.orderLine.orderQuantity
                    };
                    $scope.customerOrderLines.push(orderLineTemp);
                    $scope.closeAddOrderLine();
                };
                $scope.closeEditOrderLine = function(index) {
                    $scope.currentIndex = -1;
                    $scope.customerOrderLines[index] = $scope.currentRowLine;
                };
                $scope.saveOrderLine = function(customerOrderLine, index) {

                    $scope.customerOrderLines[index].productNumber = customerOrderLine.productNumber;
                    $scope.customerOrderLines[index].productName = customerOrderLine.productName;
                    $scope.customerOrderLines[index].orderQuantity = customerOrderLine.orderQuantity;
                    $scope.customerOrderLines[index].price = customerOrderLine.price;
                    $scope.customerOrderLines[index].lineAmount = customerOrderLine.price;
                    $scope.closeEditOrderLine();

                };
                $scope.editCustomerOrderLine = function(customerOrderLine, index) {
                    $scope.currentIndex = index;
                    $scope.currentRowLine = angular.copy(customerOrderLine);
                };
                $scope.clearLineField = function() {
                    $scope.orderLine.songName = '';
                    $scope.orderLine.playbillId = '';
                    $scope.orderLine.playbillName = '';
                    $scope.orderLine.grabComment = '';
                    $scope.orderLine.price = '';
                    $scope.orderLine.lineAmount = '';
                };
                orderFactory.show({orderNumber: $routeParams.orderNumber}).$promise
                        .then(function(result) {
                            $scope.order = result;
                            $scope.customerOrderLines = $scope.order.customerOrderLines;
                        }, function() {
                        });

                $scope.orderStatuz = tzMediaApp.orderStatusEnum;
                $scope.paymentTerms = tzMediaApp.paymentTerms;
            }]);
orderControllers.controller('orderProductDetailController',
        ['$scope', '$routeParams', 'orderFactory', '$location',
            function($scope, $routeParams, orderFactory, $location) {
                /* callback for ng-click 'updateOrderp': */
                $scope.updateOrder = function() {
                    if ($scope.customerOrderLines.length === 0) {
                        alert("订单不能没有订单行。");
                        return;
                    }
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.collectFnOrder = function() {
                    if ($scope.customerOrderLines.length === 0) {
                        alert("订单不能没有订单行。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("订单已经处理完成。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'CLOSED') {
                        alert("订单已经关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'TOBEPAID') {
                        alert("待支付的订单不能处理。");
                        return;
                    }

                    $scope.order.orderStatus = 'PAID';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.processOrder = function() {
                    if ($scope.customerOrderLines.length === 0) {
                        alert("订单不能没有订单行。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("订单已经处理完成。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'CLOSED') {
                        alert("订单已经关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'TOBEPAID') {
                        alert("待支付的订单不能处理。");
                        return;
                    }

                    $scope.order.orderStatus = 'PROCESSED';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.closeOrder = function() {
                    if ($scope.order.orderStatus === 'CLOSED') {
                        alert("订单已经关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("处理完成的订单不能关闭。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PAID') {
                        alert("支付完成的订单不能关闭。");
                        return;
                    }
                    $scope.order.orderStatus = 'CLOSED';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.deleteOrder = function() {
                    if ($scope.order.orderStatus === 'PROCESSED') {
                        alert("处理完成的订单不能删除。");
                        return;
                    }
                    if ($scope.order.orderStatus === 'PAID') {
                        alert("支付完成的订单不能删除。");
                        return;
                    }
                    orderFactory.delete({orderNumber: $scope.order.orderNumber}).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                $scope.refundOrder = function() {
                    if ($scope.order.orderStatus !== 'PAID') {
                        alert("没付款的订单不能退款。");
                        return;
                    }

                    $scope.order.orderStatus = 'REFUNDED';
                    orderFactory.update($scope.order).$promise
                        .then(function(result) {
                            $location.path('/order');
                        }, function() {
                        });
                };
                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/order');
                };
                $scope.deleteCustomerOrderLine = function(customerOrderLine) {
                    var index = $scope.customerOrderLines.indexOf(customerOrderLine);
                    if (index > -1) {
                        $scope.customerOrderLines.splice(index, 1);
                    }
                    $scope.clearLineField();
                };
                $scope.openAddOrderLine = function() {
                    $scope.isAddOrderLine = true;
                };
                $scope.closeAddOrderLine = function() {
                    $scope.isAddOrderLine = false;
                    $scope.clearLineField();
                };
                $scope.addOrderLine = function() {
                    var nextLineNumber = 0;
                    for (var i = 0; i < $scope.customerOrderLines.length; i++) {
                        if ($scope.customerOrderLines[i].lineNumber === $scope.orderLine.lineNumber) {
                            alert("当前订单行已经添加到列表中，请不要重复添加。");
                            return;
                        }
                        if ($scope.customerOrderLines[i].lineNumber > nextLineNumber) {
                            nextLineNumber = $scope.customerOrderLines[i].lineNumber;
                        }
                    }
                    nextLineNumber = nextLineNumber + 1;
                    var orderLineTemp = {lineNumber: nextLineNumber,
                        productName: $scope.orderLine.productName,
                        orderQuantity: $scope.orderLine.orderQuantity,
                        price: $scope.orderLine.price,
                        lineAmount: $scope.orderLine.price * $scope.orderLine.orderQuantity
                    };
                    $scope.customerOrderLines.push(orderLineTemp);
                    $scope.closeAddOrderLine();
                };
                $scope.closeEditOrderLine = function(index) {
                    $scope.currentIndex = -1;
                    $scope.customerOrderLines[index] = $scope.currentRowLine;
                };
                $scope.saveOrderLine = function(customerOrderLine, index) {

                    $scope.customerOrderLines[index].productNumber = customerOrderLine.productNumber;
                    $scope.customerOrderLines[index].productName = customerOrderLine.productName;
                    $scope.customerOrderLines[index].orderQuantity = customerOrderLine.orderQuantity;
                    $scope.customerOrderLines[index].price = customerOrderLine.price;
                    $scope.customerOrderLines[index].lineAmount = customerOrderLine.price * customerOrderLine.orderQuantity;
                    $scope.closeEditOrderLine();

                };
                $scope.editCustomerOrderLine = function(customerOrderLine, index) {
                    $scope.currentIndex = index;
                    $scope.currentRowLine = angular.copy(customerOrderLine);
                };
                $scope.clearLineField = function() {
                    $scope.orderLine.productName = '';
                    $scope.orderLine.orderQuantity = '';
                    $scope.orderLine.price = '';
                };
                orderFactory.show({orderNumber: $routeParams.orderNumber}).$promise
                        .then(function(result) {
                            $scope.order = result;
                            $scope.customerOrderLines = $scope.order.customerOrderLines;
                        }, function() {
                        });
                $scope.currentIndex = -1;
                $scope.orderStatuz = tzMediaApp.orderStatusEnum;
                $scope.paymentTerms = tzMediaApp.paymentTerms;
            }]);

