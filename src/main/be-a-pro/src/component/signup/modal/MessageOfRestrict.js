import styles from './MessageOfRestrict.module.scss';
import { ReactComponent as Logo } from '../../images/be-a-pro-b.svg';
import ModalFrame from './ModalFrame';

export default function MessageOfRestrict() {
    return (
        <ModalFrame>
            <Logo className={styles.logoOfSignup}/>
            <div className={styles.sectionOfRestrict}>
                <span>
                    개인정보 미입력시 프로젝트 생성 및 지원에 
                    제한이 있을 수 있습니다. 
                </span>
                <span>개인정보는 내 프로필 - 수정하기에서 언제든지 변경하실 수 있습니다.</span>
                <div className={styles.btnArea}>
                    <button>나중에 하기</button>
                    <button>회원가입</button>
                </div>
            </div>
            
        </ModalFrame>
    )
}