import React from "react";

interface TextProps {
    text: string;
    color?: string;
    margin?: number;
    onClick?: () => void;
}

const BText: React.FC<TextProps> = ({text, color, onClick, margin}) =>{
    return(
        <p style={{ fontSize: 18, fontWeight: 'bold', color: color, margin: margin, cursor: "pointer"}} onClick={onClick}>{text}</p>
    )
}

export default BText;