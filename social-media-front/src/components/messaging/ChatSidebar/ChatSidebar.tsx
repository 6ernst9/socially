import React from "react";
import Edit from '../../../assets/icons/edit.svg';
import {ReactComponent as Add} from '../../../assets/icons/user-plus.svg';
import {Message} from "../../../widgets/messaging-overview-widget/model/types";
import MessageCard from "../MessageCard/MessageCard";
import {useDispatch, useSelector} from "react-redux";
import {changeConversation, closeConversation} from "../../../widgets/messaging-overview-widget/model/reducers";
import {getPersonChats} from "../../../widgets/messaging-overview-widget/model/effects";
import SearchBar from "../SearchBar/SearchBar";
import StorySlider from "../StorySlider/StorySlider";
import {StoryType} from "../../../types/content";
import {messageSelect} from "../../../widgets/messaging-overview-widget/model/selectors";
import {layoutSelect} from "../../../redux/core/layout/selectors";
import {closeModal, openModal} from "../../../redux/core/layout/reducers";
import {ReactComponent as Logo} from "../../../assets/icons/logo.svg";

interface ChatListProps {
    profilePhoto: string;
    jwtToken: string;
    id: string;
    conversations: Message[];
    stories: StoryType[];
}

const ChatSidebar: React.FC<ChatListProps> = ({profilePhoto, jwtToken, id, stories, conversations}) => {
    const dispatch = useDispatch();
    const isModalOpen = useSelector(layoutSelect.isModalOpen);
    const currentConversation = useSelector(messageSelect.currentConversation);

    const toggleModal = () => {
        if(isModalOpen) {
            dispatch(closeModal());
        } else {
            dispatch(openModal());
        }
    }

    return (
        <div className='chat-list-widget'>
            <div className='chat-list-header'>
                <img src={profilePhoto} className='chat-list-header-profile' onClick={toggleModal}/>
                <Logo/>
                <div className='chat-list-header-icons'>
                    <div className='chat-list-header-icon-back'>
                        <Add/>
                    </div>
                    <div className='chat-list-header-icon-back header-icon-message'>
                        <img src={Edit} className='chat-list-header-icon header-message-icon'/>
                    </div>
                </div>
            </div>
            <StorySlider stories={stories}/>
            <SearchBar/>
            {conversations.map((conv, index) =>
                <MessageCard
                    key={index}
                    type={conv.type}
                    isMine={conv.senderId.id === id}
                    photo={conv.senderId.id === id ? conv.receiverId.profilePhoto : conv.senderId.profilePhoto}
                    fullName={ conv.senderId.id === id
                        ? conv.receiverId.fullName
                        : conv.senderId.fullName}
                    date={conv.timestamp}
                    isSeen={conv.isSeen}
                    onClick={() => {
                        if(currentConversation === (conv.senderId.id === id ? conv.receiverId : conv.senderId)) {
                            dispatch(closeConversation());
                        } else {
                            dispatch(changeConversation(conv.senderId.id === id ? conv.receiverId : conv.senderId));

                            getPersonChats(
                                {
                                    id: id,
                                    jwtToken,
                                    dispatch,
                                    receiverId: conv.senderId.id === id ? conv.receiverId.id : conv.senderId.id})
                        }
                    }}/>
            )}
        </div>
    );
}

export default ChatSidebar;