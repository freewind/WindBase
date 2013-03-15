angular.module('myApp.directives', [])
    .directive('focus', ['$timeout', function ($timeout) {
        return function (scope, element, attrs) {
            function tryFocus() {
                if (scope.$eval(attrs.focus)) {
                    $timeout(function () {
                        return element.focus();
                    });
                }
            };
            return scope.$watch(attrs.focus, function () {
                return tryFocus();
            });
        };
    }])
    .directive('slider', function () {
        return {
            require: "?ngModel",
            restrict: "A",
            link: function (scope, element, attrs, ngModel) {
                var opts, slider;
                opts = void 0;
                opts = angular.extend({}, scope.$eval(attrs.slider));
                slider = element.slider({
                    min: opts.min || 0,
                    max: opts.max || 100,
                    step: opts.step || 10,
                    value: attrs.ngModel && scope.$eval(attrs.ngModel) || 50,
                    slide: function (event, ui) {
                        if (ngModel) {
                            return scope.$apply(function () {
                                return ngModel.$setViewValue(ui.value);
                            });
                        }
                    }
                });
                return scope.$watch(attrs.ngModel, function (v) {
                    return slider.slider({
                        value: v
                    });
                });
            }
        };
    })
    .directive('uiFixedWidth', function () {
        return {
            restrict: "A",
            link: function (scope, element, attrs) {
                var fixedWidth;
                fixedWidth = attrs["uiFixedWidth"];
                element.css("max-width", fixedWidth + "px");
                element.css("width", fixedWidth + "px");
                element.css("min-width", fixedWidth + "px");
            }
        };
    })
    .directive('uiMaxHeight', function () {
        return {
            restrict: "A",
            compile: function (tElement, tAttrs, transclude) {
                var maxHeight;
                maxHeight = tAttrs["uiMaxHeight"];
                tElement.attr("ng-style", "{'max-height':" + maxHeight + " + 'px'}");
                return tElement.find("td").each(function () {
                    var $div, destHtml;
                    $div = $("<div>" + $(this).html() + "</div>");
                    $div.attr("ng-style", "{'max-height':" + maxHeight + " + 'px'}").css("overflow", "auto");
                    destHtml = $("<div>").append($div).html();
                    return $(this).html(destHtml);
                });
            }
        };
    })
    .directive('debugSupport', ['$rootScope', function ($rootScope) {
        return {
            restrict: "A",
            link: function (scope, element, attrs) {
                var buttons = element.find(".debug button");
                buttons.removeAttr("disabled");
                element.keydown(function (event) {
                    if (event.altKey && event.ctrlKey) {
                        $rootScope.$$debug_mode$$ = !$rootScope.$$debug_mode$$;
                        element.find(".debug").toggleClass("debug-hide");
                    }
                });
            }
        };
    }])
    .directive('ngModelOnblur', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, elm, attr, ngModelCtrl) {
                if (attr.type === 'radio' || attr.type === 'checkbox') return;

                elm.unbind('input').unbind('keydown').unbind('change');
                elm.bind('blur', function () {
                    scope.$apply(function () {
                        ngModelCtrl.$setViewValue(elm.val());
                    });
                });
            }
        };
    })
    .directive('grid', ['$compile', '$window', '$filter', 'JsRoutes',
        function ($compile, $window, $filter, JsRoutes) {
            var globalCellStyle = {
                "white-space": "nowrap",
                "overflow": "hidden"
            };
            return {
                restrict: "A",
                templateUrl: '/templates/directives/grid.html',
                scope: {
                    params: '@grid'
                },
                controller: function ($scope, $element, $attrs, $transclude) {
                    var params = $scope.$eval($scope.params);
                    $scope.gridId = params.id;
                    $scope.pagerName = params.pager;
                    $scope.onSelectedRowsName = params.onSelectedRows;
                    $scope.onSelectPageName = params.onSelectPage;

                    $scope.getPager = getPager;
                    $scope.getSelectedRows = getOnSelectedRows;
                    $scope.getOnSelectPage = getOnSelectPage;

                    $scope.getColumnDefsByRow = getColumnDefsByRow;
                    $scope.resetConfig = resetConfig;
                    $scope.toggleAllCheckboxes = toggleAllCheckboxes;
                    $scope.isAllRowsChecked = isAllRowsChecked;
                    $scope.getCellStyle = getCellStyle;
                    $scope.formatCode = formatCode;
                    $scope.validateCode = validateCode;
                    $scope.saveGridOptions = saveGridOptions;
                    $scope.getGridOptionsAsJson = getGridOptionsAsJson;
                    $scope.showCheckbox = showCheckbox;
                    $scope.gridOptions = {};
                    $scope.validSyntax = true;

                    JsRoutes.Directives.getGridOptions.get({
                        id: $scope.gridId
                    }, function (options) {
                        $scope.gridOptions = options;
                    });

                    if ($scope.onSelectedRowsName) {
                        $scope.$watch("getPager().list", function (rows) {
                            var selected = _.filter(rows, function (row) {
                                return row.checked;
                            });
                            getOnSelectedRows()(selected);
                        }, true);
                    }

                    // TODO: 这里应该改成更好的方式
                    $scope.$parent.configureGrid = function () {
                        $scope.gridOptionsForEdit = $scope.getGridOptionsAsJson();
                        $scope.showGridOptionsEditor = !$scope.showGridOptionsEditor;
                    }

                    $scope.$watch('gridOptionsForEdit', function (json) {
                        if (json) {
                            try {
                                $scope.gridOptions = $scope.$eval(json);
                                $scope.validSyntax = true;
                            } catch (err) {
                                $scope.validSyntax = false;
                            }
                        }
                    });

                    function showCheckbox() {
                        return $scope.onSelectedRowsName ? true : false;
                    }

                    function getColumnDefsByRow() {
                        var rows = $scope.getPager().list;
                        if (rows && rows.length > 0) {
                            var row = rows[0];
                            var defs = [];
                            angular.forEach(row, function (value, key) {
                                defs.push({
                                    field: key,
                                    displayName: key,
                                    width: 100
                                })
                            })
                            return defs;
                        }
                        return [];
                    }

                    function resetConfig() {
                        $scope.gridOptions = {
                            id: $scope.gridId,
                            columnDefs: $scope.getColumnDefsByRow()
                        }
                        $scope.gridOptionsForEdit = $scope.getGridOptionsAsJson();
                    }

                    function toggleAllCheckboxes() {
                        if ($scope.getPager()) {
                            var allChecked = $scope.isAllRowsChecked();
                            _.each($scope.getPager().list, function (row) {
                                row.checked = !allChecked;
                            })
                        }
                    };

                    function isAllRowsChecked() {
                        if ($scope.getPager()) {
                            var list = $scope.getPager().list;
                            if (list && list.length > 0) {
                                return _.every(list, function (row) {
                                    return row.checked;
                                });
                            } else {
                                return false;
                            }
                        }
                        return false;
                    };

                    function getCellStyle(col) {
                        return angular.extend({}, {width: col.width, 'min-width': col.width, 'max-width': col.width},
                            col.style, $scope.gridOptions.cellStyle, globalCellStyle);
                    }

                    function formatCode() {
                        // global js_beautify object
                        $scope.gridOptionsForEdit = $window.js_beautify($scope.gridOptionsForEdit);
                    }

                    function validateCode() {
                        // global jsl object
                        try {
                            $window.jsl.parser.parse($scope.gridOptionsForEdit);
                            alert('ok');
                        } catch (err) {
                            alert(err.message);
                        }
                    }

                    function saveGridOptions() {
                        var json = $scope.gridOptionsForEdit;
                        JsRoutes.Directives.saveGridOptions.post(json, function () {
                            $scope.gridOptions = $scope.$eval(json);
                            $scope.gridOptionsForEdit = $scope.getGridOptionsAsJson();
                        }, /*err*/null, {
                            postType: 'json'
                        });
                    }

                    function getGridOptionsAsJson() {
                        if ($scope.gridOptions) {
                            var json = $filter("json")($scope.gridOptions);
                            return  $window.js_beautify(json);
                        } else {
                            return null;
                        }
                    }

                    function getPager() {
                        return $scope.$parent.$eval($scope.pagerName);
                    }

                    function getOnSelectedRows() {
                        return $scope.$parent.$eval($scope.onSelectedRowsName);
                    }

                    function getOnSelectPage() {
                        return $scope.$parent.$eval($scope.onSelectPageName);
                    }
                }
            }
        }
    ])
    .directive('gridCell', ['$compile', function ($compile) {
        return {
            scope: false,
            compile: function () {
                return {
                    pre: function ($scope, iElement, attrs) {
                        var html = $scope.col.cellTemplate;
                        if (html) {
                            html = "<span>" + html + "</span>";
                            iElement.empty();
                            iElement.append($compile(html)($scope));
                            $scope.$watch(function () {
                                return $scope.row[$scope.col.field];
                            }, function (cellValue) {
                                $scope.cellValue = cellValue;
                            });
                        }
                    }
                };
            }
        };
    }])
    .directive('chart', function () {
        return {
            restrict: 'A',
            link: function (scope, elem, attrs) {
                var chart = null;
                var preOptions = null;

                function getWeek(t) {
                    var d = new Date(t);
                    var onejan = new Date(d.getFullYear(), 0, 1);
                    return Math.ceil((((d - onejan) / 86400000) + onejan.getDay() + 1) / 7);
                }

                scope.$watch(attrs.chart, function (chartData) {
                    if (chartData && chartData.data) {
                        var options = chartData.options;
                        if (options != preOptions && chart) {
                            chart.shutdown();
                            chart = null;
                        }
                        if (!chart) {
                            chart = $.plot(elem, chartData.data, options);
                            preOptions = options;
                            elem.show();
                        } else {
                            chart.setData(chartData.data);
                            chart.setupGrid();
                            chart.draw();
                        }
                    }
                });
            }
        };
    })
    .directive('buttonCode', ['$rootScope', 'privilegeButtonMap',
        function ButtonCode($rootScope, privilegeButtonMap) {
            return {
                restrict: 'A',
                link: function (scope, elm, attr, ngModelCtrl) {
                    var code = attr['buttonCode'];
                    _.each(_.values(privilegeButtonMap), function (buttons) {
                        _.each(buttons, function (button) {
                            if (button.code === code) {
                                elm.prepend("<i class='" + button.icon + "'></i> ");
                            }
                        })
                    })
                    scope.$watch("currentPrivilegeCodes", function (codes) {
                        if (codes === undefined || codes === null) return;
                        if (_.contains(codes, code)) {
                            // OK
                        } else {
                            // remove all event handlers
                            angular.forEach(
                                'click dblclick mousedown mouseup mouseover mouseout mousemove mouseenter mouseleave'.split(' '),
                                function (name) {
                                    elm.unbind(name);
                                }
                            );
                            elm.attr("title", "您没有权限点击该按钮");
                            elm.addClass("not_allowed");
                            elm.attr("disabled", "true");
                        }
                    });
                }
            };
        }
    ])
    .directive('menuCode', ['menuTree', 'TreeHelper', function (menuTree, TreeHelper) {
        return {
            link: function (scope, elm, attrs) {
                attrs.$observe('menuCode', function (code) {
                    var node = TreeHelper.findNodeBy(menuTree.data, "code", code);
                    scope.$watch(function () {
                        return node;
                    }, function (newIcon) {
                        elm.css('padding-left', node.level * 20);
                        elm.find('i.generated_icon').remove();
                        elm.prepend("<i class='" + node.icon + " generated_icon'></i> ");
                    }, true);

                });
            }
        }
    }])