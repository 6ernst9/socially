import {closeConversation} from "../../../widgets/messaging-overview-widget/model/reducers";
import MessageBubble from "../MessageBubble/MessageBubble";
import {getMessageShape} from "../../../utils/utils";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {messageSelect} from "../../../widgets/messaging-overview-widget/model/selectors";
import {sessionSelect} from "../../../redux/core/session/selectors";
import {getConversations, getPersonChats} from "../../../widgets/messaging-overview-widget/model/effects";
import {CompatClient, Stomp} from "@stomp/stompjs";

import {ReactComponent as Phone} from '../../../assets/icons/phone.svg';
import {ReactComponent as Video} from '../../../assets/icons/video.svg';
import {ReactComponent as Info} from '../../../assets/icons/info.svg';
import {ReactComponent as Image} from '../../../assets/icons/image.svg';
import {ReactComponent as Back} from '../../../assets/icons/arrow-left.svg';
import {ReactComponent as Heart} from '../../../assets/icons/heart.svg';
import {ReactComponent as Send} from '../../../assets/icons/send.svg';
import {ReactComponent as Microphone} from '../../../assets/icons/microphone.svg';
import SnapView from "../SnapView/SnapView";
import {Chat} from "../../../widgets/messaging-overview-widget/model/types";

let socket: WebSocket;
let stompClient: CompatClient;

const ChatContainer: React.FC = () => {
    const currentConversation = useSelector(messageSelect.currentConversation);
    const dispatch = useDispatch();
    const jwtToken = useSelector(sessionSelect.jwtToken);
    const messages = useSelector(messageSelect.currentChat);
    const id = useSelector(sessionSelect.id);
    const [message, setMessage] = useState<string>('');
    const [connected, setConnected] = useState<boolean>(false);
    const [seeSnap, setSeeSnap] = useState(false);
    const [currentMessage, setCurrentMessage] = useState<Chat>(messages[0]);

    const convertToXML = (data: Chat[]): string => {
        let xml = '<?xml version="1.0" encoding="UTF-8"?>\n<Chats>\n';

        data.forEach((item) => {
            xml += '  <Chat>\n';
            xml += `    <id>${item.id}</id>\n`;
            xml += `    <senderId>${item.senderId}</senderId>\n`;
            xml += `    <receiverId>${item.receiverId}</receiverId>\n`;
            xml += `    <timestamp>${item.timestamp}</timestamp>\n`;
            xml += `    <content>${item.content}</content>\n`;
            xml += '  </Chat>\n';
        });

        xml += '</Objects>';
        return xml;
    };

    const downloadXML = (xml: string) => {
        const blob = new Blob([xml], { type: 'application/xml' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'messages.xml';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    };

    const handleDownload = () => {
        const xml = convertToXML(messages);
        downloadXML(xml);
    };

    const openSnap = (msg: Chat) => {
        if(id !== msg.senderId && !msg.isSeen) {
            setCurrentMessage(msg);
            setSeeSnap(true);
        }
    }

    const updateMessages = async () => {
        await getPersonChats(
            {
                id,
                jwtToken,
                dispatch,
                receiverId: currentConversation.id});
        await getConversations({id, jwtToken, dispatch});
    }

    const connect = () => {
        socket = new WebSocket('ws://localhost:8082/chat/websocket');
        stompClient = Stomp.over(socket);
        // @ts-ignore
        stompClient.connect({}, frame => {
            setConnected(true);

            stompClient.subscribe('/topic/reply', message => {
                updateMessages();
            });
        });
    }

    const disconnect = () => {
        if(connected && socket){
            socket.close();
            setConnected(false);
        }
    }

    const sendMessage = async () => {
        if (socket && socket.readyState === WebSocket.OPEN) {
            stompClient.send("/app/sendMessage", {}, JSON.stringify({
                senderId: parseInt(id),
                receiverId: parseInt(currentConversation.id),
                content: message,
                timestamp: new Date().toISOString()
            }));
            setMessage('');
        }
    };

    const onMessageChange = (event: React.FormEvent<HTMLInputElement>) => setMessage(event.currentTarget.value);

    useEffect(() => {
        connect();
        return () => {
            if(stompClient) disconnect();
        }
    }, [])

    return (
        <div className='chat-overview'>
            <div className='chat-overview-header'>
                <div className='chat-overview-header-info'>
                    <div className='chat-overview-header-icon' onClick={() => dispatch(closeConversation())}>
                        <Back/>
                    </div>
                    <img src={currentConversation.profilePhoto} className='chat-overview-header-picture'/>
                    <p className='chat-overview-opening-title'>{currentConversation.fullName}</p>
                </div>
                <div className='chat-overview-header-actions'>
                    <div className='chat-overview-header-call'>
                        <p className='chat-overview-header-call-text'>Call</p>
                        <Phone/>
                        <p className='chat-overview-header-call-bar'>|</p>
                        <Video/>
                    </div>
                    <div className='chat-overview-header-icon' onClick={handleDownload}>
                        <Info/>
                    </div>
                </div>
            </div>
            <div className='chat-overview-messages-container'>
                {messages.map((msj, index) =>
                    <MessageBubble
                        key={index}
                        msg={msj}
                        isMine={msj.senderId === id}
                        firstCorner={getMessageShape({messages, index})[0]}
                        secondCorner={getMessageShape({messages, index})[1]}
                        openSnap={() => openSnap(msj)}/>
                )}
                {seeSnap && (<SnapView msg={currentMessage} onSnapEnd={() => setSeeSnap(false)}/>)}
            </div>
            <div className='chat-overview-bottom'>
                <div className='chat-overview-form'>
                    <input
                        className='chat-overview-chat'
                        placeholder='Send a message...'
                        value={message}
                        onChange={onMessageChange}
                        onKeyPress={async event => {
                            if (event.key === 'Enter') {
                                await sendMessage();
                            }
                        }
                    }/>
                    {message.length === 0 && (
                        <div className='chat-overview-bottom-icons'>
                            <Microphone/>
                            <Image/>
                            <Heart/>
                        </div>
                    )}
                    {message.length > 0 && (
                        <div className='chat-overview-bottom-icons'>
                            <Send onClick={async () => await sendMessage()}/>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
};

export default ChatContainer;