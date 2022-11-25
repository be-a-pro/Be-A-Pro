import { ReactComponent as Logo } from '../images/be-a-pro-b.svg';
import { ReactComponent as Kakao} from '../images/icons/kakao.svg';
import { ReactComponent as Naver} from '../images/icons/naver.svg';
import { ReactComponent as Quit} from '../images/icons/quit.svg';
import styles from './Signup.module.css';

export default function Signup(props) {

    function quitSignUp() {
        if (props.state === true)
        props.setState(false);
    }
    return (
        <div className={styles.centerLocation}>
            <div className={styles.containerOfSignup}>
                    <Quit className={styles.iconOfQuit} onClick={quitSignUp}/>
                    <div className={styles.contentOfSignup}>
                        <div className={styles.content}>
                            <Logo className={styles.logoOfSignup}/>
                            <div className={styles.textOfSignup}>
                                간편하게 가입하고<br/>커리어를 풍부하게 만들어 줄 프로젝트에 참여해 보세요
                            </div>
                            {/* 
                            <div className={styles.kakaoOfSignup}>
                                <Kakao className={styles.logoOfKakao}/>
                                <div className={styles.textOfKakao}>
                                    카카오 로그인
                                </div>
                            </div>
                            */}
                            <div className={styles.naverOfSignup}>
                                <Naver className={styles.logoOfNaver}/>
                                <div className={styles.textOfNaver}>
                                    네이버 로그인
                                </div>                
                            </div>
                        </div>
                    </div>
                </div>
        </div>
    )
}