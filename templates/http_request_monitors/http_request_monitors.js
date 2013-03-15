angular.module('http_request_monitors', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/http_request_monitors', {
            templateUrl: '/templates/http_request_monitors/index.html',
            controller: 'http_request_monitors.Ctrl'
        });
    }])
    .controller('http_request_monitors.Ctrl', ['$rootScope', '$scope', 'JsRoutes', '$filter', '$window', '$dialog',
        function Ctrl($rootScope, $scope, JsRoutes, $filter, $window, $dialog) {
            $scope.rows = {}
            $scope.showModalShown = false;
            $scope.selectedRows = [];
            $scope.onSelectedRows = onSelectedRows;

            $scope.getRows = getRows;
            $scope.showDetail = showDetail;
            $scope.onSelectPage = onSelectPage;

            $scope.getRows();

            function onSelectedRows(rows) {
                $scope.selectedRows = rows;
            }

            function getRows(page, pageSize) {
                JsRoutes.HttpRequestMonitor.list.get({
                    page: page,
                    pageSize: pageSize
                }, function (rows) {
                    $scope.rows = rows;
                });
            }

            function showDetail() {
                var d = $dialog.dialog({
                    backdrop: true,
                    keyboard: true,
                    backdropClick: true,
                    resolve: {
                        row: $scope.selectedRows[0]
                    }
                });
                d.open('/templates/http_request_monitors/show_detail_dialog.html', 'http_request_monitors.ShowDetailDialogCtrl');
            }

            function onSelectPage(page) {
                $scope.getRows(page);
            }

        }
    ])
    .controller('http_request_monitors.ShowDetailDialogCtrl', ['$scope', 'dialog', 'row',
        function ShowDetailDialogCtrl($scope, dialog, row) {
            $scope.row = row;
            $scope.close = close;
            function close() {
                dialog.close();
            }
        }
    ])
