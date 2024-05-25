import React from "react";
import {useSelector} from "react-redux";
import {layoutSelect} from "../../../redux/core/layout/selectors";
import {sessionSelect} from "../../../redux/core/session/selectors";

const ProfileModal: React.FC = () => {
    const isOpen = useSelector(layoutSelect.isProfileOpen);
    const username = useSelector(sessionSelect.username);
    const phoneNumber = useSelector(sessionSelect.phoneNumber);
    const name = useSelector(sessionSelect.fullName);
    const profilePicture = useSelector(sessionSelect.profilePhoto);

    if(isOpen) {
        return (
            <div className='profile-settings-modal'>
                <p>Profile settings</p>
                <div className='profile-settings-profile-picture'>
                    <div className='profile-settings-profile-picture-head'>
                        <img src={profilePicture}/>
                        <div className='profile-settings-profile-picture-head-names'>
                            <p className='profile-settings-profile-picture-head-name'>{name}</p>
                            <p className='profile-settings-profile-picture-head-username'>{username}</p>
                        </div>
                    </div>
                    <div className='profile-settings-change-profile-background'>
                        <p className='profile-settings-change-profile-text'>Change</p>
                    </div>
                </div>
                <div className='profile-settings-username-container'>
                    <p className='profile-settings-username-title'>Username</p>
                    <div className='profile-settings-username-background'>
                        <p className='profile-settings-username-username'>{username}</p>
                    </div>
                </div>
                <div className='profile-settings-username-container'>
                    <p className='profile-settings-username-title'>Name</p>
                    <div className='profile-settings-username-background'>
                        <p className='profile-settings-username-username'>{name}</p>
                    </div>
                </div>
                <div className='profile-settings-username-container'>
                    <p className='profile-settings-username-title'>Phone number</p>
                    <div className='profile-settings-username-background'>
                        <p className='profile-settings-username-username'>{phoneNumber}</p>
                    </div>
                </div>
                <div className='profile-settings-bottom-container'>
                    <div className='profile-settings-change-profile-background'>
                        <p className='profile-settings-change-profile-text'>Submit</p>
                    </div>
                </div>
            </div>
        )
    }

    return null;
}

export default ProfileModal;