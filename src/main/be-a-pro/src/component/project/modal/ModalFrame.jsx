import React from 'react'
import styled from 'styled-components'
import { ReactComponent as Quit } from '../../../images/icons/quit.svg';

export default function ModalFrame({ children, setCheck, listCheck }) {

    const QuitList = (event) => {
        event.stopPropagation();
        setCheck(false);
        // alert(listCheck);
    }

    const Frame = styled.div`
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        // width: 45em;
        // height: 31.5em;
        // border:solid;
        // padding: 64px 60px 63px 60px;
        box-sizing : border-box;
        border-radius: 8px;
        box-shadow: 0 12px 24px 0 rgba(0, 0, 0, 0.4);
        background-color: #fff;

        display: flex;
        flex-direction: column;
        align-items: center;
        // justify-content: center;
    `;
    return (
        <Frame onClick={QuitList}>
            <Quit className="QuickBtn" />
            {children}
        </Frame>
    )
}
