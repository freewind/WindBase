angular.module('admins', ['admin_common_data', 'myApp.services'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: '/templates/admins/login.html',
            controller: 'admins.LoginCtrl'
        })
    }])
    .controller('admins.RootCtrl', ['$rootScope', '$scope', function ($rootScope, $scope) {
        $scope.showSideMenu = function () {
            var root = $rootScope.currentRootMenuNode;
            if (root && root.children && root.children.length > 1) return true;
            return false;
        }
    }])
    .controller('admins.LoginCtrl', ['$rootScope', '$scope', 'JsRoutes', '$location', 'menuTree', 'current', 'TreeHelper',
        function ($rootScope, $scope, JsRoutes, $location, menuTree, current, TreeHelper) {
            $scope.account = null;
            $scope.password = null;

            $scope.submit = function () {
                if ($scope.account && $scope.password) {
                    JsRoutes.Users.login.post({
                        account: $scope.account,
                        password: $scope.password
                    }, function (userInfo) {
                        current.user = userInfo.user;
                        current.privilegeCodes = userInfo.privilegeCodes;
                        $rootScope.resetBasicData();

                        if ($rootScope.requestPath) {
                            $location.path($rootScope.requestPath);
                        } else {
                            var firstRoot = menuTree.data[0]
                            var path = TreeHelper.findFirstHas(menuTree, firstRoot, 'link').link;
                            $location.path(path);
                        }
                    });
                } else {
                    alert('请填写用户名和密码');
                }
            }
        }
    ])