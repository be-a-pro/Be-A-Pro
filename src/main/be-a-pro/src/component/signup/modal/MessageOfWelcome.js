import ModalFrame from "./ModalFrame";
import styles from './MessageOfWelcome.module.scss';
import { ReactComponent as Logo } from '../../images/be-a-pro-b.svg';
import { ReactComponent as Welcome} from '../../images/icons/welcome.svg';

export default function MessageOfWelcome() {
    return (
        <ModalFrame>
            <Logo className={styles.logoOfSignup}/>
            <div className={styles.sectionOfWelcome}>
                <Welcome/>
                <span>회원가입이 완료되었습니다</span>
                <span>홍길동님 회원가입을 축하합니다.<br/>멋진 프로로 성장하시길 바라겠습니다.</span>
                <div className={styles.btnArea}>
                    <button>메인으로</button>
                    <button>로그인</button>
                </div>
            </div>
        </ModalFrame>
    )
}