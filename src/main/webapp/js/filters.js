'use strict';


var tzmeidaFilters = angular.module('tzmeidaFilters', ['angular.filter']);

tzmeidaFilters.filter('startFrom', function() {
  return function(input, start) {
        start = +start; //parse to int
        return input.slice(start);
    };
});

tzmeidaFilters.filter('frontPageTypeFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.frontPageTypes.length; i++) {
            if (input === tzMediaApp.frontPageTypes[i].id) {
                return tzMediaApp.frontPageTypes[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('orderStatusFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.orderStatusEnum.length; i++) {
            if (input === tzMediaApp.orderStatusEnum[i].id) {
                return tzMediaApp.orderStatusEnum[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('orderTypeFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.orderTypes.length; i++) {
            if (input === tzMediaApp.orderTypes[i].id) {
                return tzMediaApp.orderTypes[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('paymentTermFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.paymentTerms.length; i++) {
            if (input === tzMediaApp.paymentTerms[i].id) {
                return tzMediaApp.paymentTerms[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('genderFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.genders.length; i++) {
            if (input === tzMediaApp.genders[i].id) {
                return tzMediaApp.genders[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('playbillStateFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.playbillStates.length; i++) {
            if (input === tzMediaApp.playbillStates[i].id) {
                return tzMediaApp.playbillStates[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('productShowTypeFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.productShowTypes.length; i++) {
            if (input === tzMediaApp.productShowTypes[i].id) {
                return tzMediaApp.productShowTypes[i].name;
            }
        }
    };
});
tzmeidaFilters.filter('productStateFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.productStates.length; i++) {
            
            if (input === tzMediaApp.productStates[i].id) {
                return tzMediaApp.productStates[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('userTypeFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.userTypes.length; i++) {
            if (input === tzMediaApp.userTypes[i].id) {
                return tzMediaApp.userTypes[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('userRoleFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.userRoles.length; i++) {
            if (input === tzMediaApp.userRoles[i].id) {
                return tzMediaApp.userRoles[i].name;
            }
        }
    };
});


tzmeidaFilters.filter('adminRoleFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.adminRoles.length; i++) {
            if (input === tzMediaApp.adminRoles[i].id) {
                return tzMediaApp.adminRoles[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('couponStatusFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.couponStatusEnum.length; i++) {
            if (input === tzMediaApp.couponStatusEnum[i].id) {
                return tzMediaApp.couponStatusEnum[i].name;
            }
        }
    };
});

tzmeidaFilters.filter('badgeTypesFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.badgeTypes.length; i++) {
            if (input === tzMediaApp.badgeTypes[i].id) {
                return tzMediaApp.badgeTypes[i].name;
            }
        }
    };
});


tzmeidaFilters.filter('actionTypeFilter', function() {
    return function(input) {
        for (var i = 0; i < tzMediaApp.actionTypes.length; i++) {
            if (input === tzMediaApp.actionTypes[i].id) {
                return tzMediaApp.actionTypes[i].name;
            }
        }
    };
});
tzmeidaFilters.filter('booleanFilter', function() {
    return function(input) {
        if (input === 'true'|| input === true) {
            return '是';
        }else{
            return '否';
        }
    };
});







