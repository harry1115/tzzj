/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
'use strict';
var playbillControllers = angular.module('playbillControllers', []);
playbillControllers.controller('playbillListController',
    ['$rootScope', '$scope', 'playbillsFactory', 'playbillFactory', '$location', 'userActionFactory', 'playbillActivateFactory', 'userActionListFactory',
        function ($rootScope, $scope, playbillsFactory, playbillFactory, $location, userActionFactory, playbillActivateFactory, userActionListFactory) {
            $scope.clickPlaybill = function (playbill, index) {
                $scope.currentSelect = index;
                $scope.playbillUserActionsC = userActionListFactory.query({
                    playbillId: playbill.playbillId,
                    actionType: 'COMMENT'
                });
                $scope.playbillUserActionsP = userActionListFactory.query({
                    playbillId: playbill.playbillId,
                    actionType: 'PAY'
                });
                $scope.playbillInfos = [playbill];

            }
            $scope.editPlaybill = function (playbillId) {
                $rootScope.playbillSearchCondition = $scope.playbillSearchCondition;
                $location.path('/playbill-detail/' + playbillId);
            }

            $scope.deletePlaybill = function (playbillId) {
                playbillFactory.delete({playbillId: playbillId}, function () {
                    $scope.playbillInfos = [];
                    $scope.currentSelect = 0;
                    $scope.searchPlaybill();
                });

            }

            $scope.closePlaybill = function (playbill) {

                playbill.playbillState = 'INACTIVE';
                playbillFactory.update(playbill);
                $scope.clickPlaybill(playbill, $scope.currentSelect);

            }
            $scope.reactivatePlaybill = function (playbill) {

                playbillActivateFactory.reactivate({playbillId: playbill.playbillId}, function () {
                    playbill.playbillState = "ACTIVE";
                    $scope.clickPlaybill(playbill, $scope.currentSelect);
                });


            }

            $scope.createPlaybill = function () {
                $rootScope.playbillSearchCondition = $scope.playbillSearchCondition;
                $location.path('/playbill-creation');
            };

            $scope.searchPlaybill = function () {
                var date = null;
                if ((typeof $rootScope.playbillSearchCondition !== 'undefined')
                    && $rootScope.playbillSearchCondition !== null) {
                    $scope.playbillSearchCondition = $rootScope.playbillSearchCondition;
                }
                if ((typeof $scope.playbillSearchCondition === 'undefined')
                    || $scope.playbillSearchCondition === null) {
                    $scope.playbillSearchCondition = {playbillDate: new Date()};
                }

                if ($scope.playbillSearchCondition.playbillDate !== null
                    && $scope.playbillSearchCondition.playbillDate !== ''
                    && (typeof $scope.playbillSearchCondition.playbillDate !== 'undefined')) {
                    date = moment($scope.playbillSearchCondition.playbillDate).format("YYYY-MM-DD");
                }
                sessionStorage.currentPlaybillDate = $scope.playbillSearchCondition.playbillDate;
                $scope.playbills = playbillsFactory.query({
                    playbillDate: date,
                    fromWeb: true,
                    shopId: $rootScope.currentShopId
                }, function () {
                    if ($scope.playbills != '') {
                        $scope.clickPlaybill($scope.playbills[$scope.currentSelect], $scope.currentSelect);
                    }
                });


            };

            $scope.shield = function (action) {
                action.checked = !action.checked;
                userActionFactory.update(action, function () {
                    $scope.playbillUserActionsC = userActionListFactory.query({
                        playbillId: action.playbillId,
                        actionType: 'COMMENT'
                    });
                });
            }

            $scope.deleteUserAction = function (ua) {
                if (ua.actionType !== 'COMMENT') {
                    alert("只有评论可以删除。");
                    return;
                }

                userActionFactory.delete({playbillId: ua.playbillId, actionId: ua.actionId,actionType:'COMMENT'}).$promise.then(function(){
                    var index = $scope.playbillUserActionsC.indexOf(ua);
                    if (index > -1) {
                        $scope.playbillUserActionsC.splice(index, 1);
                    }
                },function(){
                    alert('删除失败');
                });
            };

            $scope.$watch('playbillSearchCondition.playbillDate', function () {
                $scope.searchPlaybill();

            });
            $scope.currentSelect = 0;
            $scope.playbillUserActions = [];
            $scope.playbillStates = tzMediaApp.playbillStates;


        }]);

playbillControllers.controller('playbillCreationController',
    ['$rootScope', '$scope', 'playbillsFactory', '$location', 'shopsFactory', 'singersFactory', 'songsFactory', 'singerFactory', 'sessionService', 'activitiesFactory',
        function ($rootScope, $scope, playbillsFactory, $location, shopsFactory, singersFactory, songsFactory, singerFactory, sessionService, activitiesFactory) {

            /* callback for ng-click 'updatePlaybill': */
            $scope.updatePlaybill = function () {
                if ($scope.currentSingerName == "") {
                    alert("请选择歌手!!");
                } else {
                    singerFactory.update($scope.singer);
                    $scope.playbill.shopId = $rootScope.currentShopId;
                    playbillsFactory.create($scope.playbill).$promise
                        .then(function(result) {
                            $location.path('/playbill');
                        }, function() {
                        });


                }
            };



            $scope.deletePlaybillLine = function (playbillLine) {
                var index = $scope.playbillLines.indexOf(playbillLine);
                if (index > -1) {
                    $scope.playbillLines.splice(index, 1);
                }

            };
            $scope.deleteSingerOwnedSong = function (singerOwnedSong) {
                var index = $scope.singerOwnedSongs.indexOf(singerOwnedSong);
                if (index > -1) {
                    $scope.singerOwnedSongs.splice(index, 1);
                }

            };



            /* callback for ng-click 'cancel': */
            $scope.cancel = function () {
                $location.path('/playbill');
            };
            if(sessionStorage.currentPlaybillDate==='undefined'){
                $scope.playbill = {playbillDate: new Date(), fromTime: new Date(), toTime: new Date()};
            }else{
                $scope.playbill = {playbillDate: new Date(sessionStorage.currentPlaybillDate), fromTime: new Date(sessionStorage.currentPlaybillDate), toTime: new Date(sessionStorage.currentPlaybillDate)};
            }
            $scope.onDateSet = function (newDate, oldDate) {
                $scope.playbill.fromTime = newDate;
                $scope.playbill.toTime = newDate;
            };
            $scope.onTimeSet = function (newDate, oldDate) {
                newDate = new Date(newDate)
                newDate.setMinutes(newDate.getMinutes() + 45);
                $scope.playbill.toTime = newDate;
            };
            $scope.filterSingerName = function (id) {
                for (var i = 0; i < $scope.singers.length; i++) {
                    if (id === $scope.singers[i].singerId) {
                        return $scope.singers[i].singerName;
                    }
                }
            }
            $scope.filterSongName = function (id) {
                for (var i = 0; i < $scope.songs.length; i++) {
                    if (id === $scope.songs[i].songId) {
                        return $scope.songs[i].songName;
                    }
                }
            }

            $scope.SaveSongsAry = [];
            $scope.CurrentSelctCount = 0;
            $scope.MaxCount = 0;
            $scope.showSongsModal = false;
            $scope.openSong = function (showSongsModal, playbillLines, count, type) {
                if ($scope.currentSingerName != "") {
                    $scope.MaxCount = count;
                    $scope.showDivType = type;
                    $scope.SaveSongsAry = [];
                    $scope.SelectSongs = "";
                    if (playbillLines != undefined) {
                        for (var i = 0; i < playbillLines.length; i++) {
                            $scope.SelectSongs = $scope.SelectSongs + "," + playbillLines[i].songId;
                            var lMode = {};
                            lMode.songId = playbillLines[i].songId;
                            lMode.songName = playbillLines[i].songName;
                            $scope.SaveSongsAry.push(lMode);
                        }
                    }

                    if ($scope.SaveSongsAry.length >= count) {
                        showSongsModal = false;
                        alert("对不起！最多只能选取" + count + "首歌曲");
                    }
                    else {
                        $scope.showSongsModal = !$scope.showSongsModal
                        if ($scope.showSongsModal) {
                            $scope.singerName = $scope.filterSingerName($scope.playbill.singerId);
                        }
                    }
                } else {
                    alert("请先选择歌手!!");
                }

            }

            $scope.IsSelectSongs = function (id, name) {
                for (var i = 0; i < $scope.SaveSongsAry.length; i++) {
                    if ($scope.SaveSongsAry[i].songId + "" == id + "") {

                        return true;
                    }
                }
                return false;


            }

            $scope.SetSelectSongs = function (id, name, count) {
                for (var i = 0; i < $scope.SaveSongsAry.length; i++) {
                    if ($scope.SaveSongsAry[i].songId + "" == id + "") {
                        $scope.SaveSongsAry.splice(i, 1);
                        return "";
                    }
                }
                var lMode = {};
                lMode.songId = id;
                lMode.songName = name;
                $scope.SaveSongsAry.push(lMode);
            }

            $scope.SaveSong = function () {
                if ($scope.SaveSongsAry.length > $scope.MaxCount) {
                    alert("对不起！最多只能选取" + $scope.MaxCount + "首歌曲");
                    return
                }
                if ($scope.showDivType == "BG") {
                    $scope.showSongsModal = false;
                    $scope.playbill.playbillLines =  $scope.playbillLines = $scope.SaveSongsAry;
                } else {
                    $scope.showSongsModal = false;
                    $scope.singer.singerOwnedSongs = $scope.singerOwnedSongs = $scope.SaveSongsAry;
                }
            }

            $scope.showLSongsModal = false;
            $scope.openLSong = function (count, type) {
                $scope.LSongName="";
                if ($scope.currentSingerName != "") {

                    $scope.MaxCount = count;
                    $scope.showDivType = type;
                    $scope.showLSongsModal = !$scope.showLSongsModal;

                    if ($scope.showLSongsModal) {
                        if ($scope.playbill.playbillLines.length >= $scope.MaxCount && type == "BG") {
                            alert("对不起！最多只能选取" + $scope.MaxCount + "首歌曲");
                            $scope.showLSongsModal = false;
                        }

                        if ($scope.singerOwnedSongs.length >= $scope.MaxCount && type == "QG") {
                            alert("对不起！最多只能选取" + $scope.MaxCount + "首歌曲");
                            $scope.showLSongsModal = false;
                        }
                    }
                } else {
                    alert("请选择歌手!!!");
                }
            }


            $scope.SaveLSong = function () {
                for (var i = 0; i < $scope.filteredSongs.length; i++) {
                        if ($scope.LSongName===$scope.filteredSongs[i].songName) {
                            alert("当前歌曲已经存在，请不要重复添加。");
                            return;
                        }
                }
                var songTemp = {};
                var NewSongTemp={};
                if ($scope.LSongName != "") {
                    songTemp.songId = NewSongTemp.songId = new Date().format("MMddhhmmss");
                    songTemp.songName = NewSongTemp.songName = $scope.LSongName;
                    songTemp.originalSinger= $scope.currentSingerName;
                    songsFactory.create(songTemp).$promise.then(function(){
                        $scope.songs.push(songTemp);
                    });
                }
                if ($scope.showDivType == "BG") {
                    $scope.playbillLines.push(NewSongTemp);
                    $scope.playbill.playbillLines = $scope.playbillLines;
                    $scope.showLSongsModal = false;
                } else {
                    $scope.singerOwnedSongs.push(NewSongTemp);
                    $scope.singer.singerOwnedSongs = $scope.singerOwnedSongs;
                    $scope.showLSongsModal = false;
                }

            }


            $scope.CloseLDiv = function () {
                $scope.LSongName = "";
                $scope.showLSongsModal = false;
            }

            $scope.ChangeSingerSong = function (event) {
                $scope.playbillLines = $scope.playbill.playbillLines = [];
                singerFactory.show({singerId: $scope.playbill.singerId}).$promise
                    .then(function (result) {
                        $scope.singer = result;
                        $scope.singerOwnedSongs = $scope.singer.singerOwnedSongs;
                        $scope.currentSingerName = $scope.singer.singerName;
                    }, function () {
                    });
            }
            $scope.currentSingerName = "";
            $scope.singers = singersFactory.query({isBandsman: false});
            $scope.songs = songsFactory.query();
            $scope.activities = activitiesFactory.query({
                shopId: $rootScope.currentShopId
            });
            $scope.pictureTypes = pictureType;
            $scope.playbillStates = tzMediaApp.playbillStates;
            $scope.playbillLine = {};

        }]);

playbillControllers.controller('playbillDetailController',
    ['$rootScope', '$scope', '$routeParams', 'playbillFactory', '$location', '$upload', 'shopsFactory',
        'singersFactory', 'songsFactory', 'playbillImageFactory', 'fileUploadFactory', 'singerFactory', 'activitiesFactory',
        function ($rootScope, $scope, $routeParams, playbillFactory, $location, $upload, shopsFactory,
                  singersFactory, songsFactory, playbillImageFactory, fileUploadFactory, singerFactory, activitiesFactory) {

            /* callback for ng-click 'updatePlaybill': */
            $scope.updatePlaybill = function () {
                singerFactory.update($scope.singer);
                playbillFactory.update($scope.playbill).$promise
                    .then(function(result) {
                        $location.path('/playbill');
                    }, function() {
                    });
            };

            //Use ng-file-upload plugin to upload file
            /*$scope.onFileSelect = function ($files) {
                //$files: an array of files selected, each file has name, size, and type.
                var playbillPictureUploadUrl = baseUrl + "resources/playbills/" + $scope.playbill.playbillId + "/images/?pictureType=" + $scope.pictureType.id;
                fileUploadFactory.upload($scope, $files, $upload, playbillPictureUploadUrl);
                $scope.playbill = playbillFactory.show({playbillId: $routeParams.playbillId});
            };
*/


            $scope.deletePlaybillLine = function (playbillLine) {
                var index = $scope.playbillLines.indexOf(playbillLine);
                if (index > -1) {
                    $scope.playbillLines.splice(index, 1);
                }

            };
            $scope.deleteSingerOwnedSong = function (singerOwnedSong) {
                var index = $scope.singerOwnedSongs.indexOf(singerOwnedSong);
                if (index > -1) {
                    $scope.singerOwnedSongs.splice(index, 1);
                }
            };

            /* callback for ng-click 'cancel': */
            $scope.cancel = function () {
                $location.path('/playbill');
            };

            $scope.onDateSet = function (newDate, oldDate) {
                $scope.playbill.fromTime = newDate;
                $scope.playbill.toTime = newDate;
            };
            $scope.onTimeSet = function (newDate, oldDate) {
                newDate = new Date(newDate)
                newDate.setMinutes(newDate.getMinutes() + 45);
                $scope.playbill.toTime = newDate;
            };

            $scope.filterSingerName = function (id) {
                for (var i = 0; i < $scope.singers.length; i++) {
                    if (id === $scope.singers[i].singerId) {
                        return $scope.singers[i].singerName;
                    }
                }
            }
            $scope.filterSongName = function (id) {
                for (var i = 0; i < $scope.songs.length; i++) {
                    if (id === $scope.songs[i].songId) {
                        return $scope.songs[i].songName;
                    }
                }
            }

            $scope.SaveSongsAry = [];
            $scope.CurrentSelctCount = 0;
            $scope.MaxCount = 0;
            $scope.showSongsModal = false;
            $scope.openSong = function (showSongsModal, playbillLines, count, type) {
                $scope.MaxCount = count;
                $scope.showDivType = type;
                $scope.SaveSongsAry = [];
                $scope.SelectSongs = "";
                for (var i = 0; i < playbillLines.length; i++) {
                    $scope.SelectSongs = $scope.SelectSongs + "," + playbillLines[i].songId;
                    var lMode = {};
                    lMode.songId = playbillLines[i].songId;
                    lMode.songName = playbillLines[i].songName;
                    $scope.SaveSongsAry.push(lMode);
                }


                if ($scope.SaveSongsAry.length >= count) {
                    showSongsModal = false;
                    alert("对不起！最多只能选取" + count + "首歌曲");
                }
                else {
                    $scope.showSongsModal = !$scope.showSongsModal
                    if ($scope.showSongsModal) {
                        $scope.singerName = $scope.filterSingerName($scope.playbill.singerId);
                    }
                }

            }

            $scope.IsSelectSongs = function (id, name) {
                for (var i = 0; i < $scope.SaveSongsAry.length; i++) {
                    if ($scope.SaveSongsAry[i].songId + "" == id + "") {

                        return true;
                    }
                }
                return false;


            }

            $scope.SetSelectSongs = function (id, name, count) {
                for (var i = 0; i < $scope.SaveSongsAry.length; i++) {
                    if ($scope.SaveSongsAry[i].songId + "" == id + "") {

                        $scope.SaveSongsAry.splice(i, 1);
                        return "";
                    }
                }
                var lMode = {};
                lMode.songId = id;
                lMode.songName = name;
                $scope.SaveSongsAry.push(lMode);
            }

            $scope.SaveSong = function () {
                var lResult = [];
                if ($scope.SaveSongsAry.length > $scope.MaxCount) {
                    alert("对不起！最多只能选取" + $scope.MaxCount + "首歌曲");
                }
                else {
                    if ($scope.showDivType == "BG") {
                        $scope.playbillLines = $scope.SaveSongsAry;
                        $scope.playbill.playbillLines = $scope.SaveSongsAry;
                    } else {
                        $scope.singerOwnedSongs = $scope.SaveSongsAry;
                        $scope.singer.singerOwnedSongs = $scope.SaveSongsAry;
                    }


                    $scope.showSongsModal = false;
                }
            }

            $scope.showLSongsModal = false;
            $scope.openLSong = function (count, type) {
                $scope.LSongName="";
                $scope.MaxCount = count;

                $scope.showDivType = type;
                $scope.showLSongsModal = !$scope.showLSongsModal;
                if ($scope.showLSongsModal) {
                    if ($scope.playbill.playbillLines.length >= $scope.MaxCount && type == "BG") {
                        alert("对不起！最多只能选取" + $scope.MaxCount + "首歌曲");
                        $scope.showLSongsModal = false;
                    }

                    if ($scope.singerOwnedSongs.length >= $scope.MaxCount && type == "QG") {
                        alert("对不起！最多只能选取" + $scope.MaxCount + "首歌曲");
                        $scope.showLSongsModal = false;
                    }
                }
            }

            $scope.ChangeSingerSong = function (event) {
                $scope.playbillLines = $scope.playbill.playbillLines = [];
                singerFactory.show({singerId: $scope.playbill.singerId}).$promise
                    .then(function (result) {
                        $scope.singer = result;
                        $scope.singerOwnedSongs = $scope.singer.singerOwnedSongs;
                        $scope.currentSingerName = $scope.singer.singerName;
                    }, function () {
                    });
            }

            $scope.SaveLSong = function () {
                for (var i = 0; i < $scope.filteredSongs.length; i++) {
                    if ($scope.LSongName===$scope.filteredSongs[i].songName) {
                        alert("当前歌曲已经存在，请不要重复添加。");
                        return;
                    }
                }
                var songTemp = {};
                var NewSongTemp={};
                if ($scope.LSongName != "") {
                    songTemp.songId = NewSongTemp.songId = new Date().format("MMddhhmmss");
                    songTemp.songName = NewSongTemp.songName = $scope.LSongName;
                    songTemp.originalSinger= $scope.currentSingerName;
                    songsFactory.create(songTemp).$promise.then(function(){
                        $scope.songs.push(songTemp);
                    });
                }
                if ($scope.showDivType == "BG") {
                    $scope.playbillLines.push(NewSongTemp);
                    $scope.playbill.playbillLines = $scope.playbillLines;
                    $scope.showLSongsModal = false;
                } else {
                    $scope.singerOwnedSongs.push(NewSongTemp);
                    $scope.singer.singerOwnedSongs = $scope.singerOwnedSongs;
                    $scope.showLSongsModal = false;
                }
            }


            $scope.CloseLDiv = function () {
                $scope.LSongName = "";
                $scope.showLSongsModal = false;
            }

            playbillFactory.show({playbillId: $routeParams.playbillId}).$promise
                .then(function (result) {
                    $scope.playbill = result;
                    $scope.playbillLines = $scope.playbill.playbillLines;
                    singerFactory.show({singerId: $scope.playbill.singerId}).$promise
                        .then(function (result) {
                            $scope.singer = result;
                            $scope.singerOwnedSongs = $scope.singer.singerOwnedSongs;
                            $scope.currentSingerName = $scope.singer.singerName;
                        }, function () {
                        });
                }, function () {

                });
            $scope.singers = singersFactory.query({isBandsman: false});
            $scope.songs = songsFactory.query();
            $scope.activities = activitiesFactory.query({
                shopId: $rootScope.currentShopId
            });
            $scope.pictureTypes = pictureType;
            $scope.playbillStates = tzMediaApp.playbillStates;
            $scope.playbillLine = {};
        }
    ]);

