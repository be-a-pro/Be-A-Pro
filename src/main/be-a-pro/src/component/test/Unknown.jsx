/** @jsxImportSource @emotion/react */
import React from 'react'
import { css, keyframes } from '@emotion/react'
import { useEffect } from "react";
import ProjectApply from "../project/modal/ProjectApply.jsx";
import ProjectClear from "../project/modal/ProjectClear.jsx";
import ProjectComplete from "../project/modal/ProjectComplete.jsx";
import ProjectDelete from "../project/modal/ProjectDelete.jsx";
import ProjectPrev from "../project/modal/ProjectPrev.jsx";
import ProjectTemp from "../project/modal/ProjectTemp.jsx";
import MessageOfAegree from "../signup/modal/MessageOfAgree.js";
import NaverLogin from "../signup/modal/NaverLogin.js";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import loading from '../../images/loading.gif'

const Section = ({ children }) => {
    return (
        <div css={css`
        position : absolute;
        display : flex;
        flex-direction : column;
        align-items : center;
        justify-content: center;
        top : 50%;
        left : 50%;
        transform : translate(-50%, -50%);
        `}>
            {children}
        </div>
    )
}

const LoadingArea = () => {
    return (
        <div>
            <p css={css`
                font-family: 'Pretendard-Bold';
                letter-spacing: -0.03em;
                color: #09ce5b;
            `}>비어프로 서비스에서 네이버 로그인 중이에요!</p>
        </div>
    )
}

const LoadingImg = () => {
    return (
        <img src={loading} css={css`
        width : 9em;
        `}></img>
    )
}

export default function Unknown() {
    const navigate = useNavigate();
    useEffect(() => {
        const URL = window.location.href;
        const CODE = URL.split('=')[1].split('&')[0];
        const STATE = URL.split('=')[2];
        // console.log(CODE);
        // console.log(STATE);
        axios.get(`/api/oauth2/naver/redirect?code=${CODE}&state=${STATE}`)
            .then((res) => {
                localStorage.setItem('access-token', res.headers.authorization);
                if (localStorage.getItem('access-token')) {
                    navigate('/');
                }
            })
    })
    return (
        <>
            <Section>
                <LoadingImg />
                <LoadingArea />
            </Section>
        </>
    )
}