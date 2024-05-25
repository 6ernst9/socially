import {configureStore} from '@reduxjs/toolkit';
import authReducer from '../widgets/auth-login-widget/model/reducers';
import layoutReducer from './core/layout/reducers';
import sessionReducer from './core/session/reducers';
import messageReducer from '../widgets/messaging-overview-widget/model/reducers';

const rootReducer = {
    auth: authReducer,
    layout: layoutReducer,
    session: sessionReducer,
    message: messageReducer
};

const store = configureStore({
    reducer: rootReducer
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;