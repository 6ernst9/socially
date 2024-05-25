import React, {useEffect, useState} from "react";
import {getFriendsWithStreak} from "../../../widgets/messaging-overview-widget/model/effects";
import {useSelector} from "react-redux";
import {sessionSelect} from "../../../redux/core/session/selectors";
import {ReactComponent as Back} from '../../../assets/icons/arrow-left.svg';
import {ReactComponent as Send} from '../../../assets/icons/sendFill.svg';
import {UserStreak} from "../../../widgets/messaging-overview-widget/model/types";
import SendSnapCard from "../SendSnapCard/SendSnapCard";
import './styles.css';

interface SendSnapProps {
    back: () => void,
    send: (receivers: number[]) => void;
    addStory: (receivers: number[]) => void;
}

const SendSnap: React.FC<SendSnapProps> = ({back, send, addStory}) => {
    const id = useSelector(sessionSelect.id);
    const profilePhoto = useSelector(sessionSelect.profilePhoto);
    const jwtToken = useSelector(sessionSelect.jwtToken);
    const [friends, setFriends] = useState<UserStreak[]>([]);
    const [receivers, setReceivers] = useState<number[]>([]);

    const [story, setStory] = useState(false);

    useEffect(() => {
        getFriendsWithStreak({id, jwtToken}).then((friends) => setFriends(friends));
        }, []);

    const onSelect = (id: string) => {
        const users = receivers;
        users.push(parseInt(id));
        setReceivers(users);
    };

    const onDeselect = (id: string) => {
        const users = receivers.filter((userId) => userId !== parseInt(id));
        setReceivers(users);
    }

    return (
        <div className='chat-overview-send-snap-container'>
            <div className='chat-overview-send-snap-container-header'>
                <div className='chat-overview-send-snap-back' onClick={back}>
                    <Back/>
                </div>
                <div className='chat-overview-send-snap-send-back' onClick={() => {
                    if(receivers.length > 0 || story){
                        if(story) {
                            addStory(receivers);
                        } else {
                            send(receivers);
                        }
                    }
                }}>
                    <p className='chat-overview-send-snap-send-title'>Send</p>
                    <Send/>
                </div>
            </div>

            <SendSnapCard
                id={id}
                profilePhoto={profilePhoto}
                fullName='My Story'
                streak={null}
                onSelect={() => setStory(true)}
                onDeselect={() => setStory(false)}
            />
            {friends.map((friend) => {
                return <SendSnapCard
                    key={friend.id}
                    id={friend.id}
                    profilePhoto={friend.profilePhoto}
                    fullName={friend.fullName}
                    streak={friend.streak}
                    onSelect={onSelect}
                    onDeselect={onDeselect}
                />
            })}
        </div>
    )
}

export default SendSnap;