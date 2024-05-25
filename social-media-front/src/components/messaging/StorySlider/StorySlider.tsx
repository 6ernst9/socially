import React, {useState} from "react";
import {StoryType} from "../../../types/content";
import Story from "../../core/Story/Story";
import StoryView from "../StoryView/StoryView";

interface StorySliderProps {
    stories: StoryType[];
}

const StorySlider: React.FC<StorySliderProps> = ({ stories }) => {
    const [seeStory, setSeeStory] = useState(false);
    const [index, setIndex] = useState(-1);

    const openStory = (i: number) => {
        setSeeStory(true);
        setIndex(i);
    }

    const closeStory = () => {
        setSeeStory(false);
    }

    return (
        <div style={{
            width: '100%',
            maxWidth: '400px',
            display: 'flex',
            flexDirection: 'row',
            left: 0}}>
            {stories.map((story, i) => {
                return (
                    <Story story={story} key={story.id} onClick={() => openStory(i)}/>
                )
            })}
            {seeStory && (
                <StoryView stories={stories} initialIndex={index} onStoryEnd={closeStory}/>
            )}
        </div>
    )
};

export default StorySlider;