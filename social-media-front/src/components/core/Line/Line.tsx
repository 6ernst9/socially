import React from "react";
import {LIGHT_GREY} from "../../../utils/constants";

interface LineProps{
    padding?: number;
}

const Line: React.FC<LineProps> = ({padding}) => {
    return(
        <div style={{
            width: '100%',
            height: 1,
            backgroundColor: 'var(--line-color)',
            paddingLeft: padding,
            paddingRight: padding
        }}/>
    )
}

export default Line;
