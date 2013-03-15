angular.module('JsRoutes', []).factory('JsRoutes', function ($http) {
    var defaultErrorHandler = function (data, status, headers, config) {
        console.log('Sorry, server responses ' + status + ' error: ' + data);
    };
    // angular post json by default, change it to key value pairs
    var keyValuesTransformFn = function (d) {
        return jQuery.param(d);
    };
    var commonHandler = function (method, url, params, data, success, error, config) {
        config = config || {};
        config.method = config.method || method;
        config.params = config.params || params;
        config.data = config.data || data;
        config.url = config.url || url;
        config.timeout = config.timeout || 120 * 1000;
        var postType = config.postType || 'form';
        if (postType === 'form') {
            config.transformRequest = keyValuesTransformFn;
            config.headers = config.headers || {};
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
            // config.headers['Accept'] = "application/json, text/html, text/plain, */*";
        }
        $http(config).success(success).error(error || defaultErrorHandler);
    };
    var jsRoutesHandler = function (path) {
        return {
            get: function (params, success, error, config) {
                commonHandler('get', path, params, {}, success, error, config);
            },
            post: function (data, success, error, config) {
                commonHandler('post', path, {}, data, success, error, config);
            },
            link: function (params) {
                var queryString = "";
                if (params) {
                    queryString = "?" + jQuery.param(params);
                }
                return path + queryString;
            }
        }
    };

    return {
        #{list actions.keySet(), as: 'module', separator: ','}
            #{if module}
                ${module} : {
                    #{list actions.get(module).keySet(), as: 'controller', separator: ','}
                    ${controller} : {
                        #{list actions.get(module).get(controller), as: 'item', separator: ','}
                            ${item.action}: jsRoutesHandler('${prefix+item.path}')
                        #{/list}
                    }
                    #{/list}
                }
            #{/if}
            #{else}
                #{list actions.get(module).keySet(), as: 'controller', separator: ','}
                ${controller} : {
                    #{list actions.get(module).get(controller), as: 'item', separator: ','}
                        ${item.action}: jsRoutesHandler('${prefix+item.path}')
                    #{/list}
                }
                #{/list}
            #{/else}
        #{/list}
    }
});