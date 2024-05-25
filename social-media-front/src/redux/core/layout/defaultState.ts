import {LayoutState} from "./types";

export const defaultLayout : LayoutState = {
    isDark: localStorage.getItem('theme') === 'dark',
    isModalOpen: false,
    isProfileOpen: false
}