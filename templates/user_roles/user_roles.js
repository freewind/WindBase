angular.module('user_roles', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/user_roles', {
            templateUrl: '/templates/user_roles/index.html',
            controller: 'user_roles.Ctrl'
        });
    }])
    .controller('user_roles.Ctrl', ['$rootScope', '$scope', '$dialog', 'JsRoutes', 'PagerHelper',
        function Ctrl($rootScope, $scope, $dialog, JsRoutes, PagerHelper) {
            // vars
            $scope.privilegeButtonsMapTemp = {}
            $scope.roles = [];
            $scope.addRoleModalShown = false;
            $scope.addRoleName = null;
            $scope.selectedRole = null;

            $scope.editRoleModalShown = false;
            $scope.editRoleName = null;
            $scope.selectedRows = [];
            $scope.onSelectedRows = onSelectedRows;

            // methods
            $scope.getRoles = getRoles;
            $scope.showAddRoleDialog = showAddRoleDialog;
            $scope.selectRole = selectRole;
            $scope.showAddUserToRoleDialog = showAddUserToRoleDialog;
            $scope.clearSearch = clearSearch;
            $scope.removeUsersFromRole = removeUsersFromRole;
            $scope.showMenuPanel = showMenuPanel;
            $scope.togglePrivilegeButton = togglePrivilegeButton;
            $scope.checkSelfAndChildren = checkSelfAndChildren;
            $scope.toggleSelfAndChildren = toggleSelfAndChildren;
            $scope.showEditRoleDialog = showEditRoleDialog;
            $scope.updateRole = updateRole;


            // init
            JsRoutes.Admins.getMenuTree.get({}, function (tree) {
                $scope.menuTree = tree;
            });

            JsRoutes.Admins.getPrivilegeButtonsMap.get({}, function (map) {
                $scope.privilegeButtonsMapTemp = map;
            });

            $scope.getRoles();

            function onSelectedRows(rows) {
                $scope.selectedRows = rows;
            }

            function getRoles() {
                JsRoutes.UserRoles.list.get({}, function (roles) {
                    $scope.roles = roles;
                    if (roles && roles.length > 0) {
                        $scope.selectRole(roles[0]);
                    }
                })
            }

            function showAddRoleDialog() {
                var d = $dialog.dialog({});
                d.open('/templates/user_roles/add_role_dialog.html', 'user_roles.AddRoleDialogCtrl').then(function (createdRole) {
                    $scope.roles.push(createdRole);
                });
            }

            function selectRole(role) {
                $scope.selectedRole = role;
                if (!role.users) {
                    JsRoutes.Users.listForRole.get({
                        roleId: role.id
                    }, function (users) {
                        role.users = users;
                    })
                }
                if (!role.privilegeButtonsMap) {
                    // see privilege_buttons.json
                    role.privilegeButtonsMap = angular.copy($scope.privilegeButtonsMapTemp);
                    JsRoutes.PrivilegeButtons.getForRole.get({
                        roleId: role.id
                    }, function (buttonCodes) {
                        var codes = buttonCodes.split(/,/);
                        var buttonsArray = _.values(role.privilegeButtonsMap);
                        buttonsArray.forEach(function (buttons) {
                            buttons.forEach(function (button) {
                                if (_.contains(codes, button.code)) {
                                    button.selected = true;
                                }
                            });
                        });
                    });
                }
            }

            function showAddUserToRoleDialog() {
                var d = $dialog.dialog({
                    resolve: {
                        selectedRole: $scope.selectedRole
                    }
                });
                d.open('/templates/user_roles/add_user_to_role_dialog.html', 'user_roles.AddUserToRoleDialogCtrl');
            }

            function clearSearch() {
                $scope.searchUserEmail = null;
                $scope.searchUserUsername = null;
                $scope.searchUserName = null;
                $scope.searchUserResult = [];
            }

            function removeUsersFromRole() {
                var s = $scope;
                while (s != null) {
                    console.dir(s);
                    s = s.$parent;
                }
                if (confirm("确定从该角色中删除选中的用户吗？")) {
                    console.dir($scope);
                    if ($scope.selectedRows.length == 0) {
                        throw new Error();
                    }
                    var ids = _.map($scope.selectedRows, function (user) {
                        return user.id
                    });
                    JsRoutes.UserRoles.removeUsersFromRole.post({
                        userIds: ids,
                        roleId: $scope.selectedRole.id
                    }, function () {
                        PagerHelper.removeItems($scope.selectedRole.users, $scope.selectedRows);
                    })
                }

            }

            function showMenuPanel() {
                $scope.getPrivilegeButtons();
            }

            function togglePrivilegeButton(button) {
                JsRoutes.PrivilegeButtons.toggle.post({
                    roleId: $scope.selectedRole.id,
                    buttonCode: button.code
                }, function () {
                    button.selected = !button.selected;
                });
            }

            function checkSelfAndChildren(node) {
                if (!$scope.selectedRole) return false;
                var hasButton = false;
                try {
                    visitNode(node, function (node) {
                        var buttons = $scope.selectedRole.privilegeButtonsMap[node.code];
                        _.each(buttons, function (button) {
                            hasButton = true;
                            if (!button.selected) throw false;
                        });
                    });
                } catch (result) {
                    return result;
                }
                if (hasButton) return true;
                return false;
            }


            function toggleSelfAndChildren(node) {
                var allChecked = $scope.checkSelfAndChildren(node);
                var buttonCodes = [];
                visitNode(node, function (node) {
                    var buttons = $scope.selectedRole.privilegeButtonsMap[node.code];
                    _.each(buttons, function (button) {
                        button.selected = !allChecked;
                        buttonCodes.push(button.code);
                    });
                });
                JsRoutes.PrivilegeButtons.setMany.post({
                    roleId: $scope.selectedRole.id,
                    buttonCodes: buttonCodes.join(","),
                    selected: !allChecked
                }, function () {
                });
            }


            function showEditRoleDialog() {
                $scope.editRoleName = $scope.selectedRole.name;
                $scope.editRoleModalShown = true;
            }

            function updateRole() {
                JsRoutes.UserRoles.update.post({
                    roleId: $scope.selectedRole.id,
                    name: $scope.editRoleName
                }, function () {
                    $scope.selectedRole.name = $scope.editRoleName;
                    $scope.editRoleName = null;
                    $scope.editRoleModalShown = false;
                });
            }

            function visitNodes(nodes, cb) {
                _.each(nodes, function (node) {
                    visitNode(node, cb);
                });
            }

            function visitNode(node, cb) {
                cb(node);
                _.each(node.children, function (child) {
                    visitNode(child, cb);
                });
            }
        }
    ])
    .controller('user_roles.AddUserToRoleDialogCtrl', [ '$scope', 'dialog', 'selectedRole', 'JsRoutes',
        function AddUserToRoleDialogCtrl($scope, dialog, selectedRole, JsRoutes) {
            $scope.searchUserEmail = null;
            $scope.searchUserUsername = null;
            $scope.searchUserName = null;
            $scope.searchUserResult = [];
            $scope.close = close;
            $scope.addUserToRole = addUserToRole;
            $scope.searchUser = searchUser;

            function close() {
                dialog.close();
            }

            function addUserToRole(user) {
                JsRoutes.UserRoles.addUserToRole.post({
                    userId: user.id,
                    roleId: selectedRole.id
                }, function () {
                    if (!_.contains(selectedRole.users, user)) {
                        selectedRole.users.list.push(user);
                    }
                    dialog.close();
                })
            }

            function searchUser() {
                JsRoutes.Users.search.post({
                    email: $scope.searchUserEmail,
                    username: $scope.searchUserName,
                    name: $scope.searchUserName
                }, function (users) {
                    $scope.searchUserResult = users.list;
                })
            }
        }
    ])
    .controller('user_roles.AddRoleDialogCtrl', ['$scope', 'dialog', 'JsRoutes',
        function AddRoleDialogCtrl($scope, dialog, JsRoutes) {
            $scope.roleName = null;
            $scope.close = close;
            $scope.createRole = createRole;

            function close() {
                dialog.close();
            }

            function createRole() {
                JsRoutes.UserRoles.create.post({
                    name: $scope.roleName
                }, function (role) {
                    dialog.close(role);
                })
            }
        }
    ]);