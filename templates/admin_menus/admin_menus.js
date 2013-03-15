angular.module('admin_menus', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
            when('/admin_menus', {
                templateUrl: '/templates/admin_menus/index.html',
                controller: 'admin_menus.Ctrl'
            });
    }])
    .controller('admin_menus.Ctrl', [ '$scope', '$dialog', '$timeout', 'JsRoutes', 'menuTree', 'TreeHelper',
        function Ctrl($scope, $dialog, $timeout, JsRoutes, menuTree, TreeHelper) {
            $scope.menuTree = TreeHelper.rebuild(menuTree.data);
            $scope.icons = [];
            $scope.currentMenuItem = null;
            $scope.addCode = null;
            $scope.addName = null;
            $scope.addMenuDialogShown = false;
            $scope.iconDialogShown = false;
            $scope.selectedIcon = null;
            $scope.linkDialogShown = false;
            $scope.parentDialogShown = false;

            $scope.showAddMenuDialog = showAddMenuDialog;
            $scope.updateTree = updateTree;

            $scope.resetMenuTree = function (newMenuTree) {
                $scope.menuTree = newMenuTree;
            }

            function showAddMenuDialog(node) {
                var d = $dialog.dialog({
                    resolve: {
                        menuItem: node
                    }
                });
                d.open('/templates/admin_menus/add_menu_dialog.html', 'admin_menus.AddMenuDialogCtrl').then(function (newRootNode) {
                    if (newRootNode) {
                        $scope.menuTree.push(newRootNode);
                    }
                });
            }

            function updateTree() {
                JsRoutes.AdminMenus.updateTree.post($scope.menuTree, function () {
                    // do nothing
                }, null/*ignore error*/, {
                    postType: 'json'
                });
            }

        }
    ])
    .controller('admin_menus.RowCtrl', [ '$scope', '$dialog', 'JsRoutes', 'TreeHelper',
        function RowCtrl($scope, $dialog, JsRoutes, TreeHelper) {
            // $scope.item from parent
            $scope.edit = false;
            $scope.editValue = null;

            $scope.cancelEdit = cancelEdit;
            $scope.submitEdit = submitEdit;
            $scope.showEdit = showEdit;
            $scope.showIconDialog = showIconDialog;
            $scope.removeItem = removeItem;
            $scope.showLinkDialog = showLinkDialog;
            $scope.showParentDialog = showParentDialog;

            function showIconDialog(item) {
                var d = $dialog.dialog({
                    resolve: {
                        menuItem: item
                    }
                });
                d.open('/templates/admin_menus/icon_dialog.html', 'admin_menus.IconModelCtrl');
            }

            function cancelEdit() {
                $scope.edit = false;
            }

            function submitEdit() {
                if ($scope.editValue) {
                    JsRoutes.AdminMenus.updateItem.post({
                        code: $scope.item.code,
                        name: $scope.editValue
                    }, function () {
                        $scope.item.name = $scope.editValue;
                        $scope.edit = false;
                    });
                } else {
                    $scope.cancelEdit();
                }
            }

            function showEdit() {
                $scope.editValue = $scope.item.name;
                $scope.edit = true;
            }

            function removeItem(item) {
                if (confirm("确定删除当前菜单及子菜单吗？")) {
                    JsRoutes.AdminMenus.remove.post({
                        code: item.code
                    }, function () {
                        TreeHelper.removeNode($scope.menuTree, item);
                    });
                }
            }

            function showLinkDialog(item) {
                var d = $dialog.dialog({
                    resolve: {
                        menuItem: item
                    }
                });
                d.open('/templates/admin_menus/link_dialog.html', 'admin_menus.LinkDialogCtrl');
            }

            function showParentDialog(item) {
                var d = $dialog.dialog({
                    resolve: {
                        menuItem: item,
                        menuTree: $scope.menuTree
                    }
                });
                d.open('/templates/admin_menus/parent_dialog.html', 'admin_menus.ParentDialogCtrl');
            }
        }
    ])
    .controller('admin_menus.IconModelCtrl', [ '$rootScope', '$scope', 'dialog', 'JsRoutes', 'menuItem',
        function IconModelCtrl($rootScope, $scope, dialog, JsRoutes, menuItem) {
            $rootScope.icons = $rootScope.icons || [];
            $scope.menuItem = menuItem;

            $scope.close = close;
            $scope.setIcon = setIcon;

            if ($rootScope.icons.length == 0) {
                JsRoutes.Admins.getIcons.get({}, function (icons) {
                    $rootScope.icons = icons;
                });
            }

            function close() {
                dialog.close();
            }

            function setIcon(icon) {
                JsRoutes.AdminMenus.setIcon.post({
                    code: menuItem.code,
                    icon: icon
                }, function () {
                    menuItem.icon = icon;
                    dialog.close();
                });
            }

        }
    ])
    .controller('admin_menus.LinkDialogCtrl', [ '$rootScope', '$scope', 'dialog', 'JsRoutes', 'menuItem', '$route',
        function LinkDialogCtrl($rootScope, $scope, dialog, JsRoutes, menuItem, $route) {
            $rootScope.routes = $rootScope.routes || [];

            $scope.menuItem = menuItem;

            $scope.close = close;
            $scope.setLink = setLink;

            if ($rootScope.routes.length == 0) {
                var routes = [];
                _.each($route.routes, function (value, key) {
                    if (!_.string.endsWith(key, '/')) {
                        routes.push({
                            name: key,
                            templateUrl: value.templateUrl
                        })
                    }
                });
                $rootScope.routes = routes;
            }

            function close() {
                dialog.close();
            }

            function setLink(route) { // route may be null
                var routeName = (route == null ? "" : route.name);
                JsRoutes.AdminMenus.setLink.post({
                    code: menuItem.code,
                    link: routeName
                }, function () {
                    menuItem.link = routeName;
                    dialog.close();
                });
            }
        }
    ])
    .controller('admin_menus.ParentDialogCtrl', [ '$scope', 'dialog', 'menuTree', 'menuItem', 'JsRoutes', 'TreeHelper',
        function ParentDialogCtrl($scope, dialog, menuTree, menuItem, JsRoutes, TreeHelper) {
            $scope.menuTree = menuTree;
            $scope.menuItem = menuItem;

            $scope.close = close;
            $scope.setParentCode = setParentCode;

            function close() {
                dialog.close();
            }

            function setParentCode(parentCode) {
                JsRoutes.AdminMenus.resetParent.post({
                    code: menuItem.code,
                    parentCode: parentCode
                }, function () {
                    TreeHelper.removeNode(menuTree, menuItem);
                    if (parentCode) {
                        TreeHelper.findNodeBy(menuTree, 'code', parentCode).children.push(menuItem);
                    } else {
                        menuTree.push(menuItem);
                    }
                    TreeHelper.rebuild(menuTree);
                    dialog.close();
                });
            }
        }
    ])
    .controller('admin_menus.AddMenuDialogCtrl', [ '$scope', 'dialog', 'menuItem', 'JsRoutes',
        function AddMenuDialogCtrl($scope, dialog, menuItem, JsRoutes) {
            $scope.addName = null;
            $scope.menuItem = menuItem;

            $scope.close = close;
            $scope.createItem = createItem;

            function close() {
                dialog.close();
            }

            function createItem() {
                JsRoutes.AdminMenus.create.post({
                    name: $scope.addName,
                    parentCode: menuItem == null ? '' : menuItem.code
                }, function (newMenu) {
                    if (menuItem == null) {
                        dialog.close(newMenu);
                    } else {
                        menuItem.children.push(newMenu);
                    }
                    dialog.close();
                });
            }
        }
    ]);