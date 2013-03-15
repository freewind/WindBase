angular.module('users', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/users', {
            templateUrl: '/templates/users/index.html',
            controller: 'users.Ctrl'
        })
    }])
    .controller('users.Ctrl', ['$rootScope', '$scope', '$dialog', 'JsRoutes', 'PagerHelper',
        function Ctrl($rootScope, $scope, $dialog, JsRoutes, PagerHelper) {
            $scope.users = [];
            $scope.selectedRows = [];
            $scope.onSelectedRows = onSelectedRows;

            $scope.getUsers = getUsers;
            $scope.showAddDialog = showAddDialog;
            $scope.showEditDialog = showEditDialog;
            $scope.removeUser = removeUser;
            $scope.setUserStatus = setUserStatus;
            $scope.changePassword = changePassword;
            $scope.onSelectPage = onSelectPage;

            $scope.getUsers(1);

            function onSelectedRows(rows) {
                $scope.selectedRows = rows;
            }

            function getUsers(page, pageSize) {
                JsRoutes.Users.list.get({
                    page: page,
                    pageSize: pageSize
                }, function (users) {
                    $scope.users = users;
                })
            }

            function showAddDialog() {
                $dialog.dialog({}).open('/templates/users/add_dialog.html', 'users.AddDialogCtrl').then(function (user) {
                    if (user) {
                        $scope.users.list.push(user);
                    }
                });
            }

            function showEditDialog() {
                $dialog.dialog({
                    resolve: {
                        user: $scope.selectedRows[0]
                    }
                }).open('/templates/users/edit_dialog.html', 'users.EditDialogCtrl');
            }

            function removeUser() {
                if (confirm("确定删除吗?")) {
                    var ids = $scope.selectedRows.map(function (user) {
                        return user.id;
                    });
                    JsRoutes.Users.remove.post({
                        ids: ids
                    }, function () {
                        PagerHelper.removeItems($scope.users, $scope.selectedRows);
                    })
                }
            }

            function setUserStatus(status) {
                var ids = $scope.selectedRows.map(function (user) {
                    return user.id
                });
                JsRoutes.Users.setStatus.post({
                    ids: ids,
                    status: status
                }, function () {
                    $scope.selectedRows.forEach(function (user) {
                        user.disabled = !status;
                    });
                })
            }

            function changePassword() {
                var d = $dialog.dialog({
                    resolve: {
                        user: $scope.selectedRows[0]
                    }
                });
                d.open('/templates/users/change_password_dialog.html', 'users.ChangePasswordDialogCtrl');
            }

            function onSelectPage(page) {
                $scope.getUsers(page);
            }

        }
    ])
    .controller('users.AddDialogCtrl', [ '$scope', 'dialog', 'JsRoutes',
        function AddDialogCtrl($scope, dialog, JsRoutes) {
            $scope.addEmail = null;
            $scope.addUsername = null;
            $scope.addPassword = null;

            $scope.close = close;
            $scope.createUser = createUser;

            function close() {
                dialog.close();
            }

            function createUser() {
                JsRoutes.Users.create.post({
                    email: $scope.addEmail,
                    username: $scope.addUsername,
                    password: $scope.addPassword
                }, function (user) {
                    dialog.close(user);
                })
            }
        }
    ])
    .controller('users.EditDialogCtrl', ['$scope', 'dialog', 'user', 'JsRoutes',
        function EditDialogCtrl($scope, dialog, user, JsRoutes) {
            $scope.email = user.email;
            $scope.username = user.username;

            $scope.close = close;
            $scope.updateUser = updateUser;

            function close() {
                dialog.close();
            }

            function updateUser() {
                JsRoutes.Users.update.post({
                    id: user.id,
                    username: $scope.username,
                    email: $scope.email
                }, function () {
                    user.username = $scope.username;
                    user.email = $scope.email;
                    dialog.close();
                });
            }
        }
    ])
    .controller('users.ChangePasswordDialogCtrl', ['$scope', 'dialog', 'user',
        function ChangePasswordDialogCtrl($scope, dialog, user, JsRoutes) {
            $scope.user = user;
            $scope.password = null;

            $scope.change = change;
            $scope.close = close;

            function change() {
                JsRoutes.Users.changePassword.post({
                    id: user.id,
                    password: $scope.password
                }, function () {
                    dialog.close();
                });
            }

            function close() {
                dialog.close();
            }
        }
    ])