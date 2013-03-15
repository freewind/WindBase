angular.module('privilege_buttons', ['admin_common_data', 'myApp.services'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/privilege_buttons', {
            templateUrl: '/templates/privilege_buttons/index.html',
            controller: 'privilege_buttons.Ctrl'
        });
    }])
    .controller('privilege_buttons.Ctrl', ['$scope', 'JsRoutes', '$dialog', 'menuTree', 'TreeHelper',
        function Ctrl($scope, JsRoutes, $dialog, menuTree, TreeHelper) {
            $scope.menuTree = TreeHelper.rebuild(menuTree.data);
            $scope.buttonsMap = {}
            $scope.currentButton = null;
            $scope.currentMenuItem = null;

            $scope.showAddButtonDialog = showAddButtonDialog;
            $scope.editButton = editButton;
            $scope.showCopyButtonDialog = showCopyButtonDialog;

            JsRoutes.Admins.getPrivilegeButtonsMap.get({}, function (map) {
                $scope.buttonsMap = map;
            });

            function showAddButtonDialog(item) {
                $scope.currentMenuItem = item;
                $scope.currentButton = null;
                openButtonDialog();
            }

            function openButtonDialog() {
                var d = $dialog.dialog({
                    resolve: {
                        currentMenuItem: $scope.currentMenuItem,
                        currentButton: $scope.currentButton,
                        buttonsMap: $scope.buttonsMap
                    }
                });
                d.open('/templates/privilege_buttons/button_dialog.html', 'privilege_buttons.ButtonDialogCtrl');
            }


            function editButton(button) {
                $scope.currentButton = button;
                $scope.currentMenuItem = findNode($scope.menuTree, button.menuCode);
                openButtonDialog();
            }

            function showCopyButtonDialog(menuNode) {
                var d = $dialog.dialog({
                    resolve: {
                        buttons: $scope.buttonsMap[menuNode.code]
                    }
                });
                d.open('/templates/privilege_buttons/copy_dialog.html', 'privilege_buttons.CopyButtonHtmlCtrl');
            }

            function findNode(tree, code) {
                function visit(nodes, code) {
                    _.each(nodes, function (node) {
                        if (node.code == code) throw node;
                        visit(node.children, code);
                    });
                }

                try {
                    visit(tree, code);
                    return null;
                } catch (node) {
                    return node;
                }
            }
        }
    ])
    .controller('privilege_buttons.ButtonDialogCtrl', ['$scope', 'dialog', 'buttonsMap', 'currentMenuItem', 'currentButton', 'JsRoutes',
        function ($scope, dialog, buttonsMap, currentMenuItem, currentButton, JsRoutes) {
            $scope.currentMenuItem = currentMenuItem;
            $scope.currentButton = currentButton;
            $scope.buttonCode = currentButton ? currentButton.code : null;
            $scope.buttonIcon = currentButton ? currentButton.icon : null;
            $scope.buttonName = currentButton ? currentButton.name : null;

            $scope.hoverIcon = null;
            $scope.iconPanelShown = false;
            $scope.icons = [];

            $scope.showCopyInDialog = false;

            $scope.close = close;
            $scope.toggleIconPanel = toggleIconPanel;
            $scope.selectIcon = selectIcon;
            $scope.createOrUpdateButton = createOrUpdateButton;
            $scope.toggleSingleCopy = toggleSingleCopy;
            $scope.removeButton = removeButton;

            JsRoutes.Admins.getIcons.get({}, function (icons) {
                $scope.icons = icons;
            });


            function close() {
                dialog.close();
            }


            function toggleIconPanel() {
                $scope.iconPanelShown = !$scope.iconPanelShown;
            }


            function selectIcon(icon) {
                $scope.buttonIcon = icon;
                $scope.iconPanelShown = false;
                $scope.hoverIcon = null;
            }

            function removeButton() {
                if (confirm("确定删除吗?")) {
                    JsRoutes.PrivilegeButtons.removeButton.post({
                        code: $scope.buttonCode
                    }, function () {
                        var buttons = getButtonFamily(buttonsMap, $scope.buttonCode);
                        var index = buttons.indexOf($scope.currentButton);
                        buttons.splice(index, 1);
                        dialog.close();
                    });
                }
            }

            function createOrUpdateButton() {
                if ($scope.currentButton != null) {
                    JsRoutes.PrivilegeButtons.updateButton.post({
                        code: $scope.currentButton.code,
                        name: $scope.buttonName,
                        icon: $scope.buttonIcon
                    }, function () {
                        $scope.currentButton.name = $scope.buttonName;
                        $scope.currentButton.icon = $scope.buttonIcon;
                        dialog.close();
                    });
                } else {
                    JsRoutes.PrivilegeButtons.createButton.post({
                        menuCode: $scope.currentMenuItem.code,
                        name: $scope.buttonName,
                        icon: $scope.buttonIcon
                    }, function (button) {
                        var code = $scope.currentMenuItem.code;
                        var bs = buttonsMap[code];
                        if (bs == null) {
                            buttonsMap[code] = bs = [];
                        }
                        bs.push(button);
                        dialog.close();
                    });
                }
            }

            function toggleSingleCopy() {
                $scope.showCopyInDialog = !$scope.showCopyInDialog;
            }

            function getButtonFamily(buttonsMap, buttonCode) {
                var values = _.values(buttonsMap);
                return _.find(values, function (buttons) {
                    return _.find(buttons, function (button) {
                        return button.code == buttonCode
                    });
                })
            }
        }
    ])
    .controller('privilege_buttons.CopyButtonHtmlCtrl', ['$scope', 'dialog', 'buttons',
        function CopyButtonHtmlCtrl($scope, dialog, buttons) {
            $scope.buttons = buttons;
            $scope.close = close;

            function close() {
                dialog.close();
            }
        }
    ])