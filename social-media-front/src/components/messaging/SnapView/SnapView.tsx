import React, {useEffect} from "react";
import {ReactComponent as Close} from "./../../../assets/icons/x.svg";
import './styles.css';
import {openSnap} from "../../../widgets/messaging-overview-widget/model/effects";
import {useDispatch, useSelector} from "react-redux";
import {sessionSelect} from "../../../redux/core/session/selectors";
import {Chat} from "../../../widgets/messaging-overview-widget/model/types";
import {messageSelect} from "../../../widgets/messaging-overview-widget/model/selectors";

interface SnapViewProps {
    msg: Chat;
    onSnapEnd: () => void;
}

const SnapView: React.FC<SnapViewProps> = ({msg, onSnapEnd})=> {
    const id = useSelector(sessionSelect.id);
    const jwtToken = useSelector(sessionSelect.jwtToken);
    const dispatch = useDispatch();
    const currentConversation = useSelector(messageSelect.currentConversation);

    useEffect(() => {
        openSnap({id, jwtToken: jwtToken, snap: msg, dispatch})
    }, []);

    return (
        <div className='messaging-snap'>
            <div className='messaging-snap-main'>
                <div className='messaging-snap-main-header'>
                    <div className='messaging-snap-main-header-user'>
                        <img className='messaging-snap-main-header-user-profile' src={currentConversation.profilePhoto}/>
                        <p className='messaging-snap-title'>{currentConversation.username}</p>
                    </div>
                    <div className='messaging-snap-icon' onClick={onSnapEnd}>
                        <Close/>
                    </div>
                </div>
                <img src={msg?.content} className='messaging-snap-photo'/>
            </div>
        </div>
    )
}

export default SnapView;