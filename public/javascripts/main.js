require({
    shim: {
        'controllers/gitHubController': {
            deps: ['admin', 'services/gitHubService']
        },
        'controllers/personController': {
            deps: ['admin', 'services/personService']
        },
        'controllers/personDetailsController': {
            deps: ['admin', 'services/personService']
        },
        'controllers/searchHistoryController': {
            deps: ['admin', 'services/messageService']
        },
        'controllers/twitterController': {
            deps: ['admin', 'services/twitterService']
        },
        'directives/ngController': {
            deps: ['admin']
        },
        'directives/tab': {
            deps: ['admin']
        },
        'directives/tabs': {
            deps: ['admin', 'directives/tab']
        },
        'filters/twitterfy': {
            deps: ['admin']
        },
        'libs/angular-resource': {
            deps: ['libs/angular']
        },
        'responseInterceptors/dispatcher': {
            deps: ['admin']
        },
        'services/gitHubService': {
            deps: ['admin', 'services/messageService']
        },
        'services/messageService': {
            deps: ['admin']
        },
        'services/personService': {
            deps: ['admin']
        },
        'services/twitterService': {
            deps: ['admin', 'services/messageService']
        },
        'app': {
            deps: ['libs/angular', 'libs/angular-resource']
        },
        'bootstrap': {
            deps: ['admin']
        },
        'routes': {
            deps: ['admin']
        },
        'run': {
            deps: ['admin']
        }
    }
}, ['require', 'controllers/gitHubController', 'controllers/personController', 'controllers/personDetailsController', 'controllers/searchHistoryController', 'controllers/twitterController', 'directives/ngController', 'directives/tabs', 'filters/twitterfy', 'responseInterceptors/dispatcher', 'routes', 'run'], function(require) {
    return require(['bootstrap']);
});