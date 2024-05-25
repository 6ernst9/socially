import React, {useEffect} from "react";
import ChatOverview from "../../components/messaging/ChatOverview/ChatOverview";
import ChatSidebar from "../../components/messaging/ChatSidebar/ChatSidebar";
import {useDispatch, useSelector} from "react-redux";
import {sessionSelect} from "../../redux/core/session/selectors";
import {dataRequested} from "./model/effects";
import {messageSelect} from "./model/selectors";
import {useNavigate} from "react-router-dom";
import {authSelect} from "../auth-login-widget/model/selectors";
import './styles.css';
import SettingsModal from "../../components/core/SettingsModal/SettingsModal";

const MessagingOverviewWidget: React.FC = () => {
    const profilePhoto = useSelector(sessionSelect.profilePhoto);
    const id = useSelector(sessionSelect.id);
    const isLogged = useSelector(authSelect.isLogged);
    const jwtToken = useSelector(sessionSelect.jwtToken);
    const conversations = useSelector(messageSelect.conversations);
    const stories = useSelector(messageSelect.stories);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        if(!isLogged) {
            navigate('/login');
        }
        dataRequested({id, jwtToken, dispatch});
    }, [dispatch, jwtToken, id])

    return (
        <div className='messaging-overview'>
            <ChatSidebar
                stories={stories}
                jwtToken={jwtToken}
                id={id}
                profilePhoto={profilePhoto}
                conversations={conversations}/>
            <ChatOverview/>
            <SettingsModal/>
        </div>
    );
};

export default MessagingOverviewWidget;
