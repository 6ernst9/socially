import React from "react";
import {MEDIUM_GREY} from "../../../utils/constants";
import LText from "../LText/LText";
import {Link} from "react-router-dom";
import './styles.css';

interface CreditProps {
    color?: string;
}

const Credits: React.FC<CreditProps> = ({ color }) => {
    return (
        <div className='credits-container'>
            <div className='credits-links'>
                <Link to='/about' className='link-btn'>
                    <LText text="About" color={color || MEDIUM_GREY}/>
                </Link>
                <LText text=" • " color={color || MEDIUM_GREY}/>
                <Link to='/help' className='link-btn'>
                    <LText text="Help" color={color || MEDIUM_GREY}/>
                </Link>
                <LText text=" • " color={color || MEDIUM_GREY}/>
                <Link to='/privacy' className='link-btn'>
                    <LText text="Privacy" color={color || MEDIUM_GREY}/>
                </Link>
                <LText text=" • " color={color || MEDIUM_GREY}/>
                <Link to='/terms' className='link-btn'>
                    <LText text="Terms" color={color || MEDIUM_GREY}/>
                </Link>
                <LText text=" • " color={color || MEDIUM_GREY}/>
                <Link to='/language' className='link-btn'>
                    <LText text="Language" color={color || MEDIUM_GREY}/>
                </Link>
                <LText text=" • " color={color || MEDIUM_GREY}/>
                <Link to='/contact' className='link-btn'>
                    <LText text="Contact" color={color || MEDIUM_GREY}/>
                </Link>
            </div>
            <LText text="©2023 Socially from ROBERT ERNST" color={color || MEDIUM_GREY}/>
        </div>

    )
}

export default Credits;