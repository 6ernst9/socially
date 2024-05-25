import React from "react";
import './styles.css';

interface ButtonProps {
    content: string;
    onClick?: () => void;
}

const Button: React.FC<ButtonProps> = ({content,onClick}) => {
    return (
        <div className='button-container' onClick={onClick}>
            <p className='button-text'>{content}</p>
        </div>
    )
}

export default Button;