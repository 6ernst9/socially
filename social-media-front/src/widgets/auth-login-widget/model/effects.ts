import {BASE_URL} from "../../../utils/constants";
import {loginFailure, loginSuccess, logout} from "./reducers";
import {getSessionState, LoginProps} from "./types";
import {request} from "../../../components/core/Request/request";
import {continueSession, endSession, startSession} from "../../../redux/core/session/reducers";
import {getAccountByUsername} from "../../auth-registration-widget/model/types";

export const login = async ({username, password, dispatch}: LoginProps) => {
    await request({
        url: BASE_URL,
        method: 'POST',
        params: {
            path: 'account.login',
        },
        data: {username, password},
        headers: {
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => {
        dispatch(loginSuccess(response.data));
        getAccount({username, dispatch})
    }).catch((error) => {
        dispatch(loginFailure(error.response.data.message));
    })
}

export const logOut = async ({id, dispatch}: getSessionState) => {
    await request({
        url: BASE_URL,
        method: 'POST',
        params: {
            path: encodeURIComponent('account.logout/' + id),
        },
        headers: {
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => {
        console.debug(response.data);
        dispatch(endSession());
        dispatch(logout());
    })
}

export const getAccount = async({username ,dispatch} : getAccountByUsername) => {
    await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('account.get-account-by-username/' + username)
        },
        headers: {
            'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ',
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => {
        dispatch(startSession(response.data));
    }).catch((error) => {
        console.error(error);
    })
}

export const getSession = async({id ,dispatch} : getSessionState) => {
    await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('account.get-account/' + id)
        },
        headers: {
            'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ',
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => {
        dispatch(continueSession(response.data));
        dispatch(loginSuccess(localStorage.getItem('session') || ''));
    }).catch((error) => {
        console.error(error);
    })
}