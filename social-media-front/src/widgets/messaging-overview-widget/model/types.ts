import {User} from "../../../types/user";
import {Dispatch} from "redux";
import {StoryType} from "../../../types/content";

export type Message = {
    id: string;
    senderId: User;
    receiverId: User;
    content: string;
    timestamp: string;
    isSeen: boolean;
    type: string;
}

export type Chat = {
    id: string;
    senderId: string;
    receiverId: string;
    content: string;
    timestamp: string;
    isSeen: boolean;
    type: string;
}

export type MessageState = {
    currentConversation: User;
    chats: Chat[];
    conversations: Message[];
    searchResults: User[]
    stories: StoryType[];
}

export type ChatEffectsPayload = {
    id: string;
    receiverId: string;
    jwtToken: string;
    dispatch: Dispatch;
}

export type ReadChatEffectsPayload = {
    messageId: string;
    userId: string;
    jwtToken: string;
    dispatch: Dispatch;
}

export type EffectsPayload = {
    id: string;
    jwtToken: string;
    dispatch: Dispatch;
}

export type GetAccountPayload = {
    id: string;
    jwtToken: string;
}

export type SeeStoryPayload = {
    id: string;
    storyId: string;
    jwtToken: string;
    dispatch: Dispatch;
}

export type SeeSnapPayload = {
    id: string;
    snap: Chat;
    jwtToken: string;
    dispatch: Dispatch;
}

export type SearchPayload = {
    term: string;
    dispatch: Dispatch;
}

export type UserStreak = {
    id: string,
    fullName:string;
    email: string;
    username: string,
    phoneNumber: string,
    profilePhoto: string,
    streak: number
}

export type ConnectionPayload = {
    id1: string,
    id2: string,
    jwtToken: string
}

export type Connection = {
    id: string,
    sinceConnected: string;
}

export type SendSnapPayload = {
    id: string,
    url: string,
    receivers: number[]
}

export type AddStoryPayload = {
    id: string,
    url: string
}