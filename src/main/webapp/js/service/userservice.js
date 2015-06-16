'use strict';

var userServices = angular.module('userServices', ['ngResource']);


//web user factory
userServices.factory('administratorsFactory', function($resource) {
    return $resource(baseUrl + 'resources/administrators', {}, {
        query: {method: 'GET', isArray: true},
        create: {method: 'POST'}
    });
});

userServices.factory('administratorFactory', function($resource) {
     return $resource(baseUrl + 'resources/administrators/:userId', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {userId: '@userId'}},
        delete: {method: 'DELETE', params: {userId: '@userId'}}
    });
});

userServices.factory('administratorImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/administrators/:userId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {userId: '@userId', imageName: '@imageName'}}
    });
});


//mobile user factory
userServices.factory('usersFactory', function($resource) {
    return $resource(baseUrl + 'resources/users', {}, {
        query: {method: 'GET',isArray: true,
            params: {fromUserId: '@fromUserId',toUserId: '@toUserId',
                     userName:   '@userName'  ,email:    '@email',
                     qq:         '@qq'        ,weibo:    '@weibo',
                     resultLength: '@resultLength',
                     fromCreationDate: '@fromCreationDate',
                     toCreationDate: '@toCreationDate',gander:'@gander'}},
        create: {method: 'POST'}
    });
});

userServices.factory('userFactory', function($resource) {
    return $resource(baseUrl + 'resources/users/:userId', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {userId: '@userId'}},
        delete: {method: 'DELETE', params: {userId: '@userId'}}
    });
});

userServices.factory('userPasswordResetFactory', function($resource) {
    return $resource(baseUrl + 'resources/users/resetpassword/mail', {}, {
        resetPassword: {method: 'POST'}
    });
});

//user feedback factory
userServices.factory('userfeedbacksFactory', function($resource) {
    return $resource(baseUrl + 'resources/userfeedbacks', {}, {
        query: {method: 'GET', isArray: true, params: {
                userId: '@userId',
                fromDate: '@fromDate',
                toDate: '@toDate',
                resultLength: '@resultLength'
            }},
        create: {method: 'POST'}
    });
});

userServices.factory('userfeedbackFactory', function($resource) {
    return $resource(baseUrl + 'resources/userfeedbacks/', {}, {
        show: {method: 'GET'},    
        delete: {method: 'DELETE', params: {feedbackNumber: '@feedbackNumber'}}
    });
});

userServices.factory('userfeedbackDetailFactory', function($resource) {
    return $resource(baseUrl + 'resources/userfeedbacks/detail', {}, {
        show: {method: 'GET', params: {
                feedbackNumber: '@feedbackNumber'
            }}
    });
});

userServices.factory('userCouponsFactory', function($resource) {
    return $resource(baseUrl + 'resources/usercoupons', {}, {
        query: {method: 'GET', isArray: true,
            params: {userId: '@userId',
                couponNumber: '@couponNumber',
                fromDate: '@fromDate',
                toDate: '@toDate',
                couponStatus: '@couponStatus'}},
        delete: {method: 'DELETE', params: {couponNumber: '@couponNumber'}},
        create: {method: 'POST'}
    });
});

userServices.factory('couponDefinitionsFactory', function($resource) {
    return $resource(baseUrl + 'resources/coupondefinitions', {}, {
        query: {method: 'GET', isArray: true,
            params: {
                couponDefinitionNumber: '@couponDefinitionNumber',
                fromDate: '@fromDate',
                toDate: '@toDate'}},
        delete: {method: 'DELETE', params: {couponDefinitionNumber: '@couponDefinitionNumber'}},
        create: {method: 'POST'}
    });
});

userServices.factory('couponDefinitionDetailFactory', function($resource) {
    return $resource(baseUrl + 'resources/coupondefinitions/detail', {}, {
        show: {method: 'GET', params: {
                couponDefinitionNumber: '@couponDefinitionNumber'
            }},
        update: {method: 'PUT'}
    });
});
