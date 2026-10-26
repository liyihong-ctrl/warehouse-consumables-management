import Application from './Application'
import ApplicationDetail from './ApplicationDetail'

export default [
    {
        path: '/application',
        component: () => import('./Layout'),
        children: [
            {
                name: 'list',
                path: 'application',
                component: Application,
            },
            {
                name: 'action',
                path: 'application/:action/:id?',
                component: ApplicationDetail,
            }
        ]
    },
]
