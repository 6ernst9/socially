import React, {useCallback, useRef, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {messageSelect} from "../../../widgets/messaging-overview-widget/model/selectors";
import {ReactComponent as Close} from "./../../../assets/icons/x.svg";

import {ReactComponent as Camera} from '../../../assets/icons/camera.svg';
import {ReactComponent as Back} from '../../../assets/icons/arrow-left.svg';
import {ReactComponent as Send} from '../../../assets/icons/sendFill.svg';

import ChatContainer from "../ChatContainer/ChatContainer";
import Webcam from "react-webcam";
import SendSnap from "../SendSnap/SendSnap";

import {storage} from '../../core/Firebase/firebase';
import {
    getDownloadURL,
    ref as storageRef,
    uploadBytes
} from "firebase/storage";
import {
    addStory,
    getConversations,
    getStories,
    sendSnap
} from "../../../widgets/messaging-overview-widget/model/effects";
import {sessionSelect} from "../../../redux/core/session/selectors";

const ChatOverview: React.FC = () => {
    const currentConversation = useSelector(messageSelect.currentConversation);
    const convSelected = currentConversation.id === '';
    const [isCameraSelected, setIsCameraSelected] = useState(false);
    const [sending, setSending] = useState(false);
    const id = useSelector(sessionSelect.id);
    const jwtToken = useSelector(sessionSelect.jwtToken);
    const dispatch = useDispatch();

    const webcamRef = useRef(null);
    const [image, setImage] = useState<string | null>(null);

    const openCamera = () => {
        setIsCameraSelected(true);
    }

    const capturePhoto = useCallback(() => {
        if(webcamRef != null && webcamRef.current != null){
            // @ts-ignore
            const imageSrc = webcamRef.current.getScreenshot();
            setImage(imageSrc);
        }
    }, [webcamRef]);

    const videoConstraints = {
        width: { min: 360 },
        height: { min: 640 },
        aspectRatio: 9/16
    };

    const clickButton = () => {
        if(image == null) {
            setIsCameraSelected(false);
        } else {
            setImage(null);
        }
    }

    const handleImageUpload = async(): Promise<string> => {
        const uuid = crypto.randomUUID();
        const imageRef = storageRef(storage, `/snaps/${uuid}`);

        // @ts-ignore
        const img = await fetch(image);

        return img.blob().then((img) => uploadBytes(imageRef, img)
            .then((snapshot) =>
                getDownloadURL(snapshot.ref)
                    .then((url) => url)));
    }

    const sendSnapMethod = async(receivers: number[]) => {
        const url = await handleImageUpload();

        await sendSnap({id, url, receivers}).then(async () => {
            setSending(false);
            setImage(null);
            setIsCameraSelected(false);
            await getConversations({id, jwtToken, dispatch})
        });
    }

    const addStoryMethod = async(receivers: number[]) => {
        const url = await handleImageUpload();

        await sendSnap({id, url, receivers}).then(async () => {
            await addStory({id, url}).then(async() => {
                setSending(false);
                setImage(null);
                setIsCameraSelected(false);
                await getConversations({id, jwtToken, dispatch});
                await getStories({id, jwtToken, dispatch});
            })
        });
    }

    if(convSelected) {
        return (
            <div className='chat-overview-opening'>
                {sending && <SendSnap back={() => setSending(false)} send={sendSnapMethod} addStory={addStoryMethod}/>}
                {!sending && isCameraSelected && (
                    <div className='chat-overview-opening-container-filming'>
                        {image == null && (
                            <Webcam
                                className='chat-overview-opening-camera-video'
                                ref={webcamRef}
                                videoConstraints={videoConstraints}
                                screenshotFormat="image/jpeg"
                                mirrored={true}/>
                        )}
                        {image!=null && (
                            <img src={image} className='chat-overview-opening-camera-video'/>
                        )}

                        {image == null && <div onClick={capturePhoto} className="chat-overview-opening-camera-button"/>}
                        {image!=null && (
                            <div className='chat-overview-opening-send-icon-back' onClick={() => setSending(true)}>
                                <Send/>
                            </div>
                        )}

                        <div className='chat-overview-opening-camera-icon-back' onClick={clickButton}>
                            {image == null && <Close/>}
                            {image != null && <Back/>}
                        </div>
                    </div>
                )}
                {!sending && !isCameraSelected && (
                    <div className='chat-overview-opening-container'>
                        <div className='chat-overview-opening-icon-back' onClick={openCamera}>
                            <Camera/>
                        </div>
                        <p className='chat-overview-opening-text'>Click the Camera to send snaps</p>
                    </div>
                )}

            </div>
        );
    } else return <ChatContainer/>
}

export default ChatOverview;