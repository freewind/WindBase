angular.module('myApp', [
        'ui', 'ngGrid', 'ui.directives', 'ui.bootstrap',
        'myApp.filters', 'myApp.services', 'myApp.directives',
        'JsRoutes', 'admin_common_data', 'current_user_data',
        'admins', 'admin_menus', 'http_request_monitors', 'http_request_summaries', 'user_roles', 'users',
        'articles', 'privilege_buttons', 'gen_pages', 'internal_code_values'
    ])
    .config(['$locationProvider', '$routeProvider', function ($locationProvider, $routeProvider) {
        $routeProvider.otherwise('/login');
        $locationProvider.html5Mode(true);
    }])
    .run(['$rootScope', 'JsRoutes', '$location', 'Utils', 'menuTree', 'current', 'TreeHelper', 'PagerHelper',
        function ($rootScope, JsRoutes, $location, Utils, menuTree, current, TreeHelper, PagerHelper) {
            $rootScope.TreeHelper = TreeHelper;
            $rootScope.PagerHelper = PagerHelper;
            $rootScope.menuTree = null;
            $rootScope.currentUser = null;
            $rootScope.currentPrivilege = null;
            $rootScope.requestPath = null;

            $rootScope.resetBasicData = function () {
                $rootScope.menuTree = TreeHelper.rebuild(menuTree.data);
                $rootScope.currentUser = current.user;
                $rootScope.currentPrivilege = current.privilegeCodes;
            }
            $rootScope.resetBasicData();

            $rootScope.$on('$routeChangeStart', function (scope, current) {
                var path = $location.path();

                // check login
                console.log(path);
                if (path !== '/login' && !$rootScope.currentUser.id) {
                    $rootScope.requestPath = path;
                    $location.path('/login');
                }

                // find menu node
                var currentNode = TreeHelper.findNodeBy($rootScope.menuTree, 'link', $location.path());
                if (currentNode) {
                    $rootScope.currentMenuNode = currentNode
                    $rootScope.currentRootMenuNode = TreeHelper.findRootOfNode($rootScope.menuTree, currentNode);
                }
            });
        }
    ])

