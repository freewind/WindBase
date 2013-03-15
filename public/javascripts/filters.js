angular.module('myApp.filters', [])
    .filter('range', function () {
        return function (input, total) {
            var i;
            i = 0;
            while (i < total) {
                input.push(i);
                i++;
            }
            return input;
        };
    })
    .filter('tree2array', function () {
        return function (inputTree) {
            var arr, node, visit, _i, _len;
            visit = function (node, holder) {
                var child, _i, _len, _ref, _results;
                holder.push(node);
                _ref = node.children;
                _results = [];
                for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                    child = _ref[_i];
                    _results.push(visit(child, holder));
                }
                return _results;
            };
            arr = [];
            for (_i = 0, _len = inputTree.length; _i < _len; _i++) {
                node = inputTree[_i];
                visit(node, arr);
            }
            return arr;
        };
    })
    .filter('formatDate', ['$window', function ($window) {
        return function (input, formatStr) {
            return $window.moment(input).format(formatStr);
        };
    }])
    .filter('propertyJoin', [function () {
        return function (input, property, separator) {
            return _.map(input,function (item) {
                return item[property];
            }).join(separator || ',');
        }
    }]);
