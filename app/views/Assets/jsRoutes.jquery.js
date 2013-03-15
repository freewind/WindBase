var jsRoutes_handler = function(url) {
    return {
        'get': function(params,success) {
            jQuery.get(url, params, function() {
                success.apply(null, arguments);
            });
        },
        'post': function(data,success,type) {
            jQuery.post(url, data, function() {
                success.apply(null, arguments);
            }, type);
        }
    };
};

JsRoutes = {
    #{list actions.keySet(), as: 'controller'}
        ${controller} : {
            #{list actions.get(controller), as: 'action'}
                '${action.action}': jsRoutes_handler('${action.path}') ,
            #{/list}
        }  ${controller_isLast ? '' : ','}
    #{/list}
};