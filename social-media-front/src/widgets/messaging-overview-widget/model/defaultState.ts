import {MessageState} from "./types";

export const defaultConv: MessageState = {
    currentConversation: {
        id: "",
        fullName: "",
        profilePhoto: "",
        phoneNumber: "",
        username: "",
        email: "",
    },
    chats: [],
    conversations: [],
    searchResults: [],
    stories: []
}