angular.module('share',[]).controller('shareController',['$scope','$http',function($scope,$http){
    var shareResponse=$http.get(getShareDataUrl());
    shareResponse.success(function(data,status,headers,config){
       $scope.publicData=data;
    });
    $scope.isShowCode=false;
    $scope.downloadApp=function() {
        if (isWeixin()) {
            $scope.isShowCode=true;
        } else {
            window.location.href = "http://mp.weixin.qq.com/mp/redirect?url=http%3A%2F%2Fitunes.apple.com%2Fus%2Fapp%2Fid926796574%23rd";
        }
    }
    if(getQueryStr('type')=="activity"||getQueryStr('type')=="shop"){
        var map = new BMap.Map("allmap");
        var point = new BMap.Point(121.4787,31.238541);
    }
    $scope.openMap=function(address){
        map.centerAndZoom(point,12);
        map.addControl(new BMap.ZoomControl());
        // 创建地址解析器实例
        var myGeo = new BMap.Geocoder();
        // 将地址解析结果显示在地图上,并调整地图视野
        myGeo.getPoint(address, function(point){
            if (point) {
                map.centerAndZoom(point, 16);
                map.addOverlay(new BMap.Marker(point));
            }
        }, "上海");
        SetPageState("page2","#map");
        document.getElementById("share").style.display="none";
        document.getElementById("allmap").style.display="block";
    }
    window.onpopstate = function(event) {
        if(window.location.hash=="#map"){
            document.getElementById("share").style.display="none";
            document.getElementById("allmap").style.display="block";
        }else{
            document.getElementById("share").style.display="block";
            document.getElementById("allmap").style.display="none";
        }


    };

}]);
function SetPageState(page,src){
    var stateObj = { foo: "bar" };
    history.pushState(stateObj,page,src);
}
function getShareDataUrl(){
    var shareType=getQueryStr('type');
    var shareId=getQueryStr('id');
    return location.protocol+'//'+location.host+'/tzzjweixinshare/servlet/WeixinServlet?type='+shareType+'&id='+shareId;
}
function getQueryStr(str){
    var url=location.href;
    var rs = new RegExp("(^|)"+str+"=([^\&]*)(\&|$)","gi").exec(url), tmp;
    if(tmp=rs){
        return tmp[2];
    }
    return "";
}

function isWeixin(){
    var ua = navigator.userAgent.toLowerCase();

    if(ua.match(/MicroMessenger/i)=="micromessenger") {
        return true;
    } else {
        return false;
    }
}
