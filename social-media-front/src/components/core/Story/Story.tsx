import React from "react";
import {StoryType} from "../../../types/content";
import "./styles.css";

interface StoryProps {
    story: StoryType;
    onClick: () => void;
}

const Story: React.FC<StoryProps> = ({story, onClick  }) => {
    return (
        <div className="outer-story">
            <div className={story.seen ? "inner-story-seen" : "inner-story"}
                 onClick={onClick}>
                <img src={story.posterId.profilePhoto}/>
            </div>
            <p>{story.posterId.username}</p>
        </div>
    )
}

export default Story;