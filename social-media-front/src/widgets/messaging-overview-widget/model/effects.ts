import {request} from "../../../components/core/Request/request";
import {BASE_URL} from "../../../utils/constants";
import {conversationsSuccess, personChatsSuccess, searchTerm, storiesSuccess} from "./reducers";
import {
    AddStoryPayload,
    Chat,
    ChatEffectsPayload, Connection, ConnectionPayload,
    EffectsPayload,
    GetAccountPayload,
    SearchPayload,
    SeeSnapPayload,
    SeeStoryPayload, SendSnapPayload
} from "./types";
import {User} from "../../../types/user";
import {UserStreak} from './types';
import {Content, StoryResponse} from "../../../types/content";

export const dataRequested = async ({ id, jwtToken, dispatch}: EffectsPayload) => {
    if(id == null || id === ''){
        return;
    }
    await getConversations({id, jwtToken, dispatch});
    await getStories({id, jwtToken, dispatch});
}

export const getConversations = async({id, jwtToken, dispatch}: EffectsPayload) => {
    const response = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('messaging.get-conversations/' + id)
        },
        headers: {
            'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP': '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });
    const messages: Chat[] = response.data;

    const snapz = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('content.get-snaps/' + id)
        },
        headers: {
            'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP': '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });
    const snaps: Content[] = snapz.data;

    const convs = messages.map(async msg => {
        const sender = await getAccount({id: msg.senderId, jwtToken});
        const receiver = await getAccount({id: msg.receiverId, jwtToken});

        let lastSnap: Content;
        snaps.sort((a, b) => a.timestamp.localeCompare(b.timestamp));

        snaps.forEach((snap) => {
            if((msg.senderId === snap.posterId && snap.receivers.includes(parseInt(msg.receiverId)))
            || (msg.receiverId === snap.posterId && snap.receivers.includes(parseInt(msg.senderId)))){
                lastSnap = snap;
            }
        })

        // @ts-ignore
        if(lastSnap != null && lastSnap.timestamp.localeCompare(msg.timestamp) > 0) {
            return {
                id: lastSnap.id,
                senderId: msg.senderId === lastSnap.posterId ? sender : receiver,
                receiverId: msg.senderId === lastSnap.posterId ? receiver : sender,
                content: lastSnap.url,
                timestamp: lastSnap.timestamp,
                type: 'snap',
                isSeen: msg.senderId === lastSnap.posterId ? lastSnap.seen.includes(parseInt(msg.receiverId)) : lastSnap.seen.includes(parseInt(msg.senderId))
            }
        }

        return {
            id: msg.id,
            senderId: sender,
            receiverId: receiver,
            content: msg.content,
            timestamp: msg.timestamp,
            type: 'message',
            isSeen: msg.isSeen
        }
    });

    const conversations = await Promise.all(convs);
    dispatch(conversationsSuccess(conversations));
}

export const getStories = async({id, jwtToken, dispatch}: EffectsPayload) => {
    const res = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('content.get-stories/' + id)
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });

    let storyResponses: StoryResponse[] = res.data;

    const storyz = storyResponses.map(async story => {
        const posterId = await getAccount({id: story.posterId.toString(), jwtToken});

        return {
            id: story.id,
            posterId,
            seen: story.seen.includes(parseInt(id)),
            url: story.url,
            timestamp: story.timestamp
        }
    });

    const stories = await Promise.all(storyz);
    dispatch(storiesSuccess(stories));
}

export const getPersonChats = async ({ id, jwtToken, dispatch, receiverId}: ChatEffectsPayload) => {
    const response = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('messaging.get-chats/' + id + '/' + receiverId)
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });

    let chat: Chat[] = response.data;

    const res = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('content.get-snaps-with/' + id + '/' + receiverId)
        },
        headers: {
            'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP': '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });
    const snaps: Content[] = res.data;
    snaps.map((snap) => {
        const posterId = snap.posterId;
        const receiver = snap.posterId === id ? receiverId : id;

        const snapMessage: Chat = {
            id: snap.id,
            senderId: posterId,
            receiverId: receiver,
            content: snap.url,
            timestamp: snap.timestamp,
            type: snap.type,
            isSeen: snap.seen.includes(parseInt(receiver))
        };

        chat = [...chat, snapMessage]
    })

    chat.sort((a, b) => a.timestamp.localeCompare(b.timestamp));

    dispatch(personChatsSuccess(chat));
}

// export const readPersonChat = async ({userId, messageId, jwtToken, dispatch}: ReadChatEffectsPayload) => {
//     await request({
//         url: MESSAGES_BASE_URL + 'updateReadStatus',
//         method: 'POST',
//         data: {messageId, isSeen: false},
//         headers: {
//             Authorization : "Bearer " + jwtToken
//         }
//     }).then((response) => {
//         dataRequested({userId, jwtToken, dispatch});
//     }).catch((error) => {
//         console.error(error);
//     })
// }

export const seeStory = async({id, storyId, jwtToken, dispatch}: SeeStoryPayload) => {
    return await request({
        url: BASE_URL,
        method: 'POST',
        params: {
            path: encodeURIComponent('content.open-story/' + id + "/" + storyId)
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => getStories({id, jwtToken, dispatch}));
}

export const openSnap = async({id, snap, jwtToken, dispatch}: SeeSnapPayload) => {
    return await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('content.open-snap/' + id + "/" + snap.id)
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then(async (response) => {
        console.debug(response);
        await getPersonChats({id, jwtToken, dispatch, receiverId: snap.senderId});
        await getConversations({id, jwtToken, dispatch});
    });
}

export const getAccount = async({id, jwtToken}: GetAccountPayload): Promise<User> => {
    return await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('account.get-account/' + id)
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => response.data);
}

export const searchByTerm = async({term, dispatch}: SearchPayload) => {
    await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('account.search-by-term' + term)
        }
    }).then((response) => {
        dispatch(searchTerm(response.data));
    }).catch((error) => {
        console.error(error);
    })
}

export const getFriendsWithStreak = async({id, jwtToken}: GetAccountPayload): Promise<UserStreak[]> => {
    const response = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('connection.get-friends/' + id)
        },
        headers: {
            'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP': '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });
    const friends: Connection[] = response.data;

    const bros = friends.map(async friend => {
        const account =  await getAccount({id: friend.id, jwtToken});
        const streak = await getStreak({id1: id, id2: friend.id, jwtToken});

        return {
            id: account.id,
            fullName:account.fullName,
            email: account.email,
            username: account.username,
            phoneNumber: account.phoneNumber,
            profilePhoto: account.profilePhoto,
            streak
        }
    });

    return await Promise.all(bros);
}

export const getStreak = async({id1, id2, jwtToken}: ConnectionPayload): Promise<number> =>{
    return await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('connection.get-snapstreak/' + id1 + '/' + id2)
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => response.data);
}

export const sendSnap = async({id, url, receivers}:SendSnapPayload) => {
    return await request({
        url: BASE_URL,
        method: 'POST',
        params: {
            path: encodeURIComponent('content.send-snap')
        },
        data: {
          posterId: id, url, receivers, type: 'snap'
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => console.debug(response.data))
        .catch((err) => console.error(err));
}

export const addStory = async({id, url}: AddStoryPayload) => {
    const response = await request({
        url: BASE_URL,
        method: 'GET',
        params: {
            path: encodeURIComponent('connection.get-friends/' + id)
        },
        headers: {
            'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP': '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    });
    const friends: Connection[] = response.data;

    const receivers: string[] = friends.map((id) => id.id);

    return await request({
        url: BASE_URL,
        method: 'POST',
        params: {
            path: encodeURIComponent('content.add-story')
        },
        data: {
            posterId: id, url, receivers, type: 'story'
        },
        headers: {
            'Authorization' : "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ",
            'X-FI-SY-IP' : '127.0.0',
            'X-FI-SY-SITE-ID': 'COM',
            'X-FI-SY-DEVICE': 'DESKTOP'
        }
    }).then((response) => console.debug(response.data))
        .catch((err) => console.error(err));
}