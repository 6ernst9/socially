import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {defaultAuth} from "./defaultState";

const loginSlice = createSlice({
    name: 'authState',
    initialState: defaultAuth,
    reducers: {
        loginSuccess: (state, action: PayloadAction<string>) => {
            localStorage.setItem('session', action.payload);
           state.logged = true;
           state.error = 'NO-ERROR';
        },
        loginFailure: (state, action: PayloadAction<string>) => {
            state.logged = false;
            if(action.payload.includes('Incorrect password')){
                state.error = 'Incorrect password';
            } else if(action.payload.includes('Invalid username, email or phone number')){
                state.error = 'Invalid username, email or phone number';
            } else {
                state.error = 'Something went wrong'
            }
        },
        changePage: (state) => {
            state.error = null;
        },
        registrationSuccess: (state,action: PayloadAction<string>) => {
            localStorage.setItem('session', action.payload);
            state.logged = true;
            state.error = 'NO-ERROR';
        },
        registrationFailure: (state, action: PayloadAction<string>) => {
            state.logged = false;
            if(action.payload.includes('Username, email or phone number already used by another account')){
                state.error = 'Username, email or phone number already used by another account';
                } else if(action.payload.includes('Username is required')){
                state.error = 'Username is required';
            } else if(action.payload.includes('Password is required')){
                state.error = 'Password is required';
            } else if(action.payload.includes('Password must be at least 8 characters long')) {
                state.error = 'Password must be at least 8 characters long';
            } else if(action.payload.includes('Email is required')){
                state.error = 'Email is required';
            } else if(action.payload.includes('Invalid email')){
                state.error = 'Invalid email';
            } else if(action.payload.includes('Phone number is required')){
                state.error = 'Phone number is required';
            } else if(action.payload.includes('Invalid phone number')){
                state.error = 'Invalid phone number';
            } else if(action.payload.includes('Full name is required')){
                state.error = 'Full name is required';
            } else {
                state.error = 'Something went wrong';
            }
        },
        logout: (state) => {
            localStorage.removeItem('id');
            localStorage.removeItem('token');
            localStorage.removeItem('session');
            state = defaultAuth;
        }
    }
});

export const { loginSuccess, registrationSuccess, registrationFailure, loginFailure, changePage, logout } = loginSlice.actions;
export default loginSlice.reducer;