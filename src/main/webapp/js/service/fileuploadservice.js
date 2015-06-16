'use strict';

var fileUploadServices = angular.module('fileUploadServices', ['ngResource']);

//file upload factory
fileUploadServices.factory('fileUploadFactory', function() {
    return {
        upload: function($scope,$files,$upload,uploadUrl,fun) {
            $scope.error = '';
            $scope.successMessage='';
//            var doubleByteChecker = /[^\x00-\xff]/; 
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                if (file.type.indexOf('image') === -1) {
                    $scope.error = '请上传后缀名为jpg,png等格式的图片';
                    continue;
                }
                if (file.size > 204800) {
                    $scope.error = '不能上传大于200 K的文件';
                    continue;
                }
//                if(doubleByteChecker.test(file.name)){
//                    $scope.error = '图片名含有中文或其它怪异字符，上传失败，请尽量使用字母，数字，下划线组合作为图片名。';
//                    continue;
//                }

                $scope.upload = $upload.upload({
                    url: uploadUrl, //upload.php script, node.js route, or servlet url
                    method: 'POST',
                    data: {myObj: $scope.myModelObj},
                    file: file
                    /* set the file formData name ('Content-Desposition'). Default is 'file' */
//                    fileFormDataName: file.name //or a list of names for multiple files (html5).
                }).progress(function(evt) {
                   // console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                }).success(function(data, status, headers, config) {
                    // file is uploaded successfully
//                    console.log(data);

                     if(fun != undefined){
                         fun();
                     }

                     $scope.successMessage="上传成功";
                });
            }
        }
    };
});

