 'use strict';

var shopServices = angular.module('shopServices', ['ngResource']);

//shop factory
shopServices.factory('shopsFactory', function($resource) {
    return $resource(baseUrl + 'resources/shops', {}, {
        query: {method: 'GET', isArray: true},
        create: {method: 'POST'}
    });
});

shopServices.factory('shopFactory', function($resource) {
    return $resource(baseUrl + 'resources/shops/:shopId', {}, {
        show: {method: 'GET', params: {displaySharePage: '@displaySharePage'}},
        update: {method: 'PUT', params: {shopId: '@shopId'}},
        delete: {method: 'DELETE', params: {shopId: '@shopId'}}
    });
});

shopServices.factory('shopImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/shops/:shopId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {shopId: '@shopId', imageName: '@imageName'}}
    });
});


//shop activity factory
shopServices.factory('activitiesFactory', function($resource) {
    return $resource(baseUrl + 'resources/activities', {}, {
        query: {method: 'GET', isArray: true,
            params: {activityName: '@activityName', activitySubject: '@activitySubject', 
                     activityDate: '@activityDate', resultLength: '@resultLength'}},
        create: {method: 'POST'}
    });
});

shopServices.factory('shopActivitiesFactory', function($resource) {
    return $resource(baseUrl + 'resources/activities', {}, {
        query: {method: 'GET', isArray: true,
            params: {activityName: '@activityName', activitySubject: '@activitySubject', 
                     activityDate: '@activityDate', resultLength: '@resultLength',shopId:'@shopId'}},
        create: {method: 'POST'}
    });
});

shopServices.factory('activityFactory', function($resource) {
    return $resource(baseUrl + 'resources/activities/:activityName', {}, {
        show: {method: 'GET', params: {displaySharePage: '@displaySharePage'}},
        update: {method: 'PUT', params: {activityName: '@activityName'}},
        delete: {method: 'DELETE', params: {activityName: '@activityName'}}
    });
});

shopServices.factory('activityImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/activities/:activityName/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {activityName: '@activityName', imageName: '@imageName'}}
    });
});

//shop singer factory
shopServices.factory('singersFactory', function($resource) {
    return $resource(baseUrl + 'resources/singers', {}, {
        query: {method: 'GET', isArray: true,
            params: {singerId: '@singerId',singerName: '@singerName',
                     isBandsman: '@bandsman',badgeType: '@badgeType',
                     resultLength: '@resultLength',isPc:'@isPc'}},
        create: {method: 'POST'}
    });
});

shopServices.factory('singerFactory', function($resource) {
    return $resource(baseUrl + 'resources/singers/:singerId', {}, {
        show: {method: 'GET', params: {displaySharePage: '@displaySharePage'}},
        update: {method: 'PUT', params: {singerId: '@singerId'}},
        delete: {method: 'DELETE', params: {singerId: '@singerId'}}
    });
});

shopServices.factory('singerImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/singers/:singerId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {singerId: '@singerId', imageName: '@imageName'}}
    });
});

//song factory
shopServices.factory('songsFactory', function($resource) {
    return $resource(baseUrl + 'resources/songs', {}, {
        query: {method: 'GET', isArray: true,
            params: {songId: '@songId',songName: '@songName',
            resultLength: '@resultLength'}},
        create: {method: 'POST'}
    });
});

shopServices.factory('songFactory', function($resource) {
    return $resource(baseUrl + 'resources/songs/:songId', {}, {
        show: {method: 'GET'},
        update: {method: 'PUT', params: {songId: '@songId'}},
        delete: {method: 'DELETE', params: {songId: '@songId'}}
    });
});

shopServices.factory('songImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/songs/:songId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {songId: '@songId', imageName: '@imageName'}}
    });
});

//playbill factory
shopServices.factory('playbillsFactory', function($resource) {
    return $resource(baseUrl + 'resources/playbills', {}, {
        query: {method: 'GET', isArray: true,
            params: {playbillId: '@playbillId',
                playbillName: '@playbillName',
                playbillDate: '@playbillDate',
                singerId: '@singerId',
                resultLength: '@resultLength',
                playbillState: '@playbillState',
                fromWeb: '@fromWeb',shopId:'@shopId'}},
        create: {method: 'POST'}
    });
});

shopServices.factory('playbillFactory', function($resource) {
    return $resource(baseUrl + 'resources/playbills/:playbillId', {}, {
        show: {method: 'GET', params: {displaySharePage: '@displaySharePage'}},
        update: {method: 'PUT', params: {playbillId: '@playbillId'}},
        delete: {method: 'DELETE', params: {playbillId: '@playbillId'}}
    });
});

shopServices.factory('userActionFactory', function($resource) {
    return $resource(baseUrl + 'resources/playbills/:playbillId/actions/', {}, {
        query: {method: 'GET', isArray: true, params: {excludePraise: '@excludePraise'}},
        update: {method: 'POST', params: {playbillId: '@playbillId'}},
        delete: {method: 'DELETE', params: {playbillId: '@playbillId',actionId: '@actionId',actionType:'@actionType'}}
    });
});

shopServices.factory('userActionListFactory', function($resource) {
    return $resource(baseUrl + 'resources/playbills/:playbillId/actionsList/:actionType', {}, {
        query: {method: 'GET', isArray: true, params: {excludePraise: '@excludePraise'}}
    });
});

shopServices.factory('playbillImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/playbills/:playbillId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {playbillId: '@playbillId', imageName: '@imageName'}}
    });
});
shopServices.factory('playbillActivateFactory', function($resource) {
    return $resource(baseUrl + 'resources/playbills/:playbillId/reactivate/', {}, {
        reactivate: {method: 'GET', params: {playbillId: '@playbillId'}}
    });
});

//Frontpage Factory
shopServices.factory('frontpagesFactory', function($resource) {
    return $resource(baseUrl + 'resources/frontpages', {}, {        
        query: {method: 'GET', isArray: true,
            params: {
                frontPageId: '@frontPageId',
                shopId: '@shopId',
                activityName: '@activityName',
                singerId: '@singerId', 
                title: '@title',
                push: '@push',
                type: '@type'}},
        create: {method: 'POST'}
    });
});

shopServices.factory('frontpageFactory', function($resource) {
    return $resource(baseUrl + 'resources/frontpages/:frontPageId', {}, {
        show: {method: 'GET', params: {displaySharePage: '@displaySharePage'}},
        update: {method: 'PUT', params: {frontPageId: '@frontPageId'}},
        delete: {method: 'DELETE', params: {frontPageId: '@frontPageId'}}
    });
});

shopServices.factory('frontpageImageFactory', function($resource) {
    return $resource(baseUrl + 'resources/frontpages/:frontPageId/images/?imageName=:imageName', {}, {
        delete: {method: 'DELETE', params: {frontPageId: '@frontPageId', imageName: '@imageName'}}
    });
});

shopServices.factory('commonUserActionFactory', function($resource) {
    return $resource(baseUrl + 'resources/commonuseractions/', {}, {
        query: {method: 'GET', isArray: true,
            params: {shopId: '@shopId',
                activityName: '@activityName',
                playbillId: '@playbillId'}},
        delete: {method: 'DELETE', params: {
                userId: '@userId',
                actionId: '@actionId',
                actionType: '@actionType'}}
    });
});