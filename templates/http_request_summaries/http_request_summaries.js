angular.module('http_request_summaries', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/http_request_summaries', {
            templateUrl: '/templates/http_request_summaries/index.html',
            controller: 'http_request_summaries.Ctrl'
        });
    }])
    .controller('http_request_summaries.Ctrl', ['$rootScope', '$scope', 'JsRoutes',
        function Ctrl($rootScope, $scope, JsRoutes) {
            $scope.chartData = {}
            $scope.showChartPanel = false;
            $scope.rows = {}

            $scope.showChart = showChart;
            $scope.getRows = getRows;
            $scope.onSelectPage = onSelectPage;

            $scope.getRows();

            function showChart(timeUnit) {
                JsRoutes.HttpRequestSummaries.getChartData.get({
                    timeUnit: timeUnit
                }, function (data) {
                    $scope.chartData = data;
                    $scope.showChartPanel = true;
                });
            }

            function getRows(page, pageSize) {
                JsRoutes.HttpRequestSummaries.list.get({
                    page: page,
                    pageSize: pageSize
                }, function (rows) {
                    $scope.rows = rows;
                });
            }

            function onSelectPage(page) {
                $scope.getRows(page);
            }

        }
    ]);