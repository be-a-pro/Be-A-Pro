import { ReactComponent as Logo } from '../../../images/be-a-pro-b.svg';
import { ReactComponent as Kakao } from '../../../images/icons/kakao.svg';
import { ReactComponent as Naver } from '../../../images/icons/naver.svg';
import styles from './MessageOfSignup.module.css';
import ModalFrame from './ModalFrame';

export default function MessageOfSignup(props) {

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
                <div className={styles.textOfNaver}>
                    네이버 로그인
                </div>
            </div>
        </ModalFrame>
    )
}