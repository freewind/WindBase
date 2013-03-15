angular.module('myApp.services', ['admin_common_data'])
    .service("Utils", ["$location", function ($location) {
        return {
            getParams: function () {
                var url = $location.absUrl();
                var index = url.indexOf('?');
                if (index == -1) return {};
                var items = url.substr(index + 1).split(/[&]/);
                var params = {};
                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    var kv = item.split(/[=]/);
                    params[kv[0]] = kv[1];
                }
                return params;
            }
        }
    }])
    .value("TreeHelper", {
        rebuild: function (tree) {
            var nodes = this.getAllNodes(tree);
            _.each(nodes, function (node) {
                node.level = 0;
            })
            _.each(nodes, function (node) {
                _.each(node.children, function (child) {
                    child.level = node.level + 1;
                    child.parentCode = node.code;
                })
            })
            return tree;
        },
        getAllNodes: function (tree) {
            if (tree) {
                var nodes = [].concat(tree);
                for (var i = 0; i < nodes.length; i++) {
                    var node = nodes[i];
                    if (node && node.children) {
                        nodes = nodes.concat(node.children);
                    }
                }
                return nodes;
            } else {
                return [];
            }
        },
        findNodeBy: function (tree, field, value) {
            var nodes = this.getAllNodes(tree);
            return _.find(nodes, function (node) {
                return node[field] === value;
            })
        },
        findRootOfNode: function (tree, node) {
            if (!node) return undefined;
            var map = {};
            _.each(this.getAllNodes(tree), function (node) {
                map[node.code] = node;
            });

            var current = node;
            while (current.parentCode) {
                current = map[current.parentCode];
            }
            return current;
        },
        findFirstHas: function (tree, node, field) {
            if (node) {
                var nodes = [node];
                for (var i = 0; i < nodes.length; i++) {
                    var current = nodes[i];
                    if (current[field]) return current;
                    nodes = nodes.concat(current.children);
                }
            }
            return null;
        },
        removeNode: function (tree, node) {
            if (node.parentCode) {
                var parent = this.findNodeBy(tree, 'code', node.parentCode);
                var index = parent.children.indexOf(node);
                parent.children.splice(index, 1);
            } else {
                var index = tree.indexOf(node);
                tree.splice(index, 1);
            }
        },
        moveNode: function (tree, node, newParentNode) {
            this.removeNode(tree, node);
            if (newParentNode) {
                newParentNode.children.push(node);
            } else {
                tree.push(newParentNode);
            }
            this.rebuild(tree);
        }
    })
    .value('PagerHelper', {
        addItem: function (pager, item) {
            pager.list.splice(0, 0, item)
            pager.totalCount += 1;
        },
        addItems: function (pager, items) {
            var self = this;
            _.each(items, function (item) {
                self.addItem(pager, item);
            })
        },
        removeItem: function (pager, item) {
            var index = pager.list.indexOf(item);
            if (index != -1) {
                pager.list.splice(index, 1);
                pager.totalCount -= 1;
            }
        },
        removeItems: function (pager, items) {
            var self = this;
            _.each(items, function (item) {
                self.removeItem(pager, item);
            })
        }
    });