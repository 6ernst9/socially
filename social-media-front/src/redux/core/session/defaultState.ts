import {UserSession} from "./types";

export const defaultSession: UserSession = {
    token: localStorage.getItem('token') || '',
    fullName: '',
    username: '',
    email: '',
    id: localStorage.getItem( 'id') || '',
    profilePhoto: '',
    phoneNumber: ''
}