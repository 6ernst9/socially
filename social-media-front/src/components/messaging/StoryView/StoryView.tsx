import React, {useEffect, useState} from "react";
import {StoryType} from "../../../types/content";
import {ReactComponent as Left} from "./../../../assets/icons/arrow-left.svg";
import {ReactComponent as Right} from "./../../../assets/icons/arrow-right.svg";
import {ReactComponent as Dots} from "./../../../assets/icons/dots-vertical.svg";
import './styles.css';
import {seeStory} from "../../../widgets/messaging-overview-widget/model/effects";
import {useDispatch, useSelector} from "react-redux";
import {sessionSelect} from "../../../redux/core/session/selectors";
import story from "../../core/Story/Story";

interface StoryViewProps {
    stories: StoryType[];
    initialIndex: number;
    onStoryEnd: () => void;
}

const StoryView: React.FC<StoryViewProps> = ({stories, initialIndex, onStoryEnd})=> {
    const [storyIndex, setStoryIndex] = useState(initialIndex);
    const id = useSelector(sessionSelect.id);
    const jwtToken = useSelector(sessionSelect.jwtToken);
    const dispatch = useDispatch();

    useEffect(() => {
       seenStory();
        const timer = setTimeout(() => {
            goToNextStory();
        }, 5000);

        return () => {
            clearTimeout(timer);
        }
    }, [storyIndex]);

    const goToNextStory = () => {
        if (storyIndex < stories.length - 1) {
            setStoryIndex(storyIndex + 1);
        } else {
            onStoryEnd();
        }
    };

    const goToPreviousStory = () => {
        if (storyIndex > 0) {
            setStoryIndex(storyIndex - 1);
        } else {
          onStoryEnd();
        }
    };

    const seenStory = () => {
        if(!stories[storyIndex].seen) {
            seeStory({id, jwtToken: jwtToken, storyId: stories[storyIndex].id, dispatch})
        }
    }

    return (
        <div className='messaging-story'>
            <div className='messaging-story-icon' onClick={goToPreviousStory}>
                <Left/>
            </div>
            <div className='messaging-story-main'>
                <div className='messaging-story-main-header'>
                    <div className='messaging-story-main-header-user'>
                        <img className='messaging-story-main-header-user-profile' src={stories[storyIndex].posterId.profilePhoto}/>
                        <p className='messaging-story-title'>{stories[storyIndex].posterId.username}</p>
                    </div>  
                    <div className='messaging-story-icon'>
                        <Dots/>
                    </div>
                </div>
                <div className="messaging-story-timeout-bar" key={storyIndex} />
                <img src={stories[storyIndex].url} className='messaging-story-photo'/>
            </div>
            <div className='messaging-story-icon' onClick={goToNextStory}>
                <Right/>
            </div>
        </div>
    )
}

export default StoryView;