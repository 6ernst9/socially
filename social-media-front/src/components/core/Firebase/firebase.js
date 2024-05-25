import {initializeApp} from 'firebase/app'
import { getStorage } from "firebase/storage";

var firebaseConfig = {
    apiKey: "AIzaSyAg5x3qkSjtHDVe_rIWVLo2GLEs7aO3qKI",
    authDomain: "isproject-6332b.firebaseapp.com",
    projectId: "isproject-6332b",
    storageBucket: "isproject-6332b.appspot.com",
    messagingSenderId: "73506275953",
    appId: "1:73506275953:web:a313437be6b98cf49b91d0",
    measurementId: "G-ELDXM44966"
};
const app = initializeApp(firebaseConfig);
export const storage = getStorage();