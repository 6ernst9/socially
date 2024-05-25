import {Chat} from "../widgets/messaging-overview-widget/model/types";

interface UtilParameters {
    messages: Chat[];
    index: number;
}

export const getMessageShape = ({messages, index}: UtilParameters) => {
    let firstCorner: boolean = false;
    let secondCorner: boolean = false;
    if(index === 0) {
        firstCorner = true;
    }
    if(index === messages.length - 1) {
        secondCorner = true;
    }
    if(index > 0 && messages[index-1].senderId !== messages[index].senderId)
        firstCorner = true;
    if(index < messages.length -1 && messages[index + 1].senderId !== messages[index].senderId)
        secondCorner = true;
    return [firstCorner, secondCorner];
}

export const getTime = (timestamp: string) => {
    const currentTime = new Date();
    const timeDifference = currentTime.getTime() - new Date(timestamp).getTime();

    const seconds = Math.floor(timeDifference / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 60) {
        return 'now';
    } else if (minutes < 60) {
        return `${minutes}m ago`;
    } else if (hours < 24) {
        return `${hours}h ago`;
    } else {
        return `${days}d ago`;
    }
}