import React from "react";
import {getTime} from "../../../utils/utils";
import {ReactComponent as SendFill} from "../../../assets/icons/sendFill.svg";
import {ReactComponent as Send} from "../../../assets/icons/send.svg";

interface MessageCardProps {
    photo: string;
    fullName: string;
    date: string;
    isMine: boolean;
    isSeen: boolean;
    type: string;
    onClick: () => void;
}

const MessageCard: React.FC<MessageCardProps> =
    ({ photo,
         fullName,
         date,
        isSeen,
        isMine,
        type,
         onClick}) => {
    let message;
    let bubbleClassname;
    const isSnap = type === 'snap';

    if(isMine && !isSeen) {
        message = 'Delivered';
    }
    if(isMine && isSeen) {
        message = 'Opened';
    }
    if(!isMine && !isSeen) {
        message = isSnap ? 'New Snap' : 'New Chat';
    }
    if(!isMine && isSeen) {
        message = 'Received';
    }

    if(isSnap && !isSeen) {
        bubbleClassname = 'message-card-bubble-snap';
    }
    if(isSnap && isSeen) {
        bubbleClassname = 'message-card-bubble-snap-seen';
    }
    if(!isSnap && !isSeen) {
        bubbleClassname = 'message-card-bubble-message';
    }
    if(!isSnap && isSeen) {
        bubbleClassname = 'message-card-bubble-message-seen';
    }

    return (
        <div className='message-card' onClick={onClick}>
            <div className='message-card-right'>
                <img src={photo} className='message-card-img'/>
                <div className='message-card-text'>
                    <p className='message-card-title'>{fullName}</p>
                    {(isSeen || isMine) && (
                        <div className={isSnap ? 'message-card-bottom-snap' : 'message-card-bottom'}>
                            {isMine && isSeen && <Send/>}
                            {isMine && !isSeen && <SendFill/>}
                            <p className='message-card-message'>{message + ' • ' + getTime(date)}</p>
                        </div>
                    )}
                    {!isSeen && !isMine && (
                        <div className='message-card-bottom'>
                            <p className={isSnap ? 'message-card-message-snap' : 'message-card-message-message'}>{message}</p>
                            <p className='message-card-message'>{' • ' + getTime(date)}</p>
                        </div>
                    )}
                </div>
            </div>
            {!isMine && <div className={bubbleClassname}/>}
        </div>
    )
};

export default MessageCard;