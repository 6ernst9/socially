import React from "react";
import {useDispatch, useSelector} from "react-redux";
import {layoutSelect} from "../../../redux/core/layout/selectors";
import {toggleDark, toggleLight} from "../../../redux/core/layout/reducers";
import './styles.css';
import {logout} from "../../../widgets/auth-login-widget/model/reducers";
import {logOut} from "../../../widgets/auth-login-widget/model/effects";

const SettingsModal: React.FC = () => {
    const isOpen = useSelector(layoutSelect.isModalOpen);
    const isDark = useSelector(layoutSelect.isDark);
    const dispatch = useDispatch();
    const changeTheme = () => {
        if(isDark) {
            dispatch(toggleLight());
        } else {
            dispatch(toggleDark());
        }
    };

    if(isOpen) {
        return (
            <div className='settings-modal'>
                <div className='settings-modal-section multiple-items'>
                    <p className='settings-modal-section-text'>Theme</p>
                    <div className='settings-modal-button' onClick={changeTheme}>
                        <p className='settings-modal-section-text theme-text' >{isDark ? 'Dark' : 'Light'}</p>
                    </div>
                </div>
                <div className='settings-modal-section'>
                    <p className='settings-modal-section-text'>Profile settings</p>
                </div>
                <div className='settings-modal-section'>
                    <p className='settings-modal-section-text'>Help center</p>
                </div>
                <div className='settings-modal-section'>
                    <p className='settings-modal-section-text'>Terms and conditions</p>
                </div>
                <div className='settings-modal-section' onClick={async () => {
                    await logOut({id: localStorage.getItem('session') || '', dispatch});
                    dispatch(logout());
                    window.location.reload();
                }}>
                    <p className='settings-modal-section-text log-out-text'>Log Out</p>
                </div>
            </div>
        )
    }
    return null;
}

export default SettingsModal;