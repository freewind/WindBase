angular.module('articles', [])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/articles', {
            templateUrl: '/templates/articles/index.html',
            controller: 'articles.Ctrl'
        });
    }])
    .controller('articles.Ctrl', ['$scope', '$dialog', 'JsRoutes', 'TreeHelper',
        function Ctrl($scope, $dialog, JsRoutes, TreeHelper) {
            $scope.currentCategory = null;
            $scope.categoryTree = [];
            $scope.showAddCategoryDialog = showAddCategoryDialog;
            $scope.removeCategory = removeCategory;
            $scope.updateTree = updateTree;
            $scope.showChangeParentDialog = showChangeParentDialog;
            $scope.setCurrentCategory = setCurrentCategory;

            JsRoutes.Categories.getTree.get({}, function (tree) {
                $scope.categoryTree = TreeHelper.rebuild(tree);
                if (tree && tree.length > 0) {
                    $scope.currentCategory = tree[0];
                }
            })

            function setCurrentCategory(category) {
                $scope.currentCategory = category;
            }

            function showAddCategoryDialog(parent) {
                $scope.currentCategory = parent;
                var d = $dialog.dialog({
                    resolve: {
                        currentCategory: parent
                    }
                });
                d.open('/templates/articles/add_category_dialog.html', 'articles.AddCategoryDialogCtrl').then(function (newRootNode) {
                    if (newRootNode) {
                        $scope.categoryTree.push(newRootNode);
                    }
                });
            }

            function removeCategory(node) {
                if (confirm("确定删除吗?")) {
                    return JsRoutes.Categories.remove.post({
                        id: node.code
                    }, function () {
                        TreeHelper.removeNode($scope.categoryTree, node);
                    });
                }
            }

            function updateTree() {
                JsRoutes.Categories.updateOrder.post($scope.categoryTree, function () {
                }, null, {
                    postType: "json"
                });
            }

            function showChangeParentDialog(node) {
                $scope.currentCategory = node;
                var d = $dialog.dialog({
                    resolve: {
                        categoryTree: $scope.categoryTree,
                        currentCategory: node
                    }
                });
                d.open('/templates/articles/change_parent_dialog.html', 'articles.ChangeParentDialogCtrl');
            }


        }
    ])
    .controller('articles.ArticleCtrl', ['$scope', '$dialog', 'JsRoutes', 'PagerHelper',
        function ArticleCtrl($scope, $dialog, JsRoutes, PagerHelper) {
            $scope.selectedRows = [];
            $scope.onSelectedRows = onSelectedRows;
            $scope.showAddArticleDialog = showAddArticleDialog;
            $scope.removeArticles = removeArticles;
            $scope.showEditArticleDialog = showEditArticleDialog;
            $scope.onSelectPage = onSelectPage;
            $scope.showViewArticleDialog = showViewArticleDialog;


            $scope.$watch("currentCategory", function (category) {
                if (category) {
                    $scope.onSelectPage(1);
                }
            })

            function onSelectedRows(rows) {
                $scope.selectedRows = rows;
            }

            function getCurrentCategory() {
                return $scope.$parent.currentCategory;
            }

            function onSelectPage(page) {
                return JsRoutes.Articles.list.get({
                    categoryId: getCurrentCategory().code,
                    page: page
                }, function (data) {
                    getCurrentCategory().articles = data;
                });
            }

            function showViewArticleDialog() {
                var d = $dialog.dialog({
                    resolve: {
                        article: $scope.selectedRows[0]
                    }
                });
                d.open('/templates/articles/show_article_dialog.html', 'articles.ViewArticleDialogCtrl');
            }

            function showAddArticleDialog() {
                var d = $dialog.dialog({
                    resolve: {
                        currentCategory: getCurrentCategory(),
                        article: null
                    }
                })
                d.open('/templates/articles/article_dialog.html', 'articles.ArticleDialogCtrl').then(function (newArticle) {
                    if (newArticle) {
                        PagerHelper.addItem(getCurrentCategory().articles, newArticle);
                    }
                })
            }

            function showEditArticleDialog() {
                var d = $dialog.dialog({
                    resolve: {
                        currentCategory: getCurrentCategory(),
                        article: $scope.selectedRows[0]
                    }
                })
                d.open('/templates/articles/article_dialog.html', 'articles.ArticleDialogCtrl');
            }

            function removeArticles() {
                if (confirm("确定删除吗?")) {
                    var ids = $scope.selectedRows.map(function (article) {
                        return article.id;
                    });
                    return JsRoutes.Articles.remove.post({
                        ids: ids
                    }, function () {
                        PagerHelper.removeItems($scope.currentCategory.articles, $scope.selectedRows);
                    });
                }
            }
        }
    ])
    .controller('articles.RowCtrl', ['$scope', 'JsRoutes',
        function RowCtrl($scope, JsRoutes) {
            $scope.showEdit = showEdit;
            $scope.update = update;
            $scope.cancelEdit = cancelEdit;

            function showEdit() {
                $scope.editValue = $scope.node.name;
                return $scope.edit = true;
            }

            function update() {
                if ($scope.editValue) {
                    return JsRoutes.Categories.update.post({
                        id: $scope.node.code,
                        name: $scope.editValue.trim(),
                        parentId: $scope.node.parentCode
                    }, function () {
                        $scope.node.name = $scope.editValue.trim();
                        return $scope.edit = false;
                    });
                }
            }

            function cancelEdit() {
                $scope.edit = false;
            }
        }
    ])
    .controller('articles.AddCategoryDialogCtrl', ['$scope', 'dialog', 'currentCategory', 'JsRoutes',
        function AddCategoryDialogCtrl($scope, dialog, currentCategory, JsRoutes) {
            $scope.currentCategory = currentCategory;
            $scope.name = null;
            $scope.close = close;
            $scope.createCategory = createCategory;

            function close() {
                dialog.close();
            }

            function createCategory() {
                return JsRoutes.Categories.create.post({
                    parentId: currentCategory == null ? null : currentCategory.code,
                    name: $scope.name
                }, function (node) {
                    $scope.name = null;
                    if (currentCategory != null) {
                        currentCategory.children.push(node);
                        dialog.close();
                    } else {
                        dialog.close(node);
                    }
                });
            }
        }
    ])
    .controller('articles.ChangeParentDialogCtrl', ['$scope', 'dialog', 'currentCategory', 'categoryTree', 'JsRoutes', 'TreeHelper',
        function ChangeParentDialogCtrl($scope, dialog, currentCategory, categoryTree, JsRoutes, TreeHelper) {
            $scope.currentCategory = currentCategory;
            $scope.categoryTree = categoryTree;

            $scope.close = close;
            $scope.changeParent = changeParent;

            function close() {
                dialog.close();
            }

            function changeParent(parentNode) {
                JsRoutes.Categories.changeParent.post({
                    id: currentCategory.code,
                    parentId: parentNode == null ? null : parentNode.code
                }, function () {
                    TreeHelper.moveNode(categoryTree, currentCategory, parentNode);
                    dialog.close();
                });
            }
        }
    ])
    .controller('articles.ArticleDialogCtrl', ['$scope', 'dialog', 'currentCategory', 'article', 'JsRoutes',
        function ArticleDialogCtrl($scope, dialog, currentCategory, article, JsRoutes) {
            $scope.currentCategory = currentCategory;
            $scope.article = article;
            $scope.title = article ? article.title : null;
            $scope.content = article ? article.content : null;

            $scope.close = close;
            $scope.createArticle = createArticle;
            $scope.updateArticle = updateArticle;

            function close() {
                dialog.close();
            }

            function createArticle() {
                JsRoutes.Articles.create.post({
                    categoryId: currentCategory.code,
                    title: $scope.title,
                    content: $scope.content
                }, function (article) {
                    dialog.close(article);
                });
            }

            function updateArticle() {
                JsRoutes.Articles.update.post({
                    id: article.id,
                    title: $scope.title,
                    content: $scope.content
                }, function () {
                    article.title = $scope.title;
                    article.content = $scope.content;
                    dialog.close();
                });
            }
        }
    ])
    .controller('articles.ViewArticleDialogCtrl', ['$scope', 'dialog', 'article', function ($scope, dialog, article) {
        $scope.article = article;
        $scope.close = close;
        function close() {
            dialog.close();
        }
    }]);