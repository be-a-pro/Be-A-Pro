import axios from 'axios';
import { ReactComponent as Logo } from '../../../images/be-a-pro-b.svg';
import { ReactComponent as Kakao } from '../../../images/icons/kakao.svg';
import { ReactComponent as Naver } from '../../../images/icons/naver.svg';
import styles from './MessageOfSignup.module.css';
import ModalFrame from './ModalFrame';
import { redirect, useLocation, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

export default function MessageOfSignup(props) {

    const navigate = useNavigate();

    const Login = () => {
        // axios.get(`https://nid.naver.com/oauth2.0/authorize?client_id=${process.env.REACT_APP_NAVER_CLIENT_ID}&response_type=code&redirect_uri=${process.env.REACT_APP_NAVER_REDIRECT_URI}&state=${process.env.REACT_APP_NAVER_STATE}`)
        axios.get(`/api/oauth2/naver/login`)
            .then((res) => {
                const URL = res.request.responseURL;
                window.open(URL);
                console.log(res);
            })
    }

    return (
        <ModalFrame state={props.state} setState={props.setState}>
            <Logo className={styles.logoOfSignup} />
            <div className={styles.textOfSignup}>
                간편하게 가입하고<br />커리어를 풍부하게 만들어 줄 프로젝트에 참여해 보세요
            </div>
            {/* 기존 기능 구현 제한으로 카카오 로그인은 임시 보류했어요
                <div className={styles.kakaoOfSignup}>
                    <Kakao className={styles.logoOfKakao}/>
                    <div className={styles.textOfKakao}>
                        카카오 로그인
                    </div>
                </div>
            */}
            <div className={styles.naverOfSignup}>
                <Naver className={styles.logoOfNaver} />
                <div className={styles.textOfNaver} onClick={Login}>
                    {/* <a href='https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=QOmAuOIEwIW3A8MtvlhQ&state=516sc8umqqbueffjpek842dojp&redirect_uri=http://localhost:8080/api/oauth2/naver/redirect'> */}
                    네이버 로그인
                    {/* </a> */}
                </div>
                {/* <NaverLogin /> */}
            </div>
        </ModalFrame>
    )
}