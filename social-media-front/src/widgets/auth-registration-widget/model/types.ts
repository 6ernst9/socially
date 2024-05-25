import {Dispatch} from "redux";

export type RegisterProps = {
    email: string;
    fullName: string;
    username: string;
    password: string;
    phoneNumber: string;
    dispatch: Dispatch;
}

export type getAccountByUsername = {
    username: string;
    dispatch: Dispatch;
}