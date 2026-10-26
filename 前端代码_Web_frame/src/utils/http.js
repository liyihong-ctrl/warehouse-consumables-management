/**
 * axios封装
 * 请求拦截、响应拦截、错误统一处理
 */
import axios from 'axios'
import QS from 'qs'
import base from './base.js'
import {
    Loading,
    Message
} from 'element-ui'
import router from '@/router/index';
import store from '../store/index.js'

axios.defaults.baseURL = base.url;
axios.defaults.timeout = 60000;
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

let loading;

function startLoading() {
    loading = Loading.service({
        lock: true,
        text: '加载中....',
        background: 'rgba(0, 0, 0, 0.7)'
    });
}

function endLoading() {
    loading.close();
}

function redirectToLogin(message) {
    Message.error(message);

    setTimeout(() => {
        store.commit("logout");
        router.replace({
            name: "login",
            query: {
                redirect: router.currentRoute.fullPath
            }
        });
    }, 1200);
}

axios.interceptors.request.use(
    (config) => {
        startLoading();

        const token = store.state.token;
        if (token) {
            config.headers.Authorization = token;
        }

        return config;
    },
    (error) => {
        return Promise.error(error);
    }
);

axios.interceptors.response.use(
    (response) => {
        endLoading();
        if (response.status === 200) {
            switch (response.data.code) {
                case 200:
                    if (response.headers.authorization) {
                        store.commit("initToken", response.headers.authorization);
                    }
                    if (!response.data.success) {
                        Message.error(response.data.data.msg);
                    } else {
                        return Promise.resolve(response);
                    }
                    break;
                case 401:
                    redirectToLogin("没有权限");
                    break;
                case 400:
                    Message.error(response.data.data.msg);
                    break;
                case 404:
                    break;
                case 500:
                    break;
                // Excel批量导入
                case 10000:
                    return Promise.resolve(response);
            }
        } else {
            return Promise.reject(response);
        }
    },
    (error) => {
        endLoading();
        switch (error.response.status) {
            case 401:
                redirectToLogin("未登录，请先登录");
                break;
            case 403:
                Message.error("身份过期，请重新登录");
                break;
            case 404:
                Message.error("网络请求不存在");
                break;
            default:
                Message.error("哎呀~网络又开小差了，请稍后刷新重试！");
        }
    }
);

export function get(url, params) {
    return new Promise((resolve, reject) => {
        axios.get(url, {
            params: params
        })
            .then(res => {
                resolve(res.data);
            })
            .catch(err => {
                reject(err.data)
            })
    });
}

export function post(url, params) {
    return new Promise((resolve, reject) => {
        axios.post(url, QS.stringify(params))
            .then(res => {
                resolve(res.data);
            })
            .catch(err => {
                reject(err.data)
            })
    });
}