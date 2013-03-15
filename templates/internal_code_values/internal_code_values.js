angular.module('internal_code_values', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/internal_code_values', {
            templateUrl: '/templates/internal_code_values/index.html',
            controller: 'internal_code_values.Ctrl'
        })
    }])
    .controller('internal_code_values.Ctrl', ['$scope', 'JsRoutes', function Ctrl($scope, JsRoutes) {
        $scope.rows = [];
        JsRoutes.InternalCodeValues.list.get({}, function (rows) {
            $scope.rows = rows;
        });
    }])