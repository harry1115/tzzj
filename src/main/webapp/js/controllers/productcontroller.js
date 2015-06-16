'use strict';

/* Controllers */

var productControllers = angular.module('productControllers', []);

productControllers.controller('productListController',
        ['$rootScope', '$scope', 'productsFactory', 'productFactory', 'productTypesFactory', '$location', 'uiGridConstants',
            function($rootScope, $scope, productsFactory, productFactory, productTypesFactory, $location, uiGridConstants) {

                var thumbTemplate = '<div class="p10"  ng-class="{\'mark-sale\':row.entity.promotion}"><img class="mx20 thumb"   ng-src="{{productImageP.filePath}}" ng-repeat="productImageP in row.entity.productImages" ng-if="productImageP.pictureType === \'PORTRAIT\'" alt=""  /><img class="mx20 thumb" src="img/thumb-add.png" alt="" ng-if="row.entity.productImages.length==0"/>{{row.entity.productName}}</div>';
                var editTemplate = '<div class="p10" style="line-height:57px;"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editProduct(row.entity.productNumber)" ><i class="icon icon-white icon-pencil"></i></button><button type="button" class="btn btn-warning mr10 hide" title="标记促销" ><i class="icon icon-white icon-tag"></i></button><button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteProduct(row.entity.productNumber)" ng-confirm-click="删除的商品不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';
                $scope.columns = [
                    {field: 'productNumber', displayName: '序号', visible: false, sort: {direction: uiGridConstants.DESC}},
                    {field: 'productName', displayName: '商品名称', cellTemplate: thumbTemplate, suppressRemoveSort: true},
                    {field: 'price', displayName: '价格', width: '10%', cellFilter: "currency : '￥'", suppressRemoveSort: true},

                    {field: 'productShowType', displayName: '促销类型', width: '10%', cellFilter: 'productShowTypeFilter', suppressRemoveSort: true},
                    {field: 'sellable', displayName: '状态', width: '10%', cellFilter: 'productStateFilter', suppressRemoveSort: true},
                    {field: 'ordering', displayName: '显示顺序', width: '10%', suppressRemoveSort: true},
                    {name: 'detail', displayName: '操作', width: '15%', cellTemplate: editTemplate, enableSorting: false}];

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

                $scope.productListScope = {
                    editProduct: function(productNumber) {
                        $rootScope.productSearchCondition = $scope.productSearchCondition;
                        $location.path('/product-detail/' + productNumber);
                    },
                    editPromotions: function(productNumber) {
                        $rootScope.productSearchCondition = $scope.productSearchCondition;
                        $location.path('/product-promotion/' + productNumber);
                    },
                    deleteProduct: function(productNumber) {
                        productFactory.delete({productNumber: productNumber}).$promise
                                .then(function(result) {
                                    $scope.searchProduct();
                                }, function(result) {
                                    if (result.data === 2 || result.data === "2") {
                                        alert("有订单关联此产品，不能删除。");
                                        return;
                                    }
                                    if (result.data === 3 || result.data === "3") {
                                        alert("有商品以此商品为免费产品，不能删除。");
                                        return;
                                    }
                                });
                    }
                };

                /* callback for ng-click 'createProduct': */
                $scope.createProduct = function() {
                    $rootScope.productSearchCondition = $scope.productSearchCondition;
                    $location.path('/product-creation');
                };


                $scope.searchProduct = function(typeId) {
                    if(typeof typeId == 'undefined'){
                        typeId = $scope.productSearchCondition.productType = $rootScope.productSearchCondition.productType;
                    }
                    if ((typeof $rootScope.productSearchCondition !== 'undefined')
                            && $rootScope.productSearchCondition !== null) {
                        $scope.productSearchCondition = $rootScope.productSearchCondition;

                        $scope.productSearchCondition.productType = $rootScope.productSearchCondition.productType = typeId;
                    }
                    $scope.gridOptions.data = productsFactory.query({
                        productName: $scope.productSearchCondition.productName,
                        sellable: $scope.productSearchCondition.sellable,
                        productType: $scope.productSearchCondition.productType,
                        shopId: $rootScope.currentShopId
                    });


                };
                $scope.SetCurrentType = function(typeId, index){
                    $scope.currentPage = 1;
                    $rootScope.CurrentSelectIndex = index;
                    $scope.searchProduct(typeId);
                }

                $scope.clearProduct = function() {
                    var lType = $scope.productSearchCondition.productType;
                    $scope.currentPage = 1;
                    $scope.gridOptions.data = productsFactory.query({productType: lType});
                    $scope.productSearchCondition = $rootScope.productSearchCondition = {};
                    $scope.productSearchCondition.productType = $rootScope.productSearchCondition.productType = lType;
                };
                $scope.productTypes = productTypesFactory.query({
                    shopId: $rootScope.currentShopId
                },function(){
                    if ((typeof $rootScope.productSearchCondition == 'undefined')
                        && $rootScope.productSearchCondition == null) {
                        $scope.productSearchCondition=$rootScope.productSearchCondition={};
                        var currentTypeId=$scope.productTypes[0].typeId;
                        $scope.SetCurrentType(currentTypeId,0);
                    }else{
                        $scope.SetCurrentType($scope.productSearchCondition.productType, $rootScope.CurrentSelectIndex);
                    }

                });

                $scope.currentPage = 1;
                $scope.productStates = tzMediaApp.productStates;


            }]);

productControllers.controller('productCreationController',
        ['$rootScope', '$scope', 'productsFactory', '$location', 'shopsFactory', 'productTypesFactory',
            function($rootScope, $scope, productsFactory, $location, shopsFactory, productTypesFactory) {
                /* callback for ng-click 'createProduct': */
                $scope.createProduct = function() {
                    $scope.product.shopId = $rootScope.currentShopId;
                    $scope.product.productNumber = new Date().format("MMddhhmmss");
                    $scope.product.typeId = $scope.CurrentType;
                    productsFactory.create($scope.product).$promise.then(function() {
                        
                        $location.path('/product');
                    }, function() {
                        
                    });
                };

                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {

                    $location.path('/product');
                };
                $scope.shops = shopsFactory.query();
                $scope.productTypes = productTypesFactory.query({
                    shopId: $rootScope.currentShopId
                });
                $scope.productShowTypes = tzMediaApp.productShowTypes;
                $scope.productStates = tzMediaApp.productStates;
                
                if ($rootScope.productSearchCondition == undefined) {
                    $scope.CurrentType = 1
                } else{
                    $scope.CurrentType = $rootScope.productSearchCondition.productType;
                }

            }]);

productControllers.controller('productDetailController',
        ['$scope', '$routeParams', 'productFactory', 'productsFactory', '$location', '$upload', 'shopsFactory', 'productImageFactory', 'fileUploadFactory', 'productTypesFactory',
            function($scope, $routeParams, productFactory, productsFactory, $location, $upload, shopsFactory, productImageFactory, fileUploadFactory, productTypesFactory) {
                //Use ng-file-upload plugin to upload file
                $scope.onFileSelect = function($files) {
                    //$files: an array of files selected, each file has name, size, and type.
                    var productPictureUploadUrl = baseUrl + "resources/products/" + $scope.product.productNumber + "/images/?pictureType=" + 'MAIN';
                    fileUploadFactory.upload($scope, $files, $upload, productPictureUploadUrl,reloadProductImages);
                    function reloadProductImages(){
                        $scope.product = productFactory.show({productNumber: $routeParams.productNumber});
                    }
                    /*setTimeout(function() {
                        $scope.product = productFactory.show({productNumber: $routeParams.productNumber});
                    }, tzMediaApp.imageUploadWaitTime);*/
                };
                $scope.uploadImg = function(type) {
                    $scope.pictureType = type;
                    $("#onfileUpload").trigger("click");
                }
                /* callback for ng-click 'cancel': */
                $scope.cancel = function() {
                    $location.path('/product');
                };

                $scope.removeImage = function(productImage) {
                    productImageFactory.delete({productNumber: $routeParams.productNumber, imageName: productImage.imageName});
                    $scope.product = productFactory.show({productNumber: $routeParams.productNumber});
                };

                $scope.product = productFactory.show({productNumber: $routeParams.productNumber});
                $scope.shops = shopsFactory.query();
                $scope.productTypes = productTypesFactory.query();
                $scope.pictureTypes = pictureType;
                $scope.productShowTypes = tzMediaApp.productShowTypes;
                $scope.productStates = tzMediaApp.productStates;



                $scope.deleteProductPromotion = function(promotionProduct) {
                    var index = $scope.promotionProducts.indexOf(promotionProduct);
                    if (index > -1) {
                        $scope.promotionProducts.splice(index, 1);
                    }
                    $scope.clearPromotionField();
                };


                /* callback for ng-click 'updateProduct': */
                $scope.updateProduct = function() {
                    /*if ($scope.product.promotionDefinition.promotionType === 'BYQUANTITY') {
                     if ($scope.product.promotionDefinition.minimalOrderQuantity <= 0) {
                     alert("购买数量必须设置如果促销类型是买M送N");
                     return;
                     }
                     if ($scope.product.promotionDefinition.freeProductQuantity <= 0) {
                     alert("赠送数量必须设置如果促销类型是买M送N");
                     return;
                     }
                     }
                     
                     if ($scope.product.promotionDefinition.promotionType === 'BYPRODUCT') {
                     if ($scope.product.promotionDefinition.freeProductQuantity <= 0) {
                     alert("赠送数量必须设置如果促销类型是买1送N");
                     return;
                     }
                     }
                     
                     if ($scope.product.promotionDefinition.promotionType === 'BYAMOUNT') {
                     if ($scope.product.promotionDefinition.minimalOrderAmount <= 0) {
                     alert("购买金额必须设置如果促销类型是买满优惠");
                     return;
                     }
                     if ($scope.product.promotionDefinition.discount <= 0
                     && $scope.product.promotionDefinition.fixPrice <= 0) {
                     alert("折扣或者折扣价必须设置如果促销类型是买满优惠");
                     return;
                     }
                     }
                     
                     if ($scope.product.promotionDefinition.promotionType === 'BYDISCOUNT') {
                     if ($scope.product.promotionDefinition.discount <= 0
                     && $scope.product.promotionDefinition.fixPrice <= 0) {
                     alert("折扣或者折扣价必须设置如果促销类型是买满优惠");
                     return;
                     }
                     }*/
                    $scope.product.productType = null;
                    $scope.product.shop = null;
                    $scope.product.promotionDefinition.promotionProducts = $scope.promotionProducts;

                    productFactory.update($scope.product).$promise.then(function() {
                        $location.path('/product');
                    }, function() {

                    });

                };


                $scope.onFreeProductNumberChange = function() {
                    for (var i = 0; i < $scope.freeProducts.length; i++) {
                        var freeProduct = $scope.freeProducts[i];
                        if (freeProduct.productNumber === $scope.promotionProduct.freeProductNumber) {
                            $scope.promotionProduct.freeProductName = freeProduct.productName;
                            break;
                        }
                    }
                };

                $scope.onFreeProductNameChange = function() {
                    for (var i = 0; i < $scope.freeProducts.length; i++) {
                        var freeProduct = $scope.freeProducts[i];
                        if (freeProduct.productName === $scope.promotionProduct.freeProductName) {
                            $scope.promotionProduct.freeProductNumber = freeProduct.productNumber;
                            break;
                        }
                    }
                };

                $scope.addProductPromotion = function() {
                    for (var i = 0; i < $scope.promotionProducts.length; i++) {
                        if ($scope.promotionProducts[i].freeProductNumber === $scope.promotionProduct.freeProductNumber) {
                            alert("当前赠品已经添加，请勿重复操作。");
                            return;
                        }
                    }

                    var promotionProductTemp = {};
                    promotionProductTemp.freeProductNumber = $scope.promotionProduct.freeProductNumber;
                    promotionProductTemp.freeProductName = $scope.promotionProduct.freeProductName;
                    promotionProductTemp.freeProductQuantity = $scope.promotionProduct.freeProductQuantity;

                    $scope.promotionProducts.push(promotionProductTemp);
                    $scope.clearPromotionField();
                };



                $scope.clearPromotionField = function() {
                    $scope.promotionProduct.freeProductNumber = '';
                    $scope.promotionProduct.freeProductName = '';
                    $scope.promotionProduct.freeProductQuantity = '';
                };
                $scope.filterFreeProducts = function(freeProduct) {
                    return freeProduct.price === 0;
                };
                productFactory.show({productNumber: $routeParams.productNumber}).$promise
                        .then(function(result) {
                            $scope.product = result;
                            $scope.promotionProducts = $scope.product.promotionDefinition.promotionProducts;

                        }, function() {
                        });

                $scope.ShowFree = function()
                {
                    for (var i = 0; i < $scope.freeProducts.length; i++)
                    {
                        $scope.freeProducts[i].sellable = false;
                        var lCurrentId = $scope.freeProducts[i].productNumber;
                        for (var j = 0; j < $scope.promotionProducts.length; j++)
                        {
                            var lIsAddId = $scope.promotionProducts[j].freeProductNumber;

                            if (lCurrentId == lIsAddId)
                            {
                                $scope.freeProducts[i].sellable = true;
                                break;
                            }

                        }

                    }

                    $scope.showFreeProductModal = !$scope.showFreeProductModal
                }


                $scope.SaveProduct = function()
                {

                    $scope.promotionProducts = [];
                    for (var i = 0; i < $scope.freeProducts.length; i++)
                    {
                        var lIsSelect = $scope.freeProducts[i].sellable;

                        if (lIsSelect)
                        {
                            var promotionProductTemp = {};
                            promotionProductTemp.freeProductNumber = $scope.freeProducts[i].productNumber;
                            promotionProductTemp.freeProductName = $scope.freeProducts[i].productName;
                            promotionProductTemp.freeProductQuantity = 1;
                            $scope.promotionProducts.push(promotionProductTemp);
                        }
                    }
                    $scope.showFreeProductModal = !$scope.showFreeProductModal

                }

                $scope.freeProducts = productsFactory.query();
                $scope.promotionProduct = {};
                $scope.promotions = tzMediaApp.promotionTypes;

            }]);

productControllers.controller('producttypeListController', ['$scope', 'productTypesFactory', 'productTypeFactory', '$location', 'uiGridConstants','$rootScope',
    function($scope, productTypesFactory, productTypeFactory, $location, uiGridConstants,$rootScope) {
        var editTemplate = '<div class="p10" style="line-height:57px;"><button type="button" class="btn btn-success mr10" title="编辑" ng-click="getExternalScopes().editProducttype(row.entity.typeId)" ><i class="icon icon-white icon-pencil"></i></button><button type="button" class="btn btn-danger" ng-click="getExternalScopes().deleteProducttype(row.entity.typeId)" ng-confirm-click="删除的商品类型不能找回，你确定要删除么？" title="删除"><i class="icon icon-white icon-trash"></i></button></div>';
        var thumbTemplate = '<div class="p10"><img class="thumb mr20"  ng-src="{{image.filePath}}" ng-repeat="image in row.entity.productTypeImages" ng-if="image.pictureType === \'PORTRAIT\'" ><img class="thumb mr20" src="img/thumb-add.png" alt="" ng-if="row.entity.productTypeImages.length==0"/>{{row.entity.typeName}}</div>';
        $scope.columns = [{name: 'typeName', displayName: '分类名称', cellTemplate: thumbTemplate, suppressRemoveSort: true, enableSorting: false},
            {field: 'typeId', displayName: '序号', visible: false, sort: {direction: uiGridConstants.DESC}},
            {name: 'detail', displayName: '操作', width: '15%', cellTemplate: editTemplate, enableSorting: false}];

        $scope.gridOptions = {
            enableSorting: true,
            enableFiltering: false,
            enableRowSelection: true,
            enableSelectAll: true,
            enableColumnMenus: false,
            enableRowHeaderSelection: true,
            multiSelect: true,
            showSelectionCheckbox: true,
            enableCellSelection: false,
            showFooter: false,
            //rowsPerPage: tzMediaApp.rowsPerPage,
            selectionRowHeaderWidth: 50,
            selectionRowHeaderHeight: 82,
            headerRowHeight: 50,
            rowHeight: 82,
            enableHorizontalScrollbar: false,
            columnDefs: $scope.columns};

        $scope.gridOptions.onRegisterApi = function(gridApi) {
            $scope.gridApi = gridApi;
            gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                //
            });
        };

        $scope.productTypeListScope = {
            editProducttype: function(typeId) {
                $location.path('/producttype-detail/' + typeId);
            },
            deleteProducttype: function(typeId) {
                productTypeFactory.delete({typeId: typeId}).$promise.then(function(result) {
                    $scope.searchProductType();
                }, function() {
                    alert("当前分类与商品有关联，所以无法删除！");
                });

            }
        };
        $scope.searchProductType = function() {
            $scope.gridOptions.data = productTypesFactory.query({
                shopId: $rootScope.currentShopId
            });
        };

        /* callback for ng-click 'createProduct': */
        $scope.createProducttype = function() {
            $location.path('/producttype-creation');
        };


        $scope.searchProductType();

    }]);

productControllers.controller('producttypeCreationController',
    ['$scope', 'productTypesFactory', 'productTypeFactory', '$location','$rootScope',
        function($scope, productTypesFactory, productTypeFactory, $location,$rootScope) {
            /* callback for ng-click 'createProduct': */

            $scope.createProducttype = function() {
                $scope.producttype.typeId = new Date().format("MMddhhmmss");
                $scope.producttype.shopId = $rootScope.currentShopId;
                productTypesFactory.create($scope.producttype).$promise
                    .then(function(result) {
                        $location.path('/producttype');
                    }, function() {
                        $scope.isShowError = true;
                    });

            };


            /* callback for ng-click 'cancel': */
            $scope.cancel = function() {
                $location.path('/producttype');
            };
        }]);





productControllers.controller('producttypeDetailController',
    ['$scope', '$routeParams', 'productTypeFactory', '$location', '$upload', 'productTypeImageFactory', 'fileUploadFactory',
        function($scope, $routeParams, productTypeFactory, $location, $upload, productTypeImageFactory, fileUploadFactory) {

            /* callback for ng-click 'updateProductp': */
            $scope.updateProducttype = function() {
                productTypeFactory.update($scope.producttype).$promise
                    .then(function(result) {
                        $location.path('/producttype');
                    }, function() {
                    });

            };

            //Use ng-file-upload plugin to upload file
            $scope.onFileSelect = function($files) {
                //$files: an array of files selected, each file has name, size, and type.
                var producttypePictureUploadUrl = baseUrl + "resources/producttypes/" + $scope.producttype.typeId + "/images/?pictureType=" + $scope.pictureType;
                fileUploadFactory.upload($scope, $files, $upload, producttypePictureUploadUrl, reloadProductTypeImages);
                /*setTimeout(function() {
                    $scope.producttype = productTypeFactory.show({typeId: $routeParams.typeId});
                }, tzMediaApp.imageUploadWaitTime);*/
                function reloadProductTypeImages(){
                    $scope.producttype = productTypeFactory.show({typeId: $routeParams.typeId});
                }
            };
            $scope.uploadImg = function() {
                $scope.pictureType = 'PORTRAIT';
                $("#onfileUpload").trigger("click");
            }
            /* callback for ng-click 'cancel': */
            $scope.cancel = function() {
                $location.path('/producttype');
            };

            $scope.removeImage = function(productTypeImage) {
                productTypeImageFactory.delete({typeId: $routeParams.typeId, imageName: productTypeImage.imageName});
                $scope.producttype = productTypeFactory.show({typeId: $routeParams.typeId});
            };

            $scope.producttype = productTypeFactory.show({typeId: $routeParams.typeId});
            $scope.pictureTypes = pictureType;

        }]);