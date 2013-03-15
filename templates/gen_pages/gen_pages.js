angular.module('gen_pages', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/gen_pages', {
                templateUrl: '/templates/gen_pages/index.html',
                controller: 'gen_pages.Ctrl'
            })
            .when('/gen_pages/configure/:modelClsName', {
                templateUrl: '/templates/gen_pages/configure.html',
                controller: 'gen_pages.ConfigureCtrl'
            })

    }])
    .controller('gen_pages.Ctrl', ['$scope', 'JsRoutes', function Ctrl($scope, JsRoutes) {
        $scope.rows = [];
        JsRoutes.GenPages.getModels.get({}, function (rows) {
            $scope.rows = rows;
        })
    }])
    .controller('gen_pages.ConfigureCtrl', ['$scope', '$filter', 'JsRoutes', '$routeParams',
        function ConfigureCtrl($scope, $filter, JsRoutes, $routeParams) {
            $scope.rows = [];
            $scope.modelClsName = $routeParams.modelClsName;

            $scope.showCheckBoxes = true;

            JsRoutes.GenPages.getModelFields.get({
                modelClsName: $scope.modelClsName
            }, function (rows) {
                $scope.rows = rows;
                _.each($scope.rows, function (row) {
                    row.enable = true;
                });
            })

            $scope.generateTable = function () {
                return JsRoutes.GenPages.generateTable.post({
                    modelClsName: $scope.modelClsName,
                    maxLineHeight: $scope.maxLineHeight,
                    modelFieldsJson: $filter("json")($scope.rows),
                    showCheckBoxes: $scope.showCheckBoxes
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateCRUD = function () {
                return JsRoutes.GenPages.generateCRUD.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateExData = function () {
                return JsRoutes.GenPages.generateExData.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    return $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateAdd = function () {
                return JsRoutes.GenPages.generateAdd.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateShow = function () {
                return JsRoutes.GenPages.generateShow.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateEdit = function () {
                return JsRoutes.GenPages.generateEdit.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateTree = function () {
                JsRoutes.GenPages.generateTree.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateSingleJson = function () {
                JsRoutes.GenPages.generateSingleJson.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            $scope.generateListJson = function () {
                JsRoutes.GenPages.generateListJson.post({
                    modelClsName: $scope.modelClsName,
                    modelFieldsJson: $filter("json")($scope.rows)
                }, function (code) {
                    $scope.generatedCode = _.str.trim(code);
                }, null, ajaxConfig());
            }
            function ajaxConfig() {
                return {
                    transformResponse: [function (data) {
                        return data;
                    }]
                }
            }
        }]);